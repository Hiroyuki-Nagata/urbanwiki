# wiki

A play-ground in order to create fswiki clone

## Usage

* Build & Package with Leiningen

```
$ lein check                  # Compile clojure sources
$ lein uberjar                # Create uberjar
```

* Build & Package with Maven

```
$ lein pom                    # Generate pom.xml
$ mvn clojure:compile         # Compile clojure sources
$ mvn clojure:test            # Testing clojure sources
$ mvn clojure:compile package # Create uberjar with Maven
```

* Heroku

```
$ heroku login
$ heroku create your-name-todo-clj
$ heroku git:remote -a your-name-todo-clj
$ git push heroku master
```

## License

Copyright © 2017 Hiroyuki Nagata

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
