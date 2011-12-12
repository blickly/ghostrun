package players

import (
    "appengine"
    "appengine/datastore"
    "fmt"
    "http"
    "json"
    "log"
    "os"
    "time"
    "strconv"
)

type Player struct {
    PlayerId, GameId  int
    Lat, Long         int
    LastSeen          datastore.Time
}

func init() {
    http.HandleFunc("/", helloWorld)
    http.HandleFunc("/post_position", postPosition)
}

func helloWorld(w http.ResponseWriter, r *http.Request) {
    fmt.Fprint(w, "Hello, world!")
}

func serveError(c appengine.Context, w http.ResponseWriter, err os.Error) {
        w.WriteHeader(http.StatusInternalServerError)
        w.Header().Set("Content-Type", "text/plain")
        fmt.Fprintln(w, "Error:", err)
        c.Errorf("%v", err)
}

func savePlayer(c appengine.Context, p *Player) {
    k := datastore.NewKey(c, "Player", "", int64(p.PlayerId), nil)
    if _, err := datastore.Put(c, k, p); err != nil {
        log.Println("Error:", err)
        return
    }
}

func postPosition(w http.ResponseWriter, r *http.Request) {
    c := appengine.NewContext(r)
    gid, _ := strconv.Atoi(r.FormValue("gid"))
    pid, _ := strconv.Atoi(r.FormValue("pid"))
    lat, _ := strconv.Atoi(r.FormValue("lat"))
    lng, _ := strconv.Atoi(r.FormValue("lng"))
    now := datastore.SecondsToTime(time.Seconds())

    go savePlayer(c, &Player{pid, gid, lat, lng, now})

    q := datastore.NewQuery("Player").
            Filter("GameId =", gid)
    players := make([]Player, 0, 10)
    for t := q.Run(c); ; {
        var p Player
        _, err := t.Next(&p)
        if err == datastore.Done {
                break
        }
        if err != nil {
                serveError(c, w, err)
                return
        }
        if p.PlayerId != pid {
                players = append(players, p)
        }
    }
    m := map[string]interface{}{"players":players, "dead":false}
    enc := json.NewEncoder(w)
    enc.Encode(m)
}
