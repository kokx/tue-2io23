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
List<ClientInfo> clients = server.getClients();

// start the game, and hand control to the token ring
server.start();
```

Client API
----------

When we join a game:

```java
Client client = new Client();

// find servers (BLOCKS! MAYBE FOR SEVERAL SECONDS!)
List<ServerInfo> servers = client.getServers();

// connect to a server, and start the token ring
client.connect(Server server);
```
