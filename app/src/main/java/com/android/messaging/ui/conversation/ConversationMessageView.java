/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.messaging.ui.conversation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.text.util.LinkifyCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.android.messaging.Factory;
import com.android.messaging.R;
import com.android.messaging.datamodel.BugleNotifications;
import com.android.messaging.datamodel.ChatbotFavoriteTableUtils;
import com.android.messaging.datamodel.ChatbotInfoTableUtils;
import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseHelper;
import com.android.messaging.datamodel.ServerResponse;
import com.android.messaging.datamodel.action.DeleteMessageAction;
import com.android.messaging.datamodel.data.BusinessCardService;
import com.android.messaging.datamodel.data.CardTemplate;
import com.android.messaging.datamodel.data.ConversationMessageData;
import com.android.messaging.datamodel.data.FloatingWebView;
import com.android.messaging.datamodel.data.MessageData;
import com.android.messaging.datamodel.data.MessagePartData;
import com.android.messaging.datamodel.data.SubscriptionListData.SubscriptionListEntry;
import com.android.messaging.datamodel.media.ImageRequestDescriptor;
import com.android.messaging.datamodel.media.MessagePartImageRequestDescriptor;
import com.android.messaging.datamodel.media.UriImageRequestDescriptor;
import com.android.messaging.datamodel.microfountain.sms.RcsContant;
import com.android.messaging.datamodel.microfountain.sms.SendRcsMsgUtils;
import com.android.messaging.datamodel.microfountain.sms.database.RcsDatabaseUtils;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageEntity;
import com.android.messaging.product.entity.News;
import com.android.messaging.product.ui.WebViewNewsActivity;
import com.android.messaging.product.utils.GlideUtils;
import com.android.messaging.product.utils.TimeUtils;
import com.android.messaging.product.utils.UIUtils;
import com.android.messaging.sms.MmsUtils;
import com.android.messaging.ui.AsyncImageView;
import com.android.messaging.ui.AsyncImageView.AsyncImageViewDelayLoader;
import com.android.messaging.ui.AudioAttachmentView;
import com.android.messaging.ui.ContactIconView;
import com.android.messaging.ui.ConversationDrawables;
import com.android.messaging.ui.MultiAttachmentLayout;
import com.android.messaging.ui.MultiAttachmentLayout.OnAttachmentClickListener;
import com.android.messaging.ui.PersonItemView;
import com.android.messaging.ui.UIIntents;
import com.android.messaging.ui.VideoThumbnailView;
import com.android.messaging.ui.appsettings.H5WLDatabaseHelper;
import com.android.messaging.ui.chatbotservice.CardContent;
import com.android.messaging.ui.chatbotservice.CardLayout;
import com.android.messaging.ui.chatbotservice.ChatBotCardViewAdapter;
import com.android.messaging.ui.chatbotservice.ChatBotDataBean;
import com.android.messaging.ui.chatbotservice.ChatbotCard;
import com.android.messaging.ui.chatbotservice.ChatbotExtraData;
import com.android.messaging.ui.chatbotservice.ChatbotMultiCard;
import com.android.messaging.ui.chatbotservice.GeneralPurposeCardCarousel;
import com.android.messaging.ui.chatbotservice.ResponseChatbot;
import com.android.messaging.ui.chatbotservice.SuggestionAction;
import com.android.messaging.ui.chatbotservice.SuggestionActionWrapper;
import com.android.messaging.ui.conversation.chatbot.BannerHintView;
import com.android.messaging.ui.conversation.chatbot.ChatbotFavoriteEntity;
import com.android.messaging.ui.conversation.chatbot.ChatbotVideoNewsDetailsActivity;
import com.android.messaging.ui.conversation.chatbot.MultiCardItemDataBean;
import com.android.messaging.ui.conversation.chatbot.MultiCardItemViewAdapter;
import com.android.messaging.ui.conversation.chatbot.vote.VoteListener;
import com.android.messaging.ui.conversation.chatbot.vote.VoteSubView;
import com.android.messaging.ui.conversation.chatbot.vote.VoteView;
import com.android.messaging.ui.conversation.santilayout.ExpandLayout;
import com.android.messaging.ui.conversation.santilayout.ExpandListViewAdapter;
import com.android.messaging.ui.conversation.santilayout.expandableview.ExpandableTextView;
import com.android.messaging.ui.santiwebview.SantiWebChromeClient;
import com.android.messaging.ui.santiwebview.SantiWebViewClient;
import com.android.messaging.util.AccessibilityUtil;
import com.android.messaging.util.Assert;
import com.android.messaging.util.AvatarUriUtil;
import com.android.messaging.util.BuglePrefs;
import com.android.messaging.util.ContentType;
import com.android.messaging.util.ImageUtils;
import com.android.messaging.util.LogUtil;
import com.android.messaging.util.OsUtil;
import com.android.messaging.util.PhoneUtils;
import com.android.messaging.util.UiUtils;
import com.android.messaging.util.YouTubeUtil;
import com.android.messaging.videoplayer.ijk.IjkPlayer;
import com.android.messaging.videoplayer.listener.OnVideoViewStateChangeListener;
import com.android.messaging.videoplayer.player.DanmuVideoView;
import com.android.messaging.videoplayer.player.PlayerFactory;
import com.android.messaging.videoplayer.ui.StandardVideoController;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;
import com.yc.cn.ycbannerlib.banner.BannerView;
import com.yc.cn.ycbannerlib.banner.adapter.AbsStaticPagerAdapter;
import com.yhao.floatwindow.MoveType;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.functions.Consumer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

//import com.huawei.hms.support.api.pay.PayResult;

//import static com.baidu.platform.comapi.util.UiThreadUtil.runOnUiThread;

/**
 * The view for a single entry in a conversation.
 */
public class ConversationMessageView extends FrameLayout implements View.OnClickListener,
        View.OnLongClickListener, OnAttachmentClickListener {
    public interface ConversationMessageViewHost {
        boolean onAttachmentClick(ConversationMessageView view, MessagePartData attachment,
                                  Rect imageBounds, boolean longPress);
        SubscriptionListEntry getSubscriptionEntryForSelfParticipant(String selfParticipantId,
                                                                     boolean excludeDefault);
    }

    private final ConversationMessageData mData;

    private LinearLayout mMessageAttachmentsView;
    private MultiAttachmentLayout mMultiAttachmentView;
    private AsyncImageView mMessageImageView;
    private TextView mMessageTextView;
    //add by junwang start
    private static final long MAX_INTERVAL_TIME = 3*24*3600;
    public PowerWebView mMessageWebView;
    private HorizontalScrollView mHorizontalSV;
    public LinearLayout mLL_webview_container;
    private LinearLayout mH5_content;
    private SantiVideoView vv_video;
    private boolean mIsH5Expand;
    private static ArrayList<String> mWebUrls;
    private String mSelectedMessageId;
    private ListView mWebView_LV;
    private ViewPager mViewPager;
    private WebViewPagerAdapter mWVPagerAdapter;
    private List<View> mViewList;
    private TextView mTempTextView;
    private boolean mHasWebLinks;
    private int mWebViewWidth;
    private MapView mMapView;
    private Button mLocationButton;
    private Button mVivoPayButton;
    private long mIntervalTime;
    private String mUrl;
    private LoadUrl mLoadUrl;
    private int mH5TriggerCode;
    private String mH5Matcher;
    private String mH5ResponseUrl;
    private int mScreenWidth;
    private int mScreenHeight;
    private static boolean mIsFirstTimeEntry = true;
    private static String mLatestUrl = null;
    private Button mFloatingButton = null;
    private int mContentHeight;
    private boolean mUpdatedText;
    private ExpandLayout mExpand;
    private RelativeLayout mLLTagBtn;
    private ListView mExpand_list;
    private ImageView mExpand_iv;
    private boolean mIsCardMsg;
    private RecyclerView mChatbotRV;
    private TextView mChatbotTime;
    private BannerView mMultiCardChatbotBanner;
    //add by junwang end
    private boolean mMessageTextHasLinks;
    private boolean mMessageHasYouTubeLink;
    private TextView mStatusTextView;
    private TextView mTitleTextView;
    private TextView mMmsInfoTextView;
    private LinearLayout mMessageTitleLayout;
    private TextView mSenderNameTextView;
    private ContactIconView mContactIconView;
    private ConversationMessageBubbleView mMessageBubble;
    private View mSubjectView;
    private TextView mSubjectLabel;
    private TextView mSubjectText;
    private View mDeliveredBadge;
    private ViewGroup mMessageMetadataView;
    private ViewGroup mMessageTextAndInfoView;
    private TextView mSimNameView;

    private boolean mOneOnOne;
    private ConversationMessageViewHost mHost;

    //add by junwang for action type
    public static final int OPEN_LOCATION = 0;// 打开定位
    public static final int CALL_PHONE = 1;// 拨打电话
    public static final int SDK_PAY_FLAG = 2; //支付宝支付
//    String GETHTML_SCRIPT = "javascript:window.local_obj.getHTML" +
//            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
    String GETHTML_SCRIPT = "javascript:window.local_obj.getHTML" +
            "(document.documentElement.innerHTML);";
    String ADD_CLICKLISTENERONVIDEO_SCRIPT = "javascript:document.querySelector('video').addEventListener('click',function(){local_obj.playing();return false;});";
    String ADD_ONCLICKLISTENER_SCRIPT = "javascript:document.getElementsByClassName('aplayer').addEventListener('click',function(){local_obj.playing(); return false;});";
    String TEST_SCRIPT = "javascript:document.querySelector('video').play();";
    String TEST_URL1 = "file:///android_res/raw/test1.html";
    String TEST_URL2 = "file:///android_res/raw/test2.html";
    String TEST_URL3 = "file:///android_res/raw/test3.html";
    String TEST_URL4 = "file:///android_res/raw/test4.html";
    String TEST_URL5 = "file:///android_res/raw/test5.html";
    String TEST_URL6 = "file:///android_res/raw/test6.html";
    String TEST_URL7 = "file:///android_res/raw/test7.html";
    String TEST_URL8 = "file:///android_res/raw/test8.html";
    String TEST_URL9 = "file:///android_res/raw/test9.html";
    String TEST_URL10 = "file:///android_res/raw/test10.html";
    String TEST_URL11= "file:///android_res/raw/test11.html";
    String TEST_URL12 = "file:///android_res/raw/test12.html";
    String TEST_URL13 = "file:///android_res/raw/test13.html";
    String TEST_URL14 = "file:///android_res/raw/test14.html";
    String TEST_URL15 = "file:///android_res/raw/test15.html";

    //add by junwang
    private boolean mIsContactInWhiteList;

    public ConversationMessageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        // TODO: we should switch to using Binding and DataModel factory methods.
        mData = new ConversationMessageData();
    }

    //add by junwang
    public boolean isContactInWebViewWhiteList(Context context){
        H5WLDatabaseHelper helper = new H5WLDatabaseHelper(context, DatabaseHelper.DATABASE_NAME, null, 3);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        cursor =  cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE codenumber=?", new String[]{mData.getSenderNormalizedDestination()});
        if(cursor != null && cursor.getCount() > 0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public String getConversationText(){
        if(mData != null){
            return mData.getText();
        }
        return null;
    }

    @Override
    protected void onFinishInflate() {
        mContactIconView = (ContactIconView) findViewById(R.id.conversation_icon);
        mContactIconView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                ConversationMessageView.this.performLongClick();
                return true;
            }
        });

        mMessageAttachmentsView = (LinearLayout) findViewById(R.id.message_attachments);
        mMultiAttachmentView = (MultiAttachmentLayout) findViewById(R.id.multiple_attachments);
        mMultiAttachmentView.setOnAttachmentClickListener(this);

        mMessageImageView = (AsyncImageView) findViewById(R.id.message_image);
        mMessageImageView.setOnClickListener(this);
//        mMessageImageView.setOnLongClickListener(this);
//        mMessageImageView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View arg0) {
//                Bitmap bitmap = ((BitmapDrawable)mMessageImageView.getDrawable()).getBitmap();
//                Result ret = parsePic(bitmap);
//                if (null == ret) {
//                    Toast.makeText(getActivityFromView(mMessageImageView), "解析结果：null",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getActivityFromView(mMessageImageView),
//                            "解析结果：" + ret.toString(), Toast.LENGTH_LONG).show();
//                }
//                return false;
//            }
//        });

        mMessageTextView = (TextView) findViewById(R.id.message_text);
        mMessageTextView.setOnClickListener(this);
        IgnoreLinkLongClickHelper.ignoreLinkLongClick(mMessageTextView, this);

        //add by junwang
        mMessageWebView =(PowerWebView)findViewById(R.id.message_webview);
//        mViewPager = (ViewPager)findViewById(R.id.vp_webview);
        mHorizontalSV = (HorizontalScrollView)findViewById(R.id.horizontalScrollView);
        mLL_webview_container = (LinearLayout)findViewById(R.id.hsv_webview_container);
        mH5_content = (LinearLayout)findViewById(R.id.h5_content);
        mChatbotRV = (RecyclerView)findViewById(R.id.chatbot_rv);
        mChatbotTime = (TextView)findViewById(R.id.chatbot_tv_time);
        mUpdatedText = false;
        //mTempTextView = (TextView)findViewById(R.id.temp_textview);
        mIsContactInWhiteList = ConversationFragment.isContactInWebViewWhiteList(mMessageWebView.getContext())
            || ConversationFragment.isPhoneNumberInWebViewWhiteList(mMessageWebView.getContext());
        mMapView = (MapView)findViewById(R.id.map_view);
        mLocationButton = (Button)findViewById(R.id.location_button);
//        mIsFirstTimeEntry = true;
//        mLatestUrl = null;
        mLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeButtonMenu();
//                mMessageWebView.getContext().startActivity(new Intent(mMessageWebView.getContext(), BaiduMapTestActivity.class));
                mMessageWebView.getContext().startActivity(new Intent(mMessageWebView.getContext(), FullScreenVideoPlayActivity.class));
            }
        });
        //mLoadUrl = new LoadUrl();
        WindowManager manager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        mVivoPayButton = (Button)findViewById(R.id.vivopay_button);
        /*mVivoPayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //mVivoPayButton.getContext().startActivity(new Intent(mMessageWebView.getContext(), PayActivity.class));
                mVivoPayButton.getContext().startActivity(new Intent(mMessageWebView.getContext(), MiPayMainActivity.class));
//               mMessageWebView.loadUrl("file:///android_res/raw/test3.html");
//                mMessageWebView.loadUrl("file:///android_res/raw/test1.html");
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wxpay.wxutil.com/mch/pay/h5.v2.php"));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mMessageWebView.getContext().startActivity(intent);
            }
        });*/

        mStatusTextView = (TextView) findViewById(R.id.message_status);
        mTitleTextView = (TextView) findViewById(R.id.message_title);
        mMmsInfoTextView = (TextView) findViewById(R.id.mms_info);
        mMessageTitleLayout = (LinearLayout) findViewById(R.id.message_title_layout);
        mSenderNameTextView = (TextView) findViewById(R.id.message_sender_name);
        mMessageBubble = (ConversationMessageBubbleView) findViewById(R.id.message_content);
        mSubjectView = findViewById(R.id.subject_container);
        mSubjectLabel = (TextView) mSubjectView.findViewById(R.id.subject_label);
        mSubjectText = (TextView) mSubjectView.findViewById(R.id.subject_text);
        mDeliveredBadge = findViewById(R.id.smsDeliveredBadge);
        mMessageMetadataView = (ViewGroup) findViewById(R.id.message_metadata);
        mMessageTextAndInfoView = (ViewGroup) findViewById(R.id.message_text_and_info);
        mSimNameView = (TextView) findViewById(R.id.sim_name);
        //add by junwang
//        Boolean isRCSSub = RcsKit.onUserActionsDetectedForRcsSubscription(SubscriptionManager.getDefaultDataSubscriptionId());
//        LogUtil.i("Junwang", "ConversationMessageView isRCSSub = "+isRCSSub);
//        if(isRCSSub){
////            registerRCSReceiver();
//        }
//        RcsSubscriptionManager.addOnSubscriptionsChangedListener(new RcsSubscriptionManager.OnSubscriptionsChangedListener(){
//            @Override
//            public void onRcsSubscriptionsChanged() {
//                RcsSubscriptionInfo rsi = RcsSubscriptionManager.getRcsSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId());
//                LogUtil.i("Junwang", "ProvisioningStatus="+rsi.getProvisioningStatus()+", SubscriptionStatus="+rsi.getSubscriptionStatus());
//            }
//        });
    }

    public Result parsePic(Bitmap bitmap) {
        // 解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        // 新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        int lWidth = bitmap.getWidth();
        int lHeight = bitmap.getHeight();
        int[] lPixels = new int[lWidth * lHeight];
        bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(lWidth,
                lHeight, lPixels);
        // 将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                rgbLuminanceSource));
        // 初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        // 开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int horizontalSpace = MeasureSpec.getSize(widthMeasureSpec);
        final int iconSize = getResources()
                .getDimensionPixelSize(R.dimen.conversation_message_contact_icon_size);

        final int unspecifiedMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int iconMeasureSpec = MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY);

        mContactIconView.measure(iconMeasureSpec, iconMeasureSpec);

        final int arrowWidth = 0;
