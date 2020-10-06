package com.ykt.webServer.servlet;

import com.ykt.webServer.http.HttpRequest;
import com.ykt.webServer.http.HttpResponse;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * 查询所有用户信息
 */
public class SelectAllUserServlet extends HttpServlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        /*
         * 后端通过StringBuilder编写前端页面,并将其转换为字节流向客户端响应
         * 通过浏览器解析,从而达到通过后端编写前端页面的效果
         */
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<meta charset='UTF-8'>");
        builder.append("<title>用户列表</title>");

        builder.append("<style type='text/css'>");

        builder.append("body{");
        builder.append("background-color: orange;padding: 0;margin: 0;");
        builder.append("}");

        builder.append("div{");
        builder.append("text-align: center;");
        builder.append("}");

        builder.append("table{");
        builder.append("width: 400px;");
        builder.append("border: 1px solid #000;");
        builder.append("margin: 0 auto;");
        builder.append("}");

        builder.append("table tr td,th{");
        builder.append("border: 1px solid #000;");
        builder.append("border-collapse: collapse;");
        builder.append("text-align: center;");
        builder.append("}");

        builder.append(".modify{");
        builder.append("width: 100px;");
        builder.append("border-radius: 10px;");
        builder.append("color: white;");
        builder.append("background-color: green;");
        builder.append("cursor: pointer;");
        builder.append("}");

        builder.append(".index{");
        builder.append("width: 100px;");
        builder.append("border-radius: 10px;");
        builder.append("color: white;");
        builder.append("margin-left: 10px;");
        builder.append("background-color: green;");
        builder.append("cursor: pointer;");
        builder.append("}");

        builder.append("</style>");

        builder.append("</head>");
        builder.append("<body>");
        builder.append("<div>");
        builder.append("<h1>用户列表</h1>");
        builder.append("<table>");

        builder.append("<tr>");
        builder.append("<th>ID</th>");
        builder.append("<th>姓名</th>");
        builder.append("<th>密码</th>");
        builder.append("<th>昵称</th>");
        builder.append("<th>年龄</th>");
        builder.append("</tr>");

        //获取用户信息并写入HTML文件
        try(RandomAccessFile raf = new RandomAccessFile("user.dat", "r")){
            byte[] bytes = new byte[32];
            for(int i = 0; i <raf.length()/100; i++){
                //读取用户名
                raf.read(bytes);
                String username = new String(bytes, "UTF-8").trim();

                //读取用户密码
                raf.read(bytes);
                String password = new String(bytes, "UTF-8").trim();

                //读取用户昵称
                raf.read(bytes);
                String nick = new String(bytes, "UTF-8").trim();

                //读取用户年龄
                Integer age = raf.readInt();

                //将用户信息动态添加到表格中
                builder.append("<tr>");
                builder.append("<td>" + (i + 1) + "</td>");
                builder.append("<td>" + username + "</td>");
                builder.append("<td>" + password + "</td>");
                builder.append("<td>" + nick + "</td>");
                builder.append("<td>" + age + "</td>");
                builder.append("</tr>");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        builder.append("<tr>");
        builder.append("<td colspan='5'>");
        builder.append("<button class='modify' onclick='modify()'>修改用户密码</button>");
        builder.append("<button class='index' onclick='index()'>返回首页</button>");
        builder.append("</td>");
        builder.append("</tr");

        builder.append("</body>");

        builder.append("<script type='text/javascript'>");
        builder.append("function modify() {");
        builder.append("location.href = 'modify.html'");
        builder.append("}");

        builder.append("function index() {");
        builder.append("location.href = 'index.html'");
        builder.append("}");
        builder.append("</script>");

        builder.append("</html>");

        byte[] data = new byte[0];
        try {
            data = builder.toString().getBytes("UTF-8");

            response.setHeaders("Content-Type", "text/html");
            response.setHeaders("Content-Length", data.length + "");

            response.setData(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
