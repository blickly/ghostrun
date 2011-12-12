#!/bin/sh


PREFIX="$1"

run_simulation() {
  make 6.out || exit
  mkdir logs/$PREFIX
  for i in {1,2,4,8,16,32,64,128}; do
    echo "Simulating round $i";
    LOGNAME="logs/$PREFIX/out$i.log";
    ./6.out $i &> $LOGNAME;
    sleep 30
  done
}

print_results() {
  for i in {1,2,4,8,16,32,64,128,256}; do
    echo "Round $i";
    LOGNAME="logs/$PREFIX/out$i.log";
    grep "total time:" $LOGNAME |
    awk 'BEGIN { sum=0; total=0}
               { sum += $NF; total++ }
         END   { print "  Received:",total,
                     "\n  Average latency:",sum/total/1e9,"s"}';
    echo "  Errors:`grep '\[ERR\] from phone' $LOGNAME | wc -l`";
  done
}

if [ "$2" == "-s" ]; then
  run_simulation
fi
print_results
