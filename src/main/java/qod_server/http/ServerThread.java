package qod_server.http;

import qod_server.app.RequestHandler;
import qod_server.http.response.Response;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerThread implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket sock) {
        this.client = sock;

        try {
            //inicijalizacija ulaznog toka
            in = new BufferedReader(
                    new InputStreamReader(
                            client.getInputStream()));

            //inicijalizacija izlaznog sistema
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    client.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // uzimamo samo prvu liniju zahteva, iz koje dobijamo HTTP method i putanju
            String requestLine = in.readLine();

            StringTokenizer stringTokenizer = new StringTokenizer(requestLine);
            String method = stringTokenizer.nextToken();
            String path = stringTokenizer.nextToken();

            int contentLength = 0;

            System.out.println("\nHTTP ZAHTEV KLIJENTA:\n");
            do {
                System.out.println(requestLine);

                if (requestLine.startsWith("Content-Length:"))
                    contentLength = Integer.parseInt(requestLine.split(":")[1].trim());

                requestLine = in.readLine();
            } while (!requestLine.trim().equals(""));

            Request request = new Request(HttpMethod.valueOf(method), path);

            if (method.equals(HttpMethod.POST.toString())) {
//                 TODO: Ako je request method POST, procitaj telo zahteva (parametre)
                char[] buffer = new char[contentLength];
                in.read(buffer);
                String body  = new String(buffer);
                request.setBody(body);
                System.out.println(body);
            }

            RequestHandler requestHandler = new RequestHandler();
            Response response = requestHandler.handle(request);

            System.out.println("\nHTTP odgovor:\n");
            System.out.println(response.getResponseString());

            out.println(response.getResponseString());

            in.close();
            out.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
