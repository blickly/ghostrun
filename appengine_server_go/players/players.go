package players

import (
    "appengine"
    "appengine/datastore"
    "fmt"
    "http"
    "io"
    "json"
    "os"
    "time"
    "strconv"
)

type Player struct {
    PlayerId int
    GameId   int
    Lat      float32
    Lng      float32
    LastSeen datastore.Time
}

func init() {
    http.HandleFunc("/", helloWorld)
    http.HandleFunc("/post_position", postPosition)
}

func serveError(c appengine.Context, w http.ResponseWriter, err os.Error) {
        w.WriteHeader(http.StatusInternalServerError)
        w.Header().Set("Content-Type", "text/plain")
        io.WriteString(w, "Internal Server Error")
        c.Errorf("%v", err)
}

func helloWorld(w http.ResponseWriter, r *http.Request) {
    fmt.Fprint(w, "Hello, world!")
}

func postPosition(w http.ResponseWriter, r *http.Request) {
    c := appengine.NewContext(r)
    gid, _ := strconv.Atoi(r.FormValue("gid"))
    pid, _ := strconv.Atoi(r.FormValue("pid"))
    lat, _ := strconv.Atof32(r.FormValue("lat"))
    lng, _ := strconv.Atof32(r.FormValue("lng"))
    now := datastore.SecondsToTime(time.Seconds())

    k := datastore.NewKey(c, "Player", "", int64(pid), nil)
    e := new(Player)
    datastore.Get(c, k, e)
    e = &Player{pid, gid, lat, lng, now}
    if _, err := datastore.Put(c, k, e); err != nil {
        serveError(c, w, err)
        return
    }

    q := datastore.NewQuery("Player").
            Filter("GameId =", gid)
    enc := json.NewEncoder(w)
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
        if p.PlayerId == pid {
                continue
        }
        enc.Encode(p)
    }
}
