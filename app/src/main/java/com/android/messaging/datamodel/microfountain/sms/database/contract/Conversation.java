package com.android.messaging.datamodel.microfountain.sms.database.contract;

import android.net.Uri;
import android.provider.BaseColumns;

import com.microfountain.rcs.aidl.flavors.ProductFlavor;

/**
 * 会话数据表的样例代码，Rcs Service APK 暂时不会访问此数据表。
 */
public final class Conversation {

    /**
     * 数据表名称，如果接入方针对 URI 有特殊处理的话，实际数据库的表名可以与此字段不一致。
     */
    public static final String TABLE_NAME = "conversation";

    /**
     *
     */
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY).appendPath(TABLE_NAME).build();

    public static final class Columns implements BaseColumns {

        /**
         * 联系人 URI String
         * <p>
         * 如果为一对一通话的联系人，其格式应当为 tel+86xxxxxxxxxxx
         */
        public static final String CONTACT_URI = "contact_uri";

        /**
         * 会话类型
         */
        public static final String CONVERSATION_TYPE = "conversation_type";

        /**
         * 会话中的未读消息数量
         */
        public static final String UNREAD_COUNT = "unread_count";

        /**
         * 会话的最近修改时间
         */
        public static final String DATE = "date";
    }
}