//                getResources().getDimensionPixelSize(R.dimen.message_bubble_arrow_width);

        // We need to subtract contact icon width twice from the horizontal space to get
        // the max leftover space because we want the message bubble to extend no further than the
        // starting position of the message bubble in the opposite direction.
        final int maxLeftoverSpace = horizontalSpace - mContactIconView.getMeasuredWidth() * 2
                - arrowWidth - getPaddingLeft() - getPaddingRight();
        final int messageContentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(maxLeftoverSpace,
                MeasureSpec.AT_MOST);

        mMessageBubble.measure(messageContentWidthMeasureSpec, unspecifiedMeasureSpec);

        final int maxHeight = Math.max(mContactIconView.getMeasuredHeight(),
                mMessageBubble.getMeasuredHeight());
        setMeasuredDimension(horizontalSpace, maxHeight + getPaddingBottom() + getPaddingTop());
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right,
                            final int bottom) {
        final boolean isRtl = AccessibilityUtil.isLayoutRtl(this);

        final int iconWidth = mContactIconView.getMeasuredWidth();
        final int iconHeight = mContactIconView.getMeasuredHeight();
        final int iconTop = getPaddingTop();
        final int contentWidth = (right -left) - iconWidth - getPaddingLeft() - getPaddingRight();
        final int contentHeight = mMessageBubble.getMeasuredHeight();
        final int contentTop = iconTop;

        final int iconLeft;
        final int contentLeft;
        if (mData.getIsIncoming()) {
            if (isRtl) {
                iconLeft = (right - left) - getPaddingRight() - iconWidth;
                contentLeft = iconLeft - contentWidth;
            } else {
                iconLeft = getPaddingLeft();
                contentLeft = iconLeft + iconWidth;
            }
        } else {
            if (isRtl) {
                iconLeft = getPaddingLeft();
                contentLeft = iconLeft + iconWidth;
            } else {
                iconLeft = (right - left) - getPaddingRight() - iconWidth;
                contentLeft = iconLeft - contentWidth;
            }
        }

        mContactIconView.layout(iconLeft, iconTop, iconLeft + iconWidth, iconTop + iconHeight);

        mMessageBubble.layout(contentLeft, contentTop, contentLeft + contentWidth,
                contentTop + contentHeight);
        mContentHeight = contentHeight;
        LogUtil.i("Junwang", "iconLeft="+iconLeft+", iconWidth="+iconWidth+",contentLeft="+contentLeft+", content_height="+contentHeight);

    }

    /**
     * Fills in the data associated with this view.
     *
     * @param cursor The cursor from a MessageList that this view is in, pointing to its entry.
     */
    public void bind(final Cursor cursor) {
        bind(cursor, true, null);
    }

    /**
     * Fills in the data associated with this view.
     *
     * @param cursor The cursor from a MessageList that this view is in, pointing to its entry.
     * @param oneOnOne Whether this is a 1:1 conversation
     */
    public void bind(final Cursor cursor,
                     final boolean oneOnOne, final String selectedMessageId) {
        mOneOnOne = oneOnOne;
        //add by junwang for test
        mSelectedMessageId = selectedMessageId;
        // Update our UI model
        mData.bind(cursor);
        setSelected(TextUtils.equals(mData.getMessageId(), selectedMessageId));

        // Update text and image content for the view.
        updateViewContent();

        // Update colors and layout parameters for the view.
        //add by junwang
        if(!isH5Message(mData.getJson()) && !isMapView() && !mIsCardMsg) {
            updateViewAppearance();
        }else{
            setPadding(15, 30, 0, 0);
        }

        updateContentDescription();
    }

    public void setHost(final ConversationMessageViewHost host) {
        mHost = host;
    }

    /**
     * Sets a delay loader instance to manage loading / resuming of image attachments.
     */
    public void setImageViewDelayLoader(final AsyncImageViewDelayLoader delayLoader) {
        Assert.notNull(mMessageImageView);
        mMessageImageView.setDelayLoader(delayLoader);
        mMultiAttachmentView.setImageViewDelayLoader(delayLoader);
    }

    public ConversationMessageData getData() {
        return mData;
    }

    /**
     * Returns whether we should show simplified visual style for the message view (i.e. hide the
     * avatar and bubble arrow, reduce padding).
     */
    private boolean shouldShowSimplifiedVisualStyle() {
        return mData.getCanClusterWithPreviousMessage();
    }

    /**
     * Returns whether we need to show message bubble arrow. We don't show arrow if the message
     * contains media attachments or if shouldShowSimplifiedVisualStyle() is true.
     */
    private boolean shouldShowMessageBubbleArrow() {
        return !shouldShowSimplifiedVisualStyle()
                && !(mData.hasAttachments() || mMessageHasYouTubeLink);
    }

    /**
     * Returns whether we need to show a message bubble for text content.
     */
    private boolean shouldShowMessageTextBubble() {
        if (mData.hasText()) {
            return true;
        }
        final String subjectText = MmsUtils.cleanseMmsSubject(getResources(),
                mData.getMmsSubject());
        if (!TextUtils.isEmpty(subjectText)) {
            return true;
        }
        return false;
    }

    private void updateViewContent() {
        updateMessageContent();
        int titleResId = -1;
        int statusResId = -1;
        String statusText = null;
        switch(mData.getStatus()) {
            case MessageData.BUGLE_STATUS_INCOMING_AUTO_DOWNLOADING:
            case MessageData.BUGLE_STATUS_INCOMING_MANUAL_DOWNLOADING:
            case MessageData.BUGLE_STATUS_INCOMING_RETRYING_AUTO_DOWNLOAD:
            case MessageData.BUGLE_STATUS_INCOMING_RETRYING_MANUAL_DOWNLOAD:
                titleResId = R.string.message_title_downloading;
                statusResId = R.string.message_status_downloading;
                break;

            case MessageData.BUGLE_STATUS_INCOMING_YET_TO_MANUAL_DOWNLOAD:
                if (!OsUtil.isSecondaryUser()) {
                    titleResId = R.string.message_title_manual_download;
                    if (isSelected()) {
                        statusResId = R.string.message_status_download_action;
                    } else {
                        statusResId = R.string.message_status_download;
                    }
                }
                break;

            case MessageData.BUGLE_STATUS_INCOMING_EXPIRED_OR_NOT_AVAILABLE:
                if (!OsUtil.isSecondaryUser()) {
                    titleResId = R.string.message_title_download_failed;
                    statusResId = R.string.message_status_download_error;
                }
                break;

            case MessageData.BUGLE_STATUS_INCOMING_DOWNLOAD_FAILED:
                if (!OsUtil.isSecondaryUser()) {
                    titleResId = R.string.message_title_download_failed;
                    if (isSelected()) {
                        statusResId = R.string.message_status_download_action;
                    } else {
                        statusResId = R.string.message_status_download;
                    }
                }
                break;

            case MessageData.BUGLE_STATUS_OUTGOING_YET_TO_SEND:
            case MessageData.BUGLE_STATUS_OUTGOING_SENDING:
                statusResId = R.string.message_status_sending;
                break;

            case MessageData.BUGLE_STATUS_OUTGOING_RESENDING:
            case MessageData.BUGLE_STATUS_OUTGOING_AWAITING_RETRY:
                statusResId = R.string.message_status_send_retrying;
                break;

            case MessageData.BUGLE_STATUS_OUTGOING_FAILED_EMERGENCY_NUMBER:
                statusResId = R.string.message_status_send_failed_emergency_number;
                break;

            case MessageData.BUGLE_STATUS_OUTGOING_FAILED:
                // don't show the error state unless we're the default sms app
                if (PhoneUtils.getDefault().isDefaultSmsApp()) {
                    if (isSelected()) {
                        statusResId = R.string.message_status_resend;
                    } else {
                        statusResId = MmsUtils.mapRawStatusToErrorResourceId(
                                mData.getStatus(), mData.getRawTelephonyStatus());
                    }
                    break;
                }
                // FALL THROUGH HERE

            case MessageData.BUGLE_STATUS_OUTGOING_COMPLETE:
            case MessageData.BUGLE_STATUS_INCOMING_COMPLETE:
            default:
                if (!mData.getCanClusterWithNextMessage()) {
                    //add by junwang
                    if(!isH5Message(mData.getJson()) && !isMapView() && !mIsCardMsg) {
                        statusText = mData.getFormattedReceivedTimeStamp();
                    }
                }
                break;
        }

        final boolean titleVisible = (titleResId >= 0);
        if (titleVisible) {
            final String titleText = getResources().getString(titleResId);
            mTitleTextView.setText(titleText);

            final String mmsInfoText = getResources().getString(
                    R.string.mms_info,
                    Formatter.formatFileSize(getContext(), mData.getSmsMessageSize()),
                    DateUtils.formatDateTime(
                            getContext(),
                            mData.getMmsExpiry(),
                            DateUtils.FORMAT_SHOW_DATE |
                                    DateUtils.FORMAT_SHOW_TIME |
                                    DateUtils.FORMAT_NUMERIC_DATE |
                                    DateUtils.FORMAT_NO_YEAR));
            mMmsInfoTextView.setText(mmsInfoText);
            mMessageTitleLayout.setVisibility(View.VISIBLE);
        } else {
            mMessageTitleLayout.setVisibility(View.GONE);
        }

        final String subjectText = MmsUtils.cleanseMmsSubject(getResources(),
                mData.getMmsSubject());
        final boolean subjectVisible = !TextUtils.isEmpty(subjectText);

        final boolean senderNameVisible = !mOneOnOne && !mData.getCanClusterWithNextMessage()
                && mData.getIsIncoming();
        if (senderNameVisible) {
            mSenderNameTextView.setText(mData.getSenderDisplayName());
            mSenderNameTextView.setVisibility(View.VISIBLE);
        } else {
            mSenderNameTextView.setVisibility(View.GONE);
        }

        if (statusResId >= 0) {
            statusText = getResources().getString(statusResId);
        }

        // We set the text even if the view will be GONE for accessibility
        mStatusTextView.setText(statusText);
        final boolean statusVisible = !TextUtils.isEmpty(statusText);
        if (statusVisible) {
            LogUtil.i("junwang", "statusText="+statusText);
            mStatusTextView.setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setVisibility(View.GONE);
        }

        final boolean deliveredBadgeVisible =
                mData.getStatus() == MessageData.BUGLE_STATUS_OUTGOING_DELIVERED;
        mDeliveredBadge.setVisibility(deliveredBadgeVisible ? View.VISIBLE : View.GONE);

        // Update the sim indicator.
        final boolean showSimIconAsIncoming = mData.getIsIncoming() &&
                (!mData.hasAttachments() || shouldShowMessageTextBubble());
        final SubscriptionListEntry subscriptionEntry =
                mHost.getSubscriptionEntryForSelfParticipant(mData.getSelfParticipantId(),
                        true /* excludeDefault */);
        final boolean simNameVisible = subscriptionEntry != null &&
                !TextUtils.isEmpty(subscriptionEntry.displayName) &&
                !mData.getCanClusterWithNextMessage();
        if (simNameVisible) {
            final String simNameText = mData.getIsIncoming() ? getResources().getString(
                    R.string.incoming_sim_name_text, subscriptionEntry.displayName) :
                    subscriptionEntry.displayName;
            mSimNameView.setText(simNameText);
            mSimNameView.setTextColor(showSimIconAsIncoming ? getResources().getColor(
                    R.color.timestamp_text_incoming) : subscriptionEntry.displayColor);
            mSimNameView.setVisibility(VISIBLE);
        } else {
            mSimNameView.setText(null);
            mSimNameView.setVisibility(GONE);
        }

        final boolean metadataVisible = senderNameVisible || statusVisible
                || deliveredBadgeVisible || simNameVisible;
        mMessageMetadataView.setVisibility(metadataVisible ? View.VISIBLE : View.GONE);

        final boolean messageTextAndOrInfoVisible = titleVisible || subjectVisible
                || mData.hasText() || metadataVisible;
        mMessageTextAndInfoView.setVisibility(
                messageTextAndOrInfoVisible ? View.VISIBLE : View.GONE);

        if (shouldShowSimplifiedVisualStyle()) {
            mContactIconView.setVisibility(View.GONE);
            mContactIconView.setImageResourceUri(null);
        } else {
            mContactIconView.setVisibility(View.VISIBLE);
            final Uri avatarUri = AvatarUriUtil.createAvatarUri(
                    mData.getSenderProfilePhotoUri(),
                    mData.getSenderFullName(),
                    mData.getSenderNormalizedDestination(),
                    mData.getSenderContactLookupKey());
            mContactIconView.setImageResourceUri(avatarUri, mData.getSenderContactId(),
                    mData.getSenderContactLookupKey(), mData.getSenderNormalizedDestination());
        }
    }

    private void updateMessageContent() {
        // We must update the text before the attachments since we search the text to see if we
        // should make a preview youtube image in the attachments
        updateMessageText();
        updateMessageAttachments();
        updateMessageSubject();
        mMessageBubble.bind(mData);
    }

    private void updateMessageAttachments() {
        // Bind video, audio, and VCard attachments. If there are multiple, they stack vertically.
        bindAttachmentsOfSameType(sVideoFilter,
                R.layout.message_video_attachment, mVideoViewBinder, VideoThumbnailView.class);
        bindAttachmentsOfSameType(sAudioFilter,
                R.layout.message_audio_attachment, mAudioViewBinder, AudioAttachmentView.class);
        bindAttachmentsOfSameType(sVCardFilter,
                R.layout.message_vcard_attachment, mVCardViewBinder, PersonItemView.class);

        // Bind image attachments. If there are multiple, they are shown in a collage view.
        final List<MessagePartData> imageParts = mData.getAttachments(sImageFilter);
        if (imageParts.size() > 1) {
            Collections.sort(imageParts, sImageComparator);
            mMultiAttachmentView.bindAttachments(imageParts, null, imageParts.size());
            mMultiAttachmentView.setVisibility(View.VISIBLE);
        } else {
            mMultiAttachmentView.setVisibility(View.GONE);
        }

        // In the case that we have no image attachments and exactly one youtube link in a message
        // then we will show a preview.
        String youtubeThumbnailUrl = null;
        String originalYoutubeLink = null;
        if (mMessageTextHasLinks && imageParts.size() == 0) {
            CharSequence messageTextWithSpans = mMessageTextView.getText();
            final URLSpan[] spans = ((Spanned) messageTextWithSpans).getSpans(0,
                    messageTextWithSpans.length(), URLSpan.class);
            for (URLSpan span : spans) {
                String url = span.getURL();
                String youtubeLinkForUrl = YouTubeUtil.getYoutubePreviewImageLink(url);
                if (!TextUtils.isEmpty(youtubeLinkForUrl)) {
                    if (TextUtils.isEmpty(youtubeThumbnailUrl)) {
                        // Save the youtube link if we don't already have one
                        youtubeThumbnailUrl = youtubeLinkForUrl;
                        originalYoutubeLink = url;
                    } else {
                        // We already have a youtube link. This means we have two youtube links so
                        // we shall show none.
                        youtubeThumbnailUrl = null;
                        originalYoutubeLink = null;
                        break;
                    }
                }
            }
        }
        // We need to keep track if we have a youtube link in the message so that we will not show
        // the arrow
        mMessageHasYouTubeLink = !TextUtils.isEmpty(youtubeThumbnailUrl);

        // We will show the message image view if there is one attachment or one youtube link
        if (imageParts.size() == 1 || mMessageHasYouTubeLink) {
            // Get the display metrics for a hint for how large to pull the image data into
            final WindowManager windowManager = (WindowManager) getContext().
                    getSystemService(Context.WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            final int iconSize = getResources()
                    .getDimensionPixelSize(R.dimen.conversation_message_contact_icon_size);
            final int desiredWidth = displayMetrics.widthPixels - iconSize - iconSize;

            if (imageParts.size() == 1) {
                final MessagePartData imagePart = imageParts.get(0);
                // If the image is big, we want to scale it down to save memory since we're going to
                // scale it down to fit into the bubble width. We don't constrain the height.
                final ImageRequestDescriptor imageRequest =
                        new MessagePartImageRequestDescriptor(imagePart,
                                desiredWidth,
                                MessagePartData.UNSPECIFIED_SIZE,
                                false);
                adjustImageViewBounds(imagePart);
                mMessageImageView.setImageResourceId(imageRequest);
                mMessageImageView.setTag(imagePart);
            } else {
                // Youtube Thumbnail image
                final ImageRequestDescriptor imageRequest =
                        new UriImageRequestDescriptor(Uri.parse(youtubeThumbnailUrl), desiredWidth,
                                MessagePartData.UNSPECIFIED_SIZE, true /* allowCompression */,
                                true /* isStatic */, false /* cropToCircle */,
                                ImageUtils.DEFAULT_CIRCLE_BACKGROUND_COLOR /* circleBackgroundColor */,
                                ImageUtils.DEFAULT_CIRCLE_STROKE_COLOR /* circleStrokeColor */);
                mMessageImageView.setImageResourceId(imageRequest);
                mMessageImageView.setTag(originalYoutubeLink);
            }
            mMessageImageView.setVisibility(View.VISIBLE);
        } else {
            mMessageImageView.setImageResourceId(null);
            mMessageImageView.setVisibility(View.GONE);
        }

        // Show the message attachments container if any of its children are visible
        boolean attachmentsVisible = false;
        for (int i = 0, size = mMessageAttachmentsView.getChildCount(); i < size; i++) {
            final View attachmentView = mMessageAttachmentsView.getChildAt(i);
            if (attachmentView.getVisibility() == View.VISIBLE) {
                attachmentsVisible = true;
                break;
            }
        }
        mMessageAttachmentsView.setVisibility(attachmentsVisible ? View.VISIBLE : View.GONE);
    }

    private void bindAttachmentsOfSameType(final Predicate<MessagePartData> attachmentTypeFilter,
                                           final int attachmentViewLayoutRes, final AttachmentViewBinder viewBinder,
                                           final Class<?> attachmentViewClass) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        // Iterate through all attachments of a particular type (video, audio, etc).
        // Find the first attachment index that matches the given type if possible.
        int attachmentViewIndex = -1;
        View existingAttachmentView;
        do {
            existingAttachmentView = mMessageAttachmentsView.getChildAt(++attachmentViewIndex);
        } while (existingAttachmentView != null &&
                !(attachmentViewClass.isInstance(existingAttachmentView)));

        for (final MessagePartData attachment : mData.getAttachments(attachmentTypeFilter)) {
            View attachmentView = mMessageAttachmentsView.getChildAt(attachmentViewIndex);
            if (!attachmentViewClass.isInstance(attachmentView)) {
                attachmentView = layoutInflater.inflate(attachmentViewLayoutRes,
                        mMessageAttachmentsView, false /* attachToRoot */);
                attachmentView.setOnClickListener(this);
                attachmentView.setOnLongClickListener(this);
                mMessageAttachmentsView.addView(attachmentView, attachmentViewIndex);
            }
            viewBinder.bindView(attachmentView, attachment);
            attachmentView.setTag(attachment);
            attachmentView.setVisibility(View.VISIBLE);
            attachmentViewIndex++;
        }
        // If there are unused views left over, unbind or remove them.
        while (attachmentViewIndex < mMessageAttachmentsView.getChildCount()) {
            final View attachmentView = mMessageAttachmentsView.getChildAt(attachmentViewIndex);
            if (attachmentViewClass.isInstance(attachmentView)) {
                mMessageAttachmentsView.removeViewAt(attachmentViewIndex);
            } else {
                // No more views of this type; we're done.
                break;
            }
        }
    }

    private void updateMessageSubject() {
        final String subjectText = MmsUtils.cleanseMmsSubject(getResources(),
                mData.getMmsSubject());
        final boolean subjectVisible = !TextUtils.isEmpty(subjectText);

        if (subjectVisible) {
            mSubjectText.setText(subjectText);
            mSubjectView.setVisibility(View.VISIBLE);
        } else {
            mSubjectView.setVisibility(View.GONE);
        }
    }

    //add by junwang start
    //work around our wonky API by wrapping a geo permission prompt inside a regular permissionRequest.
    private static class GeoPermissionRequest extends PermissionRequest{
        private String mOrigin;
        private GeolocationPermissions.Callback mCallback;
        private static final String RESOURCE_GEO = "RESOURCE_GEO";

        public GeoPermissionRequest(String origin, GeolocationPermissions.Callback callback){
            mOrigin = origin;
            mCallback = callback;
        }

        public Uri getOrigin(){
            return Uri.parse(mOrigin);
        }

        @Override
        public String[] getResources() {
            return new String[]{this.RESOURCE_GEO};
        }

        public void grant(String[] resources){
            assert resources.length == 1;
            assert this.RESOURCE_GEO.equals(resources[0]);
            mCallback.invoke(mOrigin, true, false);
        }

        public void deny(){
            mCallback.invoke(mOrigin, false, false);
        }
    }

    /*private boolean isContactInWebViewWhiteList(String contact){
        LogUtil.d("Junwang", "DisplayDestination = "+mData.getSenderDisplayDestination());
        LogUtil.d("Junwang", "NormalizedDestination = "+mData.getSenderNormalizedDestination());

        mWVWhiteList = new HashSet<String>();
        mWVWhiteList.add("10086");

        if(mOneOnOne && mWVWhiteList.contains(contact)){
            return true;
        }

        return false;
    }*/

    // 调起支付宝并跳转到指定页面
    private void startAlipayActivity(WebView view, String url) {
        Intent intent;
        try {
            intent = Intent.parseUri(url,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            view.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AliPayH5Start(WebView view, String url){
        //定义支付域名（替换成公司申请H5的域名即可）
        String realm = "http://xxx.com";

        if(/*url.startsWith("alipays:") || url.startsWith("alipay")*/
                url.startsWith("https://mclient.alipay.com/h5Continue.htm?")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        }

        if (url.contains("platformapi/startapp")){
            startAlipayActivity(view, url);
        }else if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                && (url.contains("platformapi") && url.contains("startapp"))) {
            startAlipayActivity(view, url);
        } else {
            view.loadUrl(url);
        }
    }

    private void WeixinH5PayStart(WebView view, String url){
        //定义支付域名（替换成公司申请H5的域名即可）
        String realm = "http://xxx.com";

        if (url.startsWith("weixin://wap/pay?") || url.startsWith("https://wx.tenpay.com")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        }else{
            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Referer", realm);
            view.loadUrl(url, extraHeaders);
        }

    }

    private static String getStringFromUrl(String s) throws IOException{
        StringBuffer buffer = new StringBuffer();
        // 通过js的执行路径获取后台数据进行解析
        URL url = new URL(s);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setDoInput(true);
        http.setUseCaches(false);
        http.setRequestMethod("GET");
        http.connect();
        // 将返回的输入流转换成字符串
        InputStream inputStream = http.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        // 释放资源
        inputStream.close();
        inputStream = null;
        http.disconnect();
        str = buffer.toString();
        int index = str.indexOf("(");
        String jsonString = str.substring(index + 1, str.length() -1);
        return jsonString;
    }

    private boolean isNeedH5Display(){
        if(mData != null){
            if((mData.getStatus() == MessageData.BUGLE_STATUS_INCOMING_COMPLETE)
                    && (System.currentTimeMillis() - mData.getReceivedTimeStamp())%MAX_INTERVAL_TIME >= 1){
                return true;
            }
        }
        return false;
    }

    private boolean WXPay(WebView view, String url){
        //IWXAPI api = WXAPIFactory.createWXAPI(view.getContext(), /*"你的appid"*/"wxb4ba3c02aa476ea1");

        try{
            String content = getStringFromUrl(url);
            if(content != null && content.length() > 0){
                //Log.e("get server pay params:",content);
                JSONObject json = new JSONObject(content);
                if(null != json && !json.has("retcode") ){
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId			= json.getString("appid");
                    req.partnerId		= json.getString("partnerid");
                    req.prepayId		= json.getString("prepayid");
                    req.nonceStr		= json.getString("noncestr");
                    req.timeStamp		= json.getString("timestamp");
                    req.packageValue	= json.getString("package");
                    req.sign			= json.getString("sign");
                    req.extData			= "app data"; // optional
                    IWXAPI api = WXAPIFactory.createWXAPI(view.getContext(), /*"你的appid"*/req.appId);
                    //api.registerApp();
                    api.sendReq(req);
                    return true;
                }else{
                    LogUtil.d("Junwang", "返回错误"+json.getString("retmsg"));
                    Toast.makeText(mMessageWebView.getContext(), "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            }else{
                LogUtil.d("Junwang", "服务器请求错误");
                Toast.makeText(mMessageWebView.getContext(), "服务器请求错误", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            LogUtil.d("Junwang", "异常："+e.getMessage());
            Toast.makeText(mMessageWebView.getContext(), "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case OPEN_LOCATION:
                    Bundle bundle = new Bundle();
                    bundle.putString("Addr", "西湖文化广场");
                    bundle.putDouble("Latitude", 30.28582);
                    bundle.putDouble("Longtitude", 120.172416);

                    Intent intent = new Intent(mMessageWebView.getContext(), BaiduMapTestActivity.class);
                    intent.putExtras(bundle);
                    //intent.putExtra("Addr","西湖文化广场");
                    mMessageWebView.getContext().startActivity(intent);
                    //mMessageWebView.getContext().startActivity(new Intent(mMessageWebView.getContext(), BaiduMapTestActivity.class));
                    break;
                case CALL_PHONE:
                    break;
                case SDK_PAY_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
//                    /**
//                     *对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//                     */
//                    String resultInfo = payResult.getResult();
//                    String resultStatus = payResult.getResultStatus();
//                    // 判断resultStatus 为9000则代表支付成功
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//
//                    } else {
//                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//
//                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @JavascriptInterface
    private void nativeMethod(){
        Toast.makeText(mMessageWebView.getContext(), "Android 本地方法", Toast.LENGTH_LONG);
    }

    private class LoadUrl extends AsyncTask<String , Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            mMessageWebView.loadUrl(mUrl);
            return mUrl;
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean display_h5_sms() {
        // Check if this setting is enabled before playing
        final BuglePrefs prefs = BuglePrefs.getApplicationPrefs();
        final Context context = Factory.get().getApplicationContext();
        final String prefKey = context.getString(R.string.h5_sms_pref_key);
        final boolean defaultValue = context.getResources().getBoolean(
                R.bool.h5_sms_pref_default);
        if (!prefs.getBoolean(prefKey, defaultValue)) {
            return false;
        }
        return true;
    }

    private void showActionButton(){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = 500;
        layoutParams.height = 100;
        layoutParams.x = 300;
        layoutParams.y = 300;

        mFloatingButton = new Button(getContext());
        mFloatingButton.setText("Action BT");
        mFloatingButton.setBackgroundColor(Color.BLUE);

        addView(mFloatingButton, layoutParams);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this.getContext())) {
            // 获取WindowManager服务
            WindowManager windowManager = (WindowManager) this.getContext().getSystemService(android.content.Context.WINDOW_SERVICE);

            // 新建悬浮窗控件
            mFloatingButton = new Button(getContext());
            mFloatingButton.setText("Floating Window");
            mFloatingButton.setBackgroundColor(Color.BLUE);

            // 设置LayoutParam
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = 500;
            layoutParams.height = 100;
            layoutParams.x = 300;
            layoutParams.y = 300;

            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(mFloatingButton, layoutParams);
        }
    }

    public void getAndroiodScreenProperty() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        int bubbleViewHeight = mMessageBubble.getHeight();


        LogUtil.d("Junwang", "屏幕宽度（像素）：" + width);
        LogUtil.d("Junwang", "屏幕高度（像素）：" + height);
        LogUtil.d("Junwang", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        LogUtil.d("Junwang", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        LogUtil.d("Junwang", "屏幕宽度（dp）：" + screenWidth);
        LogUtil.d("Junwang", "屏幕高度（dp）：" + screenHeight);
        LogUtil.d("Junwang", "bubbleViewHeight=" + bubbleViewHeight);
    }

    private boolean isSystemLocationEnable() {
        LocationManager manager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsLocationEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkLocationEnable = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gpsLocationEnable || networkLocationEnable;
    }
    public static Activity getActivityFromView(View view) {
        if (null != view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }

    class MyClickableSpan extends ClickableSpan {

        private String content;

        public MyClickableSpan(String content) {
            this.content = content;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
//            Intent intent = new Intent(this, OtherActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("content", content);
//            intent.putExtra("bundle", bundle);
//            startActivity(intent);
            closeButtonMenu();
            mMessageWebView.getContext().startActivity(new Intent(mMessageWebView.getContext(),
                    BaiduMapTestActivity.class));
//            mMessageWebView.getContext().startActivity(new Intent(mMessageWebView.getContext(),
//                    FullScreenVideoPlayActivity.class));
//            FullScreenVideoPlayActivity.start(getContext(), null);
        }
    }

    private void setClickableText(String text){
        SpannableString spannableString = new SpannableString(text);
        MyClickableSpan clickableSpan = new MyClickableSpan("会议地点");
        int startidx = text.indexOf("会议地点");
        if(startidx != -1) {
            spannableString.setSpan(clickableSpan, startidx, startidx + 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            UnderlineSpan underlineSpan = new UnderlineSpan();
            spannableString.setSpan(underlineSpan, startidx, startidx + 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
            spannableString.setSpan(foregroundColorSpan, startidx, startidx+4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mMessageTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mMessageTextView.setHighlightColor(Color.parseColor("#36969696"));
        }
        mMessageTextView.setText(spannableString);
    }
    static boolean isClickedOnWV = false;
    public static boolean isClickedOnWebView(){
        return false;//isClickedOnWV;
    }
    public static void setClickOnWebView(boolean b){
        isClickedOnWV = b;
    }

    private void setWebViewSettings(WebView webView){
        WebSettings ws = webView.getSettings();
        ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
        ws.setJavaScriptEnabled(true);
        ws.setAppCacheEnabled(true);
        ws.setGeolocationEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        String cacheDirPath = webView.getContext().getFilesDir().getAbsolutePath()+"cache/";
        ws.setDatabasePath(cacheDirPath);
        ws.setDatabaseEnabled(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        //ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);
        //ws.setNeedInitialFocus(false);
//        ws.setCacheMode();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            mMessageWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            mMessageWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        // 特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        ws.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
//        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cm.getActiveNetworkInfo();
//        if(info.isAvailable()) {
//            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
//        }else {
//            ws.setCacheMode(WebSettings.LOAD_CACHE_ONLY);//不使用网络，只加载缓存
//        }
    }

    private void initViewList(ArrayList<String> urlList){
        mLL_webview_container.removeAllViews();
        int i=0;
        if((urlList == null) || (urlList.size() == 0)){
            return;
        }
        //for test
        if(urlList != null){
            if(urlList.get(0).startsWith("https://www.baidu")){
                urlList.clear();
                urlList.add(TEST_URL4);
            }else if(urlList.get(0).startsWith("https://news.sina")){
                urlList.clear();
                urlList.add(TEST_URL5);
            }else if(urlList.get(0).startsWith("https://item")){
                urlList.clear();
                urlList.add(TEST_URL6);
            }else if(urlList.get(0).startsWith("http://xhpfmapi")){
                urlList.clear();
                urlList.add(TEST_URL7);
            }else if(urlList.get(0).startsWith("https://detail")){
                urlList.clear();
                urlList.add(TEST_URL8);
            }else if(urlList.get(0).startsWith("https://www.163.com")){
                urlList.clear();
                urlList.add(TEST_URL9);
            }else if(urlList.get(0).startsWith("https://www.sohu.com")){
                urlList.clear();
                urlList.add(TEST_URL10);
            }else if(urlList.get(0).startsWith("http://www.trioly.com")){
                urlList.clear();
                urlList.add(TEST_URL11);
            }
//            urlList.clear();
//            urlList.add(TEST_URL1);
//            urlList.add(TEST_URL2);
//            urlList.add(TEST_URL3);
//            urlList.add(TEST_URL4);
        }
//        mViewList = new ArrayList<View>();
        for(String url: urlList){
            i++;
            LogUtil.i("MMMM", "initViewList url="+url+", i="+i);
//            addWebView(mViewList, url);
            addWebViewToContainer(url);
        }
////        addWebViewToContainer(TEST_URL1);
//        addWebViewToContainer(TEST_URL2);
////        addWebViewToContainer(TEST_URL3);

    }

    private void initTestUrl(){
        ArrayList<String> ltu = new ArrayList();
        ltu.add(TEST_URL1);
        ltu.add(TEST_URL2);
        ltu.add(TEST_URL3);
        initViewList(ltu);
    }

    private Point getWindowWidthHeight(Context context){
        Point pt = new Point();
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        pt.x = dm.widthPixels;
        pt.y = dm.heightPixels;
        return pt;
    }

    private void addWebViewToContainer(final String url){
        Point pt = getWindowWidthHeight(getContext());
        final WebView wv = new PowerWebView(this.getContext());
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getContext().getResources().getDimensionPixelOffset(R.dimen.container_webview_width),
//                getContext().getResources().getDimensionPixelOffset(R.dimen.container_webview_height));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pt.x - 60,
                pt.y/3);

        lp.setMargins(20, 10, 20, 10);
        wv.setLayoutParams(lp);
        setWebViewSettings(wv);
//        getActivityFromView(wv).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                wv.loadUrl(url);
//            }
//        });
        wv.loadUrl(url);
        mLL_webview_container.addView(wv);
    }

    private void addWebView(List<View> viewList, String url){
        WebView wv = new WebView(this.getContext());
        setWebViewSettings(wv);

        wv.loadUrl(url);
        wv.setWebViewClient(new MyWebViewClient());
        wv.setWebChromeClient(new MyWebChromeClient());
        viewList.add(wv);
    }

    private class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            if(url != null && url.startsWith("https://news.sina.cn")) {
                view.loadUrl(ADD_CLICKLISTENERONVIDEO_SCRIPT);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            LogUtil.i("Junwang","shouldOverrideUrlLoading enter");
            if(request == null){
                return false;
            }
            String url = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            } else {
                url = request.toString();
            }

            if(url == null){
                return true;
            }
            LogUtil.d("Junwang", "loadUrl url="+url);
            if(true) {
                FullScreenWebViewActivity.start(getContext(), url);
                return true;
            }
            if(url.startsWith("weixin://wap/pay?") || url.startsWith("https://wx.tenpay.com")){
                WXPay(view, url);
                return true;
            }
            try{
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")){
                    LogUtil.d("Junwang", "loadUrl url1="+url);

                    final PayTask task = new PayTask((Activity)mMessageWebView.getContext());
                    boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                        @Override
                        public void onPayResult(final H5PayResultModel result) {
                            final String url=result.getReturnUrl();
                            if(!TextUtils.isEmpty(url)){
                                ((Activity)mMessageWebView.getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMessageWebView.loadUrl(url);
                                    }
                                });
                            }
                        }
                    });

                    LogUtil.d("Junwang", "isIntercepted = "+isIntercepted);
                    if(!isIntercepted)
                        mMessageWebView.loadUrl(url);
                    //return true;
                    return false;
                }else {
                    if(mUpdatedText){
                        mUpdatedText = false;
                        if(!url.startsWith("xhpfm://") &&
                                !url.startsWith("baiduboxlite://")) {
                            return false;
                        }
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            getContext().startActivity(intent);
                        }else {
                            final Activity a = getActivityFromView(mMessageWebView);
                            if (a != null) {
                                a.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(a, "没有找到应用可以打开，请到应用市场下载！", Toast.LENGTH_LONG);
                                    }
                                });
                            }
                            mMessageWebView.stopLoading();
                        }
                        LogUtil.i("Junwang", "Above 6.0 shouldOverrideUrlLoading");
                    } catch (ActivityNotFoundException e) {
                        LogUtil.d("Junwang", "can't find activity to open url");
                    }
                    return false;
                }
            }catch(Exception e){
                return false;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d("Junwang", "shouldOverrideUrlLoading loadUrl url.");
            if(url == null){
                return true;
            }
            LogUtil.d("Junwang", "loadUrl url="+url);
            try{
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")){
                    LogUtil.d("Junwang", "loadUrl url1="+url);
                    /**
                     * for Alipay
                     * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
                     */
                                        /*final PayTask task = new PayTask((Activity)mMessageWebView.getContext());
                                        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                                            @Override
                                            public void onPayResult(final H5PayResultModel result) {
                                                final String url=result.getReturnUrl();
                                                if(!TextUtils.isEmpty(url)){
                                                    //mMessageWebView.loadUrl(url);
                                                    ((Activity)mMessageWebView.getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mMessageWebView.loadUrl(url);
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                        /**
                                         * 判断是否成功拦截
                                         * 若成功拦截，则无需继续加载该URL；否则继续加载
                                         */
                    return false;
                }else {
                    if(mUpdatedText){
                        mUpdatedText = false;
                        if(!url.startsWith("xhpfm://") &&
                                !url.startsWith("baiduboxlite://")) {
                            return false;
                        }
                    }
                    return false;
                }
            }catch(Exception e){
                return false;
            }
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }
    }

    private class MyWebChromeClient extends WebChromeClient{
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        @Nullable
        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(new int[]{Color.TRANSPARENT}, 1, 1, Bitmap.Config.ARGB_8888);
        }

        @Nullable
        @Override
        public View getVideoLoadingProgressView() {
            return super.getVideoLoadingProgressView();
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            builder.setTitle("对话框")
                    .setMessage(message)
                    .setPositiveButton("确定", null);

            // 不需要绑定按键事件
            // 屏蔽keycode等于84之类的按键
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    LogUtil.v("onJsAlert", "keyCode==" + keyCode + "event="+ event);
                    return true;
                }
            });
            // 禁止响应按back键的事件
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
            return true;
            // return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("对话框")
                    .setMessage(message)
                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            closeButtonMenu();
                            result.confirm();
                        }
                    })
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            closeButtonMenu();
                            result.cancel();
                        }
                    });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    result.cancel();
                }
            });
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            builder.setTitle("对话框").setMessage(message);

            final EditText et = new EditText(view.getContext());
            et.setSingleLine();
            et.setText(defaultValue);
            builder.setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            closeButtonMenu();
                            result.confirm(et.getText().toString());
                        }

                    })
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            closeButtonMenu();
                            result.cancel();
                        }
                    });
            return true;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            if(isSystemLocationEnable()) {
                callback.invoke(origin, true, false);
                onPermissionRequest(new GeoPermissionRequest(origin, callback));
            }else{
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(intent);
            }
