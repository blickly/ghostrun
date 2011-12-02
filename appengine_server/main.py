#!/usr/bin/env python
#
import cgi
import datetime
import json

from google.appengine.ext import webapp, db
from google.appengine.api import users
from google.appengine.ext.webapp import util

class Counter(db.Model):
    count = db.IntegerProperty(default=0)

def update_and_get_count(cid):
   """Increment the named counter by 1."""
   def _update_counter(cid):
       obj = db.get(cid)
       obj.count += 1
       obj.put()
       return obj.count

   return db.run_in_transaction(_update_counter, cid)

class Map(db.Model):
    index = db.IntegerProperty()
    content = db.StringProperty()

class Player(db.Model):
    player_id = db.IntegerProperty()
    game_id = db.IntegerProperty()
    location = db.GeoPtProperty()
    last_checkin = db.DateTimeProperty()

class MainHandler(webapp.RequestHandler):
    def get(self):
        self.response.out.write('hi...')

class SaveMapHandler(webapp.RequestHandler):
    def get(self):
        response = self.request.get('map')
        index = update_and_get_count(mc_counter)

        m = Map(index=index, content=response)
        m.put()
        self.response.out.write(str(m.index))

class GetMapHandler(webapp.RequestHandler):
    def get(self):
        index = long(self.request.get('index'))
        m = Map.gql("WHERE index = :1", index).get()

        if m:
            self.response.out.write(m.content)
        else:
            self.response.out.write(str(False))

class NewPlayerHandler(webapp.RequestHandler):
    def get(self):
        self.response.out.write(update_and_get_count(pc_counter))

class NewGameHandler(webapp.RequestHandler):
    def get(self):
        self.response.out.write(update_and_get_count(gc_counter))

class GameMoveHandler(webapp.RequestHandler):
    def get(self):
        gid = long(self.request.get('gid'))
        pid = long(self.request.get('pid'))
        lat = self.request.get('lat')
        lng = self.request.get('lng')
        geopt = db.GeoPt(float(lat), float(lng))

        try:
            p = Player.gql("WHERE player_id = :1", pid).get()
            if not p:
                p = Player(player_id=pid)
            p.game_id = gid
            p.location = geopt
            p.last_checkin = datetime.datetime.now()
            p.put()
            all_players = Player.gql("WHERE game_id = :1", gid)

            self.response.out.write(json.dumps(
              dict((p.player_id,str(p.location)) for p in all_players)))

        except Exception as e:
            self.response.out.write(str(e))
            self.response.out.write('false')

class DeleteAllHandler(webapp.RequestHandler):
    def get(self):
        db.delete(Map.all())
        db.delete(Player.all())

pc_counter = None
mc_counter = None
gc_counter = None

pc = Counter()
mc = Counter()
gc = Counter()

pc.put()
mc.put()
gc.put()

pc_counter = pc.key()
mc_counter = mc.key()
gc_counter = gc.key()

def main():
    global pc_counter, mc_counter, gc_counter

    application = webapp.WSGIApplication([('/', MainHandler),
                                          ('/save_map', SaveMapHandler),
                                          ('/get_map', GetMapHandler),
                                          ('/new_game', NewGameHandler),
                                          ('/post_position', GameMoveHandler),
                                          ('/delete_all', DeleteAllHandler),
                                          ('/new_player', NewPlayerHandler)],
                                         debug=True)
    util.run_wsgi_app(application)

if __name__ == '__main__':
    main()
