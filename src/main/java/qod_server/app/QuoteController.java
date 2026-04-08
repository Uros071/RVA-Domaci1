package qod_server.app;

import com.google.gson.Gson;
import qod_server.Quote;
import qod_server.http.Request;
import qod_server.http.response.HtmlResponse;
import qod_server.http.response.RedirectResponse;
import qod_server.http.response.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

        String htmlBody = "<h1>Quote of the day:</h1>" +
                "<h2>" + autor + "</h2>" +
                "<p>" + citat + "</p>" +
                "<hr>" +
                "<h2>Save a new quote</h2>" +
                "<form method=\"POST\" action=\"/save-quote\">" +
                "<label>Author: </label><input name=\"author\" type=\"text\"><br><br>" +
                "<label>Quote: </label><input name=\"quote\" type=\"text\"><br><br>" +
                "<button>Save Quote</button>" +
                "</form>";

        String content = "<html><head><meta charset=\"UTF-8\"><title>Quotes</title></head>\n";
        content += "<body>" + htmlBody + "</body></html>";

        return new HtmlResponse(content);
    }
                                              
    @Override                                 
    public Response doPost() {
        System.out.println("Stigao je POST zahtev za čuvanje citata!");
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
