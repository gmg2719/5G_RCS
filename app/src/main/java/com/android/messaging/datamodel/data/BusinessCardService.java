package com.android.messaging.datamodel.data;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseHelper;
import com.android.messaging.datamodel.DatabaseWrapper;
import com.android.messaging.datamodel.MessagingContentProvider;
import com.android.messaging.datamodel.mygsonconverter.GsonConverterFactory;
import com.android.messaging.util.DownloadImageUtils;
import com.android.messaging.util.LogUtil;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

//import retrofit2.converter.gson.GsonConverterFactory;


public class BusinessCardService extends Service {
    public static final String TAG = "BusinessCardService";
    class BusinessCardApi{
        private int ret_code;
        private String message;
        private ArrayList<BusinessCard> business_card;

        public int getRet_code() {
            return ret_code;
        }

        public String getMessage() {
            return message;
        }

        public ArrayList<BusinessCard> getBusiness_card() {
            return business_card;
        }
    }
    class BusinessCard {
        private String business_name;
        private String logo_path;
        private String code_number;
        private String port_number;
        private int card_msg_template_no;
        private String card_msg_title;
        private Map<String, String> regular_expression;
        private List<ActionButton>  action_button;
        private int trigger_code;
        private String matcher;
        private String response_url;
        private ArrayList<ButtonMenu> menus;
        private String version;
        private ArrayList<CardTemplate> card_templates;

        public void setBusinessName(String name) {
            this.business_name = name;
        }

        public String getBusinessName() {
            return business_name;
        }

        public void setBusinessLogoPath(String logoPath) {
            this.logo_path = logoPath;
        }

        public String getBusinessLogoPath() {
            return logo_path;
        }

        public void setCodeNumber(String codeNumber) {
            this.code_number = codeNumber;
        }

        public String getCodeNumber() {
            return code_number;
        }

        public void setVersion(String version){
            this.version = version;
        }

        public String getVersion(){
            return version;
        }

        public void setTriggerCode(int trigger_code){
            this.trigger_code = trigger_code;
        }

        public int getTrigger_code(){
            return trigger_code;
        }

        public void setMatcher(String matcher){
            this.matcher = matcher;
        }

        public String getMatcher(){
            return matcher;
        }

        public void setResponseUrl(String response_url){
            this.response_url = response_url;
        }

        public String getResponse_url(){
            return response_url;
        }

        public void getBusinessCardFromJson() {

        }

        public ArrayList<ButtonMenu> getMenus(){
            return menus;
        }

        public void setPort_number(String port_number){
            this.port_number = port_number;
        }

        public String getPort_number(){
            return port_number;
        }

        public void setCard_msg_title(String card_msg_title){
            this.card_msg_title = card_msg_title;
        }

        public String getCard_msg_title(){
            return card_msg_title;
        }

        public void setCard_msg_template_no(int card_msg_template_no){
            this.card_msg_template_no = card_msg_template_no;
        }

        public int getCard_msg_template_no(){
            return card_msg_template_no;
        }

        public void setRegular_expression(Map<String, String> expression_list){
            regular_expression = expression_list;
        }

        public Map<String, String> getRegular_expression(){
            return regular_expression;
        }

        public void setAction_button(List<ActionButton> action_list){
            this.action_button = action_list;
        }

        public List<ActionButton> getAction_button(){
            return action_button;
        }

        public ArrayList<CardTemplate> getCardTemplates() {
            return card_templates;
        }

        public void setCardTemplates(ArrayList<CardTemplate> cardTemplates) {
            this.card_templates = cardTemplates;
        }
    }

//    private class ButtonMenu{
//        String menuName;
//        ArrayList<BusnMenuItem> menuItems;
//    }
//
//    private class BusnMenuItem{
//        String itemName;
//        String actionUrl;
//    }

    public interface BusnCardService{
        @GET("BusnCard")
        Call<ResponseBody> getBusnCard(BusinessCard bcard);
    }

