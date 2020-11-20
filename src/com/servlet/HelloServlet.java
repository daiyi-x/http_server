package com.servlet;

import com.server.Request;
import com.server.Response;

public class HelloServlet extends Servlet{
    @Override
    protected void doGet(Request req, Response rep) throws Exception {

    }

    @Override
    protected void doPost(Request req, Response rep) throws Exception {
        rep.println("Hello word!");
    }
}
