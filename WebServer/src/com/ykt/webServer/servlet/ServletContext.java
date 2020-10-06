package com.ykt.webServer.servlet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理Servlet一些业务逻辑
 */
public class ServletContext {
    /**
     * 存储url对应的包名.类名
     * key: url
     * value: 包名.类名
     */
    private static Map<String, String> SERVLET_MAPPING = new HashMap<String, String>();

    static{
        //初始化
        initServlet();
    }

    /**
     * 初始化servlets
     */
    private static void initServlet() {
        //创建SAXReader实例
        SAXReader reader = new SAXReader();

        try{
            //获取Document实例
            File file = new File("conf/servlet.xml");
            Document document = reader.read(file);
            //获取根节点
            Element root = document.getRootElement();
            //获取根节点下的子节点servlet
            List<Element> list = root.elements("servlet");
            //继续获取子节点下的子节点的文本
            list.forEach((s)->{
                //获取子节点的内容
                String key = s.elementText("url");
                String value = s.elementText("className");

                //写入Map集合
                SERVLET_MAPPING.put(key, value);
            });
            //System.out.println(SERVLET_MAPPING.size());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过请求资源地址url获取类名,类名为全名(包名.类名)
     * @param url
     * @return
     */
    public static String getClassName(String url){
        String className = SERVLET_MAPPING.get(url);
        return className;
    }

    /**
     * 检查给定的url是否对应Servlet处理
     * @param url
     * @return
     */
    public static boolean hasServlet(String url){
        //检查给定的url是否作为key存储再SERVLET_MAPPING集合中
        boolean flag = SERVLET_MAPPING.containsKey(url);
        return flag;
    }

    /*public static void main(String[] args) {
        System.out.println(getClassName("/ModifyServlet"));
    }*/
}
