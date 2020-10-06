package com.ykt.webServer.servlet;

import com.ykt.webServer.http.HttpRequest;
import com.ykt.webServer.http.HttpResponse;

import java.io.RandomAccessFile;
import java.util.Arrays;

public class ModifyServlet extends HttpServlet {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        //获取用户输入信息
        String username = request.getPara("username");
        String password = request.getPara("password");
        String newPassword = request.getPara("newPassword");
        String confirmPassword = request.getPara("confirmPassword");
        if(newPassword == null || "".equals(newPassword)){//输入不能为null或者""
            forward("passwordEmpty.html", request, response);
            return ;
        }else{
            if(!newPassword.equals(confirmPassword)){//新密码与确认密码不一致
                forward("passwordDifferent.html", request, response);
                return ;
            }
        }

        try(RandomAccessFile raf = new RandomAccessFile("user.dat", "rw")){
            byte[] bytes = new byte[32];
            boolean nameFlag = false;//判断用户名是否存在
            boolean pwdFlag = false;//判断用户密码是否正确
            for(int i = 0; i < raf.length()/100; i++){
                raf.seek(i * 100);//设置指针位置

                raf.read(bytes);
                String name = new String(bytes, "UTF-8").trim();
                if(name.equals(username)){
                    nameFlag = true;
                    raf.read(bytes);
                    String pwd = new String(bytes, "UTF-8").trim();
                    if(pwd.equals(password)){
                        pwdFlag = true;

                        //重新设置指针位置,使得指针停留在用户密码处
                        raf.seek(i * 100 + 32);
                        //写入用户密码
                        byte[] bytes2 = newPassword.getBytes("UTF-8");
                        bytes2 = Arrays.copyOf(bytes2, 32);
                        raf.write(bytes2);
                        response.sendRedirect("modify_success.html");
                    }
                }
            }

            if(!nameFlag){//用户不存在
                response.sendRedirect("usernameNotExists.html");
            }else if(!pwdFlag){
                response.sendRedirect("passwordError.html");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
