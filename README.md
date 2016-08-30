## REST Booking Service
Service to provide business logic and data persistence via REST interfaces.

## Needs
Mongo DB 2.6.4 - can't gaurante later versions will work, see below todo's for why


## TODO
* Currently includes services for talking directly to mongo, should be re-factored into separate some project at some point.
* Using old version of Mongo-java drivers and mongo jack, need to work my way up the versions to point where mongo 
jack still works with appropriate version of mongo as some methods on the java driver have been deprecated them removed
and jack still needs them...investigate!
* Security, decide which methods need security, implement something like oauth to give service some security, let the client
applications work on there bit.


sudo apt-get install -y mongodb-org=2.6.9 mon	godb-org-server=2.6.9 mongodb-org-shell=2.6.9 mongodb-org-mongos=2.6.9 mongodb-org-tools=2.6.9
https://docs.mongodb.com/v2.6/tutorial/install-mongodb-on-ubuntu/#install-mongodb

