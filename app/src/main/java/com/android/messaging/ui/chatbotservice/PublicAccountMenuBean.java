package com.android.messaging.ui.chatbotservice;

import java.util.List;

public class PublicAccountMenuBean {
    private String mCommandId;
    private int mCommandType;
    private int mParentMenuId;
    private int mPriority;
    private String mPublicAccountUuid;
    private List<PublicAccountMenuBean> mSubMenuBeans;
    private String mTitle;

    public PublicAccountMenuBean() {}

    public PublicAccountMenuBean(String paramString1, String paramString2, int paramInt1, int paramInt2, String paramString3, int paramInt3, List<PublicAccountMenuBean> paramList)
    {
        this.mPublicAccountUuid = paramString1;
        this.mCommandId = paramString2;
        this.mCommandType = paramInt1;
        this.mPriority = paramInt2;
        this.mTitle = paramString3;
        this.mParentMenuId = paramInt3;
        this.mSubMenuBeans = paramList;
    }

    public String getCommandId()
    {
        return this.mCommandId;
    }

    public int getCommandType()
    {
        return this.mCommandType;
    }

    public int getParentMenuId()
    {
        return this.mParentMenuId;
    }

    public int getPriority()
    {
        return this.mPriority;
    }

    public String getPublicAccountUuid()
    {
        return this.mPublicAccountUuid;
    }

    public List<PublicAccountMenuBean> getSubMenuBeans()
    {
        return this.mSubMenuBeans;
    }

    public String getTitle()
    {
        return this.mTitle;
    }

    public void setCommandId(String paramString)
    {
        this.mCommandId = paramString;
    }

    public void setCommandType(int paramInt)
    {
        this.mCommandType = paramInt;
    }

    public void setParentMenuId(int paramInt)
    {
        this.mParentMenuId = paramInt;
    }

    public void setPriority(int paramInt)
    {
        this.mPriority = paramInt;
    }

    public void setPublicAccountUuid(String paramString)
    {
        this.mPublicAccountUuid = paramString;
    }

    public void setSubMenuBeans(List<PublicAccountMenuBean> paramList)
    {
        this.mSubMenuBeans = paramList;
    }

    public void setTitle(String paramString)
    {
        this.mTitle = paramString;
    }

    public String toString()
    {
        return "PaMenuBean [mPaUuid=" + this.mPublicAccountUuid + ", mCmdId=" + this.mCommandId + ", mCmdType=" + this.mCommandType + ", mPriority=" + this.mPriority + ", mTitle=" + this.mTitle + ", mParentMenuId=" + this.mParentMenuId + ", paMenuBeans=" + this.mSubMenuBeans + "]";
    }
}
