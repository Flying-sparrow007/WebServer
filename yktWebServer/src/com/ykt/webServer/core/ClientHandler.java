package com.ykt.webServer.core;

import com.ykt.webServer.http.EmptyRequestException;
import com.ykt.webServer.http.HttpRequest;
import com.ykt.webServer.http.HttpResponse;
import com.ykt.webServer.servlet.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/***
 * 处理客户请求并给予响应
 */
public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //请求
            HttpRequest request = new HttpRequest(socket);
            String url = request.getURL();
            //System.out.println(url);

            //响应
            HttpResponse response = new HttpResponse(socket);
            if (ServletContext.hasServlet(url)) {//判断请求的url是否在集合中,进行数据处理
                //获取类名
                String className = ServletContext.getClassName(url);
                //通过反射获取类
                Class cls = Class.forName(className);
                //动态创建类
                HttpServlet servlet = (HttpServlet)cls.newInstance();
                servlet.service(request, response);
            }else{
                File file = new File("webapps" + url);
                if(file.exists()){
                    response.setEntity(file);
                }else{
                    File f = new File("webapps/error.html");
                    response.setStatusCode(404);
                    response.setEntity(f);
                }
            }
            response.flush();
        } catch (EmptyRequestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*
             * 防止资源占用,当客户端断开连接时,服务器就关闭该连接,防止资源浪费
             * 使得其他客户端能够正常请求资源
             */
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
