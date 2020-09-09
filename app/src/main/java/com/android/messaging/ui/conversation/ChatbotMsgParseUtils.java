package com.android.messaging.ui.conversation;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.datamodel.BugleNotifications;
import com.android.messaging.datamodel.ChatbotFavoriteTableUtils;
import com.android.messaging.datamodel.ChatbotInfoTableUtils;
import com.android.messaging.datamodel.MessagingContentProvider;
import com.android.messaging.datamodel.ServerResponse;
import com.android.messaging.datamodel.data.ConversationMessageData;
import com.android.messaging.datamodel.microfountain.sms.RcsContant;
import com.android.messaging.product.ui.WebViewNewsActivity;
import com.android.messaging.ui.chatbotservice.CardContent;
import com.android.messaging.ui.chatbotservice.ChatbotCard;
import com.android.messaging.ui.chatbotservice.ChatbotExtraData;
import com.android.messaging.ui.chatbotservice.ChatbotMultiCard;
import com.android.messaging.ui.chatbotservice.GeneralPurposeCardCarousel;
import com.android.messaging.ui.chatbotservice.SuggestionActionWrapper;
import com.android.messaging.ui.conversation.chatbot.BannerHintView;
import com.android.messaging.ui.conversation.chatbot.ChatbotVideoNewsDetailsActivity;
import com.android.messaging.ui.conversation.chatbot.MultiCardItemDataBean;
import com.android.messaging.ui.conversation.chatbot.vote.VoteListener;
import com.android.messaging.ui.conversation.chatbot.vote.VoteSubView;
import com.android.messaging.ui.conversation.chatbot.vote.VoteView;
import com.android.messaging.util.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yc.cn.ycbannerlib.banner.BannerView;
import com.yc.cn.ycbannerlib.banner.adapter.AbsStaticPagerAdapter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatbotMsgParseUtils {
    public static boolean startParse(Activity activity, LinearLayout contentParent, ConversationMessageData cmd){
        String text = cmd.getText();
        if((text == null) || (text.length() < 1)){
            return false;
        }
        if(text.startsWith("{")){
            if(text.indexOf("generalPurposeCardCarousel") != -1) {
                LogUtil.i("Junwang", "multi card chatbot message");
                if(ParseMultiCardChatbotMsg(text, activity, contentParent, cmd)){
                    return true;
                }
            }else if(text.indexOf("generalPurposeCard") != -1){
                LogUtil.i("Junwang", "single card chatbot message");
                if(ParseSingleCardChatbotMsg(text, activity, contentParent, cmd)){
                    return true;
                }
            }
        }
        return false;
    }

    private static List<MultiCardItemDataBean> getMultiCardItemDataList(CardContent[] cardcontents){
        List<MultiCardItemDataBean> dataBeans = new ArrayList<>();
        if((cardcontents != null) && (cardcontents.length > 0)){
            SuggestionActionWrapper[] saw;
            for(int i=0; i<cardcontents.length; i++){
                LogUtil.i("Junwang", "chatbot cardType="+cardcontents[i].getCardType()+", extraData1="+cardcontents[i].getExtraData1());
                ChatbotExtraData[] ced = cardcontents[i].getExtraData();
                if((ced != null) && (ced.length > 0)){
                    for(int k=0; k<ced.length; k++) {
                        LogUtil.i("Junwang", "extraData["+k+"].itemContent="+ced[k].getItemContent()+", extraData["+k+"].itemCount="+ced[k].getItemCount());
                    }
                }
                saw = cardcontents[i].getSuggestionActionWrapper();
                if((saw != null) && (saw.length > 0)) {
                    for (int j = 0; j < saw.length; i++) {
                        if ((saw[i].action != null) && (saw[i].action.urlAction != null)) {
                            LogUtil.i("Junwang", "chatbot card message url action displayText is " + saw[i].action.displayText + ", url=" + saw[i].action.urlAction.openUrl.url);
                        } else if ((saw[i].action != null) && (saw[i].action.dialerAction != null)) {
                            LogUtil.i("Junwang", "chatbot card message dial action displayText is " + saw[i].action.displayText + ", dialerNumber=" + saw[i].action.dialerAction.dialPhoneNumber.phoneNumber);
                        } else if ((saw[i].action != null) && (saw[i].action.mapAction != null)) {
                            LogUtil.i("Junwang", "chatbot card message map action displayText is " + saw[i].action.displayText);
                        }
                        if (saw[i].reply != null) {
                            LogUtil.i("Junwang", "chatbot card message reply displayText is " + saw[i].reply.displayText + ", postback.data=" + saw[i].reply.postback.data);
                        }
                    }
                }
                dataBeans.add(new MultiCardItemDataBean(cardcontents[i].getTitle(), cardcontents[i].getMedia().getMediaContentType(),
                        cardcontents[i].getMedia().getMediaUrl(), cardcontents[i].getTitle(), cardcontents[i].getDescription(), cardcontents[i].getExtraData1()));
            }
        }
        return dataBeans;
    }

    public static class ImageNormalAdapter extends AbsStaticPagerAdapter {
        List<MultiCardItemDataBean> lists;
        Activity activity;

        public ImageNormalAdapter(List<MultiCardItemDataBean> lists, Activity activity) {
            this.lists = lists;
            this.activity = activity;
        }

        @Override
        public View getView(ViewGroup container, int position) {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            View view = layoutInflater.inflate(R.layout.item_single_product_view, null);
            ImageView img = (ImageView)view.findViewById(R.id.product_image);
            TextView tv = (TextView)view.findViewById(R.id.product_description);
//            tv.setText(lists.get(position).getTitle());
            tv.setText("橙子脐橙新鲜甜伦晚助农水果当季夏橙5斤");
            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(30));//图片圆角为30
            Glide.with(activity).load(lists.get(position).getMediaUr())
                    .apply(options)
                    .into(img);
//            Glide.with(context).load(lists.get(position).getMediaUr())
//                    .centerCrop()
//                    .into(img);

            return view;
        }

        @Override
        public int getCount() {
            return lists.size();
        }
    }

    private static void initBanner(BannerView banner, CardContent[] cardContents, Activity activity, ConversationMessageData cmd) {
        List<MultiCardItemDataBean> lists = getMultiCardItemDataList(cardContents);
        if(lists != null && lists.size()>0) {
            banner.setAdapter(new ImageNormalAdapter(lists, activity));
            banner.setOnBannerClickListener(new BannerView.OnBannerClickListener() {
                @Override
                public void onItemClick(int position) {
                    if(cmd.getmChatbotCardInvalid() ){
                        LogUtil.i("Junwang", "Card is invalid.");
                        popupCardInvalidPrompt(activity);
                    }else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String result = postVoteRequest("http://testxhs.supermms.cn/api/sms5g/my/viewProduct", cmd.getmChatbotRcsdbMsgId(), position+1, false);
                                    ServerResponse sr = new Gson().fromJson(result, ServerResponse.class);
                                    if (sr != null) {
                                        if (sr.getData().getIsValid() != 0) {
                                            LogUtil.i("Junwang", "product recommend card is valid");
                                            WebViewNewsActivity.start(activity, lists.get(position).getExtraData1());
                                        } else {
                                            ChatbotInfoTableUtils.updateChatbotCardInvalidStatus(cmd.getmChatbotRcsdbMsgId());
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(activity, "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                } catch (Exception e) {
                                    LogUtil.i("Junwang", "post Product recommend Request exception " + e.toString());
                                }
                            }
                        }).start();
                    }
//                    WebViewNewsActivity.start(getContext(), lists.get(position).getExtraData1());
//                    ChatbotFavoriteActivity.start(getContext());
                }
            });
            banner.setAnimationDuration(1000);
            banner.setHintGravity(1);
            banner.setHintPadding(0, 30, 0, 0);
            banner.setPlayDelay(3000);
            BannerHintView ihv = new BannerHintView(activity, R.drawable.pagescroll_on, R.drawable.pagescroll_off, 0);
            banner.setHintView(ihv);
//            IconHintView hv = new IconHintView(getContext(), R.drawable.point_focus, R.drawable.point_normal);
//            banner.setHintView(hv);
        }
    }

    private static void loadProductRecommendCard(CardContent[] cardcontents, Activity activity, LinearLayout contentParent, ConversationMessageData cmd){
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.item_card_product_recommend, null);
        ((TextView)view.findViewById(R.id.tv_title)).setText("商品推荐");
        ((ImageView)view.findViewById(R.id.title_image)).setImageResource(R.drawable.icon_shopping);
        BannerView mMultiCardChatbotBanner = view.findViewById(R.id.multicard_chatbot_banner);
        initBanner(mMultiCardChatbotBanner, cardcontents, activity, cmd);
        loadMoreAction(view, cardcontents[0], activity, cmd);
        contentParent.removeAllViews();
        contentParent.addView(view);
        contentParent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            }
        });
        contentParent.setVisibility(View.VISIBLE);
    }

    private static boolean ParseMultiCardChatbotMsg(String text, Activity activity, LinearLayout contentParent, ConversationMessageData cmd){
        try {
            ChatbotMultiCard cbc = new Gson().fromJson(text, ChatbotMultiCard.class);
            GeneralPurposeCardCarousel gpcc = cbc.getMessage().getGeneralPurposeCardCarousel();
            String title = null;
            if(gpcc != null) {
                CardContent[] cardcontents = gpcc.getContent();
                if((cardcontents != null) && (cardcontents.length>0)){
                    loadProductRecommendCard(cardcontents, activity, contentParent, cmd);
                }
            }
        }catch (Exception e){
            LogUtil.i("Junwang", "parse multicard chatbot message exception "+e.toString());
            return false;
        }
        return true;
    }

    private static boolean ParseSingleCardChatbotMsg(String text, Activity activity, LinearLayout contentParent, ConversationMessageData cmd){
        try {
            ChatbotCard cbc = new Gson().fromJson(text, ChatbotCard.class);
            CardContent cc = cbc.getMessage().getGeneralPurposeCard().getContent();
            if(cc != null){
                LogUtil.i("Junwang", "load cardType "+cc.getCardType()+" chatbot view");
                if(loadTypeCard(RcsContant.CardType.valueOf(cc.getCardType()), cbc, cc, activity, contentParent, cmd)){
//                    mH5_content.setVisibility(View.VISIBLE);
//                    mIsCardMsg = true;
                    return true;
                }
            }
        }catch (Exception e){
            LogUtil.i("Junwang", "parse chatbot json exception "+e.toString());
            return false;
        }
//        mIsCardMsg = true;
        return false;
    }

    private static boolean loadTypeCard(RcsContant.CardType type, ChatbotCard cbc, CardContent cc, Activity activity, LinearLayout contentParent, ConversationMessageData cmd){
        switch (type){
            case ACTIVITY_SUB:
                return loadActivitySubscribeChatbotMessage(R.layout.item_card_activity_subscribe, cc, activity, contentParent, cmd);
            case VOTE:
                return /*loadPictureCardChatbotMessage(R.layout.item_chatbot_pic_card, cc);*/loadVoteCardChatbotMessage(R.layout.item_card_vote, cc, activity,contentParent, cmd);
            case VIDEO_NEWS:
                return loadVideoNewsChatbotMessage(R.layout.item_card_chatbot_video_news,cc, activity, contentParent, cmd);
//            case PRODUCT_RECOMMEND:
//                return loadProductRecommendChatbotMessage(cbc.getMessage().getGeneralPurposeCard().getContent());
//            case SUB_ACTIVITY_START:
//                return loadActivitySubscribeStartChatbotMessage(R.layout.item_activity_subscribe_start_card, cc);
//            case PRODUCT_ORDER:
//                return loadOrderInfoPushChatbotMessage(R.layout.item_order_info_push_card, cc);
            default:
//                if(RcsContant.RcsTypes.MIME_IMAGE.equals(cmd.getmContentType())){
//                    return loadPictureCardChatbotMessage(R.layout.item_chatbot_pic_card, cc);
//                }
//                return loadSingelCardChatbotMsg(cbc);
        }
        return false;
    }

    private static boolean loadVideoNewsChatbotMessage(int resource, CardContent cardcontent, Activity activity, LinearLayout contentParent, ConversationMessageData cmd){
        if(cardcontent == null){
            return false;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(resource, null);
        if(view != null) {
            TextView activity_title = (TextView)view.findViewById(R.id.tv_title);
            activity_title.setText("视频快讯");
            ((ImageView)view.findViewById(R.id.title_image)).setImageResource(R.drawable.icon_news);
            loadMoreAction(view, cardcontent, activity, cmd);

            TextView tv_video_title = (TextView)view.findViewById(R.id.video_title);
            tv_video_title.setText(cardcontent.getTitle());

            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(20));//图片圆角为30
            Glide.with(activity).load(cardcontent.getMedia().getThumbnailUrl())
                    .apply(options)
                    .into((ImageView)view.findViewById(R.id.iv_img));
//            Glide.with(this).load(cardcontent.getMedia().getThumbnailUrl())
//                    .centerCrop()
//                    .into((ImageView)view.findViewById(R.id.iv_img));
            contentParent.removeAllViews();
            contentParent.addView(view);
            contentParent.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(cmd.getmChatbotCardInvalid()){
                        LogUtil.i("Junwang", "Card is invalid.");
                        popupCardInvalidPrompt(activity);
                    }else{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("msgId", cmd.getmChatbotRcsdbMsgId());
                                    String result = ChatbotFavoriteTableUtils.postRequest("http://testxhs.supermms.cn/api/sms5g/my/seeVideo", params, "utf-8");
                                    ServerResponse sr = new Gson().fromJson(result, ServerResponse.class);
                                    if(sr != null){
                                        if(sr.getData().getIsValid() != 0){
                                            LogUtil.i("Junwang", "video card is valid");
                                            ChatbotVideoNewsDetailsActivity.start(activity, cardcontent.getMedia().getMediaUrl(),
                                                    cardcontent.getTitle(), cardcontent.getDescription());
//                                            ChatbotFavoriteTableUtils.postRequest("http://testxhs.supermms.cn/api/sms5g/my/seeVideo", params, "utf-8");
                                        }else{
                                            LogUtil.i("Junwang", "video card is invalid");
                                            ChatbotInfoTableUtils.updateChatbotCardInvalidStatus(cmd.getmChatbotRcsdbMsgId());
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(activity, "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }catch (Exception e){
                                    LogUtil.i("Junwang", "postSubscribeRequest exception: "+e.toString());
                                }
                            }
                        }).start();
                    }
                }
            });

            return true;
        }
        return false;
    }

    private static String getVoteRefreshData(String requesturl, String msgId) throws Exception{
        StringBuffer param = new StringBuffer();
        param.append("msgId=").append(msgId);
        //建立连接
        URL url=new URL(requesturl);
        HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();
        //设置参数
        httpConn.setDoOutput(true);     //需要输出
        httpConn.setDoInput(true);      //需要输入
        httpConn.setUseCaches(false);   //不允许缓存
        httpConn.setRequestMethod("POST");      //设置POST方式连接
        //设置请求属性
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setRequestProperty("Charset", "UTF-8");
        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
        httpConn.connect();
        //建立输入流，向指向的URL传入参数
        DataOutputStream dos=new DataOutputStream(httpConn.getOutputStream());
        dos.writeBytes(param.toString());
        dos.flush();
        dos.close();
        //获得响应状态
        int resultCode=httpConn.getResponseCode();
        if(HttpURLConnection.HTTP_OK==resultCode){
            StringBuffer sb=new StringBuffer();
            String readLine=new String();
            BufferedReader responseReader=new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
            while((readLine=responseReader.readLine())!=null){
                sb.append(readLine).append("\n");
            }
            responseReader.close();
//            System.out.println(sb.toString());
            LogUtil.i("Junwang", "getVoteRefreshData response "+sb.toString());
            return sb.toString();
        }
        return null;
    }

    //ps:此函数必须在线程中调用（因为函数抛出异常，调用时要包在try--catch里面）
    private static String postVoteRequest(String requesturl, String msgId, int voteOption, boolean isVoteCard) throws Exception{
        StringBuffer param = new StringBuffer();
        if(isVoteCard) {
            param.append("msgId=").append(msgId).append("&").append("voteOption=").append(voteOption);
        }else{
            param.append("msgId=").append(msgId).append("&").append("option=").append(voteOption);
        }
        //建立连接
        URL url=new URL(requesturl);
        HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();
        //设置参数
        httpConn.setDoOutput(true);     //需要输出
        httpConn.setDoInput(true);      //需要输入
        httpConn.setUseCaches(false);   //不允许缓存
        httpConn.setRequestMethod("POST");      //设置POST方式连接
        //设置请求属性
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setRequestProperty("Charset", "UTF-8");
        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
        httpConn.connect();
        //建立输入流，向指向的URL传入参数
        DataOutputStream dos=new DataOutputStream(httpConn.getOutputStream());
        dos.writeBytes(param.toString());
        dos.flush();
        dos.close();
        //获得响应状态
        int resultCode=httpConn.getResponseCode();
        if(HttpURLConnection.HTTP_OK==resultCode){
            StringBuffer sb=new StringBuffer();
            String readLine=new String();
            BufferedReader responseReader=new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
            while((readLine=responseReader.readLine())!=null){
                sb.append(readLine).append("\n");
            }
            responseReader.close();
//            System.out.println(sb.toString());
            LogUtil.i("Junwang", "get postVoteRequest response "+sb.toString());
            return sb.toString();
        }
        return null;
    }

    private static void initVoteView(View view, CardContent cardcontent, ConversationMessageData cmd, Activity activity){
        final VoteView voteView = (VoteView)view.findViewById(R.id.vote_view);
        final TextView tvVoteAction = (TextView)view.findViewById(R.id.vote_action);
        final TextView tvRefresh = (TextView)view.findViewById(R.id.refresh_votelist);
        final TextView tvVoteTitle = (TextView)view.findViewById(R.id.vote_title);
        final TextView tvVoteDescription = (TextView)view.findViewById(R.id.vote_description);
        ((TextView)view.findViewById(R.id.tv_title)).setText("参与投票");

        LinkedHashMap<String, Integer> voteData = new LinkedHashMap<>();

        if(cardcontent != null) {
            ChatbotExtraData[] ced = cardcontent.getExtraData();
            if(ced != null && ced.length>0){
                for(int i=0; i<ced.length; i++){
                    voteData.put(ced[i].getItemContent(), ced[i].getItemCount());
                }
            }
        }
        voteView.initVote(voteData);
        tvRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String jsonString = getVoteRefreshData("http://testback.stvision.cn/xinhua/sms5g/my/getVoteResult", cmd.getmChatbotRcsdbMsgId());
                            if (jsonString != null) {
                                ConversationMessageView.VoteData temp = new GsonBuilder().setLenient().create().fromJson(jsonString, ConversationMessageView.VoteData.class);
                                LinkedHashMap<String, Integer> voteData1 = new LinkedHashMap<>();
                                ChatbotExtraData[] ced1 = temp.getData();
                                if(ced1 != null && ced1.length>0){
                                    for(int i=0; i<ced1.length; i++){
                                        voteData1.put(ced1[i].getItemContent(), ced1[i].getItemCount());
                                        LogUtil.i("Junwang", "get refresh data " + ced1[i].getItemContent()+" : " + ced1[i].getItemCount());
                                    }
                                }
                                try{
                                    activity.runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            voteView.resetNumbers(voteData1);
                                            voteView.notifyUpdateChildren(voteView.selectView, true, true);
                                        }
                                    });
                                }catch(Exception e){
                                    LogUtil.i("Junwang", "refresh voteview exception "+e.toString());
                                }
                            }
                        }catch (Exception e){
                            LogUtil.i("Junwang", "getVoteRefreshData exception "+e.toString());
                        }
                    }
                }).start();
            }
        });
        if(cmd.getIsChatbotVoted()){
            voteView.setAnimationRate(600);
//            tvVoteTitle.setText(cardcontent.getTitle());
//            tvVoteDescription.setText(cardcontent.getDescription());
            tvVoteAction.setBackgroundResource(R.drawable.border_textview_gray);
            tvVoteAction.setText("已投票");
            tvRefresh.setVisibility(View.VISIBLE);
            VoteSubView vsv;
            int position = 0;
            for(int i=0; i<voteView.getChildCount(); i++){
                if(voteView.getChildAt(i) instanceof VoteSubView){
                    vsv = (VoteSubView)voteView.getChildAt(i);
                    if(position == cmd.getChatbotVotedItemPosition()){
                        if(vsv != null){
                            LogUtil.i("Junwang", "voted item position is "+position);
                            voteView.selectView = vsv;
                            voteView.notifyUpdateChildren(vsv, true, false);
                            return;
                        }
                    }else{
                        LogUtil.i("Junwang", "Not voted item position is "+position);
                    }
                    position++;
                }
            }
        }else{
            voteView.setAnimationRate(600);
            tvVoteTitle.setText(cardcontent.getTitle());
            tvVoteDescription.setText(cardcontent.getDescription());

            tvVoteAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cmd.getmChatbotCardInvalid() ){
                        LogUtil.i("Junwang", "Card is invalid.");
                        popupCardInvalidPrompt(activity);
                    }else{
                        if(voteView.selectView != null) {
                            LogUtil.i("Junwang", "vote status="+cmd.getIsChatbotVoted());
                            if(cmd.getIsChatbotVoted()) {
                                return;
                            }
                            voteView.notifyUpdateChildren(voteView.selectView, true, false);
                            tvRefresh.setVisibility(View.VISIBLE);
                            tvVoteAction.setBackgroundResource(R.drawable.border_textview_gray);
                            tvVoteAction.setText("已投票");

                            BugleNotifications.markMessageAsVotedStatus(cmd.getmChatbotRcsdbMsgId(), voteView.selectedItemPosition);
                            cmd.setmChatbotVoteStatus(true);
                            cmd.setmChatbotVotedItemPosition(voteView.selectedItemPosition);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String result = postVoteRequest("http://testxhs.supermms.cn/api/sms5g/my/clickVote", cmd.getmChatbotRcsdbMsgId(), voteView.selectedItemPosition + 1, true);
                                        ServerResponse sr = new Gson().fromJson(result, ServerResponse.class);
                                        if(sr != null){
                                            if(sr.getData().getIsValid() != 0){
                                                LogUtil.i("Junwang", "vote card is valid");
                                            }else{
                                                ChatbotInfoTableUtils.updateChatbotCardInvalidStatus(cmd.getmChatbotRcsdbMsgId());
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(activity, "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }catch (Exception e){
                                        LogUtil.i("Junwang", "post Vote Request exception "+e.toString());
                                    }
                                }
                            }).start();
                        }
                    }
                }
            });
            voteView.setVoteListener(new VoteListener() {
                @Override
                public boolean onItemClick(View view, int index, boolean status) {
                    LogUtil.i("Junwang", "not clicked on vote item.");
                    voteView.notifyUpdateBorder(view, status);
                    return true;
                }
            });
        }
    }

    private static boolean loadVoteCardChatbotMessage(int resource, CardContent cardcontent, Activity activity, LinearLayout contentParent, ConversationMessageData cmd){
//        List<String> voteList = new ArrayList<>();

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(resource, null);
//        view.setMinimumWidth(300);
        if(view != null) {
            LogUtil.i("Junwang", "loadVoteCardChatbotMessage");
            ((ImageView)view.findViewById(R.id.title_image)).setImageResource(R.drawable.icon_vote);
            initVoteView(view, cardcontent, cmd, activity);
            loadMoreAction(view, cardcontent, activity, cmd);
            contentParent.removeAllViews();
            contentParent.addView(view);

            contentParent.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                }
            });
            return true;
        }
        return false;
    }

    private static void showAnimation(View popView) {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
        alphaAnimation.setDuration(300);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setDuration(300);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        popView.startAnimation(animationSet);
    }

    /**
     * 从底部弹出popupwindow
     */
    private static void showBottomPop(View parent, CardContent cardContent, Activity activity, ConversationMessageData cmd) {
        final View popView = View.inflate(activity, R.layout.popup_more_menu, null);
        showAnimation(popView);//开启动画
        PopupWindow mPopWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        clickPopItem(popView, mPopWindow);//条目的点击
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopWindow.showAtLocation(parent,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.update();
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        activity.getWindow().setAttributes(lp);
        ((TextView)popView.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        TextView delete = ((TextView)popView.findViewById(R.id.deleteMsg));
        delete.setText("移除收藏");
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                DeleteMessageAction.deleteMessage(cmd.getMessageId());
//                ChatbotFavoriteDetailsActivity.refresh(activity);
                MessagingContentProvider.notifyChatbotFavoritesChanged();
                ChatbotFavoriteTableUtils.deleteChatbotFavoriteInfo(null, cmd.getMessageId());
                mPopWindow.dismiss();
                activity.finish();
            }
        });
        ((TextView)popView.findViewById(R.id.insertFav)).setVisibility(View.GONE);
