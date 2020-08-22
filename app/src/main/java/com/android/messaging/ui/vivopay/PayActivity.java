package com.android.messaging.ui.vivopay;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vivo.unionsdk.open.VivoAccountCallback;
import com.vivo.unionsdk.open.VivoPayCallback;
import com.vivo.unionsdk.open.VivoPayInfo;
import com.vivo.unionsdk.open.VivoRoleInfo;
import com.vivo.unionsdk.open.VivoUnionSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayActivity extends Activity implements OnClickListener, VivoAccountCallback{
        private static final String TAG = "MainActivity";
        private String mOpenId;
        private VivoPayInfo mVivoPayInfo;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.vivopay_activity);
                Button loginBtn = (Button) findViewById(R.id.login_btn);
                Button payBtn = (Button) findViewById(R.id.vivo_pay_btn);
                Button noaccountPayBtn = (Button) findViewById(R.id.vivo_noaccount_pay_btn);
                loginBtn.setOnClickListener(this);
                payBtn.setOnClickListener(this);
                noaccountPayBtn.setOnClickListener(this);
                //SDK初始化, 请传入自己游戏的appid替换demo中的appid。
                VivoUnionSDK.initSdk(this, "1007", false);
                //注册登录回调
                VivoUnionSDK.registerAccountCallback(this, this);
        }

        @Override
        public void onClick(View view) {
                if (view.getId() == R.id.login_btn) {
                        //调用登录接口。需要游戏控制调用频率，防止用户快速多次点击，重复调起登录。频率控制代码略。
                        //严禁游戏在登录后通过调用登录接口切换帐号，需要切换帐号功能可提示用户退出游戏重新进入。
                        VivoUnionSDK.login(this);
                } else if (view.getId() == R.id.vivo_pay_btn) {
                        if (TextUtils.isEmpty(mOpenId)) {
                                Toast.makeText(this, "请先登录vivo帐号", Toast.LENGTH_SHORT).show();
                        } else {
                                pay(mOpenId);
                        }
                } else if (view.getId() == R.id.vivo_noaccount_pay_btn) {
                        pay(null);
                }
        }

        @Override
        public void onVivoAccountLogin(String arg0, String arg1, String arg2) {
                //收到登录成功回调后，调用服务端接口校验登录有效性。arg2返回值为authtoken。服务端接口详见文档。校验登录代码略。

                mOpenId = arg1;
                //登录成功后上报角色信息
                VivoUnionSDK.reportRoleInfo(new VivoRoleInfo("角色ID", "角色等级", "角色名称", "区服ID", "区服名称"));
        }

        @Override
        public void onVivoAccountLoginCancel() {

        }

        @Override
        public void onVivoAccountLogout(int arg0) {

        }

        //支付流程
        private void pay(final String openId) {
                //订单推送接口请在服务器端访问
                final HashMap<String, String> params = new HashMap<String, String>();
                params.put("notifyUrl", "http://113.98.231.125:8051/vcoin/notifyStubAction");
                params.put("orderAmount", "1"); //单位为分；
                params.put("orderDesc", "【正版】MF唱片 HIFI毒药4 毒药涅磐再造 海洛 因新4号HD天碟1CD");
                params.put("orderTitle", "MF唱片_2");
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                params.put("orderTime", format.format(new Date()));
                params.put("cpId", "20131030114035786189");
                params.put("appId", "1007");
                params.put("cpOrderNumber", UUID.randomUUID().toString().replaceAll("-", ""));
                params.put("version", "1.0");
                params.put("extInfo", "extInfo_test");
                String str = VivoSignUtils.getVivoSign(params, "20131030114035565895"); //20131030114035565895为app对应的signkey
                params.put("signature", str);
                params.put("signMethod", "MD5");

                RequestQueue mQueue = Volley.newRequestQueue(this);
                HTTPSTrustManager.allowAllSSL();
                StringRequest jsonObjectRequest = new StringRequest(Method.POST, "https://pay.vivo.com.cn/vcoin/trade",
                        new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                        JSONObject jsonObject = null;
                                        try {
                                                jsonObject = new JSONObject(response);
                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                        }
                                        if (JsonParser.getString(jsonObject, "respCode").equals("200")) {
                                                //不上报角色信息
                                                mVivoPayInfo = new VivoPayInfo(
                                                        "MF唱片_2",
                                                        "【正版】MF唱片 HIFI毒药4 毒药涅磐再造 海洛 因新4号HD天碟1CD",
                                                        "1",
                                                        JsonParser.getString(jsonObject, "accessKey"),
                                                        "1007",
                                                        JsonParser.getString(jsonObject, "orderNumber"),
                                                        openId);
                                                //上报角色信息
                                                mVivoPayInfo = new VivoPayInfo(
                                                        "MF唱片_2",
                                                        "【正版】MF唱片 HIFI毒药4 毒药涅磐再造 海洛 因新4号HD天碟1CD",
                                                        "1",
                                                        JsonParser.getString(jsonObject, "accessKey"),
                                                        "1007",
                                                        JsonParser.getString(jsonObject, "orderNumber"),
                                                        openId,
                                                        "余额", "VIP等级", "15","工会","角色Id","角色名称","区服名称","扩展参数");
                                                VivoUnionSDK.pay(PayActivity.this, mVivoPayInfo, mVivoPayCallback);
                                        } else {
                                                Toast.makeText(PayActivity.this, "获取订单错误", Toast.LENGTH_SHORT).show();
                                        }

                                }
                        }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                                Toast.makeText(PayActivity.this, "获取参数错误", Toast.LENGTH_SHORT).show();
                        }
                }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                                return params;
                        }
                };
                mQueue.add(jsonObjectRequest);
                mQueue.start();
        }

        private VivoPayCallback mVivoPayCallback = new VivoPayCallback() {
                //客户端返回的支付结果不可靠，请以服务器端最终的支付结果为准；
                public void onVivoPayResult(String arg0, boolean arg1, String arg2) {
                        if (arg1) {
                                Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        } else {
                                Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                };
        };


}
