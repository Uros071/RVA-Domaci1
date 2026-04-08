package qod_server;

import qod_server.http.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class QuoteServer {
    public static final int TCP_PORT = 8081;

    public static void main(String[] args) {

        try {
            ServerSocket ss = new ServerSocket(TCP_PORT);
            System.out.println("QOD Server radi na portu: " + TCP_PORT);
            while (true) {
                Socket sock = ss.accept();
                new Thread(new QuoteServerThread(sock)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
