package main

import (
  "fmt"
  "http"
  "json"
  "log"
  //"net"
  "rand"
  "time"
)

//const SERVER = "localhost:8080";
const SERVER = "pacmanplusplus.appspot.com:80";
const LOGINFO = true;

func main() {
  num_phones := 3
  run_time := 20 * int64(1000000000)   // in ns
  update_rate := 10*int64(1000000000)/2   // in ns between requests

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
  log.Printf("Request '%s' at local time %v, Response %v at time %v",
              url, requestTime, v["locations"], responseTime)
}
