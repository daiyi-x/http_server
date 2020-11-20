package com.nio;

import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Server {

    static Logger logger = Logger.getLogger(Server.class);
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private int queueNum = 10;
    private int bindPort = 8000;
    private int step = 0;
    private Charset charset = Charset.forName("utf-8");
    private ByteBuffer buffer = ByteBuffer.allocate(64);

    public Server(){
        try{
            //为ServerSocketChannel监控接收连接就绪事件
            //为SocketChannel监控连接就绪事件、读就绪事件以及写就绪事件
            selector = Selector.open();
            //作用相当于传统通信中的ServerSocket
            //支持阻塞模式和非阻塞模式
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().setReuseAddress(true);
            //非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //serverSocketChannel.socket()会获得一个和当前信道相关联的socket
            serverSocketChannel.socket().bind(new InetSocketAddress(bindPort),queueNum);

            //注册接收连接就绪事件
            //注册事件后会返回一个SelectionKey对象用以跟踪注册事件句柄
            //该SelectionKey将会放入Selector的all-keys集合中，如果相应的事件触发
            //该SelectionKey将会放入Selector的selected-keys集合中
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("Server establish error!");
        }
        logger.info("Server start up!");
    }

    public void service() throws Exception{
        //判断是否有触发事件
        while(selector.select() > 0){
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                //处理事件后将事件从Selector的selected-keys集合中删除
                iterator.remove();
                try{
                    if(selectionKey.isAcceptable()){
                        this.Acceptable(selectionKey);
                    }else if(selectionKey.isReadable()){
                        this.Readable(selectionKey);
                    }else if(selectionKey.isWritable()){
                        this.Writable(selectionKey);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    logger.error("event deal exception!");
                }
            }
        }
    }

    private void Acceptable(SelectionKey selectionKey) throws Exception{
        logger.info("accept:"+(++step));

        ServerSocketChannel ssc = (ServerSocketChannel)selectionKey.channel();
        SocketChannel sc = (SocketChannel)ssc.accept();

        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);

        logger.info(selectionKey.hashCode());
    }

    private void Readable(SelectionKey selectionKey) throws Exception{
        logger.info("read:"+(++step));

        SocketChannel sc = (SocketChannel)selectionKey.channel();

        buffer.clear();
        int num = sc.read(buffer);
        String request = "";
        if(num > 0){
            buffer.flip();

            request = charset.decode(buffer).toString();
            sc.register(selector, SelectionKey.OP_WRITE,request);
        }else{
            sc.close();
        }

        logger.info(selectionKey.hashCode()+":"+request);
    }

    private void Writable(SelectionKey selectionKey) throws Exception{
        logger.info("write:"+(++step));

        String request = (String)selectionKey.attachment();
        SocketChannel sc = (SocketChannel)selectionKey.channel();

        String answer = "not supported";
        if(request.equals("your name?")){
            answer = "server";
        }

        logger.info(selectionKey.hashCode()+":"+answer);

        buffer.clear();
        buffer.put(charset.encode(answer));
        buffer.flip();
        while(buffer.hasRemaining())
            sc.write(buffer);

        sc.close();
    }

    public static void main(String[] args) {
        Server server = new Server();
        try{
            server.service();
        }catch(Exception e){
            e.printStackTrace();
            logger.error("Server run exception!");
        }
    }

}
