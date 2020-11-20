package com.server;

import sun.plugin2.os.windows.Windows;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

public class Response {
    public static final String CRLF="\r\n";
    public static final String BLANK=" ";
    private BufferedWriter bw;
    private StringBuilder content;
    private StringBuilder headInfo;
    private int len = 0;
    public Response() {
        headInfo = new StringBuilder();
        content = new StringBuilder();
        len = 0;
    }
    public Response(OutputStream os) {
        this();
        bw = new BufferedWriter(new OutputStreamWriter(os));
    }
    public Response(Socket client) {
        this();
        try {
            bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        } catch (IOException e) {
            headInfo = null;
        }
    }

    public Response print(String info) {
        content.append(info);
        len+=info.getBytes().length;
        return this;
    }

    public Response println(String info) {
        content.append(info).append(CRLF);
        len+=(info+CRLF).getBytes().length;
        return this;
    }

    private void createHeadInfo(int code) {
        //1)  HTTP协议版本、状态代码、描述
        /* HTTP/1.1 200 OK
           Date: Fri, 22 May 2009 06:07:21 GMT
           Content-Type: text/html; charset=UTF-8 */

        headInfo.append("HTTP/1.1").append(BLANK).append(code).append(BLANK);
        switch(code) {
            case 200:
                headInfo.append("OK");
                break;
            case 404:
                headInfo.append("NOT FOUND");
                break;
            case 500:
                headInfo.append("SEVER ERROR");
                break;
        }
        headInfo.append(CRLF);
        headInfo.append("Server:HTTP Server/0.0.1").append(CRLF);
        headInfo.append("Date:").append(new Date()).append(CRLF);
        headInfo.append("Content-type:text/html;charset=GBK").append(CRLF);
        headInfo.append("Content-Length:").append(len).append(CRLF);
        headInfo.append(CRLF);
    }
    void pushToClient(int code) throws IOException {
        if(null==headInfo) {
            code = 500;
        }
        createHeadInfo(code);
        bw.write(headInfo.toString());
        bw.write(content.toString());
        bw.flush();
    }
    public void close() throws IOException {
        bw.close();
    }

}
