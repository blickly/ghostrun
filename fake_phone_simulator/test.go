package main

import (
  "flag"
  "fmt"
  "http"
  "json"
  "log"
  "os"
  "rand"
  "strconv"
  "time"
)

//const SERVER = "localhost:8080";
const SERVER = "pacmanplusplus.appspot.com:80";
const LOGINFO = true;

func main() {
  run_time := 30*int64(1000000000)     // in ns
  update_rate := int64(1000000000)/2    // in ns between requests
  flag.Parse()
  num_phones := 4
  if flag.NArg() > 0 {
    var err os.Error
    num_phones, err = strconv.Atoi(flag.Arg(0))
    if err != nil {
      log.Println("[ERR] Cannot parse number of phones:", flag.Arg(0))
      return
    }
  }

  pid_offset := 100000
  for i := pid_offset; i < pid_offset+num_phones; i++ {
    go run_phone(i, update_rate)
  }
  time.Sleep(run_time)
  log.Println("Quitting...")
}

func run_phone(pid int, update_rate int64) {
  gid := 1
  time.Sleep(rand.Int63n(update_rate))
  for ;; {
    lat := rand.Float32() * 90
    lng := rand.Float32() * 90
    post_position(gid, pid, lat, lng)
    time.Sleep(update_rate)
  }
}

func post_position(gid int, pid int, lat float32, lng float32) {
  url := fmt.Sprintf("/post_position?gid=%d&pid=%d&lat=%f&lng=%f",
                     gid, pid, lat, lng)
  requestTime := time.Nanoseconds()
  resp, _, err := http.Get("http://" + SERVER + url)
  responseTime := time.Nanoseconds()
  if err != nil {
    log.Printf("[ERR] from phone %d: %s", pid, err)
    return
  }
  v := make(map[string]interface{},9)
  json.NewDecoder(resp.Body).Decode(&v)
  totalTime := responseTime - requestTime
  log.Printf("[INFO] Request '%s' at time %v, Response %v at time %v, " +
             "total time: %d",
             url, requestTime, v["locations"], responseTime,
             totalTime)
}
