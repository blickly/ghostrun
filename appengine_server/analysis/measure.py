#!/usr/bin/env python
import sys

def avg(l):
  return sum(l) / float(len(l))

def main():
  db = {}
  slowlist = []
  fastlist = []
  for line in sys.stdin:
    _, _, start, lat, lng, end = line.split(" ")
    row = db.get((int(start),lat,lng), [])
    row.append(int(end))
    db[(int(start),lat,lng)] = row
  for (k,endtimes) in db.iteritems():
    start = k[0]
    stalest = max(endtimes) - start
    freshest = min(endtimes) - start
    slowlist.append(stalest)
    fastlist.append(freshest)
    print "Stalest:",stalest,"Freshest:",freshest

  print "Overall stalest avg:",avg(slowlist)
  print "Overall freshest avg:",avg(fastlist)

main()
