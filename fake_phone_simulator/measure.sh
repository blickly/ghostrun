#!/bin/sh


run_simulation() {
  make 6.out
  for i in {256,128,64,32,16,8,4,2,1}; do
    echo "Simulating round $i";
    LOGNAME="logs/out$i.log";
    ./6.out $i &> $LOGNAME;
    sleep 600
  done
}

print_results() {
  for i in {1,2,4,8,16,32,64,128,256}; do
    echo "Round $i";
    LOGNAME="logs/out$i.log";
    awk 'BEGIN { sum=0; total=0}
               { sum += $NF; total++ }
         END   { print "  Received:",total,
                     "\n  Average latency:",sum/total/1e9,"s"}' $LOGNAME;
    echo "  Errors:`grep '\[ERR\] from phone' $LOGNAME | wc -l`";
  done
}

if [ "$1" == "-s" ]; then
  run_simulation
fi
print_results
