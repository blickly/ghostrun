import logging
import json
import time

from google.appengine.ext import webapp, db

from maps import Dot

DOT_POINTS = 10

###############################################################################
#####                             Models
###############################################################################

class Player(db.Model):
    gid = db.IntegerProperty()
    isPacman = db.BooleanProperty()
    lat = db.IntegerProperty()
    lng = db.IntegerProperty()
    lastCheckin = db.IntegerProperty()

class DotAte(db.Model):
  gid = db.IntegerProperty()
  dot_id = db.IntegerProperty()
  timestamp = db.IntegerProperty()


###############################################################################
#####                             Handlers
###############################################################################

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
      otherinfo = []
      for o in others:
        otherinfo.append((o.lastCheckin, o.pid, (o.lat,o.lng)))
      logging.info('LatencyStats & now: %d & mypid: %d & mylatlng: (%d,%d) & others: %s' % (t, pid, lat, lng, str(otherinfo)))


      response = {'locations' : dict((p.key().id(), [{"lat" : p.lat, "lng" : p.lng}, p.isPacman]) for p in others),
                  'dead' : False,
                  'dotAte' : [de.dot_id for de in dotsEat]}

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
