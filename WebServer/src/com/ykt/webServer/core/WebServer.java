package com.ykt.webServer.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private ServerSocket server;
    private ExecutorService threadPoll;//线程池

    //初始化服务器
    public WebServer(){
        try {
            //设置Tomact(服务器)默认服务器端口号是8088
            this.server = new ServerSocket(8088);
            //创建线程池
            this.threadPoll = Executors.newFixedThreadPool(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            //服务器需要一直保持开启状态,用来处理客户端请求
            while (true){
                //System.out.println("等待客户端连接...");
                Socket socket = server.accept();
                //System.out.println("客户端连接成功!");
                ClientHandler client = new ClientHandler(socket);//创建任务
                /*Thread t = new Thread(client);//创建线程,并把任务放在线程中运行
                t.start();*/
                threadPoll.execute(client);//将线程任务放在线程池中运行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebServer webServer = new WebServer();
        webServer.start();
    }
}
