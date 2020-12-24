# Sample non-blocking echo server
## About
This is a sample server intended to help me understand the different features of the java.nio package.

## Usage
Run the server as follows:
```
java -jar target/echo-server-1.0.jar
```

Once, it is running, you can easily connect to the server with the `telnet` command as follows: 
```
telnet <ip_address> <port_number>
```
The strings you sent to the server using this client will be echoed.

## Notes
The server uses the java.nio subpackages.

### Channels
The sample server communicates using the java.nio.channels subpackage. It uses the `ServerSocketChannel` class. Three
concept to understand the underlying mechanism...

- A channel represents an open connection to an entity such as a hardware device, a file, a network socket, or a program 
component that is capable of performing one or more distinct I/O operations, for example reading or writing [1].
- A selectable channel is type of channel that can be multiplexed via a Selector [2].
- A server socket channel is a selectable channel for stream-oriented listening sockets [3].

### Binding
A newly-created server-socket channel is open but not yet bound. An attempt to invoke the accept method of an unbound
server-socket channel will cause a NotYetBoundException to be thrown. A server-socket channel can be bound by invoking
one of the bind methods defined by this class [3].

Note: it seems like before Java 1.7 release, the bind method didn't exist. Binding was done accessing the socket getter
and calling its bind method.

### `SO_REUSEADDR` socket option
This option can be enabled and disabled using the [ServerSocket.setReuseAddress](https://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html#setReuseAddress(boolean))
method.

When a TCP connection is closed the connection may remain in a timeout state for a period of time after the connection 
is closed (typically known as the TIME_WAIT state or 2MSL wait state). For applications using a well known socket 
address or port it may not be possible to bind a socket to the required SocketAddress if there is a connection in the 
timeout state involving the socket address or port.

Enabling `SO_REUSEADDR` prior to binding the socket using bind(SocketAddress) allows the socket to be bound even though 
a previous connection is in a timeout state.

[What are `CLOSE_WAIT` and `TIME_WAIT` states?](https://superuser.com/questions/173535/what-are-close-wait-and-time-wait-states)

### Blocking mode
A selectable channel is either in blocking mode or in non-blocking mode. In blocking mode, every I/O operation invoked 
upon the channel will block until it completes. In non-blocking mode an I/O operation will never block and may transfer 
fewer bytes than were requested or possibly no bytes at all.

Non-blocking mode is most useful in conjunction with selector-based multiplexing. **A channel must be placed into 
non-blocking mode before being registered with a selector**, and may not be returned to blocking mode until it has been 
deregistered [2].

### Selection
Check Class Selector documentation [5].

## Resources

1. [Interface Channel](https://docs.oracle.com/javase/7/docs/api/java/nio/channels/Channel.html)
2. [Class SelectableChannel](https://docs.oracle.com/javase/7/docs/api/java/nio/channels/SelectableChannel.html)
3. [Class ServerSocketChannel](https://docs.oracle.com/javase/7/docs/api/java/nio/channels/ServerSocketChannel.html)
4. [Class ServerSocket](https://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html)
5. [Class Selector](https://docs.oracle.com/javase/7/docs/api/java/nio/channels/Selector.html)
6. [Class SelectionKey](https://docs.oracle.com/javase/7/docs/api/java/nio/channels/SelectionKey.html)