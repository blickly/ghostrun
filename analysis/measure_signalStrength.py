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


def munge(filename):
  lastT4 = None
  for line in open(filename, 'r'):
    if "post_position" not in line:
      continue
    result = parseline(line)
    if lastT4 == None:
      lastT4 = result['t4']
      continue

    print result['numPlayer'], result['dbm'], result['ecio'], result['t2']-result['t1'], result['t3']-result['t2'], result['t4']-result['t3'], result['t1']-lastT4, result['t4']-lastT4

    lastT4 = result['t4']

def main():
  for name in sys.argv[1:]:
    munge(name)

main()

