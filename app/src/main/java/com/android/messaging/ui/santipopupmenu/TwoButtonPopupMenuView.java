package com.android.messaging.ui.santipopupmenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.datamodel.data.ButtonMenu;
import com.android.messaging.ui.chatbotservice.ChatbotMenuEntity;
import com.android.messaging.ui.chatbotservice.ChatbotMenuItem;
import com.android.messaging.ui.chatbotservice.SuggestionActionWrapper;
import com.android.messaging.ui.conversation.ConversationMessageView;
import com.android.messaging.ui.conversation.NativeFunctionUtil;
import com.android.messaging.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwoButtonPopupMenuView extends LinearLayout {
    private OptionMenuView mMenuView;
    private PopLayout mPopLayout;
    private ImageView mSwitchBt;
    private TextView mMenuButton1;
    private TextView mMenuButton2;
    private ArrayList<String> mMenuItem1;
    private ArrayList<String> mMenuItem2;
    private ArrayList<ButtonMenu.BusnMenuItem> mBusnMenuItem[];
    private String mButtonMenuAction[];
    private float mOffsetX;
    private int mOffsetY;
//    private Activity mActivity;

    public TwoButtonPopupMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void onMenuItemClick(ButtonMenu.BusnMenuItem bmi, View targetView){
        if(bmi != null){
            int actionType = bmi.getAction_type();
            String actionUrl = bmi.getAction_url();
            int actionLocalFunc = bmi.getAction_native_function();
            switch (actionType){
                case 1:
                    //load url
                    LogUtil.i("Junwang", "action type == 1");
                    NativeFunctionUtil.loadUrl(getContext(), actionUrl);
                    break;
                case 2:
                    //call native function
                    LogUtil.i("Junwang", "action type == 2");
                    NativeFunctionUtil.callNativeFunction(actionLocalFunc, ConversationMessageView.getActivityFromView(this), null, targetView, bmi.getAction_url());
                    break;
                case 3:
                    //jump to app
                    LogUtil.i("Junwang", "action type == 3");
                    NativeFunctionUtil.launchAPK(getContext(), actionUrl);
                    break;
                case 4:
                    //call alipay
                    LogUtil.i("Junwang", "action type == 4");
                    NativeFunctionUtil.callAlipay(ConversationMessageView.getActivityFromView(this), actionUrl, null);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        LogUtil.i("PopupMenuView", "PopupMenuView onFinishInflate");
        mSwitchBt = (ImageView)findViewById(R.id.switch_to_composemsg);
        mMenuButton1 = (TextView)findViewById(R.id.menu_button1);
//        ViewGroup.LayoutParams ly = mMenuButton1.getLayoutParams();
//        mMenuButton1.getLeft();
//        mMenuButton1.getRight();
//        mPopLayout = (PopLayout) findViewById(R.id.pl_pop);
//        mMenuView = (OptionMenuView) findViewById(R.id.omv_menu);
        mMenuButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LogUtil.i("PopupMenuView", "PopupMenuView onClick");
                mOffsetX = -0.1f; //0.2+0.4/2-0.5
                mOffsetY = TwoButtonPopupMenuView.this.getHeight();
//                final ArrayList<String> menuItem = new ArrayList<String>(){};
//                menuItem.add("拍照");
//                menuItem.add("相册");
//                menuItem.add("其他");
//                menuItem.add("查看历史记录");
//                menuItem.add("看看");
//                showCustomDialog(menuItem);
                if(mButtonMenuAction[0] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[0], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[0] != null) && (mBusnMenuItem[0].size() != 0)) {
                    showCustomDialog(mMenuItem1, 0);
                }else{
                    LogUtil.i("Junwang", "error! button1 menu action is null and menuitem is null");
                }
            }
        });
        mMenuButton2 = (TextView)findViewById(R.id.menu_button2);
        mMenuButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LogUtil.i("PopupMenuView", "PopupMenuView onClick");
                mOffsetX = 0.3f; // 1-0.4/2-0.5
                mOffsetY = TwoButtonPopupMenuView.this.getHeight();
