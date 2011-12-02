package main

import (
  "fmt"
  "net"
  "http"
  "time"
  "rand"
)

func main() {
  num_phones := 5
  for i := -num_phones; i < 0; i++ {
    go run_phone(i, 2)
  }
  time.Sleep(60*1000000000)
  fmt.Println("Quitting...")
}

func run_phone(pid int, iterations int) {
  gid := 1
  for i := 0; i < iterations; i++ {
    lat := rand.Float32() * 90
    lng := rand.Float32() * 90
    post_position(gid, pid, lat, lng)
    time.Sleep(5000000000) // in ns
  }
  fmt.Printf("Done with phone %d\n", pid)
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
  fmt.Println("Will post to ", url)
  req, err := http.NewRequest("GET", url, nil)
  if err != nil {
    fmt.Println(err)
  }
  err = ccon.Write(req)
  if err != nil {
    fmt.Println(err)
  }
}
