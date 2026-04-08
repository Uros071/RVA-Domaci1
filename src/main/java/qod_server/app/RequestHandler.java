package qod_server.app;

import qod_server.http.HttpMethod;
import qod_server.http.Request;
import qod_server.http.response.Response;

public class RequestHandler {
    public Response handle(Request request) throws Exception {
        if (request.getPath().equals("/quotes") && request.getHttpMethod().equals(HttpMethod.GET))
            return (new QuoteController(request)).doGet();
        else if (request.getPath().equals("/save-quote") && request.getHttpMethod().equals(HttpMethod.POST))
            return (new QuoteController(request)).doPost();

        throw new Exception("Page: " + request.getPath() + ". Method: " + request.getHttpMethod() + " not found!");
    }
}
