package com.android.messaging.ui.chatbotservice;

import org.json.JSONException;
import org.json.JSONObject;

public class PublicAccountMediaArticleBean {
    public static final String JSON_KEY_AUTHOR = "author";
    public static final String JSON_KEY_BODY_LINK = "bodyLink";
    public static final String JSON_KEY_ID = "id";
    public static final String JSON_KEY_MAIN_TEXT = "mainText";
    public static final String JSON_KEY_MEDIA_UUID = "mediaUuid";
    public static final String JSON_KEY_MESSAGE_ID = "messageId";
    public static final String JSON_KEY_ORIGINAL_LINK_STRING = "originalLinkString";
    public static final String JSON_KEY_SOURCE_LINK = "sourceLink";
    public static final String JSON_KEY_THUMB_LINK = "thumbLink";
    public static final String JSON_KEY_TITLE = "title";
    private String author;
    private String bodyLink;
    private int id;
    private String mainText;
    private String mediaUuid;
    private int messageId;
    private String originalLinkString;
    private String sourceLink;
    private String thumbLink;
    private String title;

    public void fromJSON(JSONObject paramJSONObject)
    {
        setId(paramJSONObject.optInt("id"));
        setMessageId(paramJSONObject.optInt("messageId"));
        setMediaUuid(paramJSONObject.optString("mediaUuid"));
        setTitle(paramJSONObject.optString("title"));
        setAuthor(paramJSONObject.optString("author"));
        setThumbLink(paramJSONObject.optString("thumbLink"));
        setOriginalLinkString(paramJSONObject.optString("originalLinkString"));
        setSourceLink(paramJSONObject.optString("sourceLink"));
        setMainText(paramJSONObject.optString("mainText"));
        setBodyLink(paramJSONObject.optString("bodyLink"));
    }

    public void fromJSONString(String paramString)
    {
        try
        {
            JSONObject localJSONObject = new JSONObject(paramString);
            fromJSON(localJSONObject);
            return;
        }
        catch (JSONException exception)
        {
            for (;;) {}
        }
    }

    public String getAuthor()
    {
        return this.author;
    }

    public String getBodyLink()
    {
        return this.bodyLink;
    }

    public int getId()
    {
        return this.id;
    }

    public String getMainText()
    {
        return this.mainText;
    }

    public String getMediaUuid()
    {
        return this.mediaUuid;
    }

    public int getMessageId()
    {
        return this.messageId;
    }

    public String getOriginalLinkString()
    {
        return this.originalLinkString;
    }

    public String getSourceLink()
    {
        return this.sourceLink;
    }

    public String getThumbLink()
    {
        return this.thumbLink;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setAuthor(String paramString)
    {
        this.author = paramString;
    }

    public void setBodyLink(String paramString)
    {
        this.bodyLink = paramString;
    }

    public void setId(int paramInt)
    {
        this.id = paramInt;
    }

    public void setMainText(String paramString)
    {
        this.mainText = paramString;
    }

    public void setMediaUuid(String paramString)
    {
        this.mediaUuid = paramString;
    }

    public void setMessageId(int paramInt)
    {
        this.messageId = paramInt;
    }

    public void setOriginalLinkString(String paramString)
    {
        this.originalLinkString = paramString;
    }

    public void setSourceLink(String paramString)
    {
        this.sourceLink = paramString;
    }

    public void setThumbLink(String paramString)
    {
        this.thumbLink = paramString;
    }

    public void setTitle(String paramString)
    {
        this.title = paramString;
    }

    public JSONObject toJSON()
    {
        try
        {
            JSONObject localJSONObject = new JSONObject();
            localJSONObject.put("id", this.id);
            localJSONObject.put("messageId", this.messageId);
            localJSONObject.put("mediaUuid", this.mediaUuid);
            localJSONObject.put("title", this.title);
            localJSONObject.put("author", this.author);
            localJSONObject.put("thumbLink", this.thumbLink);
            localJSONObject.put("originalLinkString", this.originalLinkString);
            localJSONObject.put("sourceLink", this.sourceLink);
            localJSONObject.put("mainText", this.mainText);
            localJSONObject.put("bodyLink", this.bodyLink);
            return localJSONObject;
        }
        catch (JSONException localJSONException)
        {
            for (;;)
            {
                Object localObject = null;
            }
        }
    }

    public String toJSONString()
    {
        Object localObject = toJSON();
        if (localObject != null) {}
        for (localObject = ((JSONObject)localObject).toString();; localObject = null) {
            return (String)localObject;
        }
    }
}