    public interface BusnCardRetrofitService{
        @GET("top250")
        Observable<ResponseBody> getTopMovie(@Query("start") int start, @Query("count") int count);

        @GET("{id}")
        Observable<BusinessCardApi> getBusnCardJson(@Path("id") String path);

        Observable<ArrayList<BusinessCard>> getBusnCardJson();

        @GET("BusnCard")
        Call<ResponseBody> getBusnCard(String path);

        @GET("busn/getBusnInfo")
        Observable<BusinessCardApi> getBusnInfo(@Query("code_number") String code_number);
    }


//    public static void loadData() {
//        String baseUrl = "https://api.douban.com/v2/movie/";
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))./*addConverterFactory(GsonConverterFactory.create()).*/build();
//        BusnCardRetrofitService service = retrofit.create(BusnCardRetrofitService.class);
//        service.getTopMovie(0,10).subscribe(new Observer<ResponseBody>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onComplete() {
//                LogUtil.d("retrofit", "on complete");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                LogUtil.d("retrofit", e.toString());
//            }
//            @Override
//            public void onNext(ResponseBody responseBody) {
//                try {
//                    LogUtil.d("retrofit", responseBody.string());
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//    public static final String BASE_URL = "http://172.16.30.58:1995/";//https://www.trioly.com/businesscard/";
    public static final String BASE_IMAGE_URL = "https://img.sccnn.com/"; //BASE_URL
    public static final String BUSNCARD_JSON = "http://localhost:5000/Business";
    public static final String mytestjosn = "file:///android_res/raw/";
    public static final String BASE_URL = "http://newadmin.supermms.cn/api/RCSTest/";
//    public static final String BASE_URL = "http://172.16.30.47:1995/";
//public static final String BASE_URL = "http://172.16.30.22:1995/";
    public void downloadImages(String url){
        LogUtil.i(TAG, "downloadImages start");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//        JsonReader.setLenient(true);
//        testMap2Json();
        BusnCardRetrofitService service = retrofit.create(BusnCardRetrofitService.class);

        service.getBusnCardJson(/*"db.json"*//*"news"*/"test1").subscribe(new Consumer<BusinessCardApi>() {
            @Override
            public void accept(BusinessCardApi businessCard) throws Exception {
                LogUtil.i(TAG, "get json successfully");
//                ContentValues cv = new ContentValues();
                String temp_path;
                List<ContentValues> al = new ArrayList<>();
                if(businessCard == null){
                    return;
                }
                if(businessCard.getRet_code() != 0){
                    LogUtil.i(TAG, "get json ret_code="+businessCard.getRet_code()+", message="+businessCard.getMessage());
                    return;
                }
//                ContentValues[] al = new ContentValues[businessCard.size()];
//                int i=0;
                for(BusinessCard bc : businessCard.getBusiness_card()) {
                    temp_path = null;
//                    if (DownloadImageUtils.saveImageToLocal(BusinessCardService.this, bc.getBusinessLogoPath())) {
//                        temp_path = bc.getBusinessLogoPath();
//                    }
                    ContentValues cv = new ContentValues();
                    temp_path = bc.getBusinessLogoPath();
                    DownloadImageUtils.saveImageToLocal(BusinessCardService.this, temp_path);

                    String tempNumber = bc.getCodeNumber();
                    String addtodbnumber = null;
                    if(tempNumber.length() <= 6 || tempNumber.startsWith("106") || tempNumber.startsWith("+86") ){
                        addtodbnumber = tempNumber;
                        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
                    }else if(tempNumber.startsWith("86")){
                        addtodbnumber = "+"+tempNumber;
                        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
                    }else{
                        addtodbnumber = "+86"+tempNumber;
                        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
                    }
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, /*bc.getCodeNumber()*/addtodbnumber);
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_NAME, new String(/*bc.getBusinessName().getBytes("GB2312"), "GB2312"*/bc.getBusinessName()));
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_LOGO, /*temp_path*/BusinessCardService.this.getFilesDir()+"/"+temp_path.substring(temp_path.lastIndexOf("/")+1));
//                    String logo_path = new File(BusinessCardService.this.getCacheDir(), "mediascratchspace").toString()+"/"+temp_path.substring(temp_path.lastIndexOf("/")+1);
//                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_LOGO, logo_path);
//                    LogUtil.i("Junwang", "logo_path="+logo_path);
                    LogUtil.i("Junwang", "codenumber="+bc.getCodeNumber()+", bsname="+bc.getBusinessName()+", bslogo="+BusinessCardService.this.getFilesDir()+"/"+temp_path.substring(temp_path.lastIndexOf("/")+1));
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_TRIGGER_CODE, bc.getTrigger_code());
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_MATCHER, bc.getMatcher());
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_RESPONSE_URL, bc.getResponse_url());
                    Gson gosn = new Gson();
                    String json = gosn.toJson(bc.getMenus());
                    LogUtil.i("Junwang", "json="+json);
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_MENU_JSON, json);

                    String regExpJson = new Gson().toJson(bc.getRegular_expression());
                    LogUtil.i("Junwang", "regExpJson="+regExpJson);
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_PORT_NUMBER, bc.getPort_number());
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_MSG_TEMPLATE_NO, bc.getCard_msg_template_no());
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_TITLE, bc.getCard_msg_title());
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_REGULAR_EXPRESSION, regExpJson);
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_ACTION_BUTTON, new Gson().toJson(bc.getAction_button()));
                    String cardTemplatesJson = new Gson().toJson(bc.getCardTemplates());
//                    String cardTemplatesJson1 = cardTemplatesJson.substring(0, cardTemplatesJson.length()-1)+
//                            ",\n" +
//                            "{\n" +
//                            "\"card_msg_template_no\": 1,\n" +
//                            "\"card_msg_title\": \"充值提醒\",\n" +
//                            "\"regular_expression_key\": [\"时间:\", \"金额:\", \"账号余额:\"],\n" +
//                            "\"regular_expression\": \"^尊敬的客户：您好，(.*?)您成功充值(.*?)，当前您的账户余额是(.*?).\",\n" +
//                            "\"action_button\": [{\"button_name\": \"充值\", \"button_action_type\": 1, \"button_action_url\": \"http://service.zj.10086.cn\"}]\n" +
//                            "},\n" +
//                            "{\n" +
//                            "\"card_msg_template_no\": 2,\n" +
//                            "\"card_msg_title\": \"验证码通知\",\n" +
//                            "\"regular_expression_key\": [\"验证码\"],\n" +
//                            "\"regular_expression\": \"^您的验证码是：(.*?)，请不要把验证码给其他人看，以免泄露\",\n" +
//                            "\"action_button\": [{\"button_name\": \"复制验证码\", \"button_action_type\": 2, \"button_action_url\": \"http://service.zj.10086.cn\", \"button_action_native_function\": [5]},\n" +
//                            "{\"button_name\": \"充值\", \"button_action_type\": 1, \"button_action_url\": \"http://service.zj.10086.cn\", \"button_action_native_function\": [1, 2, 3]},\n" +
//                            "{\"button_name\": \"启动app\", \"button_action_type\": 3, \"button_action_url\": \"com.greenpoint.android.mc10086.activity\", \"button_action_native_function\": [2, 3]}]\n" +
//                            "},\n" +
//                            "{\n" +
//                            "\"card_msg_template_no\": 3,\n" +
//                            "\"card_msg_title\": \"购票成功通知\",\n" +
//                            "\"regular_expression_key\": [\"订单\", \"车次\", \"发到站\", \"发车时间\", \"车厢车位\"],\n" +
//                            "\"regular_expression\": \"^订单(.*?)，您已购(.*?)的(.*?)次(.*?)号，(.*?)到(.*?)，(.*?)开\"\n" +
//                            "}\n" +
//                            "]";
                    LogUtil.i("Junwang", "cardTemplatesJson="+cardTemplatesJson);
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_TEMPLATE, cardTemplatesJson);
                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_VERSION, bc.getVersion());
                    al.add(cv);
//                    al[i] = cv;
//                    i++;
                }
                //for test sqlite3 performance
//                for(int i=0; i<50000; i++){
//                    ContentValues cv = new ContentValues();
//                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, "10086"+i);
//                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_NAME, new String("testname"+i));
//                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_LOGO, /*temp_path*/BusinessCardService.this.getFilesDir()+"/"+"testlogo"+i);
//                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_TEMPLATE, "[{\"action_button\":[{\"button_action_type\":"+ (int)(1+Math.random()*(10000-1+1))+",\"button_action_url\":\"http://service.zj.10086.cn\",\"button_name\":\"充值\"}],\"card_msg_template_no\":"+(int)(1+Math.random()*(10000-1+1))+",\"card_msg_title\":\"充值提醒\",\"regular_expression\":\"^尊敬的客户：您好，(.*?)您成功充值(.*?)，当前您的账户余额是(.*?)$\",\"regular_expression_key\":[\"时间:\",\"金额:\",\"账号余额:\"]},{\"action_button\":[{\"button_action_native_function\":[5],\"button_action_type\":2,\"button_action_url\":\"http://service.zj.10086.cn\",\"button_name\":\"复制验证码\"},{\"button_action_type\":1,\"button_action_url\":\"http://service.zj.10086.cn\",\"button_name\":\"充值\"},{\"button_action_native_function\":[2,3],\"button_action_type\":3,\"button_action_url\":\"com.greenpoint.android.mc10086.activity\",\"button_name\":\"启动app\"}],\"card_msg_template_no\":2,\"card_msg_title\":\"验证码通知\",\"regular_expression\":\"^您的验证码是：(.*?)，请不要把验证码给其他人看，以免泄露$\",\"regular_expression_key\":[\"验证码\"]},{\"card_msg_template_no\":3,\"card_msg_title\":\"购票成功通知\",\"regular_expression\":\"^订单(.*?)，您已购(.*?)的(.*?)次(.*?)号，(.*?)到(.*?)，(.*?)开$\",\"regular_expression_key\":[\"订单\",\"车次\",\"发到站\",\"发车时间\",\"车厢车位\"]}]");
//                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_MENU_JSON, "[{\"action_native_function\":0,\"action_type\":0,\"menu_items\":[{\"action_native_function\":0,\"action_type\":"+(int)(1+Math.random()*(10000-1+1))+",\"action_url\":\"http://www.10086.cn/index/fj/index_591_591.html\",\"item_name\":\"话费查询\"},{\"action_native_function\":"+(int)(1+Math.random()*(10000-1+1))+",\"action_type\":2,\"action_url\":\"10086\",\"item_name\":\"发送短信\"},{\"action_native_function\":1,\"action_type\":2,\"action_url\":\"10086\",\"item_name\":\"拨打电话\"}],\"menu_name\":\"自助服务\"},{\"action_native_function\":0,\"action_type\":0,\"menu_items\":[{\"action_native_function\":0,\"action_type\":0,\"action_url\":\"20RMB\",\"item_name\":\"20元\"},{\"action_native_function\":0,\"action_type\":0,\"action_url\":\"50RMB\",\"item_name\":\"50元\"},{\"action_native_function\":0,\"action_type\":0,\"action_url\":\"100RMB\",\"item_name\":\"100元\"},{\"action_native_function\":0,\"action_type\":0,\"action_url\":\"200RMB\",\"item_name\":\"200元\"}],\"menu_name\":\"充值\"},{\"action_native_function\":0,\"action_type\":0,\"menu_items\":[{\"action_native_function\":3,\"action_type\":2,\"item_name\":\"拍照\"},{\"action_native_function\":4,\"action_type\":2,\"item_name\":\"录像\"},{\"action_native_function\":0,\"action_type\":3,\"action_url\":\"com.greenpoint.android.mc10086.activity\",\"item_name\":\"启动app\"}],\"menu_name\":\"其他\"}]");
//                    cv.put(DatabaseHelper.H5CodeNumberColumns.H5_VERSION, new Random().nextInt(10) +"."+new Random().nextInt(10)+".0");
//                    al.add(cv);
//                }
                //for test sqlite3 performance end
                stopSelf();
                saveToDatabase(al);
//                sendBroadcast(new Intent("com.android.messaging.RefreshH5Settings"));
//                MessagingContentProvider.notifyConversationListChanged();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

//    private void queryBusnVersion(){
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
//                Cursor mCursor = mdbWrapper.query(DatabaseHelper.H5WHITELIST_TABLE, null, null, null, null, null, null);
//                mCursor.getFloat();
//            }
//        }).start();
//    }

    /**
     * 版本号比较
     *
     * @param v1
     * @param v2
     * @return 0代表相等，1代表左边大，-1代表右边大
     * Utils.compareVersion("1.0.358_20180820090554","1.0.358_20180820090553")=1
     */
    public static int compareVersion(String v1, String v2) {
        if((v1 == null) && (v2 == null)){
            return 0;
        }
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    public static String getBusnCardVersion(String code_number){
        LogUtil.i("Junwang", "getBusnCardVersion");
        String version = null;
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        try {
            Cursor cursor = mdbWrapper.rawQuery("SELECT "+ DatabaseHelper.H5CodeNumberColumns.H5_VERSION +" FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE "+ DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER+ " = ?", new String[]{code_number});
            if(cursor != null) {
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    version = cursor.getString(0);
                    LogUtil.i("Junwang", "version=" + version);
                }
            }
        }catch (Exception e){
            LogUtil.e("Junwang", e.toString());
        }
        return version;
    }

    public void addBusncard2DB(BusinessCard bc){
        if(bc == null){
            return;
        }

        ContentValues cv = new ContentValues();
        String temp_path = bc.getBusinessLogoPath();
        DownloadImageUtils.saveImageToLocal(BusinessCardService.this, temp_path);

        String tempNumber = bc.getCodeNumber();
        String addtodbnumber = null;
        if (tempNumber.length() <= 6 || tempNumber.startsWith("106") || tempNumber.startsWith("+86")) {
            addtodbnumber = tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        } else if (tempNumber.startsWith("86")) {
            addtodbnumber = "+" + tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        } else {
            addtodbnumber = "+86" + tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        }
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_NAME, bc.getBusinessName());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_LOGO, BusinessCardService.this.getFilesDir() + "/" + temp_path.substring(temp_path.lastIndexOf("/") + 1));
        LogUtil.i("Junwang", "codenumber=" + bc.getCodeNumber() + ", bsname=" + bc.getBusinessName() + ", bslogo=" + BusinessCardService.this.getFilesDir() + "/" + temp_path.substring(temp_path.lastIndexOf("/") + 1));
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_TRIGGER_CODE, bc.getTrigger_code());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_MATCHER, bc.getMatcher());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_RESPONSE_URL, bc.getResponse_url());
        Gson gosn = new Gson();
        String json = gosn.toJson(bc.getMenus());
        LogUtil.i("Junwang", "json=" + json);
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_MENU_JSON, json);

        String regExpJson = new Gson().toJson(bc.getRegular_expression());
        LogUtil.i("Junwang", "regExpJson=" + regExpJson);
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_PORT_NUMBER, bc.getPort_number());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_MSG_TEMPLATE_NO, bc.getCard_msg_template_no());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_TITLE, bc.getCard_msg_title());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_REGULAR_EXPRESSION, regExpJson);
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_ACTION_BUTTON, new Gson().toJson(bc.getAction_button()));
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_TEMPLATE, new Gson().toJson(bc.getCardTemplates()));
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_VERSION, bc.getVersion());

        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        mdbWrapper.insert(DatabaseHelper.H5WHITELIST_TABLE, null, cv);

        mdbWrapper.update(DatabaseHelper.CONVERSATIONS_TABLE, cv,
                DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
                        + " = ?", new String[]{addtodbnumber});
        MessagingContentProvider.notifyConversationListChanged();
    }

    public void updateBusncard2DB(BusinessCard bc){
        if(bc == null){
            return;
        }

        ContentValues cv = new ContentValues();
        String temp_path = bc.getBusinessLogoPath();
        DownloadImageUtils.saveImageToLocal(BusinessCardService.this, temp_path);

        String tempNumber = bc.getCodeNumber();
        String addtodbnumber = null;
        if (tempNumber.length() <= 6 || tempNumber.startsWith("106") || tempNumber.startsWith("+86")) {
            addtodbnumber = tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        } else if (tempNumber.startsWith("86")) {
            addtodbnumber = "+" + tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        } else {
            addtodbnumber = "+86" + tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        }
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_NAME, bc.getBusinessName());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_LOGO, BusinessCardService.this.getFilesDir() + "/" + temp_path.substring(temp_path.lastIndexOf("/") + 1));
        LogUtil.i("Junwang", "codenumber=" + bc.getCodeNumber() + ", bsname=" + bc.getBusinessName() + ", bslogo=" + BusinessCardService.this.getFilesDir() + "/" + temp_path.substring(temp_path.lastIndexOf("/") + 1));
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_TRIGGER_CODE, bc.getTrigger_code());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_MATCHER, bc.getMatcher());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_RESPONSE_URL, bc.getResponse_url());
        Gson gosn = new Gson();
        String json = gosn.toJson(bc.getMenus());
        LogUtil.i("Junwang", "json=" + json);
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_MENU_JSON, json);

        String regExpJson = new Gson().toJson(bc.getRegular_expression());
        LogUtil.i("Junwang", "regExpJson=" + regExpJson);
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_PORT_NUMBER, bc.getPort_number());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_MSG_TEMPLATE_NO, bc.getCard_msg_template_no());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_TITLE, bc.getCard_msg_title());
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_REGULAR_EXPRESSION, regExpJson);
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_ACTION_BUTTON, new Gson().toJson(bc.getAction_button()));
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_CARD_TEMPLATE, new Gson().toJson(bc.getCardTemplates()));
        cv.put(DatabaseHelper.H5CodeNumberColumns.H5_VERSION, bc.getVersion());

        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        mdbWrapper.update(DatabaseHelper.H5WHITELIST_TABLE, cv, DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER
                + " = ?", new String[]{bc.getCodeNumber()});
    }

    public void deleteBusnCard(BusinessCard bc, String code_number){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        mdbWrapper.delete(DatabaseHelper.H5WHITELIST_TABLE, DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER
                + " = ?", new String[]{code_number});
    }

    public void handleBusnCard(BusinessCard bc, String code_number){
        if(bc != null) {
            String ver = bc.getVersion();
            String old_ver = getBusnCardVersion(code_number);
            if((ver != null) && (old_ver == null)){
                LogUtil.i("Junwang", "It's a new busncard, save it to db!");
                addBusncard2DB(bc);
            }else if((ver == null) && (old_ver != null)){
                LogUtil.i("Junwang", code_number + " busncard has been deleted from server! Delete it from db.");
                deleteBusnCard(bc,code_number);
            }else {
                int comp = compareVersion(old_ver, ver);
                if (comp == 0) {
                    LogUtil.i("Junwang", "Busncard version is not updated!");
                    return;
                } else if (comp == 1) {
                    LogUtil.e("Junwang", "Server busncard version" + ver + " is lower than it in db version " + old_ver + ", this shouldn't be happend!");
                }else if(comp == -1){
                    LogUtil.i("Junwang", "Busncard version is updated!");
                    updateBusncard2DB(bc);
                }
            }
        }
    }

    public BusinessCard getBusnInfo(String code_number){
        LogUtil.i(TAG, "getBusnInfo start");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL+"news/").
                addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BusnCardRetrofitService service = retrofit.create(BusnCardRetrofitService.class);
        BusinessCard bc = null;
        try{
            service.getBusnInfo(code_number).subscribe(new Consumer<BusinessCardApi>(){
                @Override
                public void accept(BusinessCardApi businessCard) throws Exception {
                    if((businessCard != null) && (businessCard.getRet_code() == 0)){
                        handleBusnCard(businessCard.getBusiness_card().get(0), code_number);
                    }else{
                        LogUtil.i("Junwang", "getBusnInfo businessCard == null. message="+businessCard.getMessage());
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });
//            bc = service.getBusnInfo(code_number).execute().body();
        }catch (Exception e){
            LogUtil.e("Junwang", "query "+code_number+" from server exception "+ e.toString());
            return null;
        }
        return bc;
    }

    public void testMap2Json(){
        Map<String, String> params = new HashMap<>();

        params.put("a","11111");

        params.put("b","2222");

        String json = JSON.toJSONString(params);

        LogUtil.i("testMap2Json", "map2json="+json);
    }

    public static void getBusnCard(){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        try {
            Cursor cursor = mdbWrapper.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE, null);
            int count=0;
            String colum1, colum2, colum3;
            if(cursor != null) {
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    colum1 = cursor.getString(0);
                    colum2 = cursor.getString(1);
                    colum3 = cursor.getString(2);
                    LogUtil.i("Junwang", "colum1=" + colum1 + ", colum2=" + colum2 + ", colum3=" + colum3);
                }
            }
            LogUtil.i("Junwang", "H5 table count =" + count);
//            mdbWrapper.execSQL("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE);
        }catch (Exception e){
            LogUtil.e("Junwang", e.toString());
        }
    }

    private void UpdateIsH5MsgAsFalseInConversationsTable(List<ContentValues> al){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();

        try {
            // 开启事务
            mdbWrapper.beginTransaction();

            String deleteSql = "DELETE FROM " + DatabaseHelper.H5WHITELIST_TABLE;
            mdbWrapper.execSQL(deleteSql);

            for (ContentValues area : al) {
                mdbWrapper.insert(DatabaseHelper.H5WHITELIST_TABLE, null, area);
                ContentValues cv1 = new ContentValues();
                cv1.put(DatabaseHelper.ConversationColumns.IS_H5_MESSAGE, 0);
                mdbWrapper.update(DatabaseHelper.CONVERSATIONS_TABLE, cv1,
                        DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
                                + " = ?", new String[]{area.getAsString(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER)});
            }

            //设置事务标志为成功，当结束事务时就会提交事务
            mdbWrapper.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("Junwang", "Businesscard save to db error!");
        } finally {
            // 结束事务
            mdbWrapper.endTransaction();
        }
    }

    private boolean saveToDatabase(List<ContentValues> al){
//        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();

        try {
//            // 如果该数据库表已经存在，先删掉
//            String dropSql = "DROP TABLE IF EXISTS " + DatabaseHelper.H5WHITELIST_TABLE;
//            mdbWrapper.execSQL(dropSql);
//            // 重新创建数据库表
//            String createSql = "CREATE TABLE " + DatabaseHelper.H5WHITELIST_TABLE +" ("
//                    + "_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
//                    + DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER + " TEXT NOT NULL, "
//                    + DatabaseHelper.H5CodeNumberColumns.BUSINESS_NAME + " TEXT, "
//                    + DatabaseHelper.H5CodeNumberColumns.BUSINESS_LOGO + "TEXT"
//                    + ")";
//            mdbWrapper.execSQL(createSql);

            // 开启事务
            mdbWrapper.beginTransaction();

            String deleteSql = "DELETE FROM " + DatabaseHelper.H5WHITELIST_TABLE;
            mdbWrapper.execSQL(deleteSql);
            // 往表名为"Area"的表里的code, creatTime, fullspell...等字段“开事务”批量“合并”插入
//            String sql = "INSERT INTO Area(code, creatTime, fullspell, name, scope, singleSpell, parent) VALUES(?, ?, ?, ?, ?, ?, ?)";
//            for (Area area : areas) {
//                db.execSQL(sql, new String[]{area.getCode(), String.valueOf(area.getCreatTime()),
//                        area.getFullSpell(), area.getName(), area.getScope(), area.getSingleSpell(), area.getParent()});
//            }
            for (ContentValues area : al) {
                mdbWrapper.insert(DatabaseHelper.H5WHITELIST_TABLE, null, area);
                ContentValues cv1 = new ContentValues();
                cv1.put(DatabaseHelper.ConversationColumns.IS_H5_MESSAGE, 1);
                mdbWrapper.update(DatabaseHelper.CONVERSATIONS_TABLE, cv1,
                        DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
                                + " = ?", new String[]{area.getAsString(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER)});
            }

            //设置事务标志为成功，当结束事务时就会提交事务
            mdbWrapper.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("Junwang", "Businesscard save to db error!");
            return false;
        } finally {
            // 结束事务
            mdbWrapper.endTransaction();
            LogUtil.i("Junwang", "Businesscard save to db successfully!");
        }
        return true;
    }

    private void addItem(String addNumber, String addName, String logoPath, String version){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        ContentValues cv = new ContentValues();
        String tempNumber = addNumber.replaceAll(" ", "");
        String addtodbnumber = null;
        ContentValues cv1 = new ContentValues();

        if(tempNumber.length() <= 6 || tempNumber.startsWith("106") || tempNumber.startsWith("+86") ){
            addtodbnumber = tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        }else if(tempNumber.startsWith("86")){
            addtodbnumber = "+"+tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        }else{
            addtodbnumber = "+86"+tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        }
        cv1.put(DatabaseHelper.ConversationColumns.IS_H5_MESSAGE, 1);
        //cv.put("codenumber", addNumber);
//        mdb.insert(H5WLDatabaseHelper.H5WL_TABLENAME, null, cv);
        mdbWrapper.insert(DatabaseHelper.H5WHITELIST_TABLE, null, cv);

        mdbWrapper.update(DatabaseHelper.CONVERSATIONS_TABLE, cv1,
                DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
                        + " = ?", new String[]{addtodbnumber});

//        mdbWrapper.update(DatabaseHelper.CONVERSATIONS_TABLE, cv1,
//                DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
//                + " = " + addtodbnumber, null);
//        mdbWrapper.execSQL("UPDATE " + DatabaseHelper.CONVERSATIONS_TABLE + " SET "
//                            + DatabaseHelper.ConversationColumns.IS_H5_MESSAGE + " = 1 "
//                            + "WHERE " + DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
//                            + " = " + addtodbnumber);
//        mdbWrapper.execSQL("UPDATE " + ConversationListItemData.getConversationListView() + " SET "
//                + DatabaseHelper.ConversationColumns.IS_H5_MESSAGE + " = 1 "
//                + "WHERE " + DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
//                + " = " + addtodbnumber);
        //refreshListView();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        ArrayList al = new ArrayList(Arrays.asList(ConversationListActivity.imageUrls));
//        DownloadImageUtils.saveImagesToLocal(this, al);
        downloadImages(null);
//        if(intent != null) {
//            String code_number = intent.getStringExtra(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER);
//            BusinessCard bc = getBusnInfo(code_number);
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static class ActionButton{
        private String button_name;
        private int button_action_type;
        private String button_action_url;
        private int[] button_action_native_function;

        public String getButton_name() {
            return button_name;
        }

        public void setButton_name(String button_name) {
            this.button_name = button_name;
        }

        public int getButton_action_type() {
            return button_action_type;
        }

        public void setButton_action_type(int button_action_type) {
            this.button_action_type = button_action_type;
        }

        public String getButton_action_url() {
            return button_action_url;
        }

        public void setButton_action_url(String button_action_url) {
            this.button_action_url = button_action_url;
        }

        public int[] getButton_action_native_function() {
            return button_action_native_function;
        }

        public void setButton_action_native_function(int[] button_action_native_function) {
            this.button_action_native_function = button_action_native_function;
        }
    }

    public static String do_exec(String cmd) {
        String s = "/n";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "/n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cmd;
    }
}
