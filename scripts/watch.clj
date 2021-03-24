(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'megacorp1eu.core
   :output-to "out/megacorp1eu.js"
   :output-dir "out"})
