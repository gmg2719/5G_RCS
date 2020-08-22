package com.android.messaging.ui.chatbotservice;

public class PublicAccountBean {
    private int mId;
    private String mIntroduction;
    private String mLogoUrl;
    private String mName;
    private int mRecommendLevel;
    private boolean mSubscribed;
    private String mUuid;

    public PublicAccountBean() {}

    public PublicAccountBean(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, boolean paramBoolean)
    {
        this.mUuid = paramString1;
        this.mName = paramString2;
        this.mRecommendLevel = paramInt;
        this.mLogoUrl = paramString3;
        this.mIntroduction = paramString4;
        this.mSubscribed = paramBoolean;
    }

    public int getId()
    {
        return this.mId;
    }

    public String getIntroduction()
    {
        return this.mIntroduction;
    }

    public String getLogoLocal()
    {
        return "PA_LOGO_" + this.mUuid;
    }

    public String getLogoUrl()
    {
        return this.mLogoUrl;
    }

    public String getName()
    {
        return this.mName;
    }

    public int getRecommendLevel()
    {
        return this.mRecommendLevel;
    }

    public String getUuid()
    {
        return this.mUuid;
    }

    public boolean isSubscribed()
    {
        return this.mSubscribed;
    }

    public void setId(int paramInt)
    {
        this.mId = paramInt;
    }

    public void setIntroduction(String paramString)
    {
        this.mIntroduction = paramString;
    }

    public void setLogoUrl(String paramString)
    {
        this.mLogoUrl = paramString;
    }

    public void setName(String paramString)
    {
        this.mName = paramString;
    }

    public void setRecommendLevel(int paramInt)
    {
        this.mRecommendLevel = paramInt;
    }

    public void setSubscribed(boolean paramBoolean)
    {
        this.mSubscribed = paramBoolean;
    }

    public void setUuid(String paramString)
    {
        this.mUuid = paramString;
    }

    public String toString()
    {
        return "PaBean [mUuid=" + this.mUuid + ", mName=" + this.mName + ", mRecmdLv=" + this.mRecommendLevel + ", mLogoUrl=" + this.mLogoUrl + "]";
    }
}