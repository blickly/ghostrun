#!/usr/bin/env python
import sys
import os
import string
import json
import re

def avg(l):
  return sum(l) / float(len(l))


def parseline(line):
  sections = line.split(" ")[-1].split("&")
  result = {}
  for a in sections:
    k,v = a.split("=")
    result[k] = int(v)
  return result



def munge(filename, num_phones):
  lastT2 = None
  lastT4 = None
  for line in open(filename, 'r'):
    if "post_position" not in line:
      continue
    result = parseline(line)
    if lastT2 == None:
      lastT2 = result['t2']
      lastT4 = result['t4']
      continue

    print num_phones, result['numPlayer'], result['t2']-lastT2, result['dbm'], result['ecio'], result['t2']-result['t1'], result['t3']-result['t2'], result['t4']-result['t3'], result['t1']-lastT4

    lastT2 = result['t2']
    lastT4 = result['t4']


def main():
  for name in sys.argv[1:]:
    num_phones = int(re.findall('\d+', name)[-1])
    munge(name, num_phones)

main()

