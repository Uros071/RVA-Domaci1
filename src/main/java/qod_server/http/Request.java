package qod_server.http;

public class Request {
    private final HttpMethod httpMethod;
    private final String path;
    private String body;

    public Request(HttpMethod httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.body = "";
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
