#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import cgi
import datetime

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

class Game(db.Model):
    index = db.IntegerProperty()
    players = db.ListProperty(int)
    locations = db.ListProperty(db.GeoPt)
    timestamps = db.ListProperty(datetime.time)
    
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
        index = update_and_get_count(gc_counter)
        g = Game(index=index)
        g.put()
        
        self.response.out.write(str(g.index))

class GameMoveHandler(webapp.RequestHandler):
    def get(self):
        gid = long(self.request.get('gid'))
        pid = long(self.request.get('pid'))
        lat = self.request.get('lat')
        lng = self.request.get('lng')
        geopt = db.GeoPt(float(lat), float(lng))
        
        try:
            g = Game.gql("WHERE index = :1", gid).get()
            if pid not in g.players:
                g.players.append(pid)
                g.locations.append(geopt)
                g.timestamps.append(datetime.time())
            else:
                for i in range(len(g.players)):
                    if g.players[i] == pid:
                        g.locations[i] = geopt
                        g.timestamps[i] = datetime.time()
                        break
            g.put()
            
            self.response.out.write(str(g.players) + "</br>" + str(g.locations))
        except Exception as e:
            self.response.out.write(str(e))
            self.response.out.write('false')        
        
class DeleteAllHandler(webapp.RequestHandler):
    def get(self):
        db.delete(Map.all())
        db.delete(Game.all())

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
