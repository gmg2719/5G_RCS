package com.android.messaging.datamodel.microfountain.sms;

import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.support.FTMessageSourceFileInfo;
import com.microfountain.rcs.support.MessageTypes;
import com.microfountain.rcs.support.model.sms.GeoPushViaSms;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ParseRcsUtils {
    public static String parseMessageType(String contentType, byte[]
            contentBody) {
        if (MessageTypes.MIME_TEXT.equalsIgnoreCase(contentType)) {
            String contentBodyString = new String(contentBody,
                    StandardCharsets.UTF_8);
            if(contentBodyString != null &&
                    contentBodyString.startsWith("geo:")) {
                GeoPushViaSms.GeoPushInfo geoPushInfo =
                        GeoPushViaSms.getGeoPushInfo(contentBodyString);
                if(geoPushInfo != null) {
                    //位置消息
                    return RcsContant.RcsTypes.MIME_GEO_PUSH;
                } else {
                    //文本消息
                    return RcsContant.RcsTypes.MIME_TEXT;
                }
            }
        } else if (MessageTypes.MIME_BOT_MESSAGE.equalsIgnoreCase(contentType)) {
            //Chatbot卡片消息
            return RcsContant.RcsTypes.MIME_BOT_MESSAGE;
        } else if("multipart/mixed".equals(contentType)){
            //带建议回复chatbot消息
            return RcsContant.RcsTypes.MIME_MIXED;
        } else if (MessageTypes.MIME_COMMONTEMPLATE.equalsIgnoreCase(contentType)) {
            //中国移动A2P消息
            return RcsContant.RcsTypes.MIME_COMMONTEMPLATE;
        } else if (MessageTypes.MIME_SDP.equalsIgnoreCase(contentType) ||
                MessageTypes.MIME_FT_HTTP_XML.equalsIgnoreCase(contentType)) {
                    //文件类消息
                FTMessageSourceFileInfo ftMessageSourceFileInfo =
                        FTMessageSourceFileInfo.parseContentBody(contentType, contentBody);
                if (ftMessageSourceFileInfo != null) {
                    String fileContentType =
                            ftMessageSourceFileInfo.fileContentType;
                    if (fileContentType != null && !fileContentType.isEmpty()) {
                        if (fileContentType.startsWith(MessageTypes.Prefix.AUDIO)) {
                            //音频消息
                            return RcsContant.RcsTypes.MIME_AUDIO;
                        } else if (fileContentType.startsWith(MessageTypes.Prefix.IMAGE)) {
                            //图片消息
                            return RcsContant.RcsTypes.MIME_IMAGE;
                        } else if (fileContentType.startsWith(MessageTypes.Prefix.VIDEO)) {
                            //视频消息
                            return RcsContant.RcsTypes.MIME_VIDEO;
                        } else if (fileContentType.equalsIgnoreCase(MessageTypes.MIME_VCARD) ||
                                        fileContentType.equalsIgnoreCase(MessageTypes.MIME_X_VCARD)) {
                            //名片消息
                            return RcsContant.RcsTypes.MIME_VCARD;
                        } else if (fileContentType.equalsIgnoreCase(MessageTypes.MIME_GEO_PUSH)) {
                            //位置消息
                            return RcsContant.RcsTypes.MIME_GEO_PUSH;
                        } else if (fileContentType.equalsIgnoreCase(MessageTypes.MIME_PDF)) {
                            //PDF文件消息
                            return RcsContant.RcsTypes.MIME_PDF;
                        } else if (fileContentType.equalsIgnoreCase(MessageTypes.MIME_BINARY)) {
                            //文件消息
                            return RcsContant.RcsTypes.MIME_BINARY;
                        }else {
                            //未知类型
                            return RcsContant.RcsTypes.MIME_UNKNOWN;
                        }
                    } else {
                        //未知类型
                        return RcsContant.RcsTypes.MIME_UNKNOWN;
                    }
                } else {
                    //解析失败
                    LogUtil.i("Junwang", "parseMessageType error");
                }
        } else {
            //未知类型
            return RcsContant.RcsTypes.MIME_UNKNOWN;
        }
        return RcsContant.RcsTypes.MIME_UNKNOWN;
     }

     public static List<ChatbotContent> parseMixedType(String content, String boundary){
         List<ChatbotContent> chatbotContents = new ArrayList<>();
         String[] split = content.split(boundary);
         BufferedReader in = null;
         try{
             if((split != null) && split.length > 0){
                 for(int i=0; i<split.length; i++) {
                     ChatbotContent cc = new ChatbotContent();
                     in = new BufferedReader(new StringReader(split[i]), 256);
                     String line;
                     int start;
                     int end;
                     String subject;
                     StringBuilder temp;
                     while ((line = in.readLine()) != null){
                         if(line.startsWith("Content-Type: ")){
                             cc.setContentType(line.substring(14));
                         }else if(line.startsWith("Content-Length: ")){
                             cc.setContentLength(line.substring(16));
                         }else{
                             subject = cc.getContentString();
                             if(subject == null || subject.length() == 0) {
                                 cc.setContentString(line);
                             }else{
                                 temp = new StringBuilder(cc.getContentString());
                                 temp.append(subject);
                                 cc.setContentString(temp.toString());
                             }
                         }
                     }
                     chatbotContents.add(cc);
                 }
             }
         }catch (Exception e){

         }
         return chatbotContents;
     }
}
