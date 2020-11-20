package com.server;

import com.servlet.HelloServlet;
import com.servlet.Servlet;
import com.xml.WebApp;

import java.io.IOException;
import java.net.Socket;

public class Dispatcher implements Runnable{

    private Request request;
    private Response response;
    private int code = 200;
    private Socket client;

    Dispatcher(Socket socket){
        client = socket;
        try {
            request = new Request(client.getInputStream());
            response = new Response(client.getOutputStream());
        } catch (IOException e) {
            code = 500;
            return;
        }

    }

    @Override
    public void run() {

        try {
            Servlet servlet = WebApp.getServlet(request.getUrl());
            if(null == servlet){
                this.code = 404;
            }else{
                servlet.service(request,response);
            }
            response.pushToClient(code);
        } catch (Exception e) {
            e.printStackTrace();
            this.code = 500;

        }

        if(this.code == 500){
            try {
                response.pushToClient(500);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
