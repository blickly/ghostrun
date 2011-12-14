import json

from google.appengine.ext import webapp, db

DOT_SPACE = 300
DOT_POINTS = 10

###############################################################################
#####                             Models
###############################################################################

class Map(db.Model):
    content = db.TextProperty()

    def populate(self, gid):
      dot_id = 0
      json_m = json.loads(self.content)
      mapping = dict([(pt['id'], pt) for pt in json_m])
      done = set()
      dots = []

      for pt in json.loads(self.content):
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

class Dot(db.Model):
  gid = db.IntegerProperty()
  lat = db.IntegerProperty()
  lng = db.IntegerProperty()

  @classmethod
  def dotsAsDict(cls, gid):
    result = {}
    for d in cls.gql("WHERE gid = :1", gid):
      result[d.key().id()] = (d.lat, d.lng)

    return result


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

def distance(pt1, pt2):
  return (float(pt1[0] - pt2[0]) ** 2.0 + float(pt1[1] - pt2[1]) ** 2.0) ** 0.5



###############################################################################
#####                             Handlers
###############################################################################

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