//                                mMessageWebView.getContext().startActivity(new Intent(mMessageWebView.getContext(), BaiduMapTestActivity.class));
            callback.invoke(origin, true, false);
            onPermissionRequest(new GeoPermissionRequest(origin, callback));
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
//                                if (Build.VERSION.SDK_INT >= 23) {
//                                    int checkPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
//                                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                                        ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//                                        ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                                        return;
//                                    }
        }



        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            String[] mTemp = fileChooserParams.getAcceptTypes();
            if("image/*".equals(mTemp[0])){
                ConversationActivity.handleShowFileChooser(filePathCallback, true);
            }else if("video/*".equals(mTemp[0])){
                ConversationActivity.handleShowFileChooser(filePathCallback, false);
            }
            return true;
        }



        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            ConversationActivity.handleOpenFileChooser(uploadMsg, true);
        }
        public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType) {
            if("image/*".equals(acceptType)){
                ConversationActivity.handleOpenFileChooser(uploadMsg, true);
            }else if("video/*".equals(acceptType)){
                ConversationActivity.handleOpenFileChooser(uploadMsg, false);
            }
        }
        public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType, String capture) {
            if("image/*".equals(acceptType)){
                ConversationActivity.handleOpenFileChooser(uploadMsg, true);
            }else if("video/*".equals(acceptType)){
                ConversationActivity.handleOpenFileChooser(uploadMsg, false);
            }
        }
    }
    //for test
    private void initTestNewsData(){
        News news = new News();
    }

    private void initVideoView(SantiVideoView videoView, String title, String url){
        StandardVideoController standardVideoController = new StandardVideoController(getContext());
        standardVideoController.setTitle(title);
        videoView.setVideoController(standardVideoController);
//        mVideoView.getController().setOnTouchListener(this);
        videoView.setUrl(url);
        videoView.setPlayerFactory(new PlayerFactory<IjkPlayer>() {
            @Override
            public IjkPlayer createPlayer() {
                return new IjkPlayer() {
                    @Override
                    public void setOptions() {
                        //精准seek
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                    }
                };
            }
        });
        //播放器配置，注意：此为全局配置，按需开启
//        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
//                .setLogEnabled(BuildConfig.DEBUG)
//                .setPlayerFactory(IjkPlayerFactory.create())
//                .setPlayerFactory(IjkPlayerFactory.create())
//                .setEnableOrientation(true)
//                .setEnableMediaCodec(true)
//                .setUsingSurfaceView(true)
//                .setEnableParallelPlay(true)
//                .setEnableAudioFocus(false)
//                .setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
//                .build());
        videoView.start();

        videoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == DanmuVideoView.STATE_PREPARED) {
                    if(!videoView.isReplay()) {
//                        simulateDanmu();
                    }
                } else if (playState == DanmuVideoView.STATE_PLAYBACK_COMPLETED) {
                    mHandler.removeCallbacksAndMessages(null);
//                    mVideoView.mDanmakuView.removeAllDanmakus(true);
//                    mVideoView.removeView(mVideoView.mDanmakuView);
                }
            }
        });
    }

    private final Map<String, String> getRegularExpression(String json){
        Map<String, String> regularExp = null;
        try{
            regularExp = new Gson().fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        }catch(Exception e){
            LogUtil.e("junwang", "parse regular expression json exception: "+e.toString());
        }
        return regularExp;
    }

    private final List<BusinessCardService.ActionButton> getActionButton(String json){
        List<BusinessCardService.ActionButton> actionButtons = null;
        try{
            actionButtons = new Gson().fromJson(json, new TypeToken<List<BusinessCardService.ActionButton>>(){}.getType());
        }catch(Exception e){
            LogUtil.e("junwang", "parse action buttons json exception: "+e.toString());
        }
        return actionButtons;
    }

    private boolean LoadCardTemplate(CardTemplate ct){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        int resource = 0;
        View v = null;

        if(ct == null){
            LogUtil.i("Junwang", "LoadCardTemplate ct == null");
            return false;
        }
        int tempNo = ct.getCard_msg_template_no();
        switch (tempNo){
            case CardTemplate.RECHARGE_NOTIFICATION:
                resource = R.layout.layout_recharge_notification;
                v = layoutInflater.inflate(resource, null);
                if(loadRechargeNotificationLayout(ct, v)){
                    setTimeText(v);
                    return true;
                }
                break;
            case CardTemplate.VERICATION_CODE_NOTIFICATION:
                resource = R.layout.layout_verification_code_notification;
                v = layoutInflater.inflate(resource, null);
                if(loadVericationCodeNotificationLayout(ct, v)){
                    setTimeText(v);
                    return true;
                }
                break;
            case CardTemplate.BUY_TICKETS_SUCCESSFULLY_NOTIFICATION:
                resource = R.layout.layout_buy_tickets_successful_notification;
                v = layoutInflater.inflate(resource, null);
                if(loadBuyTicketsSuccussfullyNotificationLayout(ct, v)){
                    setTimeText(v);
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean LoadCardMsgTemplate(int tempNo, View v, LayoutInflater layoutInflater){
        switch (tempNo){
            case CardTemplate.RECHARGE_NOTIFICATION:
                if(!loadRechargeNotificationLayout(v, layoutInflater)){
                    return false;
                }
                break;
            case CardTemplate.VERICATION_CODE_NOTIFICATION:
                loadVericationCodeNotificationLayout(v, layoutInflater);
                break;
            case CardTemplate.BUY_TICKETS_SUCCESSFULLY_NOTIFICATION:
                loadBuyTicketsSuccussfullyNotificationLayout(v, layoutInflater);
                break;
            default:
                break;
        }
        return true;
    }

    public SantiVideoView getVideoView(){
        return vv_video;
    }

    private boolean canMatchRegularExpression(String regularExp, String text){
        if(regularExp != null){
            Pattern pattern = Pattern.compile(regularExp);
            Matcher matcher = pattern.matcher(text);
            if (matcher != null && matcher.find()){
                return true;
            }
        }
        return false;
    }

    private boolean canMatchRegularExpression(ArrayList<CardTemplate> regularExp, String text){
        if(getMatchRegularExpression(regularExp, text) != null){
            return true;
        }
        return false;
    }

    private CardTemplate getMatchRegularExpression(ArrayList<CardTemplate> regularExp, String text){
        Pattern pattern = null;
        Matcher matcher = null;
        if(regularExp != null){
            for(CardTemplate ct: regularExp){
                String reg = ct.getRegular_expression();
                LogUtil.i("Junwang", "reg="+reg);
                if(reg != null){
                    try {
                        pattern = Pattern.compile(reg);
                        if(pattern != null) {
                            matcher = pattern.matcher(text);
                            if (matcher != null && matcher.find()){
                                return ct;
                            }
                        }
                    }catch (Exception e){
                        LogUtil.i("Junwang", "Exception compile reg "+e.toString());
                    }

                }
            }
        }
        return null;
    }

    private String[] getRegExpValue(CardTemplate ct, String text){
        Pattern pattern = null;
        Matcher matcher = null;
        String[] regValue;
        if(ct != null){
            String reg = ct.getRegular_expression();
            if(reg != null){
                pattern = Pattern.compile(reg);
                if(pattern != null) {
                    matcher = pattern.matcher(text);
                    if (matcher != null && matcher.find()){
                        int count = matcher.groupCount();
                        LogUtil.i("Junwang", "getRegExpValue count = "+count);
                        for(int i=0; i<count+1; i++){
                            LogUtil.i("Junwang", "matcher.group["+i+"] ="+matcher.group(i));
                        }

                        if(count == 1){
                            regValue = new String[1];
                            regValue[0] = matcher.group(1);
                        }else if(count > 2) {
                            regValue = new String[matcher.groupCount()];
                            for (int i = 0; i < matcher.groupCount(); i++) {
                                regValue[i] = matcher.group(i+1);
                            }
                        }else{
                            return null;
                        }
                        return regValue;
                    }
                    LogUtil.i("Junwang", "Not matched!");
                }
            }
        }
        return null;
    }

    private String getValueFromRegularExpression(String regularExp, String text, int position){
        if(regularExp != null) {
            Pattern pattern = Pattern.compile(regularExp);
            Matcher matcher = pattern.matcher(text);
            if (matcher != null && matcher.find()) {
                LogUtil.i("getValueFromRegularExpression", "regularExp "+regularExp+"matched");
                if(matcher.groupCount() > position) {
                    for(int i=0; i<matcher.groupCount(); i++){
                        LogUtil.i("junwang", "group["+i+"]="+matcher.group(i));
                    }
                    LogUtil.i("junwang", "matcher.groupCount()="+matcher.groupCount()+", matcher_text="+matcher.group(position));
                    return matcher.group(position);
                }else{
                    LogUtil.i("junwang", "error position big than matcher group count, positon="+position);
                    return matcher.group(0);
                }
//                return matcher.group();
            }
        }
        return null;
    }

    /**
     * 从底部弹出popupwindow
     */
    private void showBottomPop(View parent, CardContent cardContent) {
        final View popView = View.inflate(getContext(), R.layout.popup_more_menu, null);
        showAnimation(popView);//开启动画
        PopupWindow mPopWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        clickPopItem(popView, mPopWindow);//条目的点击
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopWindow.showAtLocation(mMessageTextView.getRootView().getRootView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.update();
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getActivityFromView(popView).getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivityFromView(popView).getWindow().setAttributes(lp);
        ((TextView)popView.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        ((TextView)popView.findViewById(R.id.deleteMsg)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DeleteMessageAction.deleteMessage(mData.getMessageId());
                mPopWindow.dismiss();
            }
        });
        ((TextView)popView.findViewById(R.id.insertFav)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String date = year+"/"+month+"/"+day;
                LogUtil.i("Junwang", "date="+date);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(ChatbotFavoriteTableUtils.queryIsExistInChatbotFavorite(mData.getMessageId())){
//                            Toast.makeText(getContext(), "之前已经添加到收藏夹了", Toast.LENGTH_LONG);
                        }else {
                            String imageUrl = null;
                            if("video/mp4".equals(cardContent.getMedia().getMediaContentType())){
                                imageUrl = cardContent.getMedia().getThumbnailUrl();
                            }else{
                                imageUrl = cardContent.getMedia().getMediaUrl();
                            }
                            String fav_name = null;
                            String fav_logo = null;
                            String content_pre = "";
                            String sip = mData.getSenderNormalizedDestination();
                            if(sip != null && sip.startsWith("sip")) {
                                LogUtil.i("Junwang", "query sip="+sip);
                                ChatbotFavoriteEntity cfe = ChatbotInfoTableUtils.getChatbotInfo(sip);
                                if(cfe != null) {
                                    fav_name = cfe.getChatbot_fav_name();
                                    fav_logo = cfe.getChatbot_fav_logo();
                                }
                            }
                            int cardType = cardContent.getCardType();
                            switch (cardType){
                                case 1:
                                    content_pre = "[活动订阅]";
                                    break;
                                case 2:
                                    content_pre = "[参与投票]";
                                    break;
                                case 3:
                                    content_pre = "[视频快讯]";
                                    break;
                                case 4:
                                    content_pre = "[商品推荐]";
                                    break;
                                default:
                                    content_pre = "[活动订阅]";
                            }
                            ChatbotFavoriteTableUtils.insertChatbotFavoriteTable(mData.getSenderNormalizedDestination(), fav_name, fav_logo,
                                    content_pre+cardContent.getTitle(), imageUrl, date, null, mData.getMessageId());
                        }
                    }
                }).start();
                mPopWindow.dismiss();
            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivityFromView(popView).getWindow().getAttributes();
                lp.alpha = 1f;
                getActivityFromView(popView).getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 给popupwindow添加动画
     *
     * @param popView
     */
    private void showAnimation(View popView) {
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

    private class ImageNormalAdapter extends AbsStaticPagerAdapter {
        List<MultiCardItemDataBean> lists;
        Context context;

        public ImageNormalAdapter(List<MultiCardItemDataBean> lists, Context context) {
            this.lists = lists;
            this.context = context;
        }

        @Override
        public View getView(ViewGroup container, int position) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(R.layout.item_single_product_view, null);
            ImageView img = (ImageView)view.findViewById(R.id.product_image);
            TextView tv = (TextView)view.findViewById(R.id.product_description);
//            tv.setText(lists.get(position).getTitle());
            tv.setText("橙子脐橙新鲜甜伦晚助农水果当季夏橙5斤");
            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(30));//图片圆角为30
            Glide.with(context).load(lists.get(position).getMediaUr())
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

    private List<MultiCardItemDataBean> getMultiCardItemDataList(CardContent[] cardcontents){
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

    private void initBanner(BannerView banner, CardContent[] cardContents) {
        List<MultiCardItemDataBean> lists = getMultiCardItemDataList(cardContents);
        if(lists != null && lists.size()>0) {
            banner.setAdapter(new ImageNormalAdapter(lists, getContext()));
            banner.setOnBannerClickListener(new BannerView.OnBannerClickListener() {
                @Override
                public void onItemClick(int position) {
                    closeButtonMenu();
                    if(mData.getmChatbotCardInvalid() ){
                        LogUtil.i("Junwang", "Card is invalid.");
                        popupCardInvalidPrompt();
                    }else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String result = postVoteRequest("http://testxhs.supermms.cn/api/sms5g/my/viewProduct", mData.getmChatbotRcsdbMsgId(), position+1, false);
                                    ServerResponse sr = new Gson().fromJson(result, ServerResponse.class);
                                    if (sr != null) {
                                        if (sr.getData().getIsValid() != 0) {
                                            LogUtil.i("Junwang", "product recommend card is valid");
                                            WebViewNewsActivity.start(getContext(), lists.get(position).getExtraData1());
                                        } else {
                                            ChatbotInfoTableUtils.updateChatbotCardInvalidStatus(mData.getmChatbotRcsdbMsgId());
                                            getActivityFromView(mH5_content).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
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
            BannerHintView ihv = new BannerHintView(getContext(), R.drawable.pagescroll_on, R.drawable.pagescroll_off, 0);
            banner.setHintView(ihv);
//            IconHintView hv = new IconHintView(getContext(), R.drawable.point_focus, R.drawable.point_normal);
//            banner.setHintView(hv);
        }
    }

    private void loadProductRecommendCard(CardContent[] cardcontents){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.item_product_recommend_card, null);
        ((TextView)view.findViewById(R.id.tv_title)).setText("商品推荐");
        ((ImageView)view.findViewById(R.id.title_image)).setImageResource(R.drawable.icon_shopping);
        mMultiCardChatbotBanner = view.findViewById(R.id.multicard_chatbot_banner);
        initBanner(mMultiCardChatbotBanner, cardcontents);
        loadMoreAction(view, cardcontents[0]);
        mH5_content.removeAllViews();
        mH5_content.addView(view);
        mChatbotTime.setVisibility(View.VISIBLE);
        mChatbotTime.setText(mData.getFormattedReceivedTimeStamp());
        mH5_content.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                closeButtonMenu();
//                    showBottomPop(null);
            }
        });
        mH5_content.setVisibility(View.VISIBLE);
        mIsCardMsg = true;
    }

    private void loadChatbotMulticardView(CardContent[] cardcontents){
        mChatbotRV.setVisibility(View.VISIBLE);
        mChatbotRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<MultiCardItemDataBean> dataBeans = new ArrayList<>();
        String buttonDisplayText;
        String buttonActionData;
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
                for (int j = 0; j < saw.length; i++) {
                    if ((saw[i].action != null) && (saw[i].action.urlAction != null)) {
                        buttonDisplayText = saw[i].action.displayText;
                        buttonActionData = saw[i].action.urlAction.openUrl.url;
                        LogUtil.i("Junwang", "chatbot card message url action displayText is " + buttonDisplayText +", url="+buttonActionData);
                    }else if((saw[i].action != null) && (saw[i].action.dialerAction != null)){
                        buttonDisplayText = saw[i].action.displayText;
                        buttonActionData = saw[i].action.dialerAction.dialPhoneNumber.phoneNumber;
                        LogUtil.i("Junwang", "chatbot card message dial action displayText is " + buttonDisplayText+", dialerNumber="+buttonActionData);
                    }else if((saw[i].action != null) && (saw[i].action.mapAction != null)){
                        buttonDisplayText = saw[i].action.displayText;
                        LogUtil.i("Junwang", "chatbot card message map action displayText is " + buttonDisplayText);
                    }
                    if(saw[i].reply != null){
                        LogUtil.i("Junwang", "chatbot card message reply displayText is " + saw[i].reply.displayText+", postback.data="+saw[i].reply.postback.data);
                    }
                }
                dataBeans.add(new MultiCardItemDataBean(cardcontents[i].getTitle(), cardcontents[i].getMedia().getMediaContentType(),
                        cardcontents[i].getMedia().getMediaUrl(), saw[0].action.displayText, saw[0].action.urlAction.openUrl.url, null));
            }
        }
        MultiCardItemViewAdapter listAdapter = new MultiCardItemViewAdapter(dataBeans,getContext());
        setTimeText(mChatbotRV);
        mChatbotRV.setAdapter(listAdapter);
    }

    private void loadChatbotView(CardContent[] cardcontents){
        mChatbotRV.setVisibility(View.VISIBLE);
        mChatbotRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<ChatBotDataBean> dataBeans = new ArrayList<>();
        String buttonDisplayText;
        String buttonActionData;
        if((cardcontents != null) && (cardcontents.length > 0)){
            SuggestionActionWrapper[] saw;
            for(int i=0; i<cardcontents.length; i++){
                saw = cardcontents[i].getSuggestionActionWrapper();
                for (int j = 0; j < saw.length; i++) {
                    if ((saw[i].action != null) && (saw[i].action.urlAction != null)) {
                        buttonDisplayText = saw[i].action.displayText;
                        buttonActionData = saw[i].action.urlAction.openUrl.url;
                        LogUtil.i("Junwang", "chatbot card message url action displayText is " + buttonDisplayText +", url="+buttonActionData);
                    }else if((saw[i].action != null) && (saw[i].action.dialerAction != null)){
                        buttonDisplayText = saw[i].action.displayText;
                        buttonActionData = saw[i].action.dialerAction.dialPhoneNumber.phoneNumber;
                        LogUtil.i("Junwang", "chatbot card message dial action displayText is " + buttonDisplayText+", dialerNumber="+buttonActionData);
                    }else if((saw[i].action != null) && (saw[i].action.mapAction != null)){
                        buttonDisplayText = saw[i].action.displayText;
                        LogUtil.i("Junwang", "chatbot card message map action displayText is " + buttonDisplayText);
                    }
                }
                dataBeans.add(new ChatBotDataBean(/*cardcontents[i].getMedia().getMediaContentType()*/1,
                        cardcontents[i].getMedia().getMediaUrl(), saw[0].action.displayText, saw[0].action.urlAction.openUrl.url));
            }
        }
//        ChatBotDataBean cbdb1 = new ChatBotDataBean(1, "http://www.xinhuanet.com/video/2019-12/12/1210393161_15761395846321n.jpg", "跳转url", "https://www.baidu.com");
//        ChatBotDataBean cbdb2 = new ChatBotDataBean(1, "https://xh99.oss-cn-beijing.aliyuncs.com/weboss/19121216EF7AAD4FD.jpg", "跳转url1", "https://www.baidu.com");
//        ChatBotDataBean cbdb3 = new ChatBotDataBean(1, "http://p9-tt.byteimg.com/img/pgc-image/RfD31urH41bBGk~tplv-tt-cs0:750:422.jpg", "跳转url12", "https://www.baidu.com");
//        dataBeans.add(cbdb1);
//        dataBeans.add(cbdb2);
//        dataBeans.add(cbdb3);
        ChatBotCardViewAdapter listAdapter = new ChatBotCardViewAdapter(dataBeans,getContext());
        mChatbotRV.setAdapter(listAdapter);
    }

    private void loadMoreAction(View view, CardContent cardContent){
        TextView tvMoreAction = (TextView)view.findViewById(R.id.more_action);
        tvMoreAction.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LogUtil.i("Junwang", "click ... more action");
                closeButtonMenu();
                showBottomPop(view, cardContent);
            }
        });
    }

    private boolean loadPictureCardChatbotMessage(int resource, CardContent cardContent){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(resource, null);
        if(view != null) {
            ImageView iv = (ImageView) view.findViewById(R.id.chatbot_image);
            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(30));//图片圆角为30
            Glide.with(this).load(/*cardContent.getMedia().getThumbnailUrl()*//*"http://vsms-material.eos-hunan-1.cmecloud.cn/vsms_api/2/5e41089ac63f0.jpg"*/"/sdcard/DCIM/Camera/qrcode.jpg")
                    .apply(options)
                    .into(iv);
//            iv.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View arg0) {
//                    BitmapDrawable drawable = (BitmapDrawable)iv.getDrawable();
//                    if(drawable == null)
//                    {   Toast.makeText(getActivityFromView(iv), "二维码不能解析成BitmapDrawable",
//                            Toast.LENGTH_LONG).show();
//                        return false;
//                    }
//                    Bitmap bitmap = drawable.getBitmap();
//                    Result ret = parsePic(bitmap);
//                    if (null == ret) {
//                        Toast.makeText(getActivityFromView(iv), "解析结果：null",
//                                Toast.LENGTH_LONG).show();
//                    } else {
//                        LogUtil.i("Junwang", "qrcode="+ret.toString());
//                        WebViewNewsActivity.start(getContext(), ret.toString());
//                        Toast.makeText(getActivityFromView(iv),
//                                "解析结果：" + ret.toString(), Toast.LENGTH_LONG).show();
//                    }
//                    return false;
//                }
//            });
            mH5_content.removeAllViews();
            mH5_content.addView(view);
            setTimeText(view);
            return true;
        }
        return false;
    }

    private boolean loadVoteCardChatbotMessage(int resource, CardContent cardcontent){
//        List<String> voteList = new ArrayList<>();

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(resource, null);
//        view.setMinimumWidth(300);
        if(view != null) {
            LogUtil.i("Junwang", "loadVoteCardChatbotMessage");
            ((ImageView)view.findViewById(R.id.title_image)).setImageResource(R.drawable.icon_vote);
            initVoteView(view, cardcontent);
            loadMoreAction(view, cardcontent);
            mH5_content.removeAllViews();
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            mH5_content.setLayoutParams(lp);
//            mH5_content.requestLayout();
            mH5_content.addView(view);

            mH5_content.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
//                    showBottomPop(null);
                }
            });
            setTimeText(view);
            return true;
        }
        return false;
    }

    public class VoteData{
        ChatbotExtraData[] data;
        String msg;
        int code;

        public ChatbotExtraData[] getData() {
            return data;
        }

        public void setData(ChatbotExtraData[] data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }


    private void initVoteView(View view, CardContent cardcontent){
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
        if(mData.getIsChatbotVoted()){
            voteView.setAnimationRate(600);
//            tvVoteTitle.setText(cardcontent.getTitle());
//            tvVoteDescription.setText(cardcontent.getDescription());
            tvVoteAction.setBackgroundResource(R.drawable.border_textview_gray);
            tvVoteAction.setText("已投票");
            tvRefresh.setVisibility(View.VISIBLE);
            tvRefresh.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    closeButtonMenu();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String jsonString = getVoteRefreshData("http://testback.stvision.cn/xinhua/sms5g/my/getVoteResult", mData.getmChatbotRcsdbMsgId());
                                if (jsonString != null) {
                                    VoteData temp = new GsonBuilder().setLenient().create().fromJson(jsonString, VoteData.class);
                                    LinkedHashMap<String, Integer> voteData1 = new LinkedHashMap<>();
                                    ChatbotExtraData[] ced1 = temp.getData();
                                    if(ced1 != null && ced1.length>0){
                                        for(int i=0; i<ced1.length; i++){
                                            voteData1.put(ced1[i].getItemContent(), ced1[i].getItemCount());
                                            LogUtil.i("Junwang", "get refresh data " + ced1[i].getItemContent()+" : " + ced1[i].getItemCount());
                                        }
                                    }
                                    try{
                                        ((Activity)mMessageWebView.getContext()).runOnUiThread(new Runnable(){
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
            VoteSubView vsv;
            int position = 0;
            for(int i=0; i<voteView.getChildCount(); i++){
                if(voteView.getChildAt(i) instanceof VoteSubView){
                    vsv = (VoteSubView)voteView.getChildAt(i);
                    if(position == mData.getChatbotVotedItemPosition()){
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
                    closeButtonMenu();
                    if(mData.getmChatbotCardInvalid() ){
                        LogUtil.i("Junwang", "Card is invalid.");
                        popupCardInvalidPrompt();
                    }else{
                        if(voteView.selectView != null) {
                            voteView.notifyUpdateChildren(voteView.selectView, true, false);
                            tvRefresh.setVisibility(View.VISIBLE);
                            tvVoteAction.setBackgroundResource(R.drawable.border_textview_gray);
                            tvVoteAction.setText("已投票");
                            BugleNotifications.markMessageAsVotedStatus(mData.getmChatbotRcsdbMsgId(), voteView.selectedItemPosition);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String result = postVoteRequest("http://testxhs.supermms.cn/api/sms5g/my/clickVote", mData.getmChatbotRcsdbMsgId(), voteView.selectedItemPosition + 1, true);
                                        ServerResponse sr = new Gson().fromJson(result, ServerResponse.class);
                                        if(sr != null){
                                            if(sr.getData().getIsValid() != 0){
                                                LogUtil.i("Junwang", "vote card is valid");
                                            }else{
                                                ChatbotInfoTableUtils.updateChatbotCardInvalidStatus(mData.getmChatbotRcsdbMsgId());
                                                getActivityFromView(mH5_content).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getContext(), "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
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

    /**
     * 取消投票的 dialog
     */
    public void showDialog(final VoteView voteView, final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("是否取消投票？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        voteView.resetNumbers(); // 恢复初始投票数据
                        voteView.notifyUpdateChildren(view, false, false);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    private void updateSubscribeButton(){
        TextView tv = mH5_content.getChildAt(0).findViewById(R.id.action_button);
        tv.setText("已预约");
        tv.setTextColor(Color.parseColor("#858898"));
        tv.setBackgroundResource(R.drawable.border_textview_gray);
    }

    private boolean loadActivitySubscribeStartChatbotMessage(int resource, CardContent cardcontent){
        if(cardcontent == null){
            return false;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(resource, null);
        if(view != null) {
            TextView activity_title = (TextView)view.findViewById(R.id.activity_title);
//            activity_title.setText(cardcontent.getTitle());

            Glide.with(this).load(cardcontent.getMedia().getMediaUrl())
                    .centerCrop()
                    .into((ImageView)view.findViewById(R.id.activity_image));
            mH5_content.removeAllViews();
            mH5_content.addView(view);
            mH5_content.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    closeButtonMenu();
                }
            });
            setTimeText(view);

            return true;
        }
        return false;
    }

    private boolean loadOrderInfoPushChatbotMessage(int resource, CardContent cardcontent){
        if(cardcontent == null){
            return false;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(resource, null);
        if(view != null) {
            TextView activity_title = (TextView)view.findViewById(R.id.activity_title);
            ChatbotExtraData[] ced = cardcontent.getExtraData();
            if(ced != null && ced.length > 0){
                if(ced[0] != null){
                    activity_title.setText(ced[0].getItemContent());
                }
                if((ced[1] != null)){
                    ((TextView)view.findViewById(R.id.total_product)).setText(ced[1].getItemContent());
                }
                if(ced[2] != null){
                    ((TextView)view.findViewById(R.id.tv_amount)).setText(ced[2].getItemContent());
                }
            }

            Glide.with(this).load(cardcontent.getMedia().getMediaUrl())
                    .centerCrop()
                    .into((ImageView)view.findViewById(R.id.activity_image));
            mH5_content.removeAllViews();
            mH5_content.addView(view);
            mH5_content.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    closeButtonMenu();
                    WebViewNewsActivity.start(getContext(), cardcontent.getExtraData1());
                }
            });
            setTimeText(view);

            return true;
        }
        return false;
    }

    private void popupCardInvalidPrompt(){
        getActivityFromView(mH5_content).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void closeButtonMenu(){
        ConversationFragment fm = ((ConversationActivity)getActivityFromView(mMessageTextView)).getConversationFragment();
        fm.closeButtonMenu();
    }

    private boolean loadVideoNewsChatbotMessage(int resource, CardContent cardcontent){
        if(cardcontent == null){
            return false;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(resource, null);
        if(view != null) {
            TextView activity_title = (TextView)view.findViewById(R.id.tv_title);
            activity_title.setText("视频快讯");
            ((ImageView)view.findViewById(R.id.title_image)).setImageResource(R.drawable.icon_news);
            loadMoreAction(view, cardcontent);

            TextView tv_video_title = (TextView)view.findViewById(R.id.video_title);
            tv_video_title.setText(cardcontent.getTitle());

            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(20));//图片圆角为30
            Glide.with(this).load(cardcontent.getMedia().getThumbnailUrl())
                    .apply(options)
                    .into((ImageView)view.findViewById(R.id.iv_img));
//            Glide.with(this).load(cardcontent.getMedia().getThumbnailUrl())
//                    .centerCrop()
//                    .into((ImageView)view.findViewById(R.id.iv_img));
            mH5_content.removeAllViews();
            mH5_content.addView(view);
            mH5_content.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    closeButtonMenu();
                    if(mData.getmChatbotCardInvalid()){
                        LogUtil.i("Junwang", "Card is invalid.");
                        popupCardInvalidPrompt();
                    }else{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("msgId", mData.getmChatbotRcsdbMsgId());
                                    String result = ChatbotFavoriteTableUtils.postRequest("http://testxhs.supermms.cn/api/sms5g/my/seeVideo", params, "utf-8");
                                    ServerResponse sr = new Gson().fromJson(result, ServerResponse.class);
                                    if(sr != null){
                                        if(sr.getData().getIsValid() != 0){
                                            LogUtil.i("Junwang", "video card is valid");
                                            ChatbotVideoNewsDetailsActivity.start(getContext(), cardcontent.getMedia().getMediaUrl(),
                                                    cardcontent.getTitle(), cardcontent.getDescription());
//                                            ChatbotFavoriteTableUtils.postRequest("http://testxhs.supermms.cn/api/sms5g/my/seeVideo", params, "utf-8");
                                        }else{
                                            LogUtil.i("Junwang", "video card is invalid");
                                            ChatbotInfoTableUtils.updateChatbotCardInvalidStatus(mData.getmChatbotRcsdbMsgId());
                                            getActivityFromView(mH5_content).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
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
            setTimeText(view);

            return true;
        }
        return false;
    }

//    private boolean loadProductRecommendChatbotMessage(CardContent[] cardcontents){
//        if(cardcontents == null){
//            return false;
//        }
//        mChatbotRV.setVisibility(View.VISIBLE);
//        mChatbotRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        List<MultiCardItemDataBean> dataBeans = new ArrayList<>();
//        String buttonDisplayText;
//        String buttonActionData;
//        if((cardcontents != null) && (cardcontents.length > 0)){
//            SuggestionActionWrapper[] saw;
//            for(int i=0; i<cardcontents.length; i++){
//                LogUtil.i("Junwang", "chatbot cardType="+cardcontents[i].getCardType()+", extraData1="+cardcontents[i].getExtraData1());
//                ChatbotExtraData[] ced = cardcontents[i].getExtraData();
//                if((ced != null) && (ced.length > 0)){
//                    for(int k=0; k<ced.length; k++) {
//                        LogUtil.i("Junwang", "extraData["+k+"].itemContent="+ced[k].getItemContent()+", extraData["+k+"].itemCount="+ced[k].getItemCount());
//                    }
//                }
//                saw = cardcontents[i].getSuggestionActionWrapper();
//                for (int j = 0; j < saw.length; i++) {
//                    if ((saw[i].action != null) && (saw[i].action.urlAction != null)) {
//                        buttonDisplayText = saw[i].action.displayText;
//                        buttonActionData = saw[i].action.urlAction.openUrl.url;
//                        LogUtil.i("Junwang", "chatbot card message url action displayText is " + buttonDisplayText +", url="+buttonActionData);
//                    }else if((saw[i].action != null) && (saw[i].action.dialerAction != null)){
//                        buttonDisplayText = saw[i].action.displayText;
//                        buttonActionData = saw[i].action.dialerAction.dialPhoneNumber.phoneNumber;
//                        LogUtil.i("Junwang", "chatbot card message dial action displayText is " + buttonDisplayText+", dialerNumber="+buttonActionData);
//                    }else if((saw[i].action != null) && (saw[i].action.mapAction != null)){
//                        buttonDisplayText = saw[i].action.displayText;
//                        LogUtil.i("Junwang", "chatbot card message map action displayText is " + buttonDisplayText);
//                    }
//                    if(saw[i].reply != null){
//                        LogUtil.i("Junwang", "chatbot card message reply displayText is " + saw[i].reply.displayText+", postback.data="+saw[i].reply.postback.data);
//                    }
//                }
//                dataBeans.add(new MultiCardItemDataBean(cardcontents[i].getTitle(), cardcontents[i].getMedia().getMediaContentType(),
//                        cardcontents[i].getMedia().getMediaUrl(), saw[0].action.displayText, saw[0].action.urlAction.openUrl.url));
//            }
//        }
//        MultiCardItemViewAdapter listAdapter = new MultiCardItemViewAdapter(dataBeans,getContext());
//        setTimeText(mChatbotRV);
//        mChatbotRV.setAdapter(listAdapter);
//        mChatbotTime.setVisibility(View.VISIBLE);
//        mChatbotTime.setText(mData.getFormattedReceivedTimeStamp());
//
//        return true;
//    }

    private boolean loadActivitySubscribeChatbotMessage(int resource, CardContent cardcontent){
        if(cardcontent == null){
            return false;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(resource, null);
        if(view != null) {
            TextView dateView = (TextView)view.findViewById(R.id.dateView);
            TextView activity_title = (TextView)view.findViewById(R.id.activity_title);
//            activity_title.setText(cardcontent.getTitle());
            ((ImageView)view.findViewById(R.id.title_image)).setImageResource(R.drawable.icon_order);

//            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCorners(20));//图片圆角为30
            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(20));//图片圆角为30

            Glide.with(this).load(cardcontent.getMedia().getMediaUrl())
                    .apply(options)
                    .into((ImageView)view.findViewById(R.id.activity_image));
//                ((Button)findViewById(R.id.action_button)).setText(cardcontent.getSuggestionActionWrapper().);
//                GlideUtils.load(getContext(), cardcontent.getMedia().getMediaUrl(), (ImageView) findViewById(R.id.activity_image));
            if(cardcontent.getExtraData1() != null) {
                dateView.setText(cardcontent.getExtraData1());
            }
            loadMoreAction(view, cardcontent);
            mH5_content.removeAllViews();
            mH5_content.addView(view);
            mH5_content.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    closeButtonMenu();
                }
            });
            setTimeText(view);
            if(mData.getIsChatbotSubscribed()){
                updateSubscribeButton();
            }else {
                ((TextView) findViewById(R.id.action_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButtonMenu();
                        updateSubscribeButton();
                        BugleNotifications.markMessageAsSubscribedStatus(mData.getmChatbotRcsdbMsgId());
                        if(mData.getmChatbotCardInvalid()){
                            LogUtil.i("Junwang", "Card is invalid.");
                            popupCardInvalidPrompt();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("msgId", mData.getmChatbotRcsdbMsgId());
                                        String result = ChatbotFavoriteTableUtils.postRequest("http://testxhs.supermms.cn/api/sms5g/my/doAppoint", params, "utf-8");
                                        ServerResponse sr = new Gson().fromJson(result, ServerResponse.class);
                                        if(sr != null){
                                            if(sr.getData().getIsValid() != 0){
                                                LogUtil.i("Junwang", "subscribe card is valid");
                                            }else{
                                                LogUtil.i("Junwang", "subscribe card is invalid");
                                                ChatbotInfoTableUtils.updateChatbotCardInvalidStatus(mData.getmChatbotRcsdbMsgId());
                                                getActivityFromView(mH5_content).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getContext(), "消息已失效，已停止访问改内容", Toast.LENGTH_LONG).show();
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
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    LogUtil.i("Junwang", "post request");
//                                    postSubscribeRequest("http://testback.stvision.cn/xinhua/sms5g/my/doAppoint", mData.getmChatbotRcsdbMsgId());
//                                }catch (Exception e){
//                                    LogUtil.i("Junwang", "postSubscribeRequest exception: "+e.toString());
//                                }
//                            }
//                        }).start();
                    }
                });
            }

            return true;
        }
        return false;
    }

    private String mVerifyCode;

    private boolean loadVericationCodeNotificationLayout(CardTemplate ct, View v){
        LogUtil.i("Junwang", "loadVericationCodeNotificationLayout");
        if(v != null){
            mH5_content.removeAllViews();
            mH5_content.addView(v);
//            ConversationActivity activity = (ConversationActivity)this.getContext();
//            ((TextView)findViewById(R.id.card_title)).setText(activity.getCardTitle());
            String value[] = getRegExpValue(ct, mData.getText());
            if(value != null){
                String key[] = ct.getRegular_expression_key();
                LogUtil.i("Junwang", "key.length="+key.length+", value.length="+value.length);
                if(key.length != value.length){
                    LogUtil.i("Junwang", "loadVericationCodeNotificationLayout key.length != value.length error!");
                    mIsCardMsg = false;
                }else {
                    ((TextView) findViewById(R.id.card_title)).setText(ct.getCard_msg_title());
                    for (int i = 0; i < key.length; i++) {
                        if (i == 0) {
                            ((TextView) findViewById(R.id.item1_key)).setText(key[i]);
                            mVerifyCode = value[i];
                            ((TextView) findViewById(R.id.item1_value)).setText(value[i]);
                        }
                    }
                    mIsCardMsg = true;
                    ((ExpandableTextView) findViewById(R.id.expand_tv_yzm)).setText(mData.getText());
                    addActionButton(ct, v);
                }
            }else {
                mIsCardMsg = false;
            }
        }
        return mIsCardMsg;
    }

    private void onActionButtonClicked(CardTemplate.ActionButton actionButton, View targetView){
        if(actionButton != null){
            int actionType = actionButton.getButton_action_type();
            String actionUrl = actionButton.getButton_action_url();
            int[] actionLocalFunc = actionButton.getButton_action_native_function();
            switch (actionType){
                case 1:
                    //load url
                    LogUtil.i("Junwang", "action type == 1");
                    NativeFunctionUtil.loadUrl(getContext(), actionUrl);
                    break;
                case 2:
                    //call native function
                    LogUtil.i("Junwang", "action type == 2");
                    NativeFunctionUtil.callNativeFunction(actionLocalFunc[0], getActivityFromView(mH5_content), mVerifyCode, targetView, actionUrl);
                    break;
                case 3:
                    //jump to app
                    LogUtil.i("Junwang", "action type == 3");
                    NativeFunctionUtil.launchAPK(getContext(), actionUrl);
                    break;
                case 4:
                    //call alipay
                    LogUtil.i("Junwang", "action type == 4");
                    NativeFunctionUtil.callAlipay(getActivityFromView(mH5_content), actionUrl, mHandler);
                    break;
                default:
                    break;
            }
        }
    }

    private void addActionButton(CardTemplate ct, View v){
        ArrayList<CardTemplate.ActionButton> lab = ct.getAction_button();
        LogUtil.i("Junwang", "lab.size() = "+lab.size());
        if(lab != null && lab.size() != 0) {
            if(lab.size() == 1){
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                int resource = R.layout.one_textview_layout;
                View view = layoutInflater.inflate(resource, null);
                ((LinearLayout)v).addView(view);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                lp.width=v.getMeasuredWidth();
//                view.setLayoutParams(lp);
                TextView tv_action = ((TextView) findViewById(R.id.tv_action));
                tv_action.setText((lab.get(0)).getButton_name());
                tv_action.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButtonMenu();
                        onActionButtonClicked(lab.get(0), tv_action);
                    }
                });
            }else if(lab.size() == 2){
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                int resource = R.layout.two_textview_layout;
                View view = layoutInflater.inflate(resource, null);
                ((LinearLayout)v).addView(view);
                TextView tv_action1 = ((TextView) findViewById(R.id.tv_action1));
                TextView tv_action2 = ((TextView) findViewById(R.id.tv_action2));
                tv_action1.setText((lab.get(0)).getButton_name());
                tv_action1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButtonMenu();
                        onActionButtonClicked(lab.get(0), tv_action1);
                    }
                });
                tv_action2.setText((lab.get(1)).getButton_name());
                tv_action2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButtonMenu();
                        onActionButtonClicked(lab.get(1), tv_action2);
                    }
                });
            }else if(lab.size() == 3){
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                int resource = R.layout.three_textview_layout;
                View view = layoutInflater.inflate(resource, null);
                ((LinearLayout)v).addView(view);
                TextView tv_action1 = ((TextView) findViewById(R.id.tv_action1));
                TextView tv_action2 = ((TextView) findViewById(R.id.tv_action2));
                TextView tv_action3 = ((TextView) findViewById(R.id.tv_action3));
                tv_action1.setText((lab.get(0)).getButton_name());
                tv_action2.setText((lab.get(1)).getButton_name());
                tv_action3.setText((lab.get(2)).getButton_name());
                tv_action1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButtonMenu();
                        onActionButtonClicked(lab.get(0), tv_action1);
                    }
                });
                tv_action2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButtonMenu();
                        onActionButtonClicked(lab.get(1), tv_action2);
                    }
                });
                tv_action3.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButtonMenu();
                        onActionButtonClicked(lab.get(2), tv_action3);
                    }
                });

            }else{
                LogUtil.i("Junwang", "lab.size() is bigger than 3!");
            }
        }
    }

    private boolean loadBuyTicketsSuccussfullyNotificationLayout(CardTemplate ct, View v){
        LogUtil.i("Junwang", "loadBuyTicketsSuccussfullyNotificationLayout");
        if(v != null){
            mH5_content.removeAllViews();
            mH5_content.addView(v);
//            ConversationActivity activity = (ConversationActivity)this.getContext();
//            ((TextView)findViewById(R.id.card_title)).setText(activity.getCardTitle());
            String value[] = getRegExpValue(ct, mData.getText());
            if(value != null){
                String key[] = ct.getRegular_expression_key();
                /*if(key.length != value.length){
                    LogUtil.i("Junwang", "loadBuyTicketsSuccussfullyNotificationLayout key.length != value.length error!");
                    mIsCardMsg = false;
                }else*/ {
                    ((TextView) findViewById(R.id.card_title)).setText(ct.getCard_msg_title());
                    for (int i = 0; i < key.length; i++) {
                        if (i == 0) {
                            ((TextView) findViewById(R.id.item1_key)).setText(key[i]);
                            ((TextView) findViewById(R.id.item1_value)).setText(value[i]);
                        } else if (i == 1) {
                            ((TextView) findViewById(R.id.item2_key)).setText(key[i]);
                            ((TextView) findViewById(R.id.item2_value)).setText(value[i+1]);
                        } else if (i == 2) {
                            ((TextView) findViewById(R.id.item3_key)).setText(key[i]);
                            ((TextView) findViewById(R.id.item3_value1)).setText(value[i+2]);
                            ((TextView) findViewById(R.id.item3_value2)).setText(value[i+3]);
                        }else if(i == 3){
                            ((TextView) findViewById(R.id.item4_key)).setText(key[i]);
                            ((TextView) findViewById(R.id.item4_value1)).setText(value[i-2]);
                            ((TextView) findViewById(R.id.item4_value2)).setText(value[i+3]);
                        }else if(i == 4){
                            ((TextView) findViewById(R.id.item5_key)).setText(key[i]);
                            ((TextView) findViewById(R.id.item5_value)).setText(value[i-1]);
                        }
                    }
                    mIsCardMsg = true;
                    ((ExpandableTextView) findViewById(R.id.expand_tv_cztx)).setText(mData.getText());
                }
            }else {
                mIsCardMsg = false;
            }
        }
        return mIsCardMsg;
    }

    private boolean loadRechargeNotificationLayout(CardTemplate ct, View v){
        LogUtil.i("Junwang", "loadRechargeNotificationLayout");
        if(v != null){
            mH5_content.removeAllViews();
            mH5_content.addView(v);
//            ConversationActivity activity = (ConversationActivity)this.getContext();
//            ((TextView)findViewById(R.id.card_title)).setText(activity.getCardTitle());
            String value[] = getRegExpValue(ct, mData.getText());
            if(value != null){
                String key[] = ct.getRegular_expression_key();
                LogUtil.i("Junwang", "key.length="+key.length+", value.length="+value.length);
                if(key.length != value.length){
                    LogUtil.i("Junwang", "loadRechargeNotificationLayout key.length != value.length error!");
                    mIsCardMsg = false;
                }else {
                    ((TextView) findViewById(R.id.card_title)).setText(ct.getCard_msg_title());
                    for (int i = 0; i < key.length; i++) {
                        if (i == 0) {
                            ((TextView) findViewById(R.id.item1_key)).setText(key[i]);
                            ((TextView) findViewById(R.id.item1_value)).setText(value[i]);
                        } else if (i == 1) {
                            ((TextView) findViewById(R.id.item2_key)).setText(key[i]);
                            ((TextView) findViewById(R.id.item2_value)).setText(value[i]);
                        } else if (i == 2) {
                            ((TextView) findViewById(R.id.item3_key)).setText(key[i]);
                            ((TextView) findViewById(R.id.item3_value)).setText(value[i]);
                        }
                    }
                    mIsCardMsg = true;
                    ((ExpandableTextView) findViewById(R.id.expand_tv_cztx)).setText(mData.getText());
                    addActionButton(ct, v);
                }
            }else {
                mIsCardMsg = false;
            }
        }
//        return isMatchPattern;
        return mIsCardMsg;
    }

    private boolean loadRechargeNotificationLayout(View v, LayoutInflater layoutInflater){
//        boolean isMatchPattern = false;
        LogUtil.i("Junwang", "loadRechargeNotificationLayout");
        if(v != null){
            mH5_content.removeAllViews();
            mH5_content.addView(v);
            ConversationActivity activity = (ConversationActivity)this.getContext();
            ((TextView)findViewById(R.id.card_title)).setText(activity.getCardTitle());
//            String values[] = mData.getText().split("^尊敬的客户：您好，((?:19|20)\\d\\d)年(0?[1-9]|1[0-2])月(0?[1-9]|[12][0-9]|3[01])日您成功充值([1-9][0-9]*(\\.\\d{1,2})?元)|(0\\.\\d{1,2}元)，当前您的账户余额是([1-9][0-9]*(\\.\\d{1,2})?元)|(0\\.\\d{1,2}元)");
//            for(int i=0; i<values.length; i++){
//                LogUtil.i("getRegValue", "values["+i+"]="+values[i]);
//            }
            boolean match = canMatchRegularExpression("^尊敬的客户：您好，((?:19|20)\\d\\d)年(0?[1-9]|1[0-2])月(0?[1-9]|[12][0-9]|3[01])日您成功充值([1-9][0-9]*(\\.\\d{1,2})?元)|(0\\.\\d{1,2}元)，当前您的账户余额是([1-9][0-9]*(\\.\\d{1,2})?元)|(0\\.\\d{1,2}元)", mData.getText());
            if(activity.getRegularExpression() != null){
                Map<String, String> map = getRegularExpression(activity.getRegularExpression());
                int i = 0;
                String value2 = null;
                for(Map.Entry<String, String> entry : map.entrySet()) {
                    if(i == 0) {
                        ((TextView) findViewById(R.id.item1_key)).setText(entry.getKey());
//                        String value1 = getValueFromRegularExpression(entry.getValue(), mData.getText());
                        String value1 = getValueFromRegularExpression("((?:19|20)\\d\\d)年(0?[1-9]|1[0-2])月(0?[1-9]|[12][0-9]|3[01])日", mData.getText()/*"尊敬的客户：您好，2020年1月1日您成功充值50元，当前您的账户余额是43.92元"*/, 0);
                        if(value1 != null) {
//                            isMatchPattern = true;
                            mIsCardMsg = true;
                            ((TextView) findViewById(R.id.item1_value)).setText(value1);
                        }else{
//                            ((TextView) findViewById(R.id.item1_value)).setText("没有匹配到日期");
                            LogUtil.i("Junwang", "没有匹配到日期");
                        }
                    }else if(i == 1) {
                        ((TextView) findViewById(R.id.item2_key)).setText(entry.getKey());
//                        String value2 = getValueFromRegularExpression(entry.getValue(), mData.getText());
                        value2 = getValueFromRegularExpression("([1-9][0-9]*(\\.\\d{1,2})?元)|(0\\.\\d{1,2}元)", mData.getText(), 0);
                        if(value2 != null) {
                            mIsCardMsg = true;
                            ((TextView) findViewById(R.id.item2_value)).setText(value2);
                        }else{
                            LogUtil.i("Junwang", "没有匹配到充值金额");
                        }
                    }else if(i == 2) {
                        ((TextView) findViewById(R.id.item3_key)).setText(entry.getKey());
//                        String value3 = getValueFromRegularExpression(entry.getValue(), mData.getText());
                        String value3 = null;
                        if(value2 != null) {
                            String subString = mData.getText().substring(mData.getText().indexOf(value2)+value2.length());
                            LogUtil.i("Junwang", "subString="+subString);
                            value3 = getValueFromRegularExpression("([1-9][0-9]*(\\.\\d{1,2})?元)|(0\\.\\d{1,2}元)", subString, 0);
                        }else{
                            value3 = getValueFromRegularExpression("([1-9][0-9]*(\\.\\d{1,2})?元)|(0\\.\\d{1,2}元)", mData.getText(), 0);
                        }
                        if(value3 != null) {
                            mIsCardMsg = true;
                            ((TextView) findViewById(R.id.item3_value)).setText(value3);
                        }else{
                            LogUtil.i("Junwang", "没有匹配到余额");
                        }
                    }
                    i++;
                }
            }
            ((ExpandableTextView)findViewById(R.id.expand_tv_cztx)).setText(mData.getText());
        }
//        return isMatchPattern;
        return mIsCardMsg;
    }

    private boolean loadVericationCodeNotificationLayout(View v, LayoutInflater layoutInflater){
        if(v != null){
            mH5_content.removeAllViews();
            mH5_content.addView(v);
//            ((TextView)findViewById(R.id.card_title)).setText();
//            ((TextView)findViewById(R.id.item1_key)).setText();
//            ((TextView)findViewById(R.id.item1_value)).setText();
//            TextView action = (TextView)findViewById(R.id.tv_action);
//            action.setText();
//            action.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }
        return true;
    }

    private boolean loadBuyTicketsSuccussfullyNotificationLayout(View v, LayoutInflater layoutInflater){
        if(v != null){
            mH5_content.removeAllViews();
            mH5_content.addView(v);
        }
        return true;
    }

    private void loadCenterVideoPicNews(News news, View v, LayoutInflater layoutInflater){
//        int resource = R.layout.item_center_video_news;
//        View v = layoutInflater.inflate(resource, null);
        if(v != null) {
            mH5_content.removeAllViews();
            mH5_content.addView(v);
            TextView tv_title = (TextView)findViewById(R.id.tv_title);
            TextView tv_bottom_right = (TextView)findViewById(R.id.tv_bottom_right);
            /*SantiVideoView*/ vv_video = (SantiVideoView)findViewById(R.id.vv_video);
            try {
                tv_title.setText(URLDecoder.decode(news.getTitle(), "utf-8"));
            }catch (Exception e){
                LogUtil.i("Junwang", "decode title exception "+e.toString());
                tv_title.setText(news.getTitle());
            }
            if (news.isHas_video()) {
                vv_video.initVideoView(news.getTitle(), news.getVideo_path(), null, news.getMiddle_image().url);
                tv_bottom_right.setText(TimeUtils.secToTime(news.getVideo_duration()));//设置时长
                LogUtil.i("Junwang", "video path="+news.getVideo_path());
                ((StandardVideoController)(vv_video.getController())).getStartPlayButton().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeButtonMenu();
                        if(vv_video != null){
                            DanmuVideoPlayActivity.start(mMessageWebView.getContext(), news.getVideo_path(), news.getTitle());
                        }
                    }
                });
//                vv_video.setOnScrollChangeListener(new OnScrollChangeListener() {
//                    @Override
//                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                        Rect scrollBounds = new Rect();
//                        if(getLocalVisibleRect(scrollBounds) && (scrollBounds.top >200) && (scrollBounds.height() == v.getMeasuredHeight())){
//                            LogUtil.i("Junwang", "SantiVideoView change to VISIBLE");
//                            vv_video.start();
//                        }else{
//                            LogUtil.i("Junwang", "SantiVideoView change to INVISIBLE");
//                            vv_video.stopPlayback();
//                        }
//                    }
//                });
            } else {
                vv_video.setVisibility(View.GONE);
                if (news.getImage_count() == 1){
                    tv_bottom_right.setCompoundDrawables(null, null, null, null);//去除TextView左侧图标
                }else{
                    tv_bottom_right.setCompoundDrawables(getContext().getResources().getDrawable(R.drawable.icon_picture_group), null, null, null);//TextView增加左侧图标
                    tv_bottom_right.setText(news.getImage_count() + UIUtils.getString(R.string.img_unit));//设置图片数
                }
                ImageView iv_img = (ImageView)findViewById(R.id.iv_img);
                iv_img.setVisibility(View.VISIBLE);
                GlideUtils.load(getContext(), news.getImage_list().get(0).url.replace("list/300x196", "large"), iv_img);//中间图片使用image_list第一张
            }
        }
    }

    private void setTimeText(View v){
        TextView tv_time = v.findViewById(R.id.tv_time);
        if(tv_time != null) {
            tv_time.setText(mData.getFormattedReceivedTimeStamp());
        }
    }

    public static int getListViewHeightBasedOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return 0;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        int heights= totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        return heights;
    }

    private void addNewsToExpandList(List<News> news){

    }

    private void expandList(List<News> news){
//        List<News> list = new ArrayList<News>();
//        News n1 = new News();
//        n1.title = "my test title1";
//        n1.middle_image = new ImageEntity();
//        n1.middle_image.url = "https://xh99.oss-cn-beijing.aliyuncs.com/weboss/19121216EF7AAD4FD.jpg";
//        News n2 = new News();
//        n2.title = "my test title2";
//        n2.middle_image = new ImageEntity();
//        n2.middle_image.url = "http://www.xinhuanet.com/video/2019-12/12/1210393161_15761395846321n.jpg";
//        list.add(n1);
//        list.add(n2);

//        mExpand.toggleExpand();
//        mExpand.setVisibility(View.VISIBLE);
        mLLTagBtn.setVisibility(View.GONE);
        View collapse_list = ((ViewStub)findViewById(R.id.collapse_list)).inflate();
        mExpand = (ExpandLayout)collapse_list.findViewById(R.id.expand);
//        mExpand = (ExpandLayout)findViewById(R.id.expand);
        mExpand_list = (ListView)findViewById(R.id.expand_list);
        mExpand_iv = (ImageView)findViewById(R.id.iv_expand);
        mExpand_list.setAdapter(new ExpandListViewAdapter(getContext(), news));
        mExpand_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WebViewNewsActivity.start(getContext(), ((News)parent.getItemAtPosition(position)).getUrl());
            }
        });
        int h = getListViewHeightBasedOnChildren(mExpand_list);
        LogUtil.i("ExpandLayout","height = "+h);

