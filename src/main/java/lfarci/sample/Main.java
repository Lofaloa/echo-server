package lfarci.sample;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Integer port = EchoServer.DEFAULT_PORT_NUMBER;
        try {
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            }
            EchoServer server = new EchoServer(port);
            server.start();
        } catch (IOException e) {
            System.err.printf("Error: %s", e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error: invalid port number.");
        }
    }

}
