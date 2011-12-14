import json
import time

from google.appengine.ext import webapp, db

from maps import Map
from maps import Dot
from players import Player

###############################################################################
#####                             Models
###############################################################################

class Game(db.Model):
  mid = db.IntegerProperty()

###############################################################################
#####                             Handlers
###############################################################################

class NewGameHandler(webapp.RequestHandler):
    def get(self):
        mid = long(self.request.get('mid'))

        m = Map.get_by_id(mid)
        g = Game(mid=mid)
        g.put()
        gid = g.key().id()

        result = m.populate(gid)
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

    dotdict = Dot.dotsAsDict(gid)
    response = {'pid' : p.key().id(), 'dots' : dotdict}
    self.response.out.write(json.dumps(response))