//        ViewGroup.LayoutParams lp = mH5_content.getLayoutParams();
//        lp.height = mH5_content.getHeight() + h;
//        mH5_content.setLayoutParams(lp);
//        mIsH5Expand = true;

        mExpand.setHeight(true, /*tv.getHeight()*/h, 0);
        //设置动画时间
        mExpand.setAnimationDuration(300);
        mExpand.expand();
    }

    private void initExpandList(View v, List<News> news){
        mLLTagBtn = (RelativeLayout) findViewById(R.id.ll_tag_btn);
        mLLTagBtn.setVisibility(View.VISIBLE);

//        RecyclerView recyclerView = findViewById(R.id.expand_recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        //清空记录展开还是关闭的缓存数据
//        ExpandableViewHoldersUtil.getInstance().resetExpanedList();
//        recyclerView.setAdapter(new ExpandableViewAdapter());

        mLLTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeButtonMenu();
                LogUtil.d("junwang", "点击事件");
                expandList(news);
            }
        });
        //折叠或者展开操作后的监听
//        mExpand.setOnToggleExpandListener(new ExpandLayout.OnToggleExpandListener() {
//            @Override
//            public void onToggleExpand(boolean isExpand) {
//                if (isExpand){
//                    mLLTagBtn.setVisibility(View.GONE);
//                    mExpand_iv.setBackgroundResource(R.drawable.icon_btn_collapse);
//                }else {
//                    mExpand_iv.setBackgroundResource(R.drawable.icon_btn_expand);
//                }
//            }
//        });
//        mExpand.initExpand(false ,/*getListViewHeightBasedOnChildren(mExpand_list)*/0);
    }

    //add by junwang for getMenus for the selected message
    private final ArrayList<CardTemplate> getCardTemplate(){
        String json = ((ConversationActivity)this.getContext()).getCardTemplate();
        ArrayList<CardTemplate> card_template = null;
        try {
            card_template = new Gson().fromJson(json, new TypeToken<List<CardTemplate>>(){}.getType());
        }catch (Exception e){
            LogUtil.e("junwang", "parse card msg template json exception: "+e.toString());
        }
        return card_template;
    }

    private boolean initCardMsg(){
//        boolean canMatchPattern = false;
//        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
//        int resource = 0;
//        View v = null;
//
//        ConversationActivity activity = (ConversationActivity)this.getContext();
//
//        resource = R.layout.layout_recharge_notification;
//        v = layoutInflater.inflate(resource, null);
//        if(LoadCardMsgTemplate(activity.getCardMsgTempNo(), v, layoutInflater)){
//            canMatchPattern = true;
//        }
//        if(v != null && canMatchPattern) {
//            mH5_content.setVisibility(View.VISIBLE);
//            setTimeText(v);
//        }
//        return canMatchPattern;
        ArrayList<CardTemplate> al = getCardTemplate();
        if(al == null){
            LogUtil.i("Junwang", "get card template == null");
            return false;
        }
        CardTemplate ct = getMatchRegularExpression(al, mData.getText());
        if(ct == null){
            LogUtil.i("Junwang", "getMatchRegularExpression == null");
            return false;
        }
        if(LoadCardTemplate(ct)){
            mH5_content.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    public void addExpandList(View v, News news){
        if(news.getNews_list() != null) {
            initExpandList(v, news.getNews_list());
        }
    }

    private void initH5Content(News news) {
        if(news == null){
            return;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        int resource = 0;
        View v = null;

        switch (News.getViewType(news)){
            case News.CENTER_SINGLE_VIDEO_NEWS:
            case News.CENTER_SINGLE_PIC_NEWS:
                resource = R.layout.item_center_video_news;
                v = layoutInflater.inflate(resource, null);
                loadCenterVideoPicNews(news, v,layoutInflater);
                addExpandList(v, news);
//                if(v != null) {
//                    mH5_content.removeAllViews();
//                    mH5_content.addView(v);
//                    TextView tv_title = (TextView)findViewById(R.id.tv_title);
//                    TextView tv_bottom_right = (TextView)findViewById(R.id.tv_bottom_right);
//                    ImageView iv_img = (ImageView)findViewById(R.id.iv_img);
//                    ImageView iv_playbutton =  (ImageView)findViewById(R.id.iv_play);
////                    iv_playbutton.setVisibility(View.GONE);
//                    SantiVideoView vv_video = (SantiVideoView)findViewById(R.id.vv_video);
//                    vv_video.initVideoView(news.title, news.video_path, iv_playbutton, news.middle_image.url);
////                    vv_video.setOnScrollChangeListener(new OnScrollChangeListener() {
////                        @Override
////                        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
////                            Rect scrollBounds = new Rect();
////                            if(getLocalVisibleRect(scrollBounds)){
////                                setVisibility(View.VISIBLE);
////                                if(iv_playbutton != null){
////                                    iv_playbutton.setVisibility(View.INVISIBLE);
////                                }
////                                LogUtil.i("Junwang", "SantiVideoView change to VISIBLE");
////                                vv_video.start();
////                            }else{
////                                setVisibility(View.INVISIBLE);
////                                if(iv_playbutton != null){
////                                    iv_playbutton.setVisibility(View.VISIBLE);
////                                }
////                                LogUtil.i("Junwang", "SantiVideoView change to INVISIBLE");
////                                vv_video.stopPlayback();
////                            }
////                        }
////                    });
////                    AutoPlayVideoView vv_video = (AutoPlayVideoView)findViewById(R.id.vv_video);
////                    tv_title.setText(news.title);
//                    try {
//                        tv_title.setText(URLDecoder.decode(news.title, "utf-8"));
//                    }catch (Exception e){
//                        LogUtil.i("Junwang", "decode title exception "+e.toString());
//                        tv_title.setText(news.title);
//                    }
//                    if (news.has_video) {
//                        tv_bottom_right.setText(TimeUtils.secToTime(news.video_duration));//设置时长
////                        GlideUtils.load(getContext(), news.middle_image.url, iv_img);//中间图片使用视频大图
//                        LogUtil.i("Junwang", "video path="+news.video_path);
////                        vv_video.setVisibility(View.INVISIBLE);
////                        initVideoView(vv_video, news.title, news.video_path);
////                        vv_video.setVideoPath(news.video_path);
////                        vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                            @Override
////                            public void onPrepared(MediaPlayer mp) {
////                                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
////                                    @Override
////                                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
////                                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
////                                            vv_video.setBackgroundColor(Color.TRANSPARENT);
////                                        return true;
////                                    }
////                                });
////                            }
////                        });
////                        try {
////                            Bitmap myBitmap = Glide.with(getContext())
////                                    .asBitmap()
////                                    .load(news.middle_image.url)
////                                    .centerCrop()
////                                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
////                                    .get();
//////                            vv_video.setBackground(new BitmapDrawable(this.getResources(), myBitmap));
////                            vv_video.initVideoView(news.title, news.video_path, iv_playbutton, myBitmap);
////                        }catch(Exception e){
////                            LogUtil.i("Junwang", "Glide get bitmap exception:"+e.toString());
////                        }
////                        vv_video.seekTo(1);
////                        Glide.with(getContext()).asDrawable().load(news.middle_image.url).listener(new RequestListener<Drawable>() {
////                            @Override
////                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
////                                return false;
////                            }
////
////                            @Override
////                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
////                                vv_video.setBackground(resource);
////                                return false;
////                            }
////                        });
////                        vv_video.setOnClickListener(this);
////                        vv_video.setMediaController(new MediaController(getContext()));
//                        iv_playbutton.setVisibility(View.GONE);
//                        ((StandardVideoController)(vv_video.getController())).getStartPlayButton().setOnClickListener(new OnClickListener() {
////                        iv_playbutton.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if(vv_video != null){
//                                    Rect rect = new Rect();
//                                    boolean cover = vv_video.getGlobalVisibleRect(rect);
//                                    LogUtil.i("SantiVideoView", "cover="+cover);
////                                    vv_video.setVisibility(View.VISIBLE);
////                                    vv_video.start();
////                                    iv_playbutton.setVisibility(View.INVISIBLE);
////                                    FullScreenVideoPlayActivity.start(getContext(), news.video_path);
////                                    Intent intent = new Intent(mMessageWebView.getContext(),
////                                            FullScreenVideoPlayActivity.class);
////                                    intent.putExtra(FullScreenVideoPlayActivity.URL, news.video_path);
////                                    Intent intent = new Intent(mMessageWebView.getContext(),
////                                            WebViewNewsActivity.class);
////                                    intent.putExtra(WebViewNewsActivity.URL, news.video_path);
////                                    mMessageWebView.getContext().startActivity(intent);
//                                    DanmuVideoPlayActivity.start(mMessageWebView.getContext(), news.video_path, news.title);
//                                }
//                            }
//                        });
//                    } else {
//                        vv_video.setVisibility(View.GONE);
//                        iv_playbutton.setVisibility(View.GONE);//隐藏播放按钮
//                        if (news.image_count == 1){
//                            tv_bottom_right.setCompoundDrawables(null, null, null, null);//去除TextView左侧图标
//                        }else{
//                            tv_bottom_right.setCompoundDrawables(getContext().getResources().getDrawable(R.drawable.icon_picture_group), null, null, null);//TextView增加左侧图标
//                            tv_bottom_right.setText(news.image_count + UIUtils.getString(R.string.img_unit));//设置图片数
//                        }
//                        GlideUtils.load(getContext(), news.image_list.get(0).url.replace("list/300x196", "large"), iv_img);//中间图片使用image_list第一张
//                    }
////                    ((TextView)findViewById(R.id.tv_title)).setText(news.title);
////                    GlideUtils.load(getContext(), news.image_list.get(0).url, (ImageView)findViewById(R.id.iv_img));
//                }
                break;
            case News.THREE_PICS_NEWS:
                resource = R.layout.item_three_picture;
                v = layoutInflater.inflate(resource, null);
                if(v != null) {
                    mH5_content.removeAllViews();
                    mH5_content.addView(v);
                    ((TextView) findViewById(R.id.tv_title)).setText(news.getTitle());
                    GlideUtils.load(getContext(), news.getImage_list().get(0).url, (ImageView) findViewById(R.id.iv_img1));
                    GlideUtils.load(getContext(), news.getImage_list().get(1).url, (ImageView) findViewById(R.id.iv_img2));
                    GlideUtils.load(getContext(), news.getImage_list().get(2).url, (ImageView) findViewById(R.id.iv_img3));
                }
                break;
            case News.RIGHT_PIC_VIDEO_NEWS:
//            case News.DIDI_DACHE:
                resource = R.layout.item_pic_video_news;
                v = layoutInflater.inflate(resource, null);
                if(v != null) {
                    mH5_content.removeAllViews();
                    mH5_content.addView(v);
                    ((TextView) findViewById(R.id.tv_title)).setText(news.getTitle());
                    TextView tv_dura = (TextView)findViewById(R.id.tv_duration);
                    if (news.isHas_video()) {
//                        ((LinearLayout)findViewById(R.id.ll_duration)).setVisibility(View.VISIBLE);//显示时长
                        tv_dura.setText(TimeUtils.secToTime(news.getVideo_duration()));//设置时长
                    } else {
                        ((LinearLayout)findViewById(R.id.ll_duration)).setVisibility(View.GONE);//隐藏时长
                    }
                    ImageView iv = (ImageView) findViewById(R.id.iv_img);
                    GlideUtils.load(getContext(), news.getMiddle_image().url, iv);//右侧图片或视频的图片使用middle_image
                    if(news.isHas_video()) {
                        iv.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closeButtonMenu();
                                DanmuVideoPlayActivity.start(mMessageWebView.getContext(), news.getVideo_path(), news.getTitle());
                            }
                        });
                    }
                    addExpandList(v, news);
                }
                break;
            case News.ZHIBO_NEWS:
                resource = R.layout.card_message_item;
                v = layoutInflater.inflate(resource, null);
                if(v != null){
                    mH5_content.removeAllViews();
                    mH5_content.addView(v);
                    ((TextView)findViewById(R.id.zhibo_title)).setText(news.getTitle());
                    ImageView zhiboImg = (ImageView) findViewById(R.id.zhibo_image);
                    GlideUtils.load(getContext(), news.isHas_video() ?  news.getMiddle_image().url : news.getImage_list().get(0).url, zhiboImg);
                    zhiboImg.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closeButtonMenu();
//                            getContext().startActivity(new Intent(getContext(), ProductDetailsActivity.class));
                            DanmuVideoPlayActivity.start(mMessageWebView.getContext(), news.getVideo_path(), news.getTitle());
                        }
                    });
                }
                break;
            case News.TWO_BUTTON:
                resource = R.layout.item_two_button_card_message;
                v = layoutInflater.inflate(resource, null);
                if(v != null){
                    mH5_content.removeAllViews();
                    mH5_content.addView(v);
//                    ((TextView)findViewById(R.id.business_name)).setText(news.title);
                    Button btn1 = (Button) findViewById(R.id.tv_button1);
//                    btn1.setText(news.bae_list.get(0).getBusnActionName());
                    btn1.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            closeButtonMenu();
                            WebViewNewsActivity.start(getContext(), news.getBae_list().get(0).getBusnWebUrl());
                        }
                    });
                    Button btn2 = (Button) findViewById(R.id.tv_button2);
