package games

import (
  "http"
  "json"
  "rand"
)

func init() {
  http.HandleFunc("/join_game", joinGame)
}

func joinGame(w http.ResponseWriter, r *http.Request) {
  v := map[string]int{"pid" : rand.Int()}
  json.NewEncoder(w).Encode(v)
}
