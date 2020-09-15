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
package com.android.messaging.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.messaging.R;
import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseHelper;
import com.android.messaging.datamodel.DatabaseWrapper;
import com.android.messaging.datamodel.data.ParticipantData;
import com.android.messaging.datamodel.media.AvatarGroupRequestDescriptor;
import com.android.messaging.datamodel.media.AvatarRequestDescriptor;
import com.android.messaging.datamodel.media.FileImageRequestDescriptor;
import com.android.messaging.ui.conversation.chatbot.ChatbotIntroduceActivity;
import com.android.messaging.util.Assert;
import com.android.messaging.util.AvatarUriUtil;
import com.android.messaging.util.ContactUtil;
import com.android.messaging.util.LogUtil;

/**
 * A view used to render contact icons. This class derives from AsyncImageView, so it loads contact
 * icons from MediaResourceManager, and it handles more rendering logic than an AsyncImageView
 * (draws a circular bitmap).
 */
public class ContactIconView extends AsyncImageView {
    private static final int NORMAL_ICON_SIZE_ID = 0;
    private static final int LARGE_ICON_SIZE_ID = 1;
    private static final int SMALL_ICON_SIZE_ID = 2;

    protected final int mIconSize;
    private final int mColorPressedId;

    private long mContactId;
    private String mContactLookupKey;
    private String mNormalizedDestination;
    private Uri mAvatarUri;
    private boolean mDisableClickHandler;
    //add by junwang
    private boolean mIsH5Contact = false;

