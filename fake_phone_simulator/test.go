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
const SERVER = "ghostrungo.appspot.com:80";
const LOGINFO = true;

func main() {
  run_time := 30*int64(1000000000)     // in ns
  update_rate := int64(1000000000)    // in ns between requests
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

func run_phone(simid int, update_rate int64) {
  gid := rand.Intn(65536)
  pid := simid //get_pid(gid, simid)
  time.Sleep(rand.Int63n(update_rate))
  for ;; {
    lat := rand.Intn(4000) +  37875505 - 2000
    lng := rand.Intn(4000) - 122257438 - 2000
    post_position(gid, pid, lat, lng)
    time.Sleep(update_rate)
  }
}

func get_pid(gid, simid int) int {
  url := fmt.Sprintf("/join_game?gid=%d&lat=%d&lng=%d&pacman=true",
    gid,rand.Intn(90000000),rand.Intn(90000000))
  fullUrl := "http://" + SERVER + url
  resp, _, err := http.Get(fullUrl)
  if err != nil {
    log.Println("[ERR] On join game:", err)
  }
  //log.Printf("[INFO] Request '%s', Response %v", fullUrl, resp)
  body := resp.Body
  defer body.Close()
  log.Printf("[INFO] Request '%s', Response %v", fullUrl, body)
  /*
  v := make(map[string]int,9)
  json.NewDecoder(body).Decode(&v)
  body.Close()
  pid := v["pid"]
  return pid + simid
  // */
  return simid
}

func post_position(gid int, pid int, lat int, lng int) {
  url := fmt.Sprintf("/post_position?gid=%d&pid=%d&lat=%d&lng=%d",
                     gid, pid, lat, lng)
  fullUrl := "http://" + SERVER + url
  requestTime := time.Nanoseconds()
  resp, _, err := http.Get(fullUrl)
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
             fullUrl, requestTime, v, responseTime, totalTime)
}
