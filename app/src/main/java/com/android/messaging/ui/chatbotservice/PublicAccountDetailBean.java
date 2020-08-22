package com.android.messaging.ui.chatbotservice;

public class PublicAccountDetailBean extends PublicAccountBean
{
    private String mCompany;
    private int mMenuTimestamp;
    private int mMenuType;
    private String mQrCode;
    private boolean mReceiveMessage;
    private int mUpdateTime;

    public PublicAccountDetailBean() {}

    public PublicAccountDetailBean(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, String paramString5, String paramString6)
    {
        super(paramString1, paramString2, paramInt1, paramString5, paramString3, paramBoolean1);
        this.mCompany = paramString4;
        this.mUpdateTime = paramInt2;
        this.mMenuType = paramInt3;
        this.mMenuTimestamp = paramInt4;
        this.mReceiveMessage = paramBoolean2;
        this.mQrCode = paramString6;
    }

    public String getCompany()
    {
        return this.mCompany;
    }

    public int getMenuTimestamp()
    {
        return this.mMenuTimestamp;
    }

    public int getMenuType()
    {
        return this.mMenuType;
    }

    public String getQrCode()
    {
        return this.mQrCode;
    }

    public int getUpdateTime()
    {
        return this.mUpdateTime;
    }

    public boolean isReceiveMessage()
    {
        return this.mReceiveMessage;
    }

    public void setCompany(String paramString)
    {
        this.mCompany = paramString;
    }

    public void setMenuTimestamp(int paramInt)
    {
        this.mMenuTimestamp = paramInt;
    }

    public void setMenuType(int paramInt)
    {
        this.mMenuType = paramInt;
    }

    public void setQrCode(String paramString)
    {
        this.mQrCode = paramString;
    }

    public void setReceiveMessage(boolean paramBoolean)
    {
        this.mReceiveMessage = paramBoolean;
    }

    public void setUpdateTime(int paramInt)
    {
        this.mUpdateTime = paramInt;
    }

    public String toString()
    {
        return "PaDetailBean [mUuid=" + getUuid() + ", mName=" + getName() + ", mIntro=" + getIntroduction() + ", mCompany=" + this.mCompany + ", mRecmdLv=" + getRecommendLevel() + ", mUpdTime=" + this.mUpdateTime + ", mMenuType=" + this.mMenuType + ", mMenuTimestamp=" + this.mMenuTimestamp + ", mSubsStat=" + isSubscribed() + ", mOrCode=" + this.mQrCode + "]";
    }
}

