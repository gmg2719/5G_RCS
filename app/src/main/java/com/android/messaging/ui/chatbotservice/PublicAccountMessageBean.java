package com.android.messaging.ui.chatbotservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PublicAccountMessageBean {
    public static final String JSON_KEY_ACTIVE_STATUS = "activeStatus";
    public static final String JSON_KEY_BOX_TYPE = "boxType";
    public static final String JSON_KEY_CREATE_TIME = "createTime";
    public static final String JSON_KEY_ERROR_CODE = "errorCode";
    public static final String JSON_KEY_FILE_DURATION = "fileDuration";
    public static final String JSON_KEY_FILE_NAME = "fileName";
    public static final String JSON_KEY_FILE_PATH = "filePath";
    public static final String JSON_KEY_FILE_SIZE = "fileSize";
    public static final String JSON_KEY_FILE_THUMB_PATH = "fileThumbPath";
    public static final String JSON_KEY_FILE_TRANS_ID = "fileTransId";
    public static final String JSON_KEY_FILE_TRANS_SIZE = "fileTransSize";
    public static final String JSON_KEY_FILE_TYPE = "fileType";
    public static final String JSON_KEY_FORWARDABLE = "forwardable";
    public static final String JSON_KEY_GEO_FREE_TEXT = "geoFreeText";
    public static final String JSON_KEY_GEO_LATITUDE = "geoLatitude";
    public static final String JSON_KEY_GEO_LONGITUDE = "geoLongitude";
    public static final String JSON_KEY_GEO_RADIUS = "geoRadius";
    public static final String JSON_KEY_ID = "id";
    public static final String JSON_KEY_IMDN_MSG_ID = "imdnMsgId";
    public static final String JSON_KEY_IMDN_TYPE = "imdnType";
    public static final String JSON_KEY_IS_READ = "isRead";
    public static final String JSON_KEY_MEDIA_ARTICLES = "mediaArticles";
    public static final String JSON_KEY_MEDIA_TYPE = "mediaType";
    public static final String JSON_KEY_MEDIA_UUID = "mediaUuid";
    public static final String JSON_KEY_MSG_UUID = "msgUuid";
    public static final String JSON_KEY_ORIGINAL_LINK = "originalLink";
    public static final String JSON_KEY_SENDER_NUMBER = "senderNumber";
    public static final String JSON_KEY_SMS_DIGEST = "smsDigest";
    public static final String JSON_KEY_STATUS = "status";
    public static final String JSON_KEY_TEXT = "text";
    public static final String JSON_KEY_THUMB_LINK = "thumbLink";
    public static final String JSON_KEY_TITLE = "title";
    public static final String JSON_KEY_UUID = "uuid";
    private boolean mActiveStatus;
    private int mBoxType;
    private int mCreateTime;
    private int mErrorCode;
    private int mFileDuration;
    private String mFileName;
    private String mFilePath;
    private int mFileSize;
    private String mFileThumbPath;
    private String mFileTransId;
    private int mFileTransSize;
    private String mFileType;
    private boolean mForwardable;
    private String mGeoFreeText;
    private String mGeoLatitude;
    private String mGeoLongitude;
    private String mGeoRadius;
    private int mId;
    private String mImdnMsgId;
    private int mImdnType;
    private List<PublicAccountMediaArticleBean> mMediaArticles;
    private int mMediaType;
    private String mMediaUuid;
    private String mMsgUuid;
    private String mOriginalLink;
    private boolean mRead;
    private String mSenderNumber;
    private String mSmsDigest;
    private int mStatus;
    private String mText;
    private String mThumbLink;
    private String mTitle;
    private String mUuid;

    public void fromJSON(JSONObject paramJSONObject)
    {
        setId(paramJSONObject.optInt("id"));
        setUuid(paramJSONObject.optString("uuid"));
        setSenderNumber(paramJSONObject.optString("senderNumber"));
        setMsgUuid(paramJSONObject.optString("msgUuid"));
        setMediaType(paramJSONObject.optInt("mediaType"));
        setCreateTime(paramJSONObject.optInt("createTime"));
        setSmsDigest(paramJSONObject.optString("smsDigest"));
        setText(paramJSONObject.optString("text"));
        setActiveStatus(paramJSONObject.optBoolean("activeStatus"));
        setForwardable(paramJSONObject.optBoolean("forwardable"));
        setRead(paramJSONObject.optBoolean("isRead"));
        setErrorCode(paramJSONObject.optInt("errorCode"));
        setStatus(paramJSONObject.optInt("status"));
        setBoxType(paramJSONObject.optInt("boxType"));
        setFileName(paramJSONObject.optString("fileName"));
        setFileType(paramJSONObject.optString("fileType"));
        setFilePath(paramJSONObject.optString("filePath"));
        setFileThumbPath(paramJSONObject.optString("fileThumbPath"));
        setFileTransId(paramJSONObject.optString("fileTransId"));
        setFileDuration(paramJSONObject.optInt("fileDuration"));
        setFileSize(paramJSONObject.optInt("fileSize"));
        setFileTransSize(paramJSONObject.optInt("fileTransSize"));
        setGeoLatitude(paramJSONObject.optString("geoLatitude"));
        setGeoLongitude(paramJSONObject.optString("geoLongitude"));
        setGeoFreeText(paramJSONObject.optString("geoFreeText"));
        setGeoRadius(paramJSONObject.optString("geoRadius"));
        setImdnMsgId(paramJSONObject.optString("imdnMsgId"));
        setImdnType(paramJSONObject.optInt("imdnType"));
        setMediaUuid(paramJSONObject.optString("mediaUuid"));
        setThumbLink(paramJSONObject.optString("thumbLink"));
        setOriginalLink(paramJSONObject.optString("originalLink"));
        setTitle(paramJSONObject.optString("title"));
        JSONArray localJSONArray = paramJSONObject.optJSONArray("mediaArticles");
        paramJSONObject = null;
        ArrayList localArrayList = null;
        PublicAccountMediaArticleBean pamab = null;
        if (localJSONArray != null)
        {
            localArrayList = new ArrayList(localJSONArray.length());
            for (int i = 0;; i++)
            {
                if (i >= localJSONArray.length()) {
                    break;
                }
                pamab = new PublicAccountMediaArticleBean();
                pamab.fromJSON(localJSONArray.optJSONObject(i));
                localArrayList.add(pamab);
            }
        }
        setMediaArticles(localArrayList);
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

    public int getBoxType()
    {
        return this.mBoxType;
    }

    public int getCreateTime()
    {
        return this.mCreateTime;
    }

    public int getErrorCode()
    {
        return this.mErrorCode;
    }

    public int getFileDuration()
    {
        return this.mFileDuration;
    }

    public String getFileName()
    {
        return this.mFileName;
    }

    public String getFilePath()
    {
        return this.mFilePath;
    }

    public int getFileSize()
    {
        return this.mFileSize;
    }

    public String getFileThumbPath()
    {
        return this.mFileThumbPath;
    }

    public String getFileTransId()
    {
        return this.mFileTransId;
    }

    public int getFileTransSize()
    {
        return this.mFileTransSize;
    }

    public String getFileType()
    {
        return this.mFileType;
    }

    public String getGeoFreeText()
    {
        return this.mGeoFreeText;
    }

    public String getGeoLatitude()
    {
        return this.mGeoLatitude;
    }

    public String getGeoLongitude()
    {
        return this.mGeoLongitude;
    }

    public String getGeoRadius()
    {
        return this.mGeoRadius;
    }

    public int getId()
    {
        return this.mId;
    }

    public String getImdnMsgId()
    {
        return this.mImdnMsgId;
    }

    public int getImdnType()
    {
        return this.mImdnType;
    }

    public List<PublicAccountMediaArticleBean> getMediaArticles()
    {
        return this.mMediaArticles;
    }

    public int getMediaType()
    {
        return this.mMediaType;
    }

    public String getMediaUuid()
    {
        return this.mMediaUuid;
    }

    public String getMsgUuid()
    {
        return this.mMsgUuid;
    }

    public String getOriginalLink()
    {
        return this.mOriginalLink;
    }

    public String getSenderNumber()
    {
        return this.mSenderNumber;
    }

    public String getSmsDigest()
    {
        return this.mSmsDigest;
    }

    public int getStatus()
    {
        return this.mStatus;
    }

    public String getText()
    {
        return this.mText;
    }

    public String getThumbLink()
    {
        return this.mThumbLink;
    }

    public String getTitle()
    {
        return this.mTitle;
    }

    public String getUuid()
    {
        return this.mUuid;
    }

    public boolean isActiveStatus()
    {
        return this.mActiveStatus;
    }

    public boolean isForwardable()
    {
        return this.mForwardable;
    }

    public boolean isRead()
    {
        return this.mRead;
    }

    public void setActiveStatus(boolean paramBoolean)
    {
        this.mActiveStatus = paramBoolean;
    }

    public void setBoxType(int paramInt)
    {
        this.mBoxType = paramInt;
    }

    public void setCreateTime(int paramInt)
    {
        this.mCreateTime = paramInt;
    }

    public void setErrorCode(int paramInt)
    {
        this.mErrorCode = paramInt;
    }

    public void setFileDuration(int paramInt)
    {
        this.mFileDuration = paramInt;
    }

    public void setFileName(String paramString)
    {
        this.mFileName = paramString;
    }

    public void setFilePath(String paramString)
    {
        this.mFilePath = paramString;
    }

    public void setFileSize(int paramInt)
    {
        this.mFileSize = paramInt;
    }

    public void setFileThumbPath(String paramString)
    {
        this.mFileThumbPath = paramString;
    }

    public void setFileTransId(String paramString)
    {
        this.mFileTransId = paramString;
    }

    public void setFileTransSize(int paramInt)
    {
        this.mFileTransSize = paramInt;
    }

    public void setFileType(String paramString)
    {
        this.mFileType = paramString;
    }

    public void setForwardable(boolean paramBoolean)
    {
        this.mForwardable = paramBoolean;
    }

    public void setGeoFreeText(String paramString)
    {
        this.mGeoFreeText = paramString;
    }

    public void setGeoLatitude(String paramString)
    {
        this.mGeoLatitude = paramString;
    }

    public void setGeoLongitude(String paramString)
    {
        this.mGeoLongitude = paramString;
    }

    public void setGeoRadius(String paramString)
    {
        this.mGeoRadius = paramString;
    }

    public void setId(int paramInt)
    {
        this.mId = paramInt;
    }

    public void setImdnMsgId(String paramString)
    {
        this.mImdnMsgId = paramString;
    }

    public void setImdnType(int paramInt)
    {
        this.mImdnType = paramInt;
    }

    public void setMediaArticles(List<PublicAccountMediaArticleBean> paramList)
    {
        this.mMediaArticles = paramList;
    }

    public void setMediaType(int paramInt)
    {
        this.mMediaType = paramInt;
    }

    public void setMediaUuid(String paramString)
    {
        this.mMediaUuid = paramString;
    }

    public void setMsgUuid(String paramString)
    {
        this.mMsgUuid = paramString;
    }

    public void setOriginalLink(String paramString)
    {
        this.mOriginalLink = paramString;
    }

    public void setRead(boolean paramBoolean)
    {
        this.mRead = paramBoolean;
    }

    public void setSenderNumber(String paramString)
    {
        this.mSenderNumber = paramString;
    }

    public void setSmsDigest(String paramString)
    {
        this.mSmsDigest = paramString;
    }

    public void setStatus(int paramInt)
    {
        this.mStatus = paramInt;
    }

    public void setText(String paramString)
    {
        this.mText = paramString;
    }

    public void setThumbLink(String paramString)
    {
        this.mThumbLink = paramString;
    }

    public void setTitle(String paramString)
    {
        this.mTitle = paramString;
    }

    public void setUuid(String paramString)
    {
        this.mUuid = paramString;
    }

    public JSONObject toJSON()
    {
        JSONObject localJSONObject1=null;
        JSONArray localJSONArray=null;
        JSONObject localJSONObject2;
        try
        {
            localJSONObject1 = new JSONObject();
            localJSONObject1.put("id", this.mId);
            localJSONObject1.put("uuid", this.mUuid);
            localJSONObject1.put("senderNumber", this.mSenderNumber);
            localJSONObject1.put("msgUuid", this.mMsgUuid);
            localJSONObject1.put("mediaType", this.mMediaType);
            localJSONObject1.put("createTime", this.mCreateTime);
            localJSONObject1.put("smsDigest", this.mSmsDigest);
            localJSONObject1.put("text", this.mText);
            localJSONObject1.put("activeStatus", this.mActiveStatus);
            localJSONObject1.put("forwardable", this.mForwardable);
            localJSONObject1.put("isRead", this.mRead);
            localJSONObject1.put("errorCode", this.mErrorCode);
            localJSONObject1.put("status", this.mStatus);
            localJSONObject1.put("boxType", this.mBoxType);
            localJSONObject1.put("fileName", this.mFileName);
            localJSONObject1.put("fileType", this.mFileType);
            localJSONObject1.put("filePath", this.mFilePath);
            localJSONObject1.put("fileThumbPath", this.mFileThumbPath);
            localJSONObject1.put("fileTransId", this.mFileTransId);
            localJSONObject1.put("fileDuration", this.mFileDuration);
            localJSONObject1.put("fileSize", this.mFileSize);
            localJSONObject1.put("fileTransSize", this.mFileTransSize);
            localJSONObject1.put("geoLatitude", this.mGeoLatitude);
            localJSONObject1.put("geoLongitude", this.mGeoLongitude);
            localJSONObject1.put("geoFreeText", this.mGeoFreeText);
            localJSONObject1.put("geoRadius", this.mGeoRadius);
            localJSONObject1.put("imdnMsgId", this.mImdnMsgId);
            localJSONObject1.put("imdnType", this.mImdnType);
            localJSONObject1.put("mediaUuid", this.mMediaUuid);
            localJSONObject1.put("thumbLink", this.mThumbLink);
            localJSONObject1.put("originalLink", this.mOriginalLink);
            localJSONObject1.put("title", this.mTitle);
            Object localObject = localJSONObject1;
            if (this.mMediaArticles != null)
            {
                localJSONArray = new JSONArray();
                localObject = this.mMediaArticles.iterator();
                while (((Iterator)localObject).hasNext()) {
                    localJSONArray.put(((PublicAccountMediaArticleBean)((Iterator)localObject).next()).toJSON());
                }
            }
            for (;;)
            {
                localJSONObject1.put("mediaArticles", localJSONArray);
                localJSONObject2 = localJSONObject1;
            }

        }
        catch (JSONException localJSONException)
        {
            localJSONObject2 = null;
        }
        return localJSONObject2;
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