//                final ArrayList<String> menuItem = new ArrayList<String>(){};
//                menuItem.add("打电话");
//                menuItem.add("发短信");
//                menuItem.add("新年快乐");
//                menuItem.add("我的");
//                showCustomDialog(menuItem);
                if(mButtonMenuAction[1] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[1], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[1] != null) && (mBusnMenuItem[1].size() != 0)) {
                    showCustomDialog(mMenuItem2, 1);
                }else{
                    LogUtil.i("Junwang", "error! button2 menu action is null and menuitem is null");
                }
            }
        });
        super.onFinishInflate();
    }

    //add by junwang for chatbot menu
    public void setMenu(ChatbotMenuEntity menuEntity){
        ChatbotMenuItem[] menuItem = menuEntity.getMenu().getEntries();
        if(menuItem == null || menuItem.length == 0){
            return;
        }
        int i = 0;
        mMenuItem1 = new ArrayList<String>(){};
        mMenuItem2 = new ArrayList<String>(){};
        mBusnMenuItem = new ArrayList[2];
        mButtonMenuAction = new String[2];
        for(ChatbotMenuItem menuitem : menuItem){
            i++;
            if(i == 1){
                mMenuButton1.setText(menuitem.getMenu().getDisplayText());
                mBusnMenuItem[0] = new ArrayList<>();
                SuggestionActionWrapper[] childMenu = menuitem.getMenu().getEntries();
                if(childMenu != null){
                    for(SuggestionActionWrapper child : childMenu){
                        mMenuItem1.add(child.getReply().displayText);
                        ButtonMenu.BusnMenuItem item = new ButtonMenu().new BusnMenuItem(child.getReply().getDisplayText(), child.getReply().getPostback().data, 1, 0);
                        mBusnMenuItem[0].add(item);
                    }
                    if(mMenuItem1.size() > 1){
                        Drawable left = getResources().getDrawable(R.drawable.icon_accordion);
                        left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
                        mMenuButton1.setCompoundDrawables(left, null, null, null);
                    }
                }
            }else if(i == 2){
                mMenuButton2.setText(menuitem.getMenu().getDisplayText());
                mBusnMenuItem[1] = new ArrayList<>();
                SuggestionActionWrapper[] childMenu = menuitem.getMenu().getEntries();
                if(childMenu != null){
                    for(SuggestionActionWrapper child : childMenu){
                        ButtonMenu.BusnMenuItem item = new ButtonMenu().new BusnMenuItem(child.getReply().getDisplayText(), child.getReply().getPostback().data, 1, 0);
                        mBusnMenuItem[1].add(item);
                        mMenuItem2.add(child.getReply().displayText);
                    }
                    if(mMenuItem2.size() > 1){
                        Drawable left = getResources().getDrawable(R.drawable.icon_accordion);
                        left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
                        mMenuButton2.setCompoundDrawables(left, null, null, null);
                    }
                }
            }
        }
    }

    public void setMenu(ArrayList<ButtonMenu> menu){
        if(menu == null){
            return;
        }
        mMenuItem1 = new ArrayList<String>(){};
        mMenuItem2 = new ArrayList<String>(){};
        mButtonMenuAction = new String[2];
        mBusnMenuItem = new ArrayList[2];
        int i = 0;
        for(ButtonMenu bm : menu) {
            i++;
            if(i == 1) {
                mMenuButton1.setText(bm.getMenu_name());
                if(bm.getAction_url() != null) {
                    mButtonMenuAction[0] = bm.getAction_url();
                }
                if(bm.getMenuList() != null) {
                    mBusnMenuItem[0] = new ArrayList<>(bm.getMenuList());
                    for (ButtonMenu.BusnMenuItem bmi : bm.getMenuList()) {
                        mMenuItem1.add(bmi.getItem_name());
                    }
                }
            }else if(i == 2 ){
//                mMenuItem2.clear();
                mMenuButton2.setText(bm.getMenu_name());
                if(bm.getAction_url() != null) {
                    mButtonMenuAction[1] = bm.getAction_url();
                }
                if(bm.getMenuList() != null) {
                    for (ButtonMenu.BusnMenuItem bmi : bm.getMenuList()) {
                        mMenuItem2.add(bmi.getItem_name());
                    }
                    mBusnMenuItem[1] = new ArrayList<>(bm.getMenuList());
                }
            }
        }
    }

    /**
     * 展示对话框视图，构造方法创建对象
     */
    private CustomSelectDialog showDialog(CustomSelectDialog.SelectDialogListener listener, List<String> names) {
        CustomSelectDialog dialog = new CustomSelectDialog(ConversationMessageView.getActivityFromView(this),
                R.style.transparentFrameWindowStyle, listener, names);
        dialog.setPopupMenuLayoutParams(mOffsetX, mMenuButton1.getWidth(), mOffsetY);
        LogUtil.i("Junwang", "CustomSelectDialog left="+mMenuButton1.getLeft()+", x="+ mMenuButton1.getX()+", right="
                +mMenuButton1.getRight()+", top="+mMenuButton1.getTop()+", height="+mMenuButton1.getY()+", parent_height="+this.getHeight());
        dialog.setItemColor(R.color.colorAccent,R.color.colorPrimary);
        //判断activity是否finish
        if (/*!this.isFinishing()*/true) {
            dialog.show();
        }
        return dialog;
    }

    private void showCustomDialog(ArrayList<String> menuItem, int buttonNo) {
        final List<String> names = new ArrayList<>(menuItem);
        showDialog(new CustomSelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.i("junwang", "action_url="+mBusnMenuItem[buttonNo].get(position).getAction_url());
                onMenuItemClick(mBusnMenuItem[buttonNo].get(position), view);
//                Toast.makeText(getContext(), /*names.get(position)*/mBusnMenuItem[buttonNo].get(position).getAction_url(), Toast.LENGTH_SHORT).show();
            }
        }, names);
    }

    //add by junwang
    private void popupBusnMenu(){
        mPopLayout.setVisibility(View.VISIBLE);
        mMenuView.setOptionMenus(Arrays.asList(
                new OptionMenu("复制"), new OptionMenu("转发到朋友圈"),
                new OptionMenu("收藏"), new OptionMenu("翻译"),
                new OptionMenu("删除")));
        mMenuView.setOrientation(LinearLayout.VERTICAL);

//        Path triangle = new Path();
//        triangle.lineTo(32, 0);
//        triangle.lineTo(16, 16);
//        triangle.close();
//
//        Path path = new Path();
//        path.addRoundRect(new RectF(0, 0, 100, 32), 16, 16, Path.Direction.CW);
//        path.addPath(triangle, 16, 32);
    }
}
