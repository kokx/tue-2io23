tue-2io23
=========

TU/e 2IO23 Project

Server API
----------

When we create a game:

```java
// starts a game
InitServer server = new InitServer();

// get the list of clients
List<PeerInfo> clients = server.getClients();

// start the game, and hand control to the token ring
server.start();
```

Client API
----------

When we join a game:

```java
Client client = new Client();

// find servers (BLOCKS! MAYBE FOR SEVERAL SECONDS!)
List<PeerInfo> servers = client.findServers();

// set the TokenChangeListener, this should receive the token, change it, and return the changed token
client.setTokenChangeListener(listener);

// connect to a server, and start the token ring
client.connect(PeerInfo server);
```
