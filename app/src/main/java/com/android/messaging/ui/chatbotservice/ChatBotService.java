package com.android.messaging.ui.chatbotservice;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class ChatBotService {
    public static JSONObject HttpRequest(String requestUrl, String requestMethod,
                                         String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        OutputStream out = null;
        InputStream input = null;
        InputStreamReader inputReader = null;
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        try {
            // 建立连接
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            // 设置通用的请求属性 start  看需要自行增加和删除
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("contentType", "UTF-8");
            connection.setRequestProperty("Accept-Language", Locale.getDefault().toString());
            // 设置通用的请求属性 end
            // 设置http头部信息
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(requestMethod);
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(6000);
            // 写入消息实体
            if (outputStr != null) {
                out = connection.getOutputStream();
                out.write(outputStr.getBytes("UTF-8"));
            }
            // flush输出流的缓冲
            out.flush();

            // 流处理
            input = connection.getInputStream();
            inputReader = new InputStreamReader(input, "UTF-8");
            reader = new BufferedReader(inputReader);

            // 方式二 start
            //reader = new BufferedReader(new InputStreamReader(
            //      conn.getInputStream(), "UTF-8"));
            // 方式二 end

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (Exception e) {
            //日志处理等，请自行实现
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (input != null) {
                    input.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (inputReader != null) {
                    inputReader.close();
                }
                if (connection != null)
                {
                    connection.disconnect();
                }
            } catch (Exception e) {
                //日志处理等，请自行实现
//                logger.info(e.toString());
            }
        }
        return jsonObject;
    }
}