    public ContactIconView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final Resources resources = context.getResources();
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ContactIconView);

        final int iconSizeId = a.getInt(R.styleable.ContactIconView_iconSize, 0);
        switch (iconSizeId) {
            case NORMAL_ICON_SIZE_ID:
                mIconSize = (int) resources.getDimension(
                        R.dimen.contact_icon_view_normal_size);
                break;
            case LARGE_ICON_SIZE_ID:
                mIconSize = (int) resources.getDimension(
                        R.dimen.contact_icon_view_large_size);
                break;
            case SMALL_ICON_SIZE_ID:
                mIconSize = (int) resources.getDimension(
                        R.dimen.contact_icon_view_small_size);
                break;
            default:
                // For the compiler, something has to be set even with the assert.
                mIconSize = 0;
                Assert.fail("Unsupported ContactIconView icon size attribute");
        }
        mColorPressedId = resources.getColor(R.color.contact_avatar_pressed_color);

        setImage(null);
        a.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            setColorFilter(mColorPressedId);
        } else {
            clearColorFilter();
        }
        return super.onTouchEvent(event);
    }

    /**
     * Method which allows the automatic hookup of a click handler when the Uri is changed
     */
    public void setImageClickHandlerDisabled(final boolean isHandlerDisabled) {
        mDisableClickHandler = isHandlerDisabled;
        setOnClickListener(null);
        setClickable(false);
    }

    /**
     * A convenience method that sets the URI of the contact icon by creating a new image request.
     */
    public void setImageResourceUri(final Uri uri) {
        setImageResourceUri(uri, 0, null, null);
    }

    public void setImageResourceUri(final Uri uri, final long contactId,
            final String contactLookupKey, final String normalizedDestination) {
        if (uri == null) {
            setImageResourceId(null);
        } else {
            //add by junwang for get contact icon from database if contact is in H5 white list.
            final String avatarType = AvatarUriUtil.getAvatarType(uri);
            if (AvatarUriUtil.TYPE_GROUP_URI.equals(avatarType)) {
                setImageResourceId(new AvatarGroupRequestDescriptor(uri, mIconSize, mIconSize));
            } else {
//                //add by junwang for H5 contact icon
//                if(DatabaseHelper.isAddrInWebViewWhiteList(normalizedDestination, contactLookupKey)){
//                    LogUtil.i("Junwang", "normalizedDestination="+normalizedDestination+", contactLookupKey="+contactLookupKey);
//                    String profilePhotoPath = queryContactFromH5DB(normalizedDestination);
//                    LogUtil.i("Junwang", "profilePhotoPath="+profilePhotoPath);
//                    if(profilePhotoPath != null) {
//                        setImageResourceId(new AvatarRequestDescriptor(Uri.parse(profilePhotoPath), mIconSize, mIconSize));
//                    }else{
//                        setImageResourceId(new AvatarRequestDescriptor(uri, mIconSize, mIconSize));
//                    }
//                }else {
//                    //add by junwang end
//                    setImageResourceId(new AvatarRequestDescriptor(uri, mIconSize, mIconSize));
                setImageResourceId(new AvatarRequestDescriptor(uri, mIconSize, mIconSize));
//                setImageResourceId(new FileImageRequestDescriptor(uri.getPath(), mIconSize, mIconSize, true));
//                }
            }
        }

        mContactId = contactId;
        mContactLookupKey = contactLookupKey;
        mNormalizedDestination = normalizedDestination;
        mAvatarUri = uri;

        maybeInitializeOnClickListener();
    }

    //add by junwang
    public void setImageResourceUri(final Uri uri, final long contactId,
                                    final String contactLookupKey, final String normalizedDestination, boolean mIsH5Contact) {
        if (uri == null) {
            setImageResourceId(null);
        } else {
            //add by junwang for get contact icon from database if contact is in H5 white list.
            final String avatarType = AvatarUriUtil.getAvatarType(uri);
            if (AvatarUriUtil.TYPE_GROUP_URI.equals(avatarType)) {
                setImageResourceId(new AvatarGroupRequestDescriptor(uri, mIconSize, mIconSize));
            } else {
//                //add by junwang for H5 contact icon
//                if(DatabaseHelper.isAddrInWebViewWhiteList(normalizedDestination, contactLookupKey)){
//                    LogUtil.i("Junwang", "normalizedDestination="+normalizedDestination+", contactLookupKey="+contactLookupKey);
//                    String profilePhotoPath = queryContactFromH5DB(normalizedDestination);
//                    LogUtil.i("Junwang", "profilePhotoPath="+profilePhotoPath);
//                    if(profilePhotoPath != null) {
//                        setImageResourceId(new AvatarRequestDescriptor(Uri.parse(profilePhotoPath), mIconSize, mIconSize));
//                    }else{
//                        setImageResourceId(new AvatarRequestDescriptor(uri, mIconSize, mIconSize));
//                    }
//                }else {
//                    //add by junwang end
//                    setImageResourceId(new AvatarRequestDescriptor(uri, mIconSize, mIconSize));
                if(!mIsH5Contact) {
                    setImageResourceId(new AvatarRequestDescriptor(uri, mIconSize, mIconSize));
                }else {
                    if(uri != null && uri.toString().startsWith("/data")) {
                        setImageResourceId(new FileImageRequestDescriptor(uri.getPath(), mIconSize, mIconSize, true));
                    }else{
                        LogUtil.i("Junwang", "[setImageResourceUri] H5 contact with logo not saved in /data/...");
                        setImageResourceId(new AvatarRequestDescriptor(uri, mIconSize, mIconSize));
                    }
                }
//                }
            }
        }

        mContactId = contactId;
        mContactLookupKey = contactLookupKey;
        mNormalizedDestination = normalizedDestination;
        mAvatarUri = uri;

        maybeInitializeOnClickListener();
    }

    private String queryContactFromH5DB(String normalizedDestination){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        Cursor cursor = null;
        try {
            cursor = mdbWrapper.rawQuery("SELECT  "+ DatabaseHelper.H5CodeNumberColumns.H5_BUSINESS_LOGO + " FROM "
                    + DatabaseHelper.H5WHITELIST_TABLE + " WHERE " + DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER
                    + " = " + normalizedDestination, null);

            if(cursor != null) {
                cursor.moveToFirst();
                LogUtil.i("Junwang", "H5 table count =" + cursor.getCount());
                return cursor.getString(0);
            }
        }catch (Exception e){
            LogUtil.e("Junwang", e.toString());
        }
        return null;
    }

    protected void maybeInitializeOnClickListener() {
        if ((mContactId > ParticipantData.PARTICIPANT_CONTACT_ID_NOT_RESOLVED
                && !TextUtils.isEmpty(mContactLookupKey)) ||
                !TextUtils.isEmpty(mNormalizedDestination)) {
            if (!mDisableClickHandler) {
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        if(mNormalizedDestination.startsWith("sip:")){
                            ChatbotIntroduceActivity.start(getContext(), mNormalizedDestination);
                        }else {
                            ContactUtil.showOrAddContact(view, mContactId, mContactLookupKey,
                                    mAvatarUri, mNormalizedDestination);
                        }
                    }
                });
            }
        } else {
            // This should happen when the phone number is not in the user's contacts or it is a
            // group conversation, group conversations don't have contact phone numbers. If this
            // is the case then absorb the click to prevent propagation.
            setOnClickListener(null);
        }
    }
}
