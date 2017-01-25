# wiki

A play-ground in order to test Maven federation

## Usage

* Build

```
$ lein pom
$ mvn clojure:compile clojure:run
```

* Package

```
$ lein uberjar
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
