#!/usr/bin/env python
import sys
import os
import string
import json
import re

def avg(l):
  return sum(l) / float(len(l))


def parseline(line):
  sections = line.split("&")
  time = string.strip(sections[1]).split(" ")[1]
  pid = string.strip(sections[2]).split(" ")[1]
  point = string.strip(sections[3]).split(" ")[1]
  others = string.strip(sections[4][8:])
  return {
      'time':long(time),
      'pid':int(pid),
      'location':point,
      'others': eval(others)
      }



def munge(filename, num_phones):
  db = {}
  slowlist = []
  fastlist = []
  for line in open(filename, 'r'):
    if "LatencyStat" not in line:
      continue
    try:
      result = parseline(line)
    except:
      continue
    now = result['time']
    latencies = [now - o[0] for o in result['others']]
    print num_phones, now, result['pid'], \
        min(latencies), avg(latencies), max(latencies)
    #print "%d;%d;%d;$f;%f;%f;%s" % (num_phones, now, result['pid'],
    #    min(latencies), avg(latencies), max(latencies), json.dumps(latencies))


def main():
  for name in sys.argv[1:]:
    num_phones = int(re.findall('\d+', name)[-1])
    munge(name, num_phones)

main()

