package com.android.messaging.ui.chatbotservice;

import com.android.messaging.util.LogUtil;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class XMLUtil {
    public void parseXMLString(String xmlString){
        Document doc = null;
        try {

            // 读取并解析XML文档
            // SAXReader就是一个管道，用一个流的方式，把xml文件读出来
            //
            // SAXReader reader = new SAXReader(); //User.hbm.xml表示你要解析的xml文档
            // Document document = reader.read(new File("User.hbm.xml"));
            // 下面的是通过解析xml字符串的
            doc = DocumentHelper.parseText(xmlString); // 将字符串转为XML

            Element rootElt = doc.getRootElement(); // 获取根节点
            System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称

            Iterator iter = rootElt.elementIterator("head"); // 获取根节点下的子节点head

            // 遍历head节点
            while (iter.hasNext()) {

                Element recordEle = (Element) iter.next();
                String title = recordEle.elementTextTrim("title"); // 拿到head节点下的子节点title值
                System.out.println("title:" + title);

                Iterator iters = recordEle.elementIterator("script"); // 获取子节点head下的子节点script

                // 遍历Header节点下的Response节点
                while (iters.hasNext()) {

                    Element itemEle = (Element) iters.next();

                    String username = itemEle.elementTextTrim("username"); // 拿到head下的子节点script下的字节点username的值
                    String password = itemEle.elementTextTrim("password");

                    System.out.println("username:" + username);
                    System.out.println("password:" + password);
                }
            }
            Iterator iterss = rootElt.elementIterator("body"); ///获取根节点下的子节点body
            // 遍历body节点
            while (iterss.hasNext()) {

                Element recordEless = (Element) iterss.next();
                String result = recordEless.elementTextTrim("result"); // 拿到body节点下的子节点result值
                System.out.println("result:" + result);

                Iterator itersElIterator = recordEless.elementIterator("form"); // 获取子节点body下的子节点form
                // 遍历Header节点下的Response节点
                while (itersElIterator.hasNext()) {

                    Element itemEle = (Element) itersElIterator.next();

                    String banlce = itemEle.elementTextTrim("banlce"); // 拿到body下的子节点form下的字节点banlce的值
                    String subID = itemEle.elementTextTrim("subID");

                    System.out.println("banlce:" + banlce);
                    System.out.println("subID:" + subID);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Element转list
     *
     * @param root  ontnull
     * @param clazz ontnull
     * @param <T>   ontnull
     * @return bean
     */
    public static <T> List<T> getList(Element root, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            List<Element> elements = root.elements();
            for (int i = 0; i < elements.size(); i++) {
                T t = getBean(elements.get(i), clazz);
                list.add(t);
            }
        } catch (Exception e) {

        }
        return list;
    }

    /**
     * Element转Bean
     *
     * @param root  ontnull
     * @param clazz ontnull
     * @param <T>   ontnull
     * @return bean
     */
    public static <T> T getBean(Element root, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            Field[] properties = clazz.getDeclaredFields();
            Method setmeth;
            String fieldType;
            String fieldGenericType;
            String className;
            for (int i = 0; i < properties.length; i++) {
                fieldType = (properties[i].getType() + "");
                setmeth = t.getClass().getMethod(
                        "set"
                                + properties[i].getName().substring(0, 1)
                                .toUpperCase()
                                + properties[i].getName().substring(1), properties[i].getType());
                if ("interface java.util.List".equals(fieldType)) {
                    fieldGenericType = properties[i].getGenericType() + "";
                    String[] sp1 = fieldGenericType.split("<");
                    String[] sp2 = sp1[1].split(">");
                    className = sp2[0];
                    Object listNode = getList(root.element(properties[i].getName()),
                            Class.forName(className));
                    setmeth.invoke(t, listNode);
                } else {
                    setmeth.invoke(t, root.elementText(properties[i].getName()));
                }
            }
            return t;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 判断是否是合法的list
     *
     */
    public static boolean isList(Element root) {
        int type = 0;
        if (root != null) {
            List<Element> elements = root.elements();
            String elementName;
            String elementNameFlag;
            if (elements != null && elements.size() > 0) {
                elementNameFlag = elements.get(0).getName();
                for (int i = 1; i < elements.size(); i++) {
                    elementName = elements.get(i).getName();
                    if (elementNameFlag.equals(elementName)) {
                        // 是list
                        type = 1;
                    } else {
                        if (type == 1) {
                            LogUtil.e("XMLUtil","This XML is not in the right format,"
                                    + "please add a parent node for Node of the same name!");
                        } else {
                            elementNameFlag = elementName;
                        }
                    }
                }
            }
        }
        if (type == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void createOutboundIMMessage(Element root){
        Element eleSubject = root.addElement("subject");
        eleSubject.addText("hello from the rest of us!");

        Element eleContentType = root.addElement("contentType");
        eleContentType.addText("multipart/mixed");
        eleContentType.addAttribute("boundary", "next");

        Element eleDestinationTerminal = root.addElement("destinationTerminal");
        eleDestinationTerminal.addText("Native");

        String conversationID = UUID.randomUUID().toString();
        Element eleConversationID = root.addElement("conversationID");
        eleConversationID.addText(conversationID);

        String contributionID = UUID.randomUUID().toString();
        Element eleContributionID = root.addElement("contributionID");
        eleContributionID.addText(contributionID);

        Element eleServiceCapability = root.addElement("serviceCapability");
        Element eleCapabilityId = eleServiceCapability.addElement("capabilityId");
        eleCapabilityId.addText("ChatbotSA ");
        Element eleVersion = eleServiceCapability.addElement("version");
        eleVersion.addAttribute("+g.gsma.rcs.botversion", "1");

        Element eleMessageId = root.addElement("messageId");
        eleMessageId.addText("5eae954c-42ca-4181-9ab4-9c0ef2e2ac66");

//        Element eleBodyText = root.addElement("bodyText");
//        eleBodyText.addCDATA()
    }

    /** Dom4j方式，创建 XML  */
    public String dom4jXMLCreate(){
        String hostName = "maap01.hdn.rcs.chinamobile.com";
        String requestUrl = hostName + "/messaging/v1/outbound/sip:12599@botplatform.rcs.chinamobile.com/requests";

        StringWriter xmlWriter = new StringWriter();

//        Person []persons = new Person[3];		// 创建节点Person对象
//        persons[0] = new Person(1, "sunboy_2050", "http://blog.csdn.net/sunboy_2050");
//        persons[1] = new Person(2, "baidu", "http://www.baidu.com");
//        persons[2] = new Person(3, "google", "http://www.google.com");

        try {
            org.dom4j.Document doc = DocumentHelper.createDocument();

            doc.setXMLEncoding("utf-8");

            org.dom4j.Element eleRoot = doc.addElement("msg:outboundMessageRequest");
            eleRoot.addAttribute("xmlns:msg", "urn:oma:xml:rest:netapi:messaging:1");

            Element eleAddress = eleRoot.addElement("address");
            eleAddress.addText("tel:+8619585550103");

            Element eleDestinationAddress = eleRoot.addElement("senderAddress");
            eleDestinationAddress.addText("sip:12599@botplatform.rcs.chinamobile.com");

            Element eleSenderName = eleRoot.addElement("senderName");
            eleSenderName.addText("MyName");

            Element eleOutboundIMMessage = eleRoot.addElement("outboundIMMessage");
            createOutboundIMMessage(eleOutboundIMMessage);


//            int personsLen = persons.length;
//            for(int i=0; i<personsLen; i++){
//
//                Element elePerson = eleRoot.addElement("person");	// 创建person节点，引用类为 org.dom4j.Element
//
//                Element eleId = elePerson.addElement("id");
//                eleId.addText(persons[i].getId()+"");
//
//                Element eleName = elePerson.addElement("name");
//                eleName.addText(persons[i].getName());
//
//                Element eleBlog = elePerson.addElement("blog");
//                eleBlog.addText(persons[i].getBlog());
//            }

            org.dom4j.io.OutputFormat outputFormat = new org.dom4j.io.OutputFormat();	// 设置xml输出格式
            outputFormat.setEncoding("utf-8");
            outputFormat.setIndent(false);
            outputFormat.setNewlines(true);
            outputFormat.setTrimText(true);

            org.dom4j.io.XMLWriter output = new XMLWriter(xmlWriter, outputFormat);		// 保存xml
            output.write(doc);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        savedXML(fileName, xmlWriter.toString());
        return xmlWriter.toString();
    }
}
