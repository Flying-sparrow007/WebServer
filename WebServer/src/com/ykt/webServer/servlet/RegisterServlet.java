package com.ykt.webServer.servlet;

import com.ykt.webServer.http.HttpRequest;
import com.ykt.webServer.http.HttpResponse;

import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * 处理用户注册信息
 */
public class RegisterServlet extends HttpServlet {
    @Override
    public void service(HttpRequest request, HttpResponse response){
        /*
         * 处理用户注册信息
         * 1.获取用户信息
         * 2.将用户信息写入user.dat文件
         * 3.想客户端响应注册成功页面
         */
        //获取用户信息
        String username = request.getPara("username");//用户名
        String password = request.getPara("password");//用户密码
        String nick = request.getPara("nick");//用户昵称
        String age = request.getPara("age");//用户年龄

        if(username == null || password == null || nick == null || age == null){
            forward("register_failure.html", request, response);
            return ;
        }else{
            if("".equals(username) || "".equals(password) || "".equals(nick) || "".equals(age)){
                forward("register_failure.html", request, response);
                return ;
            }
        }
        Integer intAge = Integer.parseInt(age);//将String类型转换为Integer类型
        //System.out.println(username + "," + password + "," + nick + "," + intAge);

        try (RandomAccessFile raf = new RandomAccessFile("user.dat", "rw")) {
            /*
             * 设置用户名,用户密码,用户昵称占据各32个字节,用户年龄占4个字节
             * 刚好100个字节便于后续操作
             */
            raf.seek(raf.length());

            //用户姓名
            byte[] bytes = username.getBytes("UTF-8");
            bytes = Arrays.copyOf(bytes, 32);//扩容
            raf.write(bytes);

            //用户密码
            bytes = password.getBytes("UTF-8");
            bytes = Arrays.copyOf(bytes, 32);
            raf.write(bytes);

            //用户昵称
            bytes = nick.getBytes("UTF-8");
            bytes = Arrays.copyOf(bytes, 32);
            raf.write(bytes);

            //用户年龄
            raf.writeInt(intAge);

            /*
             * 向客户端响应注册成功信息
             */
            forward("reg_success.html", request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
