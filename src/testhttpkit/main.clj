(ns testhttpkit.main
  (:require [org.httpkit.server :refer :all]
            [clojure.tools.namespace.repl]))


(defn async-handler [ring-request]
  ;; unified API for WebSocket and HTTP long polling/streaming
  (with-channel ring-request channel    ; get the channel
    (if (websocket? channel)            ; if you want to distinguish them
      (on-receive channel (fn [data]     ; two way communication
                            (send! channel "Pong")))
      (send! channel {:status 200
                      :body (slurp "resources/public/index.html")}))))

(defonce server (atom nil))

(defn stop-server []
  (when @server
    (println "Stopping server")
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (stop-server)
  (println "Starting Server")
  (reset! server (run-server #'async-handler {:port 8080})))

(defn reset
  []
  (stop-server)
  (clojure.tools.namespace.repl/refresh :after 'testhttpkit.main/start-server))

(defn -main
  [& args]
  (start-server))

;(start-server)
;(reset)
;(stop-server)