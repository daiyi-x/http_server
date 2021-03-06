package com.servlet;

import com.server.Request;
import com.server.Response;

public abstract class Servlet {

    public void service(Request req, Response rep) throws Exception {
        this.doGet(req,rep);
        this.doPost(req,rep);
    }
    protected abstract void doGet(Request req,Response rep) throws Exception;
    protected abstract void doPost(Request req,Response rep) throws Exception;

}
