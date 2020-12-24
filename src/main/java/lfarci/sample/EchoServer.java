package lfarci.sample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {

    public static final String DEFAULT_HOST_NAME = "localhost";
    public static final int DEFAULT_PORT_NUMBER = 8080;
    public static final int BUFFER_SIZE = 256;

    private Boolean running;
    private final Selector selector;
    private final ServerSocketChannel channel;
    private final ByteBuffer buffer;

    public EchoServer(Integer port) throws IOException {
        SocketAddress address = new InetSocketAddress(DEFAULT_HOST_NAME, port);
        running = true;
        selector = Selector.open();
        channel = openServerSocketChannel(address);
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public EchoServer() throws IOException {
        this(DEFAULT_PORT_NUMBER);
    }

    private String getBufferAsASCIIString() throws IOException {
        return new String(buffer.array(), "ASCII").trim();
    }

    private ServerSocketChannel openServerSocketChannel(SocketAddress address) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().setReuseAddress(true);
        channel.configureBlocking(false);
        channel.bind(address);
        return channel;
    }

    private void accept() throws IOException {
        try {
            SocketChannel client = channel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            System.out.printf("Connection from %s accepted.\n", client.getRemoteAddress().toString());
        } catch (IOException e) {
            throw new IOException("Could not accept a connection.");
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        if (client.read(buffer) > 0) {
            key.interestOps(SelectionKey.OP_WRITE);
            System.out.printf("Reading \"%s\" from %s.\n", getBufferAsASCIIString(), client.getRemoteAddress().toString());
        } else {
            close(key);
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        buffer.flip();
        System.out.printf("Writing \"%s\" to %s.\n", getBufferAsASCIIString(), client.getRemoteAddress().toString());
        client.write(buffer);
        key.interestOps(SelectionKey.OP_READ);
        buffer.clear();
    }

    private void close(SelectionKey key) throws IOException {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            System.out.printf("Connection to %s closed.\n", client.getRemoteAddress().toString());
            key.cancel();
            client.close();
        } catch (IOException e) {
            throw new IOException("could not close a channel.");
        }
    }

    private void handleChannelReadyForIOOperation(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            accept();
        } else if (key.isReadable()) {
            read(key);
        } else if (key.isWritable()) {
            write(key);
        } else {
            System.out.println("IO Operation isn't currently handled");
        }
    }

    private void handleChannelsReadyForIOOperation() throws IOException {
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = keys.iterator();
        while(iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            handleChannelReadyForIOOperation(key);
        }
    }

    public void start() throws IOException {
        System.out.printf("The server is accepting connections at %s\n", channel.getLocalAddress());
        while (running) {
            if (selector.selectNow() > 0) {
                handleChannelsReadyForIOOperation();
            }
        }
    }

}
