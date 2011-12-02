package main

import (
  "fmt"
  "net"
  "http"
  "time"
  "rand"
)

const DEBUG = true;

func main() {
  num_phones := 5
  run_time := 60 * int64(1000000000)   // in ns
  update_rate := 5 * int64(1000000000) // in ns between requests

  simulator_offset := 100000
  for i := simulator_offset; i < simulator_offset+num_phones; i++ {
    go run_phone(i, update_rate)
  }
  time.Sleep(run_time)
  fmt.Println("Quitting...")
}

func run_phone(pid int, update_rate int64) {
  gid := 1
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
  con, err := net.Dial("tcp", "localhost:8080")
  if err != nil {
    fmt.Println(err)
  }
  ccon := http.NewClientConn(con, nil)
  defer ccon.Close()
  if DEBUG {
    fmt.Println("Will post to ", url)
  }
  req, err := http.NewRequest("GET", url, nil)
  if err != nil {
    fmt.Println(err)
  }
  err = ccon.Write(req)
  if err != nil {
    fmt.Println(err)
  }
}
