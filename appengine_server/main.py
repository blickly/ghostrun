#!/usr/bin/env python

from google.appengine.ext import webapp, db
from google.appengine.ext.webapp import util

import games
import maps
import players

###############################################################################
#####                             Handlers
###############################################################################

class MainHandler(webapp.RequestHandler):
    def get(self):
        self.response.out.write('hi...')

class DeleteAllHandler(webapp.RequestHandler):
    def get(self):
      db.delete(maps.Dot.all())
      db.delete(maps.Map.all())
      db.delete(games.Game.all())
      db.delete(players.Player.all())
      db.delete(players.DotAte.all())

###############################################################################
#####                             Mappings
###############################################################################
def main():
    application = webapp.WSGIApplication(
        [('/', MainHandler),
         ('/save_map', maps.SaveMapHandler),
         ('/get_map', maps.GetMapHandler),
         ('/get_maps', maps.GetMapsHandler),
         ('/new_game', games.NewGameHandler),
         ('/get_games', games.GetGamesHandler),
         ('/join_game', games.JoinGameHandler),
         ('/post_position', players.GameMoveHandler),
         ('/eat_dot', players.EatDotHandler),
         ('/delete_all', DeleteAllHandler)],
        debug = True)
    util.run_wsgi_app(application)

if __name__ == '__main__':
    main()
