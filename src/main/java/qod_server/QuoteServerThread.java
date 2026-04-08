package qod_server;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class QuoteServerThread implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public QuoteServerThread(Socket socket){
        this.client = socket;
        try {

            in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8")), true);

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try{
            String reqLine = in.readLine();
            System.out.println("QOD dobio zahtev: " + reqLine);

            List<Quote> quotes = new ArrayList<>();
            quotes.add(new Quote("Nebojša Glogovac", "Sine, znaš kako se postaje šampion? Tako što izađeš na teren kad je najteže – i pobediš."));
            quotes.add(new Quote("Wayne Gretzky", "Promašićeš 100 posto šuteva koje uopšte ne šutneš."));
            quotes.add(new Quote("Duško Vujošević", "Džaba vam novci moji sinovci."));
            quotes.add(new Quote("Michael Jordan", "Promašio sam preko 9000 šuteva u svojoj karijeri. Izgubio sam skoro 300 utakmica. 26 puta su mi verovali da šutnem za pobedu i ja sam promašio. Nisam uspeo iznova i iznova u životu, to je razlog zašto sam uspeo."));

            int quoteIndex = (int)Math.random() * quotes.size();
            Quote qod = quotes.get(quoteIndex);

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(qod);
            out.print("HTTP/1.1 200 OK\r\nContent-Type: application/json; charset=utf-8\r\n\r\n");
            out.print(jsonResponse);
            out.flush();

            in.close();
            out.close();
            client.close();


        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
