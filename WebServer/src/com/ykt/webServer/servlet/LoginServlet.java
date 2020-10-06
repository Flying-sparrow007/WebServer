package com.ykt.webServer.servlet;

import com.ykt.webServer.http.HttpRequest;
import com.ykt.webServer.http.HttpResponse;

import java.io.RandomAccessFile;

/**
 * 处理用户登录信息
 */
public class LoginServlet extends HttpServlet {
    @Override
    public void service(HttpRequest request, HttpResponse response){
        //获取用户输入
        String username = request.getPara("username");
        String password = request.getPara("password");
        try {
            RandomAccessFile raf = new RandomAccessFile("user.dat", "r");
            boolean nameFlag = false;//判断用户名是否存在
            boolean pwdFlag = false;//判断用户密码是否正确
            byte[] bytes = new byte[32];//存储用户信息
            for(int i = 0; i < raf.length()/100; i++){
                /*
                 * 设置指针位置,每一个完整的用户信息长度为100个字节,保证每次查找的开始位置都是从用户名开始
                 */
                raf.seek(i * 100);

                //读取文件中的用户名
                raf.read(bytes);
                //因为存储的时候扩容为32个字节,所以获取的用户名后面有空格,需要调用trim()方法去掉后面的空格
                String name = new String(bytes, "UTF-8").trim();
                /*
                 * 这里采用name.equals()而没有采用username.equals()是因为用户可能会输入null
                 * null没有equals(),但是从文件读取的用户名不会为null,并且null作为equals()
                 * 中的参数能够正常判断字符串是否相等
                 */
                if(name.equals(username)){//判断用户名
                    nameFlag = true;
                    raf.read(bytes);

                    //获取用户名
                    String pwd = new String(bytes, "UTF-8").trim();
                    if(pwd.equals(password)){//判断用户密码
                        pwdFlag = true;
                        //forward("log_success.html", request, response);
                        //重定向
                        response.sendRedirect("log_success.html");
                        break;
                    }
                }
            }

            if(!nameFlag){//用户名不存在
                //forward("usernameNotExists.html", request, response);
                response.sendRedirect("usernameNotExists.html");
            }else if(!pwdFlag){//密码错误
                //forward("passwordError.html", request, response);
                response.sendRedirect("passwordError.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
