#!/bin/sh


LOGNAME="$1"

grep "LatencyStats" $LOGNAME |
awk 'BEGIN { sum=0; total=0}
           { print "Sent:",$3,"Returned:",$NF,"Latency:",$NF-$3;
             sum += $NF - $3; total++
           }
     END   { print "  Received:",total,
                  "\n  Average latency:",sum/total,"ms"}';

