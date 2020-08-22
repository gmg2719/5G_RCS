package com.android.messaging.ui.conversation;

import com.android.messaging.util.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ConversationWebViewJsInject {

    /**
     * Js注入
     * @param url 加载的网页地址
     * @return 注入的js内容，若不是需要适配的网址则返回空javascript
     */
    public static String fullScreenByJs(String url){
        String refer = referParser(url);
        if (null != refer) {
//            return "javascript:document.getElementsByClassName('" + refer + "')[0].addEventListener('click',function(){local_obj.playing();return false;});";
            return "javascript:document.querySelector(\"video\").addEventListener('click',function(){local_obj.playing();return false;});";
        }else {
            return "javascript:";
        }
    }

    /**
     * 对不同的视频网站分析相应的全屏控件
     * @param url 加载的网页地址
     * @return 相应网站全屏按钮的class标识
     */
    public static String referParser(String url){
        LogUtil.i("Junwang", "parse HTML enter");
        if (url.contains("letv")) {
            return "hv_ico_screen";               //乐视Tv
        }else if (url.contains("youku")) {
            return "x-zoomin";                    //优酷
        }else if (url.contains("bilibili")) {
            return "icon-widescreen";             //bilibili
        }else if (url.contains("qq")) {
            return "tvp_fullscreen_button";       //腾讯视频
        }else if(url.contains("video-container link-video")){
            LogUtil.i("Junwang", "新华社内嵌video");
            return "video-container link-video";
        }else if(url.contains("aloading")){
            LogUtil.i("Junwang", "sina内嵌video");
            return "aloading";
        }
        return null;
    }

    public static String parseVideo(String html) {
        if (!html.contains("</video>")) {
            return html;
        }
        String style = "<style>.video-main{position:relative;margin:0;padding:0;height:0;padding-bottom:56.25%}.video-main .img{position:absolute;top:0;left:0;z-index:99;width:100%;height:100%!important}.i-icon.play-icon{position:absolute;top:50%;left:50%;z-index:99;width:80px;height:80px;margin-left:-20px;margin-top:-20px}.i-icon.play-icon:before{background:url(http://resource.qingbao.cn/image/medals/play_icon80.png) no-repeat 0 0;background-size:100% auto;width:48px;height:48px}.i-icon:before{content:\"\";position:absolute;top:0;left:0}</style><script>function videoClick(poster,src){window.videolistener.clickVideo(poster,src)}</script>";
        StringBuilder stringBuffer = new StringBuilder(html);
        stringBuffer.insert(html.indexOf("<head>") + 6, style);
        Document document = Jsoup.parse(stringBuffer.toString());
        Elements elements = document.getElementsByTag("video");
        for (Element element : elements) {
            String poster = element.attr("poster");
            String src = element.attr("src");
            Element elem = document.createElement("div");
            elem.addClass("video-main");
            //
            Element imgElem = document.createElement("img");
            imgElem.addClass("img");
            imgElem.attr("src", poster);
            Element iElem = document.createElement("i");
            iElem.addClass("i-icon play-icon");
            iElem.attr("onclick", "videoClick('" + poster + "','" + src + "')");
            elem.appendChild(imgElem);
            elem.appendChild(iElem);
            element.replaceWith(elem);
        }
        return document.toString();
    }
}
