package com.ykt.webServer.http;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 给予客户端响应
 */
public class HttpResponse {

    private Socket socket;

    /**输出流*/
    private OutputStream out;

    /**状态码*/
    private Integer statusCode = 200;

    /**状态描述*/
    private String statusReason = "OK";

    /**响应实体文件*/
    private File entity;

    /**
     * 响应头的具体内容
     * key: type,length
     * value: type和length对应的值
     */
    private Map<String, String> headers = new HashMap<String, String>();

    /**用户信息*/
    private byte[] data = new byte[0];

    public HttpResponse(Socket socket) {
        try {
            this.socket = socket;
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 响应客户端
     * 将当前相应的内容以HTTP格式响应给客户端,需按照HTTP标准格式响应
     */
    public void flush(){
        //1.状态行
        statusLine();

        //2.响应头
        responseHead();

        //3.响应正文
        responseContent();
    }

    /**状态行*/
    private void statusLine() {
        String line = "HTTP/1.1" + " " + statusCode + " " + statusReason;
        println(line);
    }

    /**响应头*/
    private void responseHead() {
        Set<Map.Entry<String, String>> entry = headers.entrySet();
        for(Map.Entry<String, String> e: entry){
            String line = e.getKey() + ": " + e.getValue();
            println(line);
        }
        //响应头是以一个单独的CRLF为结尾的,因此这个传入一个空字符串,会输出给客户端一个单独的CRLF
        println("");
    }

    /**响应正文*/
    private void responseContent(){
        if(entity != null){
            //JDK1.7版本以上可用此方式关闭流
            try (FileInputStream in = new FileInputStream(entity);) {
                byte[] bytes = new byte[1024 * 10];
                int len = -1;
                while((len = in.read(bytes)) != -1){
                    out.write(bytes, 0, len);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                out.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 输出客户端
     * @param str
     */
    private void println(String str){
        try{
            out.write(str.getBytes("ISO8859-1"));//HTTP响应头响应正文以ISO8859-1编码
            out.write(13);//CR
            out.write(10);//LF
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置状态码和状态描述
     * @param statusCode
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        this.statusReason = HttpContext.getStatusReason(statusCode);
    }

    /**
     * 描述重定向和转发之间的区别
     * 重定向是两次请求(两次request请求,两次response响应)
     * 重定向地址栏改变为第二次请求的页面地址
     *
     * 转发之后地址栏不发生改变(一次request请求,一次response响应)
     * 请求的还是第一次的地址
     * @param url
     */
    public void sendRedirect(String url){
        //设置重定向状态码302
        setStatusCode(302);
        headers.put("Location", url);
    }

    /**
     * 设置用户信息
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * 设置响应头
     * @param key
     * @param value
     */
    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 设置实体文件
     * @param entity
     */
    public void setEntity(File entity) {
        /*
         * 通过获取文件后缀,来匹配响应头中的Content-Type对应的内容
         * 以"."分割字符串,分割出的字符串数组的第二个字符串为文件后缀,即数组下标为1
         */
        String fileName = entity.getName();
        String regex = "^.+\\.[a-zA-Z0-9]+$";
        if(fileName.matches(regex)){
            String endName = fileName.split("\\.")[1];
            headers.put("Content-Type", endName);
            headers.put("Content-Length", entity.length() + "");
        }
        this.entity = entity;
    }
}
