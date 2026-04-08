package qod_server.app;

import qod_server.http.Request;
import qod_server.http.response.Response;

public abstract class Controller {
    protected Request request;

    public Controller(Request request) {
        this.request = request;
    }

    public abstract Response doGet();
    public abstract Response doPost();
}