//                    btn2.setText(news.bae_list.get(1).getBusnActionName());
                    btn2.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            closeButtonMenu();
                            WebViewNewsActivity.start(getContext(), /*news.bae_list.get(1).getBusnWebUrl()*/"https://common.diditaxi.com.cn/general/webEntry?wx=true&bizid=257&channel=70365#");
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(/*"https://common.diditaxi.com.cn/general/webEntry?wx=true&bizid=257&channel=70365"*/news.bae_list.get(1).getBusnWebUrl()));
////                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://common.diditaxi.com.cn/general/webEntry?channel=74113&maptype=wgs"));
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            getContext().startActivity(intent);
                        }
                    });
                    ImageView backImg = (ImageView) findViewById(R.id.back_image);
                    GlideUtils.load(getContext(), news.getMiddle_image().url, backImg);
                    backImg.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closeButtonMenu();
                            WebViewNewsActivity.start(getContext(), news.getUrl());
                        }
                    });
                }
                break;
            case News.THREE_BUTTON:
                resource = R.layout.item_three_button_card_message;
                v = layoutInflater.inflate(resource, null);
                if(v != null){
                    mH5_content.removeAllViews();
                    mH5_content.addView(v);
                    Button btn1 = (Button) findViewById(R.id.tv_button1);
                    btn1.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            closeButtonMenu();
                            WebViewNewsActivity.start(getContext(), news.getBae_list().get(0).getBusnWebUrl());
                        }
                    });
                    Button btn2 = (Button) findViewById(R.id.tv_button2);
                    btn2.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            closeButtonMenu();
                            WebViewNewsActivity.start(getContext(), news.getBae_list().get(1).getBusnWebUrl());
                        }
                    });
                    ImageView backImg = (ImageView) findViewById(R.id.back_image);
                    GlideUtils.load(getContext(), news.getMiddle_image().url, backImg);
                    backImg.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closeButtonMenu();
                            getContext().startActivity(new Intent(getContext(), ProductDetailsActivity.class));
                        }
                    });
                }
                break;
            default:
                resource = R.layout.item_three_picture;
        }

        if(v != null) {
            mH5_content.setVisibility(View.VISIBLE);
            setTimeText(v);
        }
        mH5_content.setOnLongClickListener(this);
        mH5_content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeButtonMenu();
                switch (v.getId()){
                    case R.id.vv_video:
                        break;
//                    case R.id.zhibo_image:
//                        getContext().startActivity(new Intent(getContext(), ProductDetailsActivity.class));
//                        break;
                    default:
                        /*if(News.getViewType(news) == News.DIDI_DACHE) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://common.diditaxi.com.cn/general/webEntry?wx=true&bizid=257&channel=70365"));
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://common.diditaxi.com.cn/general/webEntry?channel=74113&maptype=wgs"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(intent);
                        }else*/ {
                            WebViewNewsActivity.start(getContext(), news.getUrl());
                        }
                }
            }
        });
    }

    private boolean isCardMessage(){
        if(((ConversationActivity)(this.getContext())).getRegularExpression() != null){
            return true;
        }
        return false;
    }

    private boolean isH5Message(String jsonString){
        if(MessageData.BUGLE_STATUS_INCOMING_COMPLETE == mData.getStatus()
                || MessageData.BUGLE_STATUS_INCOMING_YET_TO_MANUAL_DOWNLOAD == mData.getStatus()) {
            if ((jsonString != null) && !jsonString.equals("a")) {
                try {
                    News temp = new Gson().fromJson(jsonString, News.class);
                    if (temp != null) {
                        initH5Content(temp);
                        mMessageTextView.setVisibility(View.GONE);
                        return true;
                    }
                }catch (Exception e){
                    LogUtil.i("Junwang", "isH5Message parse Json exception "+e.toString());
                }
            }
        }
        return false;
    }

    private boolean isMapView(){
        return false;
    }

    private void loadUrlInWebView(){
        mHasWebLinks = false;
        mWebUrls = new ArrayList<String>();
        Linkify.TransformFilter mentionFilter = new Linkify.TransformFilter(){
            public final String transformUrl(final Matcher match, final String url){
                LogUtil.i("Junwang", "UpdateMessageText url="+url);
                if(url != null){
                    mHasWebLinks = true;
                    mMessageTextView.setVisibility(View.GONE);
                    mWebUrls.add(url);
                }

                setWebViewSettings(mMessageWebView);
                mUrl = url;

                mMessageWebView.setOnTouchListener(new View.OnTouchListener(){
                    float mDownY;
                    boolean mOnclickToken;
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getActionMasked();
                        float webcontent = mMessageWebView.getContentHeight() * mMessageWebView.getScaleY();
                        LogUtil.i("Junwang", "webcontent="+webcontent);
                        View fc = mMessageWebView.getFocusedChild();
                        ArrayList<View> alv = mMessageWebView.getFocusables(View.FOCUS_DOWN);
                        for(View view: alv){
                            LogUtil.i("Junwang", "focusable view = "+view);
                        }
                        if(fc != null) {
                            LogUtil.i("Junwang", "focused child =" + fc);
                        }
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        if(action == MotionEvent.ACTION_DOWN){
                            mDownY = event.getY();
                            mOnclickToken = true;
                            isClickedOnWV = false;
                        }else if(action == MotionEvent.ACTION_MOVE){
                            mOnclickToken = false;
                            float h = mMessageWebView.getHeight()* mMessageWebView.getScaleY();
                            float webnow = h + mMessageWebView.getScrollY();
                            float defaultHeight = RFABTextUtil.dip2px(getContext(), 700.0f);
                            LogUtil.i("Junwang", "webnow="+webnow+", height="+h+", scale="+mMessageWebView.getScaleY()+", defaultH="+defaultHeight);
                            boolean canScrollUp = mMessageWebView.canScrollVertically(1);
                            boolean canScrollDown = mMessageWebView.canScrollVertically(-1);

                            if((!canScrollUp && event.getY()<mDownY) /*&& Math.abs(webnow - webcontent) < 1*/){
                                if(!(canScrollUp || canScrollDown) &&  !mMessageWebView.getIframeState() && (RFABTextUtil.dip2px(getContext(), webcontent) >= (mScreenHeight-300))){
                                    mMessageWebView.setIframeState(false);
                                    LogUtil.i("Junwang", "WebView iframe scroll1");
                                    return false;
                                }
                                mMessageWebView.setIframeState(false);
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                            }else if(!canScrollDown && (event.getY()>mDownY) && getScrollY() == 0){
                                if(!(canScrollUp || canScrollDown) && !mMessageWebView.getIframeState() && /*(webcontent >= 600)*/(RFABTextUtil.dip2px(getContext(), webcontent) >= (mScreenHeight-300))){
                                    mMessageWebView.setIframeState(false);
                                    LogUtil.i("Junwang", "WebView iframe scroll2");
                                    return false;
                                }
                                mMessageWebView.setIframeState(false);
                                v.getParent().requestDisallowInterceptTouchEvent(false);

                            }
                        }else if(event.getAction() == MotionEvent.ACTION_UP){
                            if(mOnclickToken){
                                setClickOnWebView(true);
                            }
                            getActivityFromView(mMessageWebView).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                        }
                        return false;
                    }
                });

                mMessageWebView.addJavascriptInterface(new Object(){
                    @JavascriptInterface
                    public void playing(){
                        LogUtil.i("Junwang", "start play video full screen");
                        mMessageWebView.getContext().startActivity(new Intent(mMessageWebView.getContext(), FullScreenVideoPlayActivity.class));

                    }
                    @JavascriptInterface
                    public void getVideo(){

                    }
                    @JavascriptInterface
                    public void getHTML(final String html) {
                        getActivityFromView(mMessageWebView).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.i("Junwang", "getHTML html="+html);
                                String test_html = "<head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=1023, initial-scale=0.3333333333333333,  minimum-scale=0.3333333333333333,maximum-scale=0.3333333333333333,user-scalable=no\"><meta content=\"telephone=no\" name=\"format-detection\"><meta http-equiv=\"Access-Control-Allow-Origin\" content=\"*\"><title>习近平讲述的故事 | 好官冯梦龙 - 新华社客户端</title><link rel=\"dns-prefetch\" href=\"//bt.zhongguowangshi.com\"><script src=\"https://hm.baidu.com/hm.js?12aae9df6cc49c807cea2ed5c9ec8e7e\"></script><script>var _hmt = _hmt || [];    (function() {      var hm = document.createElement(\"script\");      hm.src = \"https://hm.baidu.com/hm.js?12aae9df6cc49c807cea2ed5c9ec8e7e\";      var s = document.getElementsByTagName(\"script\")[0];       s.parentNode.insertBefore(hm, s);    })();</script><script type=\"text/javascript\" charset=\"utf-8\" src=\"https://g.alicdn.com/de/prismplayer/2.8.2/aliplayer-min.js\"></script><script src=\"//res.wx.qq.com/open/js/jweixin-1.0.0.js\"></script><link href=\"/vh512static/static/css/app.4d90fd4496c362aa8bd9441288c7e6bf.css\" rel=\"stylesheet\"></head><body style=\"margin:0;padding:0\"><div id=\"app\"><div keep-alive=\"\" transition=\"\" transition-mode=\"out-in\" class=\"view\"><div data-v-5a01d1a5=\"\" class=\"load-container\" style=\"display: none;\"><div data-v-5a01d1a5=\"\" class=\"loading\"><img data-v-5a01d1a5=\"\" src=\"/vh512static/static/img/load-earth.7c2461e.png\" alt=\"\"> <img data-v-5a01d1a5=\"\" src=\"/vh512static/static/img/load-text.c0c88cc.png\" alt=\"\"> <img data-v-5a01d1a5=\"\" src=\"/vh512static/static/img/load-highlight.81da616.png\" alt=\"\" class=\"load-highlight\"></div></div> <!----> <div data-v-76b82cae=\"\" class=\"content-container\"><div data-v-69250c6e=\"\" data-v-76b82cae=\"\" class=\"banner-container\"><a data-v-69250c6e=\"\" href=\"https://a.app.qq.com/o/simple.jsp?pkgname=net.xinhuamm.mainclient&amp;android_schema=xhpfm%3A%2F%2Fnews%2F%3Fid%3D6550437%26newstype%3D1001\" class=\"banner-bg\"><!----> <!----></a></div> <header data-v-76b82cae=\"\" class=\"news-basic\"><h1 data-v-76b82cae=\"\">习近平讲述的故事 | 好官冯梦龙</h1> <div data-v-76b82cae=\"\" class=\"scrible-container\"><div data-v-76b82cae=\"\" class=\"headimg\"><img data-v-76b82cae=\"\" src=\"https://img-xhpfm.zhongguowangshi.com/Column/201903/de2e4d4f39c74e55a1a1716ae1e47207.jpg@120w_120h_4e_1c_80Q_2o_1x_237-237-237bgc.png\"></div> <div data-v-76b82cae=\"\" class=\"s-sub\"><p data-v-76b82cae=\"\" class=\"s-sub-main\">新视频</p> <p data-v-76b82cae=\"\" class=\"s-sub-sub\">2019-10-09 08:29:18</p></div> <a data-v-76b82cae=\"\" class=\"s-btn s-inverse\"><span data-v-76b82cae=\"\">查看</span></a></div> <div data-v-76b82cae=\"\" class=\"radio-container\"><p data-v-76b82cae=\"\" class=\"source\">来源：新华社</p> <span data-v-76b82cae=\"\" class=\"line\"></span> <p data-v-76b82cae=\"\" class=\"pull-right\">浏览量：1444257</p></div> <div data-v-76b82cae=\"\" class=\"news-abstract\"><p data-v-76b82cae=\"\" class=\"abstract\"></p></div></header> <section data-v-76b82cae=\"\" class=\"head-video-container\"><div data-v-76b82cae=\"\" class=\"video-container link-video\"><video data-v-76b82cae=\"\" src=\"https://vod-xhpfm.zhongguowangshi.com/NewsVideo/201910/443fc211710f494b977e1cdfce312a41.mp4\" poster=\"https://img-xhpfm.zhongguowangshi.com/News/201910/20191009083142_2601.jpg\" controls=\"controls\" x5-playsinline=\"\" webkit-playsinline=\"\"></video></div></section> <div data-v-76b82cae=\"\"><section class=\"main-text-container fold-content\" style=\"height: 2298.67px;\"><p style=\"text-indent: 2em; min-height: 1px;\">\n" +
                                        "    \t当前，第二批“不忘初心、牢记使命”主题教育活动正在扎实推进。以史为鉴，面向未来。习近平总书记曾经在不同场合讲述一代文豪冯梦龙在福建寿宁为官的故事。\n" +
                                        "    </p> <p style=\"text-indent: 2em; min-height: 1px;\">\n" +
                                        "    \t为什么总书记会多次为冯梦龙点赞？冯梦龙究竟为当地百姓做了哪些事？本期《习近平讲述的故事》为您娓娓道来。\n" +
                                        "    </p> <p style=\"text-indent: 2em; min-height: 1px;\"><a href=\"https://img-xhpfm.zhongguowangshi.com/News/201910/aa5dedf07c1a45b3a34538db720e78a";
                                if(mMessageWebView != null) {
                                    mMessageWebView.loadUrl(ConversationWebViewJsInject.fullScreenByJs(test_html));
                                }
                            }
                        });
                    }

                }, "local_obj");
