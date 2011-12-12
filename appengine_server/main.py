#!/usr/bin/env python

import logging
import json
import time

from google.appengine.ext import webapp, db
from google.appengine.api import users
from google.appengine.ext.webapp import util

# some constants
EAT_DIST = 0.0001
DOT_SPACE = 300
DOT_POINTS = 10

#class Counter(db.Model):
#  count = db.IntegerProperty(default=0)
#def update_and_get_count(cid):
#   """Increment the named counter by 1."""
#   def _update_counter(cid):
#       obj = db.get(cid)
#       obj.count += 1
#       obj.put()
#       return obj.count
#return db.run_in_transaction(_update_counter, cid)

class Game(db.Model):
  mid = db.IntegerProperty()

class DotAte(db.Model):
  gid = db.IntegerProperty()
  dot_id = db.IntegerProperty()
  timestamp = db.IntegerProperty()

class Dot(db.Model):
  gid = db.IntegerProperty()
  lat = db.IntegerProperty()
  lng = db.IntegerProperty()

class Map(db.Model):
    content = db.TextProperty()

class Player(db.Model):
    gid = db.IntegerProperty()
    isPacman = db.BooleanProperty()
    lat = db.IntegerProperty()
    lng = db.IntegerProperty()
    lastCheckin = db.IntegerProperty()

class MainHandler(webapp.RequestHandler):
    def get(self):
        self.response.out.write('hi...')

class SaveMapHandler(webapp.RequestHandler):
    def get(self):
      self.post()

    def post(self):
        response = self.request.get('map')
        m = Map(content=response)
        m.put()
        self.response.out.write(str(m.key().id()))

class GetMapsHandler(webapp.RequestHandler):
    def get(self):
        maps = map(lambda x: x.key().id(), Map.all())
        self.response.out.write(json.dumps(maps))

class GetMapHandler(webapp.RequestHandler):
    def get(self):
        mid = long(self.request.get('mid'))
        m = Map.get_by_id(mid)

        if m:
            self.response.out.write(m.content)
        else:
            self.response.out.write(str(False))

class GeoPtOffset:
  def __init__(self, start, end):

    self.deltaLat = end[0] - start[0]
    self.deltaLng = end[1] - start[1]

    self.length = float(distance(start, end))
    self.length /= DOT_SPACE

    try:
      self.scaleBy(1.0 / self.length)
    except:
      self.length = 0.0

    self.curPt = start

  def addTo(self, pt):
    return (long(pt[0] + self.deltaLat), long(pt[1] + self.deltaLng))

  def scaleBy(self, factor):
    self.deltaLat = self.deltaLat * factor
    self.deltaLng = self.deltaLng * factor

  def next(self):
    if self.length > 0:
      self.curPt = self.addTo(self.curPt)
      self.length -= 1
      return self.curPt

    return None

def getGeoPt(pt_json):
  return (long(pt_json['lat']), long(pt_json['lng']))

def populate_map(m, gid):
  dot_id = 0
  json_m = json.loads(m.content)
  mapping = dict([(pt['id'], pt) for pt in json_m])
  done = set()
  dots = []

  for pt in json.loads(m.content):
    curpt = getGeoPt(pt)
    for n in pt['neighbors']:
      if n not in done:
        offset = GeoPtOffset(curpt, getGeoPt(mapping[n]))

        dot = offset.next()
        while dot:
          if (dot == None):
            print "what is going on here?"
          d = Dot(gid=gid, lat=dot[0], lng=dot[1])
          d.put()
          dots.append(d)
          dot = offset.next()

    done.add(pt['id'])

  return dots

def distance(pt1, pt2):
  return (float(pt1[0] - pt2[0]) ** 2.0 + float(pt1[1] - pt2[1]) ** 2.0) ** 0.5


def dotsToMap(ds):
  result = {}
  for d in ds:
    result[d.key().id()] = (d.lat, d.lng)

  return result

class NewGameHandler(webapp.RequestHandler):
    def get(self):
        mid = long(self.request.get('mid'))

        m = Map.get_by_id(mid)
        g = Game(mid=mid)
        g.put()
        gid = g.key().id()

        result = populate_map(m, gid)
        self.response.out.write(str(gid))

class GetGamesHandler(webapp.RequestHandler):
  def get(self):
    gs = Game.all()

    games_info = []
    for g in gs:
      players = Player.gql('WHERE gid = :1', g.key().id())
      maxTime = 0
      if players:
        maxTime = max(map(lambda x: x.lastCheckin, players))

      games_info.append((g.key().id(), maxTime, players.count()))

    self.response.out.write(json.dumps(games_info))

class JoinGameHandler(webapp.RequestHandler):
  def get(self):
    gid = long(self.request.get('gid'))
    lat = self.request.get('lat')
    lng = self.request.get('lng')
    pacman = True
    if self.request.get('pacman') == 'false':
        pacman = False

    p = Player()
    p.gid = gid
    p.lat = long(lat)
    p.lng = long(lng)
    p.isPacman = bool(pacman)
    p.lastCheckin = long(time.time() * 1000)
    p.put()
            
    ds = Dot.gql("WHERE gid=:1", gid)
    response = {'pid' : p.key().id(), 'dots' : dotsToMap(ds)}
    self.response.out.write(json.dumps(response))

class EatDotHandler(webapp.RequestHandler):
  def get(self):
    gid = long(self.request.get('gid'))
    eat = json.loads(self.request.get('eat'))

    points = 0
    for e in eat:
      d = Dot.get_by_id(e)
      if d:
        d.delete()
        points += 1
        da = DotAte(gid=gid, dot_id=e, timestamp=int(time.time()*1000))
        da.put()
    
    self.response.out.write(str(points * DOT_POINTS))

class GameMoveHandler(webapp.RequestHandler):
    def get(self):
      gid = long(self.request.get('gid'))
      pid = long(self.request.get('pid'))
      lat = self.request.get('lat')
      lng = self.request.get('lng')

      others = Player.gql("WHERE gid = :1", gid)
      p = Player.get_by_id(pid)
      dotsEat = DotAte.gql('WHERE gid = :1 AND timestamp >= :2', gid, p.lastCheckin)

      others = filter(lambda x: x.key().id() != pid, others)

      p.lat = long(lat)
      p.lng = long(lng)
      p.lastCheckin = int(time.time() * 1000)
      p.put()
      
      t = int(round(time.time() * 1000.0))
      for p in others:
        logging.info('LatencyStats %d %d %d %d' % (p.lastCheckin, p.lat, p.lng, t))

      response = {'locations' : dict((p.key().id(), [{"lat" : p.lat, "lng" : p.lng}, p.isPacman]) for p in others),
                  'dead' : False,
                  'dotAte' : [de.dot_id for de in dotsEat]}

      self.response.out.write(json.dumps(response))
        

class DeleteAllHandler(webapp.RequestHandler):
    def get(self):
      db.delete(Game.all())
      db.delete(Map.all())
      db.delete(Player.all())
      db.delete(Dot.all())
      db.delete(DotAte.all())


def main():
    application = webapp.WSGIApplication([('/', MainHandler),
                                          ('/save_map', SaveMapHandler),
                                          ('/get_map', GetMapHandler),
                                          ('/get_maps', GetMapsHandler),
                                          ('/new_game', NewGameHandler),
                                          ('/get_games', GetGamesHandler),
                                          ('/post_position', GameMoveHandler),
                                          ('/delete_all', DeleteAllHandler),
                                          ('/eat_dot', EatDotHandler),
                                          ('/join_game', JoinGameHandler)],
                                         debug = True)
    util.run_wsgi_app(application)

if __name__ == '__main__':
    main()
