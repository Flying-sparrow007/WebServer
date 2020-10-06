package com.ykt.webServer.http;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理HTTP的一些基础业务
 */
public class HttpContext {
    /**
     * 采用Map集合存储状态码和状态描述
     * key: 存储状态码
     * value: 存储状态描述
     */
    private static Map<Integer, String> STATUS_CODE_REASON_MAPPING = new HashMap<Integer, String>();

    /**
     * 采用Map集合存储web.xml文件中的<mime-mapping>节点中的内容
     * key: 存储<extension>节点的内容
     * value: 存储<mime-type>节点的内容
     */
    private static Map<String, String> MIME_MAPPING = new HashMap<String, String>();

    static{
        //初始化
        initStatusCodeReasonMapping();
        initMimeMapping();
    }

    /**
     * 初始化存储状态码和状态描述的Map集合
     */
    private static void initStatusCodeReasonMapping() {
        STATUS_CODE_REASON_MAPPING.put(200, "OK");
        STATUS_CODE_REASON_MAPPING.put(201, "Created");
        STATUS_CODE_REASON_MAPPING.put(202, "Accepted");
        STATUS_CODE_REASON_MAPPING.put(204, "No Content");
        STATUS_CODE_REASON_MAPPING.put(301, "Moved Permanently");
        STATUS_CODE_REASON_MAPPING.put(302, "Moved Temporarily");
        STATUS_CODE_REASON_MAPPING.put(304, "Not Modified");
        STATUS_CODE_REASON_MAPPING.put(400, "Bad Request");
        STATUS_CODE_REASON_MAPPING.put(401, "Unauthorized");
        STATUS_CODE_REASON_MAPPING.put(403, "Forbidden");
        STATUS_CODE_REASON_MAPPING.put(404, "Not Found");
        STATUS_CODE_REASON_MAPPING.put(500, "Internal Server Error");
        STATUS_CODE_REASON_MAPPING.put(501, "Not Implemented");
        STATUS_CODE_REASON_MAPPING.put(502, "Bad Gateway");
        STATUS_CODE_REASON_MAPPING.put(503, "Service Unavailable");
    }

    /**
     * 初始化存储<mime-mapping>节点内容的Map集合
     */
    private static void initMimeMapping() {

        try {
            //1.创建SAXReader实体对象
            SAXReader reader = new SAXReader();
            //2.创建document树,即获取Document实例
            File file = new File("conf/web.xml");
            Document document = reader.read(file);
            //3.获取根节点
            Element root = document.getRootElement();
            //4.获取根节点的子节点mime-mapping节点
            List<Element> list = root.elements("mime-mapping");
            //5.获取mime-mapping节点下的子节点extension和mime-type
            list.forEach((s)->{
                //获取节点中的内容
                String key = s.elementText("extension");
                String value = s.elementText("mime-type");
                //将每一组数据添加到Map集合中
                MIME_MAPPING.put(key, value);
            });
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过状态码获取状态描述
     * @param statusCode
     * @return
     */
    public static String getStatusReason(Integer statusCode){
        String statusReason = STATUS_CODE_REASON_MAPPING.get(statusCode);
        return statusReason;
    }

    /**
     * 根据extension获取对应的mime-type,从而是的服务器能够正常返回文件类型
     * @param key
     * @return
     */
    public static String getMimeType(String key){
        String value = MIME_MAPPING.get(key);
        return value;
    }

    /*public static void main(String[] args) {
        //System.out.println(getStatusReason(1531));
        //System.out.println(getMimeType("weadas"));
    }*/
}
