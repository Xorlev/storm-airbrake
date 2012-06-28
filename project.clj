(defproject com.fullcontact/storm-airbrake "0.1.3"
  :description "Bolt wrapper to report exceptions to airbreak"
  :license {:name "The Apache Software License, Version 2.0"
            :url "http://www.eclipse.org/legal/epl-v10.htm"}

  :min-lein-version "2.0"
  :dependencies [[org.codehaus.groovy/groovy-all "1.8.6"]
                 [org.apache.httpcomponents/httpclient "4.2-alpha1"]
                 ]
  :dev-dependencies [[storm "0.7.2"]]
;; deployment password should be specified in LIEN_PASSWORD
  :deploy-repositories  {"snapshots" {:url "https://fullcontact.artifactoryonline.com/fullcontact/libs-snapshots-local"}
                         "releases" {:url "https://fullcontact.artifactoryonline.com/fullcontact/libs-releases-local"}}

  :groovy-source-paths [ "src/groovy"]
  :plugins [[lein-groovyc "0.2.1"]]
  :hooks [leiningen.hooks.groovyc]
  )
