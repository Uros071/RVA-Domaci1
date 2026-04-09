package qod_server.app;

import com.google.gson.Gson;
import qod_server.Quote;
import qod_server.http.Request;
import qod_server.http.response.HtmlResponse;
import qod_server.http.response.RedirectResponse;
import qod_server.http.response.Response;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuoteController extends Controller{

    public QuoteController(Request request) {
        super(request);
    }

    @Override
    public Response doGet() {
        String qodJson = getQOD();
        String autor = "";
        String citat = "";

        try{
            Gson gson = new Gson();
            Quote quote = gson.fromJson(qodJson, Quote.class);
            autor = quote.getAuthor();
            citat = quote.getText();
        }catch (Exception e){
            citat = "Greska pri parsiranju citata: " + qodJson;
        }

        StringBuilder savedQuotesHtml = new StringBuilder();
        try {
            File file = new File("quotes.json");
            if(file.exists()){
                BufferedReader br = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();
                Quote[] savedQuotes = gson.fromJson(br, Quote[].class);

                if(savedQuotes != null && savedQuotes.length > 0){
                    for(Quote quote : savedQuotes){
                        savedQuotesHtml.append("<h2>" + quote.getAuthor() + "</h2>");
                        savedQuotesHtml.append("<p>" + quote.getText() + "</p>");
                        savedQuotesHtml.append("<hr>");
                    }
                } else {
                    savedQuotesHtml.append("<p>Nema sačuvanih citata.</p>");
                }
                br.close();
            }else {
                savedQuotesHtml.append("<p>Nema sačuvanih citata.</p>");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        String htmlBody = "<h1>Quote of the day:</h1>" +
                "<h2>" + autor + "</h2>" +
                "<p>" + citat + "</p>" +
                "<hr>" +
                "<h2>Save a new quote</h2>" +
                "<form method=\"POST\" action=\"/save-quote\">" +
                "<label>Author: </label><input name=\"author\" type=\"text\"><br><br>" +
                "<label>Quote: </label><input name=\"quote\" type=\"text\"><br><br>" +
                "<button>Save Quote</button>" +
                "</form>" +
                "<hr>" +
                "<h2>Saved Quotes</h2>" + savedQuotesHtml;

        String content = "<html><head><meta charset=\"UTF-8\"><title>Quotes</title></head>\n";
        content += "<body>" + htmlBody + "</body></html>";

        return new HtmlResponse(content);
    }
                                              
    @Override                                 
    public Response doPost() {
        try{
            String body = request.getBody();

            String[] podaci = body.split("&");
            String autor = "";
            String citat = "";

            for(String podatak : podaci){
                String[] keyValue = podatak.split("=");
                if (keyValue.length > 1) {
                    String key = keyValue[0];
                    // URLDecoder pretvara '+' nazad u razmak i sređuje naša slova!
                    String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");

                    if (key.equals("author"))
                        autor = value;

                    if (key.equals("quote"))
                        citat = value;
                }

                Gson gson = new Gson();
                List<Quote> quotes = new ArrayList<>();
                File file = new File("quotes.json");

                if(file.exists()){
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    Quote[] existingQuotes = gson.fromJson(br, Quote[].class);
                    if(existingQuotes != null){
                        quotes = new ArrayList<>(Arrays.asList(existingQuotes));
                    }
                    br.close();
                }

                if(!(autor.trim().isEmpty()) && !(citat.trim().isEmpty()))
                    quotes.add(new Quote(autor, citat));

                PrintWriter pw = new PrintWriter(new FileWriter("quotes.json"));
                pw.print(gson.toJson(quotes));
                pw.close();

                System.out.println("Citat uspešno sačuvan kao JSON");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return new RedirectResponse("/quotes");
    }

    private String getQOD() {
        try{
            Socket socket = new Socket("localhost", 8081);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.print("GET / HTTP/1.1\r\nHost: localhost:8081\r\n\r\n");
            out.flush();

            String line;
            boolean isBody = false;
            StringBuilder sb = new StringBuilder();

            while((line = in.readLine()) != null){
                if(isBody)
                    sb.append(line);

                if(line.isEmpty())
                    isBody = true;
            }
            socket.close();
            return sb.toString();

        } catch (Exception e){
            e.printStackTrace();
            return "Greska pri uzimanju QOD-a";
        }
    }
}
