package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        Socket socket;
        while(true){
            socket = server.accept();
            Thread t =new Thread(new Dispatcher(socket));
            t.start();
        }
    }
}