//                        mMessageWebView.setOnTouchListener(new OnTouchListener() {
//
//                            @Override
//                            public boolean onTouch(View v, MotionEvent ev) {
//
//                                ((WebView)v).requestDisallowInterceptTouchEvent(true);
//
//                                return false;
//                            }
//                        });
//                        mMessageWebView.setOnScrollChangeListener(new OnScrollChangeListener() {
//                            @Override
//                            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                                // webview的高度
//                                float webcontent = mMessageWebView.getContentHeight() * mMessageWebView.getScaleY();
//                                // 当前webview的高度
//                                ((WebView)v).requestDisallowInterceptTouchEvent(true);
//                                onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
//                                float webnow = getHeight() + getScrollY();
//                                if (!mMessageWebView.canScrollVertically(1) &&
//                                        scrollY>oldScrollY && (Math.abs(webcontent - webnow) < 1)) {
//                                    //处于底端
//                                    ((WebView)v).requestDisallowInterceptTouchEvent(false);
//                                } else if (!mMessageWebView.canScrollVertically(-1)
//                                        && (scrollY <= oldScrollY) &&getScrollY() == 0) {
//                                    //处于顶端
//                                    ((WebView)v).requestDisallowInterceptTouchEvent(false);
//                                } else {
//                                    ((WebView)v).requestDisallowInterceptTouchEvent(true);
//                                    onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
//                                }
//                            }
//
//
//                        });
                {
                    mMessageWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            mUpdatedText = true;
                            if(mMessageWebView != null) {
                                ConversationMessageView cmv = (ConversationMessageView)findViewWithTag(mData.getText());
                                LogUtil.w("Junwang", "Update text getTag="+mData.getText());
                                if(cmv != null){
                                    cmv.mMessageWebView.loadUrl(mUrl);
                                }else{
                                    return;
                                }
                            }else{
                                LogUtil.e("Junwang", "mMessageWebView is null, don't load url");
                            }
                        }
                    });
                }
                mMessageWebView.setWebViewClient(new SantiWebViewClient(mMessageWebView.getContext(),
                                            getActivityFromView(mMessageWebView), mMessageWebView, mUpdatedText));
                mMessageWebView.setWebChromeClient(new SantiWebChromeClient(mMessageWebView.getContext(), getActivityFromView(mMessageWebView)));
                return url;
            }
        };

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(Linkify.addLinks(mMessageTextView, Linkify.WEB_URLS)) {
                Linkify.addLinks(mMessageTextView, Patterns.WEB_URL, "https://", new String[]{"http://", "https://", "rtsp://"}, null, mentionFilter);
            }else{
                Linkify.addLinks(mMessageTextView, Linkify.ALL);
                mMessageTextView.setVisibility(View.VISIBLE);
            }
        }else{
            if(Linkify.addLinks(mMessageTextView, Linkify.WEB_URLS)) {
                LinkifyCompat.addLinks(mMessageTextView, Patterns.WEB_URL, "https://", new String[]{"http://", "https://", "rtsp://"}, null, mentionFilter);
            }else {
                LinkifyCompat.addLinks(mMessageTextView, Linkify.ALL);
                mMessageTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void sendResponseChatbotMsg(String messageId){
        //Need to do: origin messageId to RCS DB messageId
        int rcs_msgId = 0;
        RcsDatabaseUtils.queryMessage(rcs_msgId).subscribe(new Consumer<MessageEntity>() {
            @Override
            public void accept(MessageEntity messageEntity) throws Exception {
                if (messageEntity != null) {
                    LogUtil.i("Junwang", "send response chatbot message content:" + messageEntity.getSubject());
                    ResponseChatbot rc = new ResponseChatbot();
                    rc.response.reply.displayText = "press button";
                    rc.response.reply.postback.data = "postback data";
                    String jsonContent = JSON.toJSONString(rc);
                    SendRcsMsgUtils.sendSuggestedChipResponseMessage(getContext(), messageEntity.getContactUri(),
                            messageEntity.getConversationUuid(), messageEntity.getContributionUuid(),
                            messageEntity.getMaapTrafficType(), messageEntity.getSubject(), jsonContent,
                            true, messageEntity.getContactIdentityType(), messageEntity.getSubId(),
                            messageEntity.getContributionUuid(),messageId);
                } else {
                    LogUtil.i("Junwang", "send response chatbot message is null");
                }
            }
        });
    }

    private void setSuggestionsView(CardContent cardcontent, View text_suggestion_view){
        SuggestionActionWrapper[] saw = cardcontent.getSuggestionActionWrapper();
        if(saw != null && saw.length>0){
            LinearLayout ll1 = (LinearLayout)text_suggestion_view.findViewById(R.id.text_suggestion_layout);
            int i = 0;
            int j = 2;
            TextView tv1;
            for(; i<saw.length; i++){
                if(saw[i].action != null) {
                    tv1 = new TextView(getContext());
//                    tv1.setPadding(0,4,0,0);
                    tv1.setText(saw[i].action.displayText);
//                    tv1.setWidth(120);
                    tv1.setTextColor(Color.GREEN);
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setPadding(0, 15, 0, 0);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ll1.addView(tv1, j, lp);
//                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(120, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        tv1.setLayoutParams(lp);
                    SuggestionAction sa = saw[i].action;
                    if ((sa != null) && (sa.urlAction != null)) {
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                WebViewNewsActivity.start(getContext(), sa.urlAction.openUrl.url);
                            }
                        });
                    }else if((sa != null) && (sa.dialerAction != null)){
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                closeButtonMenu();
                                NativeFunctionUtil.callNativeFunction(CardTemplate.NativeActionType.PHONE_CALL, getActivityFromView(mMessageTextView),
                                        null, null, sa.dialerAction.dialPhoneNumber.phoneNumber);
                            }
                        });
                    }else if((sa != null) && (sa.mapAction != null)){
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                closeButtonMenu();
                                NativeFunctionUtil.openLocation(sa.mapAction.showLocation.location.label, sa.mapAction.showLocation.location.latitude,
                                        sa.mapAction.showLocation.location.longitude, getActivityFromView(mMessageTextView));
                            }
                        });
                    }
                }
                if(saw[i].reply != null){
                    tv1 = new TextView(getContext());
//                    tv1.setPadding(0,4,0,0);
                    tv1.setText(saw[i].reply.displayText);
                    tv1.setTextColor(Color.GREEN);
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setPadding(0, 15, 0, 0);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ll1.addView(tv1, j, lp);
//                    tv1.setLayoutParams(lp);
                }
            }
        }
    }

    private boolean loadHorizontalCard(int resource, CardContent cardcontent, boolean isImageLeft){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(resource, null);
//        view.setMinimumWidth(300);
        if(view != null) {
            LogUtil.i("Junwang", "loadHorizontalCard");
            View text_suggestion_view = layoutInflater.inflate(R.layout.item_chatbot_text_suggestions_layout, null);
            TextView tv_title = (TextView)text_suggestion_view.findViewById(R.id.hor_title);
            TextView tv_description = (TextView)text_suggestion_view.findViewById(R.id.hor_description);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayout ll = (LinearLayout)view.findViewById(R.id.hori_relativelayout);
            if(isImageLeft){
                ll.addView(text_suggestion_view,1);
            }else{
                ll.addView(text_suggestion_view,0);
            }
            setSuggestionsView(cardcontent, text_suggestion_view);

            tv_title.setText(cardcontent.getTitle());
            tv_description.setText(cardcontent.getDescription());
            ImageView iv = (ImageView)view.findViewById(R.id.hor_image);
            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(20));//图片圆角为30

            Glide.with(this).load(cardcontent.getMedia().getMediaUrl())
                    .apply(options)
                    .into(iv);
//            loadMoreAction(view);
            mH5_content.removeAllViews();
            mH5_content.addView(view);

            mH5_content.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    closeButtonMenu();
//                    showBottomPop(null);
                }
            });
            setTimeText(view);
            return true;
        }
        return false;
    }

    private boolean loadVerticalCard(int resource, CardContent cardcontent, boolean isImageTop){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(resource, null);
//        view.setMinimumWidth(300);
        if(view != null) {
            LogUtil.i("Junwang", "loadVerticalCard");
            View text_suggestion_view = layoutInflater.inflate(R.layout.item_chatbot_text_suggestions_layout, null);
            TextView tv_title = (TextView)text_suggestion_view.findViewById(R.id.hor_title);
            TextView tv_description = (TextView)text_suggestion_view.findViewById(R.id.hor_description);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout ll = (LinearLayout)view.findViewById(R.id.hori_relativelayout);
            if(isImageTop){
                ll.addView(text_suggestion_view,1, lp);
            }else{
                ll.addView(text_suggestion_view,0, lp);
            }
            setSuggestionsView(cardcontent, text_suggestion_view);

            tv_title.setText(cardcontent.getTitle());
            tv_description.setText(cardcontent.getDescription());
            ImageView iv = (ImageView)view.findViewById(R.id.ver_image);
            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(20));//图片圆角为30

            Glide.with(this).load(cardcontent.getMedia().getMediaUrl())
                    .apply(options)
                    .into(iv);
//            loadMoreAction(view);
            mH5_content.removeAllViews();
            mH5_content.addView(view);

            mH5_content.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    closeButtonMenu();
//                    showBottomPop(null);
                }
            });
            setTimeText(view);
            return true;
        }
        return false;
    }

    private boolean loadSingelCardChatbotMsg(ChatbotCard cbc){
        try {
            CardContent cc = cbc.getMessage().getGeneralPurposeCard().getContent();
            CardLayout cl = cbc.getMessage().getGeneralPurposeCard().getLayout();
            if(cl != null){
                if("HORIZONTAL".equals(cl.getCardOrientation())){
//                    loadHorizontalCard(R.layout.item_chatbot_horizontal_layout_card, cc, "LEFT".equals(cl.getImageAlignment()) ? true : false);
                    loadVerticalCard(R.layout.item_chatbot_vertical_layout_card, cc, "LEFT".equals(cl.getImageAlignment()) ? true : false);
                }else{
                    loadVerticalCard(R.layout.item_chatbot_vertical_layout_card, cc, "TOP".equals(cl.getImageAlignment()) ? true : false);
                }
            }

            mH5_content.setVisibility(View.VISIBLE);
        }catch (Exception e){
            LogUtil.i("Junwang", "parse chatbot json exception "+e.toString());
            return false;
        }
        mIsCardMsg = true;
        return true;
    }

    //ps:此函数必须在线程中调用（因为函数抛出异常，调用时要包在try--catch里面）
    private void postSubscribeRequest(String requesturl, String msgId) throws Exception{
//        String urlPath = new String(url);
        //String urlPath = new String("http://localhost:8080/Test1/HelloWorld?name=丁丁".getBytes("UTF-8"));
//        String param="msgId="+ URLEncoder.encode("丁丁","UTF-8");
        String param = "msgId="+msgId;
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
//        httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
        httpConn.setRequestProperty("Charset", "UTF-8");
        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
        httpConn.connect();
        //建立输入流，向指向的URL传入参数
        DataOutputStream dos=new DataOutputStream(httpConn.getOutputStream());
        dos.writeBytes(param);
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
            LogUtil.i("Junwang", "get postSubscribeRequest response "+sb.toString());
        }
    }

    //ps:此函数必须在线程中调用（因为函数抛出异常，调用时要包在try--catch里面）
    private String postVoteRequest(String requesturl, String msgId, int voteOption, boolean isVoteCard) throws Exception{
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

    private String getVoteRefreshData(String requesturl, String msgId) throws Exception{
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

    private boolean loadTypeCard(RcsContant.CardType type, ChatbotCard cbc, CardContent cc){
        switch (type){
            case ACTIVITY_SUB:
                return loadActivitySubscribeChatbotMessage(R.layout.item_activity_subscribe_card, cc);
            case VOTE:
                return /*loadPictureCardChatbotMessage(R.layout.item_chatbot_pic_card, cc);*/loadVoteCardChatbotMessage(R.layout.item_vote_card, cc);
            case VIDEO_NEWS:
                return loadVideoNewsChatbotMessage(R.layout.item_chatbot_video_news_card,cc);
//            case PRODUCT_RECOMMEND:
//                return loadProductRecommendChatbotMessage(cbc.getMessage().getGeneralPurposeCard().getContent());
            case SUB_ACTIVITY_START:
                return loadActivitySubscribeStartChatbotMessage(R.layout.item_activity_subscribe_start_card, cc);
            case PRODUCT_ORDER:
                return loadOrderInfoPushChatbotMessage(R.layout.item_order_info_push_card, cc);
            default:
                if(RcsContant.RcsTypes.MIME_IMAGE.equals(mData.getmContentType())){
                    return loadPictureCardChatbotMessage(R.layout.item_chatbot_pic_card, cc);
                }
                return loadSingelCardChatbotMsg(cbc);
        }
//        return false;
    }

    private boolean ParseSingleCardChatbotMsg(String text){
        try {
            ChatbotCard cbc = new Gson().fromJson(text, ChatbotCard.class);
            CardContent cc = cbc.getMessage().getGeneralPurposeCard().getContent();
            if(cc != null){
                LogUtil.i("Junwang", "load cardType "+cc.getCardType()+" chatbot view");
                if(loadTypeCard(RcsContant.CardType.valueOf(cc.getCardType()), cbc, cc)){
                    mH5_content.setVisibility(View.VISIBLE);
                    mIsCardMsg = true;
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

    private boolean ParseMultiCardChatbotMsg(String text){
        try {
            ChatbotMultiCard cbc = new Gson().fromJson(text, ChatbotMultiCard.class);
            GeneralPurposeCardCarousel gpcc = cbc.getMessage().getGeneralPurposeCardCarousel();
            String title = null;
            if(gpcc != null) {
                CardContent[] cardcontents = gpcc.getContent();
                if((cardcontents != null) && (cardcontents.length>0)){
                    loadProductRecommendCard(cardcontents);
//                    loadProductRecommendChatbotMessage(cardcontents);
//                    loadChatbotMulticardView(cardcontents);
//                    loadChatbotView(cardcontents);
//                    for(int i=0; i<cardcontents.length; i++){
//                        title = cardcontents[i].getTitle();
//                        LogUtil.i("Junwang", "card " + i + " media url="+cardcontents[i].getMedia().getMediaUrl()+", media type="+cardcontents[i].getMedia().getMediaContentType());
//                        SuggestionActionWrapper[] sa = cardcontents[i].getSuggestionActionWrapper();
//                        if((sa != null) && (sa.length > 0)){
//                            for (int j = 0; j < sa.length; j++) {
//                                if ((sa[j].action != null) && (sa[j].action.urlAction != null)) {
//                                    LogUtil.i("Junwang", "chatbot card message url action displayText is " + sa[j].action.displayText+", url="+sa[j].action.urlAction.openUrl.url);
//                                }else if((sa[j].action != null) && (sa[j].action.dialerAction != null)){
//                                    LogUtil.i("Junwang", "chatbot card message dial action displayText is " + sa[j].action.displayText+", postback="+sa[j].action.postback.data);
//                                }else if((sa[j].action != null) && (sa[j].action.mapAction != null)){
//                                    LogUtil.i("Junwang", "chatbot card message map action displayText is " + sa[j].action.displayText);
//                                }
//                            }
//                        }
//                    }
                }
            }
        }catch (Exception e){
            LogUtil.i("Junwang", "parse multicard chatbot message exception "+e.toString());
            return false;
        }
        return true;
    }

    private void updateMessageText() {
        final String text = mData.getText();
        if (!TextUtils.isEmpty(text)) {
            String jsonString = mData.getJson();
            LogUtil.i("Junwang", "text="+text+", json="+jsonString);
//            if(text.equals("http://172.16.30.54:1995/zhibo")){
//                loadChatbotView();
//                return;
//            }
            if(text.startsWith("{")){
                if(text.indexOf("generalPurposeCardCarousel") != -1) {
                    LogUtil.i("Junwang", "multi card chatbot message");
                    if(ParseMultiCardChatbotMsg(text)){
                        return;
                    }
                }else if(text.indexOf("generalPurposeCard") != -1){
                    LogUtil.i("Junwang", "single card chatbot message");
                    if(ParseSingleCardChatbotMsg(text)){
                        return;
                    }
                }
            }
            else if(/*isCardMessage() &&*/ initCardMsg()){
                return;
            }
            else if(isH5Message(jsonString)){
                autoPopupFloatingWebview(text);
                return;
            }else if(isMapView()){
                mMapView.setVisibility(View.VISIBLE);
                BaiduMapView bv = new BaiduMapView(mMapView);
                bv.initMap();
                mMapView.getMap().setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Message message = mHandler.obtainMessage();
                        message.what = OPEN_LOCATION;
                        mHandler.sendMessage(message);
//                        mMessageWebView.getContext().startActivity(new Intent(mMessageWebView.getContext(), BaiduMapTestActivity.class));
                    }

                    @Override
                    public boolean onMapPoiClick(MapPoi mapPoi) {
                        return false;
                    }
                });
                return;
            }
            setClickableText(text);
            /*if(mIsContactInWhiteList && isNetworkConnected(mMessageWebView.getContext())
                    && display_h5_sms()){
                loadUrlInWebView();
            }else*/{
                // Linkify phone numbers, web urls, emails, and map addresses to allow users to
                // click on them and take the default intent.
                mMessageTextHasLinks = Linkify.addLinks(mMessageTextView, Linkify.ALL);
                mMessageTextView.setVisibility(View.VISIBLE);
//                BusinessCardService.do_exec(" am start -a android.intent.action.VIEW -d "+"https://www.baidu.com");
            }
            autoPopupFloatingWebview(text);
        } else {
            mMessageTextView.setVisibility(View.GONE);
            mMessageTextHasLinks = false;
        }
    }

    private void autoPopupFloatingWebview(String text){
        if(text == null || (text.length() == 0)){
            return;
        }
        Activity a = getActivityFromView(mMessageTextView);
        if(a != null){
            LogUtil.i("Junwang", "get activity from mMessageTextView "+a);
            if(a instanceof ConversationActivity){
                int tc = ((ConversationActivity) a).getTriggerCode();
                String mc = ((ConversationActivity) a).getMatcher();
                String ru = ((ConversationActivity) a).getResponseUrl();
                LogUtil.i("Junwang", "a is ConversationActivity "+", tc="+tc+", mc="+mc+", ru="+ru);
                    tc = 1;
                    mc = "^我的位置";
                    ru = "https://common.diditaxi.com.cn/general/webEntry?wx=true&bizid=257&channel=70365";
                if(tc == 1){
                    if(mc != null) {
                        Pattern pattern = Pattern.compile(mc);
                        Matcher matcher = pattern.matcher(text);
                        if (/*text.equals(mc)*/matcher != null && matcher.find()) {
                            //                            FloatingButton.popupFloatingWindow(getContext().getApplicationContext(), null, 0.2f, 0.1f,
                            //                                    0.6f, 0.8f, MoveType.inactive, "floatingbt",
                            //                                    null, null);
                            //                            Button bt1 = FloatingButton.popupFloatingWindow(getContext().getApplicationContext(), null, RFABTextUtil.dip2px(getContext(), 150), RFABTextUtil.dip2px(getContext(), 35),
                            //                                    RFABTextUtil.dip2px(getContext(), 200), RFABTextUtil.dip2px(getContext(), 500), MoveType.inactive, null,
                            //                                    null, null);
                            //                            FloatingButton.popupFloatingWindow(getContext().getApplicationContext(), null, RFABTextUtil.dip2px(getContext(), 150), RFABTextUtil.dip2px(getContext(), 35),
                            //                                    bt1.getX()-RFABTextUtil.dip2px(getContext(), 40), RFABTextUtil.dip2px(getContext(), 500), MoveType.inactive, "叫外卖",
                            //                                    null, null);
                            //                            if (!FloatWindow.get("百度地图").isShowing()) {
                            //                                FloatWindow.get("百度地图").show();
                            //                            }
                            //                            if (!FloatWindow.get("叫外卖").isShowing()) {
                            //                                FloatWindow.get("叫外卖").show();
                            //                            }
                            WebView wv = FloatingWebView.popupFloatingWindow(getContext().getApplicationContext(), ru, 0.8f, 0.6f,
                                    0.1f, 0.2f, MoveType.inactive, null, null);
                            ObjectAnimator alpha = ObjectAnimator.ofFloat(wv, "alpha", 0.0f, 1.0f);
                            ObjectAnimator translationX = ObjectAnimator.ofFloat(wv, "translationX", 0f, 200.0f, 0.0f);
                            ObjectAnimator rotationY = ObjectAnimator.ofFloat(wv, "rotationY", 0.0f, 360.0f);
                            ObjectAnimator animator = ObjectAnimator.ofFloat(wv, "scaleX", 1.0f, 0.5f, 1.0f);
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(alpha).with(translationX);//.before(animator);
                            animatorSet.setDuration(2000);
                            animatorSet.start();
                            //                            animator.setDuration(2000);
                            //                            animator.start();

                            //                            WebView targetView = new WebView(getContext());
                            //                            FloatingWebView.setWebViewSetting(targetView);
                            //                            targetView.loadUrl(ru);
                            //                            ViewGroup.LayoutParams lp = targetView.getLayoutParams();
                            //                            lp.width = ViewGroup.LayoutParams.MATCH_PARENT*3/2;
                            //                            lp.height = ViewGroup.LayoutParams.MATCH_PARENT/2;
                            //                            targetView.setLayoutParams(lp);
                            //
                            ////                            AnyLayer.dialog(getActivityFromView(mMessageTextView))
                            //                            AnyLayer.dialog()
                            //                                    .contentView(targetView)
                            //                                    .gravity(Gravity.CENTER)
                            //                                    .contentAnimator(new Layer.AnimatorCreator(){
                            //                                        @Override
                            //                                        public Animator createInAnimator(View content) {
                            //                                            return AnimatorHelper.createTopInAnim(content);
                            //                                        }
                            //
                            //                                        @Override
                            //                                        public Animator createOutAnimator(View content) {
                            //                                            return AnimatorHelper.createTopOutAnim(content);
                            //                                        }
                            //                                    })
                            //                                    .show();
                        }
                    }
                }
            }
        }
    }

    private void updateViewAppearance() {
        final Resources res = getResources();
        final ConversationDrawables drawableProvider = ConversationDrawables.get();
        final boolean incoming = mData.getIsIncoming();
        final boolean outgoing = !incoming;
        final boolean showArrow =  false;//shouldShowMessageBubbleArrow();

        final int messageTopPaddingClustered =
                res.getDimensionPixelSize(R.dimen.message_padding_same_author);
        final int messageTopPaddingDefault =
                res.getDimensionPixelSize(R.dimen.message_padding_default);
        final int arrowWidth = /*res.getDimensionPixelOffset(R.dimen.message_bubble_arrow_width)*/0;
        final int messageTextMinHeightDefault = res.getDimensionPixelSize(
                R.dimen.conversation_message_contact_icon_size);
        final int messageTextLeftRightPadding = res.getDimensionPixelOffset(
                R.dimen.message_text_left_right_padding);
        final int textTopPaddingDefault = res.getDimensionPixelOffset(
                R.dimen.message_text_top_padding);
        final int textBottomPaddingDefault = res.getDimensionPixelOffset(
                R.dimen.message_text_bottom_padding);

        // These values depend on whether the message has text, attachments, or both.
        // We intentionally don't set defaults, so the compiler will tell us if we forget
        // to set one of them, or if we set one more than once.
        final int contentLeftPadding, contentRightPadding;
        final Drawable textBackground;
        final int textMinHeight;
        final int textTopMargin;
        final int textTopPadding, textBottomPadding;
        final int textLeftPadding, textRightPadding;

        if (mData.hasAttachments()) {
            if (shouldShowMessageTextBubble()) {
                // Text and attachment(s)
                contentLeftPadding = incoming ? arrowWidth : 0;
                contentRightPadding = outgoing ? arrowWidth : 0;
                textBackground = drawableProvider.getBubbleDrawable(
                        isSelected(),
                        incoming,
                        false /* needArrow */,
                        mData.hasIncomingErrorStatus());
                textMinHeight = messageTextMinHeightDefault;
                textTopMargin = messageTopPaddingClustered;
                textTopPadding = textTopPaddingDefault;
                textBottomPadding = textBottomPaddingDefault;
                textLeftPadding = messageTextLeftRightPadding;
                textRightPadding = messageTextLeftRightPadding;
            } else {
                // Attachment(s) only
                contentLeftPadding = incoming ? arrowWidth : 0;
                contentRightPadding = outgoing ? arrowWidth : 0;
                textBackground = null;
                textMinHeight = 0;
                textTopMargin = 0;
                textTopPadding = 0;
                textBottomPadding = 0;
                textLeftPadding = 0;
                textRightPadding = 0;
            }
        } else {
            // Text only
            contentLeftPadding = (!showArrow && incoming) ? arrowWidth : 0;
            contentRightPadding = (!showArrow && outgoing) ? arrowWidth : 0;
            textBackground = drawableProvider.getBubbleDrawable(
                    isSelected(),
                    incoming,
                    shouldShowMessageBubbleArrow(),
                    mData.hasIncomingErrorStatus());
            textMinHeight = messageTextMinHeightDefault;
            textTopMargin = 0;
            textTopPadding = textTopPaddingDefault;
            textBottomPadding = textBottomPaddingDefault;
            if (showArrow && incoming) {
                textLeftPadding = messageTextLeftRightPadding + arrowWidth;
            } else {
                textLeftPadding = messageTextLeftRightPadding;
            }
            if (showArrow && outgoing) {
                textRightPadding = messageTextLeftRightPadding + arrowWidth;
            } else {
                textRightPadding = messageTextLeftRightPadding;
            }
        }

        // These values do not depend on whether the message includes attachments
        final int gravity = incoming ? (Gravity.START | Gravity.CENTER_VERTICAL) :
                (Gravity.END | Gravity.CENTER_VERTICAL);
        final int messageTopPadding = shouldShowSimplifiedVisualStyle() ?
                messageTopPaddingClustered : messageTopPaddingDefault;
        final int metadataTopPadding = res.getDimensionPixelOffset(
                R.dimen.message_metadata_top_padding);

        // Update the message text/info views
        ImageUtils.setBackgroundDrawableOnView(mMessageTextAndInfoView, textBackground);
        mMessageTextAndInfoView.setMinimumHeight(textMinHeight);
        final LinearLayout.LayoutParams textAndInfoLayoutParams =
                (LinearLayout.LayoutParams) mMessageTextAndInfoView.getLayoutParams();
        textAndInfoLayoutParams.topMargin = textTopMargin;

        if (UiUtils.isRtlMode()) {
            // Need to switch right and left padding in RtL mode
            mMessageTextAndInfoView.setPadding(textRightPadding, textTopPadding, textLeftPadding,
                    textBottomPadding);
            mMessageBubble.setPadding(contentRightPadding, 0, contentLeftPadding, 0);
        } else {
            mMessageTextAndInfoView.setPadding(textLeftPadding, textTopPadding, textRightPadding,
                    textBottomPadding);
            mMessageBubble.setPadding(contentLeftPadding, 0, contentRightPadding, 0);
        }

        // Update the message row and message bubble views
        setPadding(getPaddingLeft(), messageTopPadding, getPaddingRight(), 0);
        mMessageBubble.setGravity(gravity);
        updateMessageAttachmentsAppearance(gravity);

        mMessageMetadataView.setPadding(0, metadataTopPadding, 0, 0);

        updateTextAppearance();

        requestLayout();
    }

    private void updateContentDescription() {
        StringBuilder description = new StringBuilder();

        Resources res = getResources();
        String separator = res.getString(R.string.enumeration_comma);

        // Sender information
        boolean hasPlainTextMessage = !(TextUtils.isEmpty(mData.getText()) ||
                mMessageTextHasLinks);
        if (mData.getIsIncoming()) {
            int senderResId = hasPlainTextMessage
                    ? R.string.incoming_text_sender_content_description
                    : R.string.incoming_sender_content_description;
            description.append(res.getString(senderResId, mData.getSenderDisplayName()));
        } else {
            int senderResId = hasPlainTextMessage
                    ? R.string.outgoing_text_sender_content_description
                    : R.string.outgoing_sender_content_description;
            description.append(res.getString(senderResId));
        }

        if (mSubjectView.getVisibility() == View.VISIBLE) {
            description.append(separator);
            description.append(mSubjectText.getText());
        }

        if (mMessageTextView.getVisibility() == View.VISIBLE) {
            // If the message has hyperlinks, we will let the user navigate to the text message so
            // that the hyperlink can be clicked. Otherwise, the text message does not need to
            // be reachable.
            if (mMessageTextHasLinks) {
                mMessageTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            } else {
                mMessageTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                description.append(separator);
                description.append(mMessageTextView.getText());
            }
        }

        if (mMessageTitleLayout.getVisibility() == View.VISIBLE) {
            description.append(separator);
            description.append(mTitleTextView.getText());

            description.append(separator);
            description.append(mMmsInfoTextView.getText());
        }

        if (mStatusTextView.getVisibility() == View.VISIBLE) {
            description.append(separator);
            description.append(mStatusTextView.getText());
        }

        if (mSimNameView.getVisibility() == View.VISIBLE) {
            description.append(separator);
            description.append(mSimNameView.getText());
        }

        if (mDeliveredBadge.getVisibility() == View.VISIBLE) {
            description.append(separator);
            description.append(res.getString(R.string.delivered_status_content_description));
        }

        setContentDescription(description);
    }

    private void updateMessageAttachmentsAppearance(final int gravity) {
        mMessageAttachmentsView.setGravity(gravity);

        // Tint image/video attachments when selected
        final int selectedImageTint = getResources().getColor(R.color.message_image_selected_tint);
        if (mMessageImageView.getVisibility() == View.VISIBLE) {
            if (isSelected()) {
                mMessageImageView.setColorFilter(selectedImageTint);
            } else {
                mMessageImageView.clearColorFilter();
            }
        }
        if (mMultiAttachmentView.getVisibility() == View.VISIBLE) {
            if (isSelected()) {
                mMultiAttachmentView.setColorFilter(selectedImageTint);
            } else {
                mMultiAttachmentView.clearColorFilter();
            }
        }
        for (int i = 0, size = mMessageAttachmentsView.getChildCount(); i < size; i++) {
            final View attachmentView = mMessageAttachmentsView.getChildAt(i);
            if (attachmentView instanceof VideoThumbnailView
                    && attachmentView.getVisibility() == View.VISIBLE) {
                final VideoThumbnailView videoView = (VideoThumbnailView) attachmentView;
                if (isSelected()) {
                    videoView.setColorFilter(selectedImageTint);
                } else {
                    videoView.clearColorFilter();
                }
            }
        }

        // If there are multiple attachment bubbles in a single message, add some separation.
        final int multipleAttachmentPadding =
                getResources().getDimensionPixelSize(R.dimen.message_padding_same_author);

        boolean previousVisibleView = false;
        for (int i = 0, size = mMessageAttachmentsView.getChildCount(); i < size; i++) {
            final View attachmentView = mMessageAttachmentsView.getChildAt(i);
            if (attachmentView.getVisibility() == View.VISIBLE) {
                final int margin = previousVisibleView ? multipleAttachmentPadding : 0;
                ((LinearLayout.LayoutParams) attachmentView.getLayoutParams()).topMargin = margin;
                // updateViewAppearance calls requestLayout() at the end, so we don't need to here
                previousVisibleView = true;
            }
        }
    }

    private void updateTextAppearance() {
        int messageColorResId;
        int statusColorResId = -1;
        int infoColorResId = -1;
        int timestampColorResId;
        int subjectLabelColorResId;
        if (isSelected()) {
            messageColorResId = R.color.message_text_color_incoming;
            statusColorResId = R.color.message_action_status_text;
            infoColorResId = R.color.message_action_info_text;
            if (shouldShowMessageTextBubble()) {
                timestampColorResId = R.color.message_action_timestamp_text;
                subjectLabelColorResId = R.color.message_action_timestamp_text;
            } else {
                // If there's no text, the timestamp will be shown below the attachments,
                // against the conversation view background.
                timestampColorResId = R.color.timestamp_text_outgoing;
                subjectLabelColorResId = R.color.timestamp_text_outgoing;
            }
        } else {
            messageColorResId = (mData.getIsIncoming() ?
                    R.color.message_text_color_incoming : R.color.message_text_color_outgoing);
            statusColorResId = messageColorResId;
            infoColorResId = R.color.timestamp_text_incoming;
            switch(mData.getStatus()) {

                case MessageData.BUGLE_STATUS_OUTGOING_FAILED:
                case MessageData.BUGLE_STATUS_OUTGOING_FAILED_EMERGENCY_NUMBER:
                    timestampColorResId = R.color.message_failed_timestamp_text;
                    subjectLabelColorResId = R.color.timestamp_text_outgoing;
                    break;

                case MessageData.BUGLE_STATUS_OUTGOING_YET_TO_SEND:
                case MessageData.BUGLE_STATUS_OUTGOING_SENDING:
                case MessageData.BUGLE_STATUS_OUTGOING_RESENDING:
                case MessageData.BUGLE_STATUS_OUTGOING_AWAITING_RETRY:
                case MessageData.BUGLE_STATUS_OUTGOING_COMPLETE:
                case MessageData.BUGLE_STATUS_OUTGOING_DELIVERED:
                    timestampColorResId = R.color.timestamp_text_outgoing;
                    subjectLabelColorResId = R.color.timestamp_text_outgoing;
                    break;

                case MessageData.BUGLE_STATUS_INCOMING_EXPIRED_OR_NOT_AVAILABLE:
                case MessageData.BUGLE_STATUS_INCOMING_DOWNLOAD_FAILED:
                    messageColorResId = R.color.message_text_color_incoming_download_failed;
                    timestampColorResId = R.color.message_download_failed_timestamp_text;
                    subjectLabelColorResId = R.color.message_text_color_incoming_download_failed;
                    statusColorResId = R.color.message_download_failed_status_text;
                    infoColorResId = R.color.message_info_text_incoming_download_failed;
                    break;

                case MessageData.BUGLE_STATUS_INCOMING_AUTO_DOWNLOADING:
                case MessageData.BUGLE_STATUS_INCOMING_MANUAL_DOWNLOADING:
                case MessageData.BUGLE_STATUS_INCOMING_RETRYING_AUTO_DOWNLOAD:
                case MessageData.BUGLE_STATUS_INCOMING_RETRYING_MANUAL_DOWNLOAD:
                case MessageData.BUGLE_STATUS_INCOMING_YET_TO_MANUAL_DOWNLOAD:
                    timestampColorResId = R.color.message_text_color_incoming;
                    subjectLabelColorResId = R.color.message_text_color_incoming;
                    infoColorResId = R.color.timestamp_text_incoming;
                    break;

                case MessageData.BUGLE_STATUS_INCOMING_COMPLETE:
                default:
                    timestampColorResId = R.color.timestamp_text_incoming;
                    subjectLabelColorResId = R.color.timestamp_text_incoming;
                    infoColorResId = -1; // Not used
                    break;
            }
        }
        final int messageColor = getResources().getColor(messageColorResId);
        mMessageTextView.setTextColor(messageColor);
        mMessageTextView.setLinkTextColor(messageColor);
        mSubjectText.setTextColor(messageColor);
        if (statusColorResId >= 0) {
            mTitleTextView.setTextColor(getResources().getColor(statusColorResId));
        }
        if (infoColorResId >= 0) {
            mMmsInfoTextView.setTextColor(getResources().getColor(infoColorResId));
        }
        if (timestampColorResId == R.color.timestamp_text_incoming &&
                mData.hasAttachments() && !shouldShowMessageTextBubble()) {
            timestampColorResId = R.color.timestamp_text_outgoing;
        }
        mStatusTextView.setTextColor(getResources().getColor(timestampColorResId));

        mSubjectLabel.setTextColor(getResources().getColor(subjectLabelColorResId));
        mSenderNameTextView.setTextColor(getResources().getColor(timestampColorResId));
    }

    /**
     * If we don't know the size of the image, we want to show it in a fixed-sized frame to
     * avoid janks when the image is loaded and resized. Otherwise, we can set the imageview to
     * take on normal layout params.
     */
    private void adjustImageViewBounds(final MessagePartData imageAttachment) {
        Assert.isTrue(ContentType.isImageType(imageAttachment.getContentType()));
        final ViewGroup.LayoutParams layoutParams = mMessageImageView.getLayoutParams();
        if (imageAttachment.getWidth() == MessagePartData.UNSPECIFIED_SIZE ||
                imageAttachment.getHeight() == MessagePartData.UNSPECIFIED_SIZE) {
            // We don't know the size of the image attachment, enable letterboxing on the image
            // and show a fixed sized attachment. This should happen at most once per image since
            // after the image is loaded we then save the image dimensions to the db so that the
            // next time we can display the full size.
            layoutParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.image_attachment_fallback_width);
            layoutParams.height = getResources()
                    .getDimensionPixelSize(R.dimen.image_attachment_fallback_height);
            mMessageImageView.setScaleType(ScaleType.CENTER_CROP);
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // ScaleType.CENTER_INSIDE and FIT_CENTER behave similarly for most images. However,
            // FIT_CENTER works better for small images as it enlarges the image such that the
            // minimum size ("android:minWidth" etc) is honored.
            mMessageImageView.setScaleType(ScaleType.FIT_CENTER);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if ((mMessageWebView.getVisibility() == View.VISIBLE) && mMessageWebView.canGoBack()) {
                mMessageWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(final View view) {
        closeButtonMenu();
        final Object tag = view.getTag();
        if (tag instanceof MessagePartData) {
            final Rect bounds = UiUtils.getMeasuredBoundsOnScreen(view);
            onAttachmentClick((MessagePartData) tag, bounds, false /* longPress */);
        } else if (tag instanceof String) {
            // Currently the only object that would make a tag of a string is a youtube preview
            // image
            UIIntents.get().launchBrowserForUrl(getContext(), (String) tag);
        }
    }

    @Override
    public boolean onLongClick(final View view) {
        //modified by junwang
//        if (view == mMessageTextView) {
            if (view == mMessageTextView || view == mH5_content) {
            // Preemptively handle the long click event on message text so it's not handled by
            // the link spans.
            return performLongClick();
        }

        final Object tag = view.getTag();
        if (tag instanceof MessagePartData) {
            final Rect bounds = UiUtils.getMeasuredBoundsOnScreen(view);
            return onAttachmentClick((MessagePartData) tag, bounds, true /* longPress */);
        }

        return false;
    }

    @Override
    public boolean onAttachmentClick(final MessagePartData attachment,
                                     final Rect viewBoundsOnScreen, final boolean longPress) {
        return mHost.onAttachmentClick(this, attachment, viewBoundsOnScreen, longPress);
    }

    public ContactIconView getContactIconView() {
        return mContactIconView;
    }

    // Sort photos in MultiAttachLayout in the same order as the ConversationImagePartsView
    static final Comparator<MessagePartData> sImageComparator = new Comparator<MessagePartData>(){
        @Override
        public int compare(final MessagePartData x, final MessagePartData y) {
            return x.getPartId().compareTo(y.getPartId());
        }
    };

    static final Predicate<MessagePartData> sVideoFilter = new Predicate<MessagePartData>() {
        @Override
        public boolean apply(final MessagePartData part) {
            return part.isVideo();
        }
    };

    static final Predicate<MessagePartData> sAudioFilter = new Predicate<MessagePartData>() {
        @Override
        public boolean apply(final MessagePartData part) {
            return part.isAudio();
        }
    };

    static final Predicate<MessagePartData> sVCardFilter = new Predicate<MessagePartData>() {
        @Override
        public boolean apply(final MessagePartData part) {
            return part.isVCard();
        }
    };

    static final Predicate<MessagePartData> sImageFilter = new Predicate<MessagePartData>() {
        @Override
        public boolean apply(final MessagePartData part) {
            return part.isImage();
        }
    };

    interface AttachmentViewBinder {
        void bindView(View view, MessagePartData attachment);
        void unbind(View view);
    }

    final AttachmentViewBinder mVideoViewBinder = new AttachmentViewBinder() {
        @Override
        public void bindView(final View view, final MessagePartData attachment) {
            ((VideoThumbnailView) view).setSource(attachment, mData.getIsIncoming());
        }

        @Override
        public void unbind(final View view) {
            ((VideoThumbnailView) view).setSource((Uri) null, mData.getIsIncoming());
        }
    };

    final AttachmentViewBinder mAudioViewBinder = new AttachmentViewBinder() {
        @Override
        public void bindView(final View view, final MessagePartData attachment) {
            final AudioAttachmentView audioView = (AudioAttachmentView) view;
            audioView.bindMessagePartData(attachment, mData.getIsIncoming(), isSelected());
            audioView.setBackground(ConversationDrawables.get().getBubbleDrawable(
                    isSelected(), mData.getIsIncoming(), false /* needArrow */,
                    mData.hasIncomingErrorStatus()));
        }

        @Override
        public void unbind(final View view) {
            ((AudioAttachmentView) view).bindMessagePartData(null, mData.getIsIncoming(), false);
        }
    };

    final AttachmentViewBinder mVCardViewBinder = new AttachmentViewBinder() {
        @Override
        public void bindView(final View view, final MessagePartData attachment) {
            final PersonItemView personView = (PersonItemView) view;
            personView.bind(DataModel.get().createVCardContactItemData(getContext(),
                    attachment));
            personView.setBackground(ConversationDrawables.get().getBubbleDrawable(
                    isSelected(), mData.getIsIncoming(), false /* needArrow */,
                    mData.hasIncomingErrorStatus()));
            final int nameTextColorRes;
            final int detailsTextColorRes;
            if (isSelected()) {
                nameTextColorRes = R.color.message_text_color_incoming;
                detailsTextColorRes = R.color.message_text_color_incoming;
            } else {
                nameTextColorRes = mData.getIsIncoming() ? R.color.message_text_color_incoming
                        : R.color.message_text_color_outgoing;
                detailsTextColorRes = mData.getIsIncoming() ? R.color.timestamp_text_incoming
                        : R.color.timestamp_text_outgoing;
            }
            personView.setNameTextColor(getResources().getColor(nameTextColorRes));
            personView.setDetailsTextColor(getResources().getColor(detailsTextColorRes));
        }

        @Override
        public void unbind(final View view) {
            ((PersonItemView) view).bind(null);
        }
    };

    /**
     * A helper class that allows us to handle long clicks on linkified message text view (i.e. to
     * select the message) so it's not handled by the link spans to launch apps for the links.
     */
    private static class IgnoreLinkLongClickHelper implements OnLongClickListener, OnTouchListener {
        private boolean mIsLongClick;
        private final OnLongClickListener mDelegateLongClickListener;

        /**
         * Ignore long clicks on linkified texts for a given text view.
         * @param textView the TextView to ignore long clicks on
         * @param longClickListener a delegate OnLongClickListener to be called when the view is
         *        long clicked.
         */
        public static void ignoreLinkLongClick(final TextView textView,
                                               @Nullable final OnLongClickListener longClickListener) {
            final IgnoreLinkLongClickHelper helper =
                    new IgnoreLinkLongClickHelper(longClickListener);
            textView.setOnLongClickListener(helper);
            textView.setOnTouchListener(helper);
        }

        private IgnoreLinkLongClickHelper(@Nullable final OnLongClickListener longClickListener) {
            mDelegateLongClickListener = longClickListener;
        }

        @Override
        public boolean onLongClick(final View v) {
            // Record that this click is a long click.
            mIsLongClick = true;
            if (mDelegateLongClickListener != null) {
                return mDelegateLongClickListener.onLongClick(v);
            }
            return false;
        }

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_UP && mIsLongClick) {
                // This touch event is a long click, preemptively handle this touch event so that
                // the link span won't get a onClicked() callback.
                mIsLongClick = false;
                return true;
            }

            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                mIsLongClick = false;
            }
            return false;
        }
    }
}
