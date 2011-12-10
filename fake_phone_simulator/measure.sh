#!/bin/sh
for i in {256,128,64,32,16,8,4,2,1}; do
  echo "Simulating round $i";
  LOGNAME="logs/out$i.log";
  ./6.out $i &> $LOGNAME;
  sleep 60;
done

for i in {1,2,4,8,16,32,64,128,256}; do
  echo "Round $i";
  LOGNAME="logs/out$i.log";
  awk 'BEGIN {sum=0;total=0}{ sum += $NF; total++ } END{ print "  Received:",total,"\n  Average latency:",sum/total,"ns"}' $LOGNAME;
  echo "  Errors:`grep '\[ERR\] from phone' $LOGNAME | wc -l`";
done

