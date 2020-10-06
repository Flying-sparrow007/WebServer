package com.ykt.webServer.servlet;

import com.ykt.webServer.http.HttpRequest;
import com.ykt.webServer.http.HttpResponse;

import java.io.File;

/**
 * 处理用户业务的父类
 */
public abstract class HttpServlet {
    /**
     * 该方法是用来处理用户业务逻辑的
     * @param request
     * @param response
     */
    public abstract void service(HttpRequest request, HttpResponse response);

    /**
     * 该方法是用来处理转发业务的
     * @param name
     * @param request
     * @param response
     */
    public void forward(String name, HttpRequest request, HttpResponse response){
        File file = new File("webapps/" + name);
        response.setEntity(file);
    }
}
