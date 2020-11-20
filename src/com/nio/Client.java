package com.nio;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {

    static Logger logger = Logger.getLogger(Client.class);
    private int port = 8000;
    private SocketChannel socketChannel;

    public Client(){
        try {
            socketChannel = SocketChannel.open();
            InetAddress host = InetAddress.getLocalHost();
            InetSocketAddress addr = new InetSocketAddress(host, port);

            socketChannel.connect(addr);

            logger.debug("***");
            logger.debug("client ip:"+socketChannel.socket().getLocalAddress());
            logger.debug("client port:"+socketChannel.socket().getLocalPort());
            logger.debug("server ip:"+socketChannel.socket().getInetAddress());
            logger.debug("server port:"+socketChannel.socket().getPort());
            logger.debug("***");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Cilent socket establish failed!");
        }
        logger.info("Client socket establish success!");
    }

    public void request(String request){
        try{
            DataInputStream input = SocketIO.getInput(socketChannel.socket());
            DataOutputStream output = SocketIO.getOutput(socketChannel.socket());

            if(null != request && !request.equals("")){
                byte[] bytes = request.getBytes("utf-8");
                output.write(bytes);

                bytes = new byte[64];
                int num = input.read(bytes);
                byte[] answer = new byte[num];
                System.arraycopy(bytes, 0, answer, 0, num);
                if(num > 0){
                    logger.info("server answer:"+new String(answer,"utf-8"));
                }else{
                    logger.info("No server answer.");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("client request error");
        }finally{
            if(null != socketChannel){
                try{
                    socketChannel.close();
                }catch(Exception e){
                    e.printStackTrace();
                    logger.error("socket close error");
                }
            }
        }
    }

    public static void main(String[] args){
        Client client1 = new Client();
        //Client client2 = new Client();
        client1.request("your name?");
        //client2.request("your name?");
    }

}
