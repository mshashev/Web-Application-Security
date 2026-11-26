# Java Client-Server Chat

## Description
This project is a simple chat application developed in Java using sockets. The server accepts multiple clients and forwards messages between them. Clients register a nickname using the `NICK` command and send messages using the `MSG` command.

## How to Run

1. Open the project in IntelliJ IDEA.
2. Start `Server.java` first. The server listens on port `8080`.
3. Run `Client.java` in a separate console.
4. Enter a nickname when prompted.
5. Run `Client.java` again in another console and enter a different nickname.
6. Type messages in either client to exchange messages.


## Commands
- `NICK name` — registers a client's nickname.
- `MSG message` — sends a chat message.
- `/quit` — closes the client.

## Testing
The application was tested with one server and two clients. Both clients connected successfully and exchanged messages.

## Questions
The client starts the communication by connecting to the server and sending a request, such as registering a nickname. The server must be started first so that it can listen for incoming client connections. The server needs to handle many clients at once because several users may connect and send messages simultaneously. Using separate threads allows the server to handle each client independently and forward messages without blocking communication with other clients.