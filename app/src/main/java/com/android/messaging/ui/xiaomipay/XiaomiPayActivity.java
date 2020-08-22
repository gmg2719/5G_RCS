package com.android.messaging.ui.xiaomipay;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.android.messaging.R;

/**
 * 可消耗商品和不可消耗商品购买页
 *
 * @author guowb
 *
 */
public class XiaomiPayActivity extends Activity /*implements OnPayProcessListener*/
{
    private String from;

    private final static int ID_PAYMENT = 10000;



    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.payment );

//        Intent intent = getIntent();
//        from = intent.getStringExtra( "from" );
//
//        int screen = intent.getIntExtra( "screen", 1 );
//        this.setRequestedOrientation( screen );
//
//        TextView titleText = (TextView) findViewById( R.id.txt_title );
//        LinearLayout repeatLayout = (LinearLayout) findViewById( R.id.layout_repeatpay );
//        LinearLayout unrepeatLayout = (LinearLayout) findViewById( R.id.layout_unrepeatpay );
//        TextView botTipText = (TextView) findViewById( R.id.txt_bottext );
//
//        String titleTxt = "购买可消耗商品(可买多个)";
//        String botTxt = "在正式环境中一定要注明米币和人民币的关系";
//        if ( from.equals( "repeatpay" ) )
//        {
//            repeatLayout.setVisibility( View.VISIBLE );
//            repeatPayButton();
//        }
//        else
//        {
//            titleTxt = "购买不可消耗商品";
//            botTxt = "此类商品一个用户只能购买一次";
//            unrepeatLayout.setVisibility( View.VISIBLE );
//            unRepeatPayButton();
//        }
//        titleText.setText( titleTxt );
//        botTipText.setText( botTxt );
    }

//    /**
//     * 为可消耗时显示
//     */
//    private void repeatPayButton()
//    {
//        Button button1 = (Button) findViewById( R.id.btn_0_01mibi );
//        button1.setOnClickListener( new OnClickListener()
//        {
//            @Override
//            public void onClick( View v )
//            {
//                MiBuyInfo miBuyInfo = createMiBuyInfo( /*"com.demo_1"*/"Ticket", 1 );
//                handler.sendMessage( handler.obtainMessage( ID_PAYMENT, miBuyInfo ) );
//            }
//        } );
//
//        Button button2 = (Button) findViewById( R.id.btn_0_05mibi );
//        button2.setOnClickListener( new OnClickListener()
//        {
//            @Override
//            public void onClick( View v )
//            {
//                MiBuyInfo miBuyInfo = createMiBuyInfo(/*"com.demo_2"*/"Ticket2", 1 );
//                handler.sendMessage( handler.obtainMessage( ID_PAYMENT, miBuyInfo ) );
//            }
//        } );
//
//        Button button3 = (Button) findViewById( R.id.btn_0_1mibi );
//        button3.setOnClickListener( new OnClickListener()
//        {
//            @Override
//            public void onClick( View v )
//            {
//                MiBuyInfo miBuyInfo = createMiBuyInfo( /*"com.demo_3"*/"Ticket3", 1 );
//                handler.sendMessage( handler.obtainMessage( ID_PAYMENT, miBuyInfo ) );
//            }
//        } );
//    }
//
//    /**
//     * 为不可消耗时显示
//     */
//    private void unRepeatPayButton()
//    {
//        Button button1 = (Button) findViewById( R.id.btn_1mibi );
//        button1.setOnClickListener( new OnClickListener()
//        {
//            @Override
//            public void onClick( View v )
//            {
//                MiBuyInfo miBuyInfo = createMiBuyInfo( "com.demo_4", 1 );
//                handler.sendMessage( handler.obtainMessage( ID_PAYMENT, miBuyInfo ) );
//            }
//        } );
//
//        Button button2 = (Button) findViewById( R.id.btn_0_5mibi );
//        button2.setOnClickListener( new OnClickListener()
//        {
//            @Override
//            public void onClick( View v )
//            {
//                MiBuyInfo miBuyInfo = createMiBuyInfo( "com.demo_5", 1 );
//                handler.sendMessage( handler.obtainMessage( ID_PAYMENT, miBuyInfo ) );
//            }
//        } );
//    }
//
//    private MiBuyInfo createMiBuyInfo( String productCode, int count )
//    {
//        MiBuyInfo miBuyInfo = new MiBuyInfo();
//        miBuyInfo.setProductCode( productCode );
//        miBuyInfo.setCount( count );
//        miBuyInfo.setCpOrderId( UUID.randomUUID().toString() );
//
//        return miBuyInfo;
//    }
//
//    private Handler handler = new Handler()
//    {
//        @Override
//        public void handleMessage( Message msg )
//        {
//            switch( msg.what )
//            {
//                case ID_PAYMENT:
//                    MiBuyInfo miBuyInfo = (MiBuyInfo) msg.obj;
//                    int errcode = MiCommplatform.getInstance().miUniPay( XiaomiPayActivity.this, miBuyInfo, XiaomiPayActivity.this );
//                    LogUtil.d("Junwang", "errcode = "+ errcode);
//                    break;
//                case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
//                    Toast.makeText( XiaomiPayActivity.this, "购买成功", Toast.LENGTH_LONG ).show();
//                    break;
//                case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_CANCEL:
//                case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL:
//                    Toast.makeText( XiaomiPayActivity.this, "取消购买", Toast.LENGTH_LONG ).show();
//                    break;
//                case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE:
//                    Toast.makeText( XiaomiPayActivity.this, "购买失败", Toast.LENGTH_LONG ).show();
//                    break;
//                case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_REPEAT:
//                    Toast.makeText( XiaomiPayActivity.this, "您已经购买过，无需购买", Toast.LENGTH_LONG ).show();
//                    break;
//                case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
//                    Toast.makeText( XiaomiPayActivity.this, "正在执行，不要重复操作", Toast.LENGTH_SHORT ).show();
//                    break;
//                case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_LOGIN_FAIL:
//                    Toast.makeText( XiaomiPayActivity.this, "您还没有登陆，请先登陆", Toast.LENGTH_LONG ).show();
//                    break;
//                default:
//                    break;
//            }
//        };
//    };
//
//    @Override
//    public void finishPayProcess( int arg0 )
//    {
//        handler.sendEmptyMessage( arg0 );
//    }
}

