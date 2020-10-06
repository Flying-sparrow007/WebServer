package com.ykt.webServer.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 处理客户端请求数据
 */
public class HttpRequest {

    private Socket socket;

    /**输入流*/
    private InputStream in;

    /**请求方式*/
    private String method;

    /**请求资源及参数*/
    private String requestPara;

    /**HTTP协议版本*/
    private String protocol;

    /**实际请求资源*/
    private String URL;

    /**实际请求参数*/
    private String parameter;

    /**
     * 存储请求参数
     * key: 存储请求参数名
     * value: 存储请求参数名对应的内容
     */
    private Map<String, String> para = new HashMap<String, String>();

    /**
     * 存储消息头
     * key: 存储消息头中": "前面的内容,即每一组的名称
     * value: 存储消息头中": "后面的内容,即每一组的值
     */
    private Map<String, String> headers = new HashMap<String, String>();

    public HttpRequest(Socket socket){
        try {
            this.socket = socket;
            //获取输入流
            this.in = this.socket.getInputStream();
            /**
             * 解析用户请求
             * 1.请求行
             * 2.消息头
             * 3.消息正文
             */
            //1.请求行
            parseRequestLine();

            //2.消息头
            parseMessageHeader();

            //3.消息正文
            parseMessageContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**解析请求行*/
    private void parseRequestLine() {
        //获取请求行
        String line = getRequestLine();
        /*
         * 请求行包含请求行、请求资源、HTTP协议版本
         * 它们三个被空格隔开,因此需要通过字符串分割来获取请求资源
         */
        String[] data = line.split("\\s");
        if(data.length < 3){//空请求,没有请求资源,需要抛出异常
            throw new EmptyRequestException("空请求");
        }
        this.method = data[0];
        this.requestPara = data[1];
        this.protocol = data[2];
        /*
         * 当请求资源里包含请求参数时,实际请求资源和实际请求参数之间通过"?"隔开
         */
        if(requestPara.contains("?")){
            String[] parData = requestPara.split("\\?");
            if(parData.length < 2){
                this.URL = parData[0];
            }else{
                this.URL = parData[0];
                this.parameter = parData[1];
                //解析请求参数
                parseParameter(parameter);
            }
        }else{
            this.URL = requestPara;
        }
    }

    /**
     * 解析消息头
     */
    private void parseMessageHeader(){
        /*
         * 这里直接读取请求信息,因为请求行已读完,读取文件的指针已经停留在了消息头处
         * 因此这里直接读取,直到读取到一个空字符串就意味着读取消息头已读完
         */
        while(true){
            String line = getRequestLine();
            if("".equals(line)){//请求头读取完毕
                break;
            }
            /*
             * 请求头中的每一条数据的具体内容被": "隔开
             * 例如:Host: localhost:8080
             */
            String[] data = line.split(": ");
            if(data.length < 2){
                headers.put(data[0], "");
            }else{
                headers.put(data[0], data[1]);
            }
        }
        //测试,输出消息头
        /*Set<Map.Entry<String, String>> entry = headers.entrySet();
        for(Map.Entry<String, String> e: entry){
            System.out.println(e.getKey() + ": " + e.getValue());
        }*/
    }

    /**
     * 解析消息正文
     */
    private void parseMessageContent() {
        /*
         * Post请求的消息头中会包含
         * Content-Length: "消息正文长度"
         * Content-Type: application/x-www-form-urlencoded
         */
        if(headers.containsKey("Content-Length")){
            Integer length = Integer.valueOf(headers.get("Content-Length"));
            String type = headers.get("Content-Type");
            byte[] bytes = new byte[length];
            if("application/x-www-form-urlencoded".equals(type)){
                try {
                    in.read(bytes);
                    //将字节数据转换为字符串
                    String parameter = new String(bytes, "UTF-8");
                    //解析消息头
                    parseParameter(parameter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解析请求参数
     * @param parameter
     */
    private void parseParameter(String parameter) {
        try {
            /*
             * 当请求参数含有非ISO8859-1的字符时,会被浏览器解析为"%E9%AB%98%E8%BD%B2"的乱码字符
             * 使得服务器无法识别,因此这里需要调用URLDecoder.decode(String str, String enc)静态方法
             * 来重新解析非ISO8859-1的字符从而是的服务器能够正常识别
             */
            parameter = URLDecoder.decode(parameter, "UTF-8");
            //请求参数中的每组数据被"&"隔开
            String[] data = parameter.split("&");
            //每组参数的具体内容被"="隔开
            for(String groupStr: data){
                String[] groupData = groupStr.split("=");//每一组参数中的具体数据
                if(groupData.length < 2){
                    para.put(groupData[0], "");
                }else{
                    para.put(groupData[0], groupData[1]);
                }
            }

            //输出Map集合中的内容
            /*Set<Map.Entry<String, String>> enrty = para.entrySet();
            for(Map.Entry<String, String> e: enrty){
                System.out.println(e.getKey() + "," + e.getValue());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**获取请求行*/
    private String getRequestLine() {
        try {
            /**
             * 读取一行字符串,以CRLF结尾表示一行
             * 顺序从in中读取每个字符,当连续读取到CRLF时停止
             * 并将之前读取的字符转换为字符串
             */
            StringBuilder builder = new StringBuilder();
            char cr = 'a';//表示上次读取到的字符
            char lf = 'a';//表示本次读取到的字符
            int d = -1;
            while((d = in.read()) != -1){
                lf = (char)d;
                if(cr == 13 && lf == 10){
                    break;
                }
                builder.append(lf);
                cr = lf;
            }
            /**
             * CR: 回车符,对应编码为13
             * LF: 换行符,对应编码为10
             * 由于程序逻辑问题,会导致读取的字符串多一个CR(回车符,也就是空格字符),需要通过trim()清除空格字符
             */
            return builder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取请求参数的value
     * @param key
     * @return
     */
    public String getPara(String key) {
        String value = para.get(key);
        return value;
    }

    /**获取请求资源*/
    public String getURL() {
        return URL;
    }
}