//        ((TextView)popView.findViewById(R.id.insertFav)).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH)+1;
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//                String date = year+"/"+month+"/"+day;
//                LogUtil.i("Junwang", "date="+date);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(ChatbotFavoriteTableUtils.queryIsExistInChatbotFavorite(cmd.getMessageId())){
////                            Toast.makeText(getContext(), "之前已经添加到收藏夹了", Toast.LENGTH_LONG);
//                        }else {
//                            String imageUrl = null;
//                            if("video/mp4".equals(cardContent.getMedia().getMediaContentType())){
//                                imageUrl = cardContent.getMedia().getThumbnailUrl();
//                            }else{
//                                imageUrl = cardContent.getMedia().getMediaUrl();
//                            }
//                            String fav_name = null;
//                            String fav_logo = null;
//                            String content_pre = "";
//                            String sip = cmd.getSenderNormalizedDestination();
//                            if(sip != null && sip.startsWith("sip")) {
//                                LogUtil.i("Junwang", "query sip="+sip);
//                                ChatbotFavoriteEntity cfe = ChatbotInfoTableUtils.getChatbotInfo(sip);
//                                if(cfe != null) {
//                                    fav_name = cfe.getChatbot_fav_name();
//                                    fav_logo = cfe.getChatbot_fav_logo();
//                                }
//                            }
//                            int cardType = cardContent.getCardType();
//                            switch (cardType){
//                                case 1:
//                                    content_pre = "[活动订阅]";
//                                    break;
//                                case 2:
//                                    content_pre = "[参与投票]";
//                                    break;
//                                case 3:
//                                    content_pre = "[视频快讯]";
//                                    break;
//                                case 4:
//                                    content_pre = "[商品推荐]";
//                                    break;
//                                default:
//                                    content_pre = "[活动订阅]";
//                            }
//                            ChatbotFavoriteTableUtils.insertChatbotFavoriteTable(cmd.getSenderNormalizedDestination(), fav_name, fav_logo,
//                                    content_pre+cardContent.getTitle(), imageUrl, date, null, cmd.getMessageId(), cmd.getConversationId());
//                        }
//                    }
//                }).start();
//                mPopWindow.dismiss();
//            }
//        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
    }

    private static void loadMoreAction(View view, CardContent cardContent, Activity activity, ConversationMessageData cmd){
        TextView tvMoreAction = (TextView)view.findViewById(R.id.more_action);
        tvMoreAction.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LogUtil.i("Junwang", "click ... more action");
                showBottomPop(view, cardContent, activity, cmd);
            }
        });
    }

    private static void popupCardInvalidPrompt(Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void updateSubscribeButton(LinearLayout contentParent){
        TextView tv = contentParent.getChildAt(0).findViewById(R.id.action_button);
        tv.setText("已预约");
        tv.setTextColor(Color.parseColor("#858898"));
        tv.setBackgroundResource(R.drawable.border_textview_gray);
    }

    private static boolean loadActivitySubscribeChatbotMessage(int resource, CardContent cardcontent, Activity activity, LinearLayout contentParent, ConversationMessageData cmd){
        if(cardcontent == null){
            return false;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(resource, null);
        if(view != null) {
            TextView dateView = (TextView)view.findViewById(R.id.dateView);
            TextView activity_title = (TextView)view.findViewById(R.id.activity_title);
//            activity_title.setText(cardcontent.getTitle());
            ((ImageView)view.findViewById(R.id.title_image)).setImageResource(R.drawable.icon_order);

//            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCorners(20));//图片圆角为30
            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(20));//图片圆角为30

            Glide.with(activity).load(cardcontent.getMedia().getMediaUrl())
                    .apply(options)
                    .into((ImageView)view.findViewById(R.id.activity_image));
//                ((Button)findViewById(R.id.action_button)).setText(cardcontent.getSuggestionActionWrapper().);
//                GlideUtils.load(getContext(), cardcontent.getMedia().getMediaUrl(), (ImageView) findViewById(R.id.activity_image));
            if(cardcontent.getExtraData1() != null) {
                dateView.setText(cardcontent.getExtraData1());
            }
            loadMoreAction(view, cardcontent, activity, cmd);
            contentParent.removeAllViews();
            contentParent.addView(view);
            if(cmd.getIsChatbotSubscribed()){
                updateSubscribeButton(contentParent);
            }else {
                ((TextView)view.findViewById(R.id.action_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateSubscribeButton(contentParent);
                        BugleNotifications.markMessageAsSubscribedStatus(cmd.getmChatbotRcsdbMsgId());
                        if(cmd.getmChatbotCardInvalid()){
                            LogUtil.i("Junwang", "Card is invalid.");
                            popupCardInvalidPrompt(activity);
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("msgId", cmd.getmChatbotRcsdbMsgId());
                                        String result = ChatbotFavoriteTableUtils.postRequest("http://testxhs.supermms.cn/api/sms5g/my/doAppoint", params, "utf-8");
                                        ServerResponse sr = new Gson().fromJson(result, ServerResponse.class);
                                        if(sr != null){
                                            if(sr.getData().getIsValid() != 0){
                                                LogUtil.i("Junwang", "subscribe card is valid");
                                            }else{
                                                LogUtil.i("Junwang", "subscribe card is invalid");
                                                ChatbotInfoTableUtils.updateChatbotCardInvalidStatus(cmd.getmChatbotRcsdbMsgId());
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(activity, "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }catch (Exception e){
                                        LogUtil.i("Junwang", "postSubscribeRequest exception: "+e.toString());
                                    }
                                }
                            }).start();
                        }
                    }
                });
            }

            return true;
        }
        return false;
    }
}
