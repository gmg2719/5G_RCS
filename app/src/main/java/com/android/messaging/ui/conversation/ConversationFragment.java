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

package com.android.messaging.ui.conversation;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.text.BidiFormatter;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseHelper;
import com.android.messaging.datamodel.MessagingContentProvider;
import com.android.messaging.datamodel.action.InsertNewMessageAction;
import com.android.messaging.datamodel.binding.Binding;
import com.android.messaging.datamodel.binding.BindingBase;
import com.android.messaging.datamodel.binding.ImmutableBindingRef;
import com.android.messaging.datamodel.data.ButtonMenu;
import com.android.messaging.datamodel.data.ConversationData;
import com.android.messaging.datamodel.data.ConversationData.ConversationDataListener;
import com.android.messaging.datamodel.data.ConversationMessageData;
import com.android.messaging.datamodel.data.ConversationParticipantsData;
import com.android.messaging.datamodel.data.DraftMessageData;
import com.android.messaging.datamodel.data.DraftMessageData.DraftMessageDataListener;
import com.android.messaging.datamodel.data.FloatingWebView;
import com.android.messaging.datamodel.data.MessageData;
import com.android.messaging.datamodel.data.MessagePartData;
import com.android.messaging.datamodel.data.ParticipantData;
import com.android.messaging.datamodel.data.SubscriptionListData.SubscriptionListEntry;
import com.android.messaging.ui.AttachmentPreview;
import com.android.messaging.ui.BugleActionBarActivity;
import com.android.messaging.ui.ConversationDrawables;
import com.android.messaging.ui.SnackBar;
import com.android.messaging.ui.UIIntents;
import com.android.messaging.ui.animation.PopupTransitionAnimation;
import com.android.messaging.ui.appsettings.H5WLDatabaseHelper;
import com.android.messaging.ui.chatbotservice.ChatbotMenuEntity;
import com.android.messaging.ui.contact.AddContactsConfirmationDialog;
import com.android.messaging.ui.conversation.ComposeMessageView.IComposeMessageViewHost;
import com.android.messaging.ui.conversation.ConversationInputManager.ConversationInputHost;
import com.android.messaging.ui.conversation.ConversationMessageView.ConversationMessageViewHost;
import com.android.messaging.ui.conversation.chatbot.ChatbotFavoriteActivity;
import com.android.messaging.ui.mediapicker.MediaPicker;
import com.android.messaging.ui.santipopupmenu.ThreeButtonPopupMenuView;
import com.android.messaging.ui.santipopupmenu.TwoButtonPopupMenuView;
import com.android.messaging.util.AccessibilityUtil;
import com.android.messaging.util.Assert;
import com.android.messaging.util.AvatarUriUtil;
import com.android.messaging.util.ChangeDefaultSmsAppHelper;
import com.android.messaging.util.ContentType;
import com.android.messaging.util.ImeUtil;
import com.android.messaging.util.LogUtil;
import com.android.messaging.util.OsUtil;
import com.android.messaging.util.PhoneUtils;
import com.android.messaging.util.SafeAsyncTask;
import com.android.messaging.util.TextUtil;
import com.android.messaging.util.UiUtils;
import com.android.messaging.util.UriUtil;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Shows a list of messages/parts comprising a conversation.
 */
public class ConversationFragment extends Fragment implements ConversationDataListener,
        IComposeMessageViewHost, ConversationMessageViewHost, ConversationInputHost,
        DraftMessageDataListener,RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener , View.OnClickListener {

    public interface ConversationFragmentHost extends ImeUtil.ImeStateHost {
        void onStartComposeMessage();
        void onConversationMetadataUpdated();
        boolean shouldResumeComposeMessage();
        void onFinishCurrentConversation();
        void invalidateActionBar();
        ActionMode startActionMode(ActionMode.Callback callback);
        void dismissActionMode();
        ActionMode getActionMode();
        void onConversationMessagesUpdated(int numberOfMessages);
        void onConversationParticipantDataLoaded(int numberOfParticipants);
        boolean isActiveAndFocused();
    }

    public static final String FRAGMENT_TAG = "conversation";

    static final int REQUEST_CHOOSE_ATTACHMENTS = 2;
    private static final int JUMP_SCROLL_THRESHOLD = 15;
    // We animate the message from draft to message list, if we the message doesn't show up in the
    // list within this time limit, then we just do a fade in animation instead
    public static final int MESSAGE_ANIMATION_MAX_WAIT = 500;

    private ComposeMessageView mComposeMessageView;
    //add by junwang
    private RecyclerView mRecyclerView;
    private ConversationMessageAdapter mAdapter;
    private ConversationFastScroller mFastScroller;
    private TwoButtonPopupMenuView mTwoButtonMenu;
    private ThreeButtonPopupMenuView mThreeButtonMenu;
    private int mMenuCount;
    private ArrayList<ButtonMenu> mButtonMenu;
    private ChatbotMenuEntity mChatbotMenuEntity;
    //add by junwang
    private FloatingActionsMenu fam;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private View conversation_fragment_view;

    private View mConversationComposeDivider;
    private ChangeDefaultSmsAppHelper mChangeDefaultSmsAppHelper;

    private String mConversationId;
    // If the fragment receives a draft as part of the invocation this is set
    private MessageData mIncomingDraft;

    // This binding keeps track of our associated ConversationData instance
    // A binding should have the lifetime of the owning component,
    //  don't recreate, unbind and bind if you need new data
    @VisibleForTesting
    final Binding<ConversationData> mBinding = BindingBase.createBinding(this);

    // Saved Instance State Data - only for temporal data which is nice to maintain but not
    // critical for correctness.
    private static final String SAVED_INSTANCE_STATE_LIST_VIEW_STATE_KEY = "conversationViewState";
    private Parcelable mListState;

    private ConversationFragmentHost mHost;

    protected List<Integer> mFilterResults;

    // The minimum scrolling distance between RecyclerView's scroll change event beyong which
    // a fling motion is considered fast, in which case we'll delay load image attachments for
    // perf optimization.
    private int mFastFlingThreshold;

    // ConversationMessageView that is currently selected
    private ConversationMessageView mSelectedMessage;

    // Attachment data for the attachment within the selected message that was long pressed
    private MessagePartData mSelectedAttachment;

    // Normally, as soon as draft message is loaded, we trust the UI state held in
    // ComposeMessageView to be the only source of truth (incl. the conversation self id). However,
    // there can be external events that forces the UI state to change, such as SIM state changes
    // or SIM auto-switching on receiving a message. This receiver is used to receive such
    // local broadcast messages and reflect the change in the UI.
    private final BroadcastReceiver mConversationSelfIdChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String conversationId =
                    intent.getStringExtra(UIIntents.UI_INTENT_EXTRA_CONVERSATION_ID);
            final String selfId =
                    intent.getStringExtra(UIIntents.UI_INTENT_EXTRA_CONVERSATION_SELF_ID);
            Assert.notNull(conversationId);
            Assert.notNull(selfId);
            if (TextUtils.equals(mBinding.getData().getConversationId(), conversationId)) {
                mComposeMessageView.updateConversationSelfIdOnExternalChange(selfId);
            }
        }
    };

    // Flag to prevent writing draft to DB on pause
    private boolean mSuppressWriteDraft;

    // Indicates whether local draft should be cleared due to external draft changes that must
    // be reloaded from db
    private boolean mClearLocalDraft;
    private ImmutableBindingRef<DraftMessageData> mDraftMessageDataModel;

    private boolean isScrolledToBottom() {
        if (mRecyclerView.getChildCount() == 0) {
            return true;
        }
        final View lastView = mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1);
        int lastVisibleItem = ((LinearLayoutManager) mRecyclerView
                .getLayoutManager()).findLastVisibleItemPosition();
        if (lastVisibleItem < 0) {
            // If the recyclerView height is 0, then the last visible item position is -1
            // Try to compute the position of the last item, even though it's not visible
            final long id = mRecyclerView.getChildItemId(lastView);
            final RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForItemId(id);
            if (holder != null) {
                lastVisibleItem = holder.getAdapterPosition();
            }
        }
        final int totalItemCount = mRecyclerView.getAdapter().getItemCount();
        final boolean isAtBottom = (lastVisibleItem + 1 == totalItemCount);
        return isAtBottom && lastView.getBottom() <= mRecyclerView.getHeight();
    }

    private void scrollToBottom(final boolean smoothScroll) {
        if (mAdapter.getItemCount() > 0) {
            scrollToPosition(mAdapter.getItemCount() - 1, smoothScroll);
        }
    }

    private int mScrollToDismissThreshold;
    private final RecyclerView.OnScrollListener mListScrollListener =
        new RecyclerView.OnScrollListener() {
            // Keeps track of cumulative scroll delta during a scroll event, which we may use to
            // hide the media picker & co.
            private int mCumulativeScrollDelta;
            private boolean mScrollToDismissHandled;
            private boolean mWasScrolledToBottom = true;
            private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;

            @Override
            public void onScrollStateChanged(final RecyclerView view, final int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Reset scroll states.
                    mCumulativeScrollDelta = 0;
                    mScrollToDismissHandled = false;
                    //add by junwang start
                    int lastVisibleItem = ((LinearLayoutManager) mRecyclerView
                            .getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    int firstVisibleItem = ((LinearLayoutManager) mRecyclerView
                            .getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    if(lastVisibleItem > 0){
                        int i = 0;
                        if(firstVisibleItem >= 0){
                            i = firstVisibleItem;
                        }
                        for(; i<= lastVisibleItem; i++) {
                            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(i);
                            if (holder != null && holder.itemView != null) {
                                ConversationMessageView cmv = (ConversationMessageView) (holder.itemView);
                                if (cmv != null) {
                                    LogUtil.i("junwang", "SantiVideoView onScrollStateChanged videoview visiable");
                                    SantiVideoView svv = cmv.getVideoView();
                                    if(svv != null){
                                        svv.setMute(true);
                                        svv.start();
                                    }

                                }
                            }
                        }
                    }
                    //add by junwang end
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mRecyclerView.getItemAnimator().endAnimations();
                }
                mScrollState = newState;
            }

            @Override
            public void onScrolled(final RecyclerView view, final int dx, final int dy) {
                closeButtonMenu();
                if (mScrollState == RecyclerView.SCROLL_STATE_DRAGGING &&
                        !mScrollToDismissHandled) {
                    mCumulativeScrollDelta += dy;
                    //add by junwang
                    mHasScrolled = true;
                    // Dismiss the keyboard only when the user scroll up (into the past).
                    if (mCumulativeScrollDelta < -mScrollToDismissThreshold) {
                        mComposeMessageView.hideAllComposeInputs(false /* animate */);
                        mScrollToDismissHandled = true;
                    }
                }
                if (mWasScrolledToBottom != isScrolledToBottom()) {
                    mConversationComposeDivider.animate().alpha(isScrolledToBottom() ? 0 : 1);
                    mWasScrolledToBottom = isScrolledToBottom();
                }
            }
    };

    /*add by junwang start*/
    static String mConversationContactName;
    static String mConversationPhoneNumber;
    static boolean mHasScrolled = false;

    public static boolean hasBubbleViewScrolled(){
        return mHasScrolled;
    }

    public static boolean isContactInWebViewWhiteList(Context context){
        if(mConversationContactName == null){
            return false;
        }
        H5WLDatabaseHelper helper = new H5WLDatabaseHelper(context, DatabaseHelper.DATABASE_NAME, null, 3);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        //String ts = new String(mConversationContactName.replaceAll(" ", ""));
        cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE codenumber LIKE ?", new String[]{ "%" + "+86" + "%" +mConversationContactName.replaceAll(" ", "")});
//        if(mConversationContactName != null && mConversationContactName.length() == 13 && mConversationPhoneNumber.contains("")){
//            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE codenumber=?", new String[]{mConversationContactName.replaceAll(" ", "")});
//        }else if(mConversationPhoneNumber != null && mConversationPhoneNumber.length() == 14 && mConversationPhoneNumber.startsWith("+86")){
//            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE codenumber=?", new String[]{mConversationPhoneNumber.replaceAll(" ", "").substring(3)});
//        } else {
//            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE codenumber=?", new String[]{mConversationContactName});
//        }
        /*Cursor cursor = db.query(H5WLDatabaseHelper.H5WL_TABLENAME, null,
                "codenumber=?", new String[]{mConversationContactName}, null, null, null);*/
        if(cursor != null && cursor.getCount() > 0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public static boolean isPhoneNumberInWebViewWhiteList(Context context){
        if(mConversationPhoneNumber == null){
            return false;
        }
        H5WLDatabaseHelper helper = new H5WLDatabaseHelper(context, DatabaseHelper.DATABASE_NAME, null, 3);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        //String ts = new String(mConversationContactName.replaceAll(" ", ""));
        cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE codenumber = ? ", new String[]{mConversationPhoneNumber.replaceAll(" ", "")});
        if(cursor != null && cursor.getCount() > 0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public static boolean isContactInWebViewWhiteList(Context context, String displayName, String phoneNumber){
        if((displayName == null) && (phoneNumber == null)){
            return false;
        }

        H5WLDatabaseHelper helper = new H5WLDatabaseHelper(context, "h5wldb", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        if(displayName != null && displayName.length() == 13 && displayName.contains("")){
            cursor = db.rawQuery("SELECT * FROM h5_whitelists WHERE codenumber=?", new String[]{displayName.replaceAll(" ", "")});
        }else if(phoneNumber != null && phoneNumber.length() == 14 && phoneNumber.startsWith("+86")){
            cursor = db.rawQuery("SELECT * FROM h5_whitelists WHERE codenumber=?", new String[]{phoneNumber.replaceAll(" ", "").substring(3)});
        } else {
            cursor = db.rawQuery("SELECT * FROM h5_whitelists WHERE codenumber=?", new String[]{displayName});
        }
        /*Cursor cursor = db.query(H5WLDatabaseHelper.H5WL_TABLENAME, null,
                "codenumber=?", new String[]{mConversationContactName}, null, null, null);*/
        if(cursor != null && cursor.getCount() > 0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    /*add by junwang end*/

    private final ActionMode.Callback mMessageActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            if (mSelectedMessage == null) {
                return false;
            }
            final ConversationMessageData data = mSelectedMessage.getData();
            final MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.conversation_fragment_select_menu, menu);
            menu.findItem(R.id.action_download).setVisible(data.getShowDownloadMessage());
            menu.findItem(R.id.action_send).setVisible(data.getShowResendMessage());

            // ShareActionProvider does not work with ActionMode. So we use a normal menu item.
            menu.findItem(R.id.share_message_menu).setVisible(data.getCanForwardMessage());
            menu.findItem(R.id.save_attachment).setVisible(mSelectedAttachment != null);
            menu.findItem(R.id.forward_message_menu).setVisible(data.getCanForwardMessage());

            // TODO: We may want to support copying attachments in the future, but it's
            // unclear which attachment to pick when we make this context menu at the message level
            // instead of the part level
            menu.findItem(R.id.copy_text).setVisible(data.getCanCopyMessageToClipboard());

            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
            final ConversationMessageData data = mSelectedMessage.getData();
            final String messageId = data.getMessageId();
            switch (menuItem.getItemId()) {
                case R.id.save_attachment:
                    if (OsUtil.hasStoragePermission()) {
                        final SaveAttachmentTask saveAttachmentTask = new SaveAttachmentTask(
                                getActivity());
                        for (final MessagePartData part : data.getAttachments()) {
                            saveAttachmentTask.addAttachmentToSave(part.getContentUri(),
                                    part.getContentType());
                        }
                        if (saveAttachmentTask.getAttachmentCount() > 0) {
                            saveAttachmentTask.executeOnThreadPool();
                            mHost.dismissActionMode();
                        }
                    } else {
                        getActivity().requestPermissions(
                                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                    }
                    return true;
                case R.id.action_delete_message:
                    if (mSelectedMessage != null) {
                        deleteMessage(messageId);
                    }
                    return true;
                case R.id.action_download:
                    if (mSelectedMessage != null) {
                        retryDownload(messageId);
                        mHost.dismissActionMode();
                    }
                    return true;
                case R.id.action_send:
                    if (mSelectedMessage != null) {
                        retrySend(messageId);
                        mHost.dismissActionMode();
                    }
                    return true;
                case R.id.copy_text:
                    Assert.isTrue(data.hasText());
                    final ClipboardManager clipboard = (ClipboardManager) getActivity()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(
                            ClipData.newPlainText(null /* label */, data.getText()));
                    mHost.dismissActionMode();
                    return true;
                case R.id.details_menu:
                    MessageDetailsDialog.show(
                            getActivity(), data, mBinding.getData().getParticipants(),
                            mBinding.getData().getSelfParticipantById(data.getSelfParticipantId()));
                    mHost.dismissActionMode();
                    return true;
                case R.id.share_message_menu:
                    shareMessage(data);
                    mHost.dismissActionMode();
                    return true;
                case R.id.forward_message_menu:
                    // TODO: Currently we are forwarding one part at a time, instead of
                    // the entire message. Change this to forwarding the entire message when we
                    // use message-based cursor in conversation.
                    final MessageData message = mBinding.getData().createForwardedMessage(data);
                    UIIntents.get().launchForwardMessageActivity(getActivity(), message);
                    mHost.dismissActionMode();
                    return true;
            }
            return false;
        }

        private void shareMessage(final ConversationMessageData data) {
            // Figure out what to share.
            MessagePartData attachmentToShare = mSelectedAttachment;
            // If the user long-pressed on the background, we will share the text (if any)
            // or the first attachment.
            if (mSelectedAttachment == null
                    && TextUtil.isAllWhitespace(data.getText())) {
                final List<MessagePartData> attachments = data.getAttachments();
                if (attachments.size() > 0) {
                    attachmentToShare = attachments.get(0);
                }
            }

            final Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            if (attachmentToShare == null) {
                shareIntent.putExtra(Intent.EXTRA_TEXT, data.getText());
                shareIntent.setType("text/plain");
            } else {
                shareIntent.putExtra(
                        Intent.EXTRA_STREAM, attachmentToShare.getContentUri());
                shareIntent.setType(attachmentToShare.getContentType());
            }
            final CharSequence title = getResources().getText(R.string.action_share);
            startActivity(Intent.createChooser(shareIntent, title));
        }

        @Override
        public void onDestroyActionMode(final ActionMode actionMode) {
            selectMessage(null);
        }
    };

    public void closeButtonMenu(){
        if(mTwoButtonMenu != null){

        }else if(mThreeButtonMenu != null){
            mThreeButtonMenu.closeMenu();
        }
    }

    /**
     * {@inheritDoc} from Fragment
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SwipeBackHelper.onCreate(this.getActivity());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            SwipeBackHelper.getCurrentPage(this.getActivity())
//                    .setSwipeBackEnable(true)
//                    .setSwipeSensitivity(0.5f)
//                    .setSwipeRelateEnable(true)
//                    .setSwipeSensitivity(1);
//        } else {
//            SwipeBackHelper.getCurrentPage(this.getActivity())
//                    .setSwipeBackEnable(false);
//        }
        mFastFlingThreshold = getResources().getDimensionPixelOffset(
                R.dimen.conversation_fast_fling_threshold);
        mAdapter = new ConversationMessageAdapter(getActivity(), null, this,
                null,
                // Sets the item click listener on the Recycler item views.
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        closeButtonMenu();
                        LogUtil.i("Junwang", "ConversationMessageAdapter onClick");
                        final ConversationMessageView messageView = (ConversationMessageView) v;
                        handleMessageClick(messageView);
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        selectMessage((ConversationMessageView) view);
                        return true;
                    }
                }
        );
    }



    /**
     * setConversationInfo() may be called before or after onCreate(). When a user initiate a
     * conversation from compose, the ConversationActivity creates this fragment and calls
     * setConversationInfo(), so it happens before onCreate(). However, when the activity is
     * restored from saved instance state, the ConversationFragment is created automatically by
     * the fragment, before ConversationActivity has a chance to call setConversationInfo(). Since
     * the ability to start loading data depends on both methods being called, we need to start
     * loading when onActivityCreated() is called, which is guaranteed to happen after both.
     */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Delay showing the message list until the participant list is loaded.
        mRecyclerView.setVisibility(View.INVISIBLE);
        mBinding.ensureBound();
        mBinding.getData().init(getLoaderManager(), mBinding);

        // Build the input manager with all its required dependencies and pass it along to the
        // compose message view.
        final ConversationInputManager inputManager = new ConversationInputManager(
                getActivity(), this, mComposeMessageView, mHost, getFragmentManagerToUse(),
                mBinding, mComposeMessageView.getDraftDataModel(), savedInstanceState);
        mComposeMessageView.setInputManager(inputManager);
        mComposeMessageView.setConversationDataModel(BindingBase.createBindingReference(mBinding));
        mHost.invalidateActionBar();

        mDraftMessageDataModel =
                BindingBase.createBindingReference(mComposeMessageView.getDraftDataModel());
        mDraftMessageDataModel.getData().addListener(this);
    }

    public void onAttachmentChoosen() {
        // Attachment has been choosen in the AttachmentChooserActivity, so clear local draft
        // and reload draft on resume.
        mClearLocalDraft = true;
    }

    private int getScrollToMessagePosition() {
        final Activity activity = getActivity();
        if (activity == null) {
            return -1;
        }

        final Intent intent = activity.getIntent();
        if (intent == null) {
            return -1;
        }

        return intent.getIntExtra(UIIntents.UI_INTENT_EXTRA_MESSAGE_POSITION, -1);
    }

    private void clearScrollToMessagePosition() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final Intent intent = activity.getIntent();
        if (intent == null) {
            return;
        }
        intent.putExtra(UIIntents.UI_INTENT_EXTRA_MESSAGE_POSITION, -1);
    }

    //add by junwang
    public void testRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.apiopen.top/")
                .build();
        WeatherService blogService = retrofit.create(WeatherService.class);
        Call<ResponseBody> call = blogService.getWeather("杭州");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s = response.body().string();
                    LogUtil.i("Junwang", "response body" + s);
                }catch (Exception e){
                    LogUtil.e("Junwang","Get response body error");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.e("Junwang", t.toString());
            }
        });
    }

    public interface WeatherService {
        @GET("weatherApi？{city}")
        Call<ResponseBody> getWeather(@Path("city") String city);
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
//        Toast.makeText(getContext(), "clicked label: " + position, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
        if(position == 0){
            startActivity(new Intent(this.getContext(), BaiduMapTestActivity.class));
        }
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
//        testRetrofit();
//        Toast.makeText(getContext(), "clicked icon: " + position, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
        if(position == 0){
//            try {
//                Thread.sleep(1000);
//            }catch(InterruptedException e){
//                LogUtil.d("Junwang", "Sleep thread exception");
//            }
            startActivity(new Intent(this.getContext(), BaiduMapTestActivity.class));
        }
    }

    private void setFloatingActionMenu(View v){
        rfaLayout = (RapidFloatingActionLayout) v.findViewById(R.id.floating_am);
        rfaButton = (RapidFloatingActionButton) v.findViewById(R.id.rfab);
        rfaButton.setVisibility(View.VISIBLE);
        rfaButton.setAlpha(0.7f);
        /*
        // 可通过代码设置属性
        rfaLayout.setFrameColor(Color.RED);
        rfaLayout.setFrameAlpha(0.4f);

        rfaButton.setNormalColor(0xff37474f);
        rfaButton.setPressedColor(0xff263238);
        rfaButton.getRfabProperties().setShadowDx(ABTextUtil.dip2px(this, 3));
        rfaButton.getRfabProperties().setShadowDy(ABTextUtil.dip2px(this, 3));
        rfaButton.getRfabProperties().setShadowRadius(ABTextUtil.dip2px(this, 5));
        rfaButton.getRfabProperties().setShadowColor(0xffcccccc);
        rfaButton.getRfabProperties().setStandardSize(RFABSize.MINI);
        rfaButton.build();
        */

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("我的位置")
                .setResId(R.drawable.ico_test_d)
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("test Button2")
                .setResId(R.drawable.ico_test_b)
                .setIconNormalColor(0xff056f00)
                .setIconPressedColor(0xff0d5302)
                .setLabelColor(0xff056f00)
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("test Button3")
                .setResId(R.drawable.ico_test_a)
                .setIconNormalColor(0xff283593)
                .setIconPressedColor(0xff1a237e)
                .setLabelColor(0xff283593)
                .setWrapper(3)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(RFABTextUtil.dip2px(getContext(), 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(RFABTextUtil.dip2px(getContext(), 5))
        ;

        rfabHelper = new RapidFloatingActionHelper(
                getContext(),
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();
    }

    private final Handler mHandler = new Handler();

    //add by junwang for getMenus for the selected message
    private final ArrayList<ButtonMenu> getButtonMenu(String json){
        ArrayList<ButtonMenu> button_menu = null;
        try {
            button_menu = new Gson().fromJson(json, new TypeToken<List<ButtonMenu>>(){}.getType());
        }catch (Exception e){
            LogUtil.e("junwang", "parse button menu json exception: "+e.toString());
        }
        return button_menu;
    }

    private final ChatbotMenuEntity getChatbotMenuEntity(String json){
        ChatbotMenuEntity menuEntity = null;
        try{
            menuEntity = new GsonBuilder().setLenient().create().fromJson(json, ChatbotMenuEntity.class);
//            menuEntity = new Gson().fromJson(json, new TypeToken<ChatbotMenuEntity>(){}.getType());
        }catch(Exception e){
            LogUtil.e("Junwang", "parse chatbot menu json exception: "+e.toString());
        }
        return menuEntity;
    }


    /**
     * {@inheritDoc} from Fragment
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.conversation_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        //add by junwang
        conversation_fragment_view = view;
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        manager.setReverseLayout(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator() {
            private final List<ViewHolder> mAddAnimations = new ArrayList<ViewHolder>();
            private PopupTransitionAnimation mPopupTransitionAnimation;

            @Override
            public boolean animateAdd(final ViewHolder holder) {
                final ConversationMessageView view =
                        (ConversationMessageView) holder.itemView;
                final ConversationMessageData data = view.getData();
                endAnimation(holder);
                final long timeSinceSend = System.currentTimeMillis() - data.getReceivedTimeStamp();
                if (data.getReceivedTimeStamp() ==
                                InsertNewMessageAction.getLastSentMessageTimestamp() &&
                        !data.getIsIncoming() &&
                        timeSinceSend < MESSAGE_ANIMATION_MAX_WAIT) {
                    final ConversationMessageBubbleView messageBubble =
                            (ConversationMessageBubbleView) view
                                    .findViewById(R.id.message_content);
                    /*final*/ Rect startRect = UiUtils.getMeasuredBoundsOnScreen(mComposeMessageView);
                    //add by junwang
//                    startRect = UiUtils.getMeasuredBoundsOnScreen(mPopupMenuView);
                    final View composeBubbleView = mComposeMessageView.findViewById(
                            R.id.compose_message_text);
                    final Rect composeBubbleRect =
                            UiUtils.getMeasuredBoundsOnScreen(composeBubbleView);
                    final AttachmentPreview attachmentView =
                            (AttachmentPreview) mComposeMessageView.findViewById(
                                    R.id.attachment_draft_view);
                    final Rect attachmentRect = UiUtils.getMeasuredBoundsOnScreen(attachmentView);
                    if (attachmentView.getVisibility() == View.VISIBLE) {
                        startRect.top = attachmentRect.top;
                    } else {
                        startRect.top = composeBubbleRect.top;
                    }
                    startRect.top -= view.getPaddingTop();
                    startRect.bottom =
                            composeBubbleRect.bottom;
                    startRect.left += view.getPaddingRight();

                    view.setAlpha(0);
                    mPopupTransitionAnimation = new PopupTransitionAnimation(startRect, view);
                    mPopupTransitionAnimation.setOnStartCallback(new Runnable() {
                            @Override
                            public void run() {
                                final int startWidth = composeBubbleRect.width();
                                attachmentView.onMessageAnimationStart();
                                messageBubble.kickOffMorphAnimation(startWidth,
                                        messageBubble.findViewById(R.id.message_text_and_info)
                                        .getMeasuredWidth());
                            }
                        });
                    mPopupTransitionAnimation.setOnStopCallback(new Runnable() {
                            @Override
                            public void run() {
                                view.setAlpha(1);
                            }
                        });
                    mPopupTransitionAnimation.startAfterLayoutComplete();
                    mAddAnimations.add(holder);
                    return true;
                } else {
                    return super.animateAdd(holder);
                }
            }

            @Override
            public void endAnimation(final ViewHolder holder) {
                if (mAddAnimations.remove(holder)) {
                    holder.itemView.clearAnimation();
                }
                super.endAnimation(holder);
            }

            @Override
            public void endAnimations() {
                for (final ViewHolder holder : mAddAnimations) {
                    holder.itemView.clearAnimation();
                }
                mAddAnimations.clear();
                if (mPopupTransitionAnimation != null) {
                    mPopupTransitionAnimation.cancel();
                }
                super.endAnimations();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(SAVED_INSTANCE_STATE_LIST_VIEW_STATE_KEY);
        }

        mConversationComposeDivider = view.findViewById(R.id.conversation_compose_divider);
        mScrollToDismissThreshold = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
        mRecyclerView.addOnScrollListener(mListScrollListener);
        //add by junwang start
//        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
//            @Override
//            public void onChildViewAttachedToWindow(View view) {
//                VideoView vv_video = (VideoView) view.findViewById(R.id.vv_video);
//                ImageView iv = (ImageView) view.findViewById(R.id.iv_play);
//                if (vv_video != null && iv != null && iv.getVisibility() == View.VISIBLE) {
//                    vv_video.setVisibility(View.VISIBLE);
//                    vv_video.start();
//                    iv.setVisibility(View.INVISIBLE);
//                    ImageView iv_img = (ImageView) view.findViewById(R.id.iv_img);
//                    if (iv_img != null) {
//                        iv_img.setVisibility(View.GONE);
//                    }
//                }
//            }
//
//            @Override
//            public void onChildViewDetachedFromWindow(View view) {
//                VideoView vv_video = (VideoView) view.findViewById(R.id.vv_video);
//                ImageView iv = (ImageView) view.findViewById(R.id.iv_play);
//                if (vv_video != null && iv != null && iv.getVisibility() == View.INVISIBLE) {
//                    vv_video.pause();
//                    vv_video.setVisibility(View.INVISIBLE);
//                    iv.setVisibility(View.VISIBLE);
//                    ImageView iv_img = (ImageView) view.findViewById(R.id.iv_img);
//                    if (iv_img != null) {
//                        iv_img.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        });
        //add by junwang end
        mFastScroller = ConversationFastScroller.addTo(mRecyclerView,
                UiUtils.isRtlMode() ? ConversationFastScroller.POSITION_LEFT_SIDE :
                    ConversationFastScroller.POSITION_RIGHT_SIDE);

        mComposeMessageView = (ComposeMessageView)
                view.findViewById(R.id.message_compose_view_container);
        //add by junwang for chatbot menu start
        String chatbotMenu = ((ConversationActivity)getActivity()).getmChatbotMenu();
        if(chatbotMenu != null){
            mChatbotMenuEntity = getChatbotMenuEntity(chatbotMenu);
            int i= 0;
            for(;i<mChatbotMenuEntity.getMenu().getEntries().length;i++){
//                LogUtil.i("Junwang", "menuitem"+i+"="+mChatbotMenuEntity.getMenu().getEntries()[i].getMenu().getDisplayText());
                i++;
            }
            mMenuCount = i;
        }else{
            mMenuCount = 0;
        }
        if(mChatbotMenuEntity != null){
            int menuNumber = mChatbotMenuEntity.getMenu().getEntries().length;
            LogUtil.i("Junwang", "menuNumber="+menuNumber);
            if(menuNumber > 0){
                final View mSwitchButton =
                        (ImageView) mComposeMessageView.findViewById(R.id.switch_button);
                mSwitchButton.setVisibility(View.VISIBLE);
                mSwitchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View clickView) {
                        closeButtonMenu();
                        LogUtil.i("junwang", "mAttachMediaButton onClicked.");
                        ConversationInputManager cim = mComposeMessageView.getInputManager();
                        if(cim != null){
                            cim.hideAllInputs(false);
                        }
                        LogUtil.i("Junwang", "menuNumber="+menuNumber);
                        if (menuNumber == 2) {
                            startMenuSwitchAnimation(mComposeMessageView, mTwoButtonMenu);
                        } else if (menuNumber == 3) {
                            LogUtil.i("Junwang", "start switch from compose to menu");
                            startMenuSwitchAnimation(mComposeMessageView, mThreeButtonMenu);
                        }
                    }
                });

                final View mDivider = (View)mComposeMessageView.findViewById(R.id.button_divider_margin);
                mDivider.setVisibility(View.VISIBLE);

                if(menuNumber == 2) {
                    mTwoButtonMenu = (TwoButtonPopupMenuView) view.findViewById(R.id.two_button_menu_container);
                    final View mTwoBtnSwitchButton = (ImageView) mTwoButtonMenu.findViewById(R.id.switch_to_composemsg);
                    if(mChatbotMenuEntity != null) {
                        mTwoButtonMenu.setMenu(mChatbotMenuEntity);
                    }
                    mTwoBtnSwitchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View clickView) {
                            closeButtonMenu();
                            LogUtil.i("junwang", "mAttachMediaButton onClicked.");
                            startMenuSwitchAnimation(mTwoButtonMenu, mComposeMessageView);
                        }
                    });
                    mComposeMessageView.setVisibility(View.GONE);
                    mTwoButtonMenu.setVisibility(View.VISIBLE);
                }else if(menuNumber == 3) {
                    mThreeButtonMenu = (ThreeButtonPopupMenuView) view.findViewById(R.id.three_button_menu_container);
                    final View mThreeBtnSwitchButton = (ImageView) mThreeButtonMenu.findViewById(R.id.switch_to_composemsg);
                    if(mChatbotMenuEntity != null) {
                        mThreeButtonMenu.setMenu(mChatbotMenuEntity);
                    }
                    mThreeBtnSwitchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View clickView) {
                            closeButtonMenu();
                            LogUtil.i("junwang", "mAttachMediaButton onClicked.");
                            startMenuSwitchAnimation(mThreeButtonMenu, mComposeMessageView);
                        }
                    });
                    mComposeMessageView.setVisibility(View.GONE);
                    mThreeButtonMenu.setVisibility(View.VISIBLE);
                }
            }
        }
        //add by junwang for chatbot menu end

        //add by junwang start

//        String menuJson = ((ConversationActivity)getActivity()).getMenuJson();
//        LogUtil.i("junwang", "fragment add animate. menuJson = "+ menuJson);
////        ArrayList<ButtonMenu> bm;
//        if(menuJson != null){
//            mButtonMenu = getButtonMenu(menuJson);
//            if(mButtonMenu != null){
//                int i = 0;
//                for(ButtonMenu tmp : mButtonMenu){
//                    i++;
//                }
//                mMenuCount = i;
//            }else{
//                mMenuCount = 0;
//            }
//
//        }
//        if(getMenuCount() != 0) {
//            final View mSwitchButton =
//                    (ImageView) mComposeMessageView.findViewById(R.id.switch_button);
//            mSwitchButton.setVisibility(View.VISIBLE);
//            mSwitchButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View clickView) {
//                    // Showing the media picker is treated as starting to compose the message.
////                mInputManager.showHideMediaPicker(true /* show */, true /* animate */);
//                    LogUtil.i("junwang", "mAttachMediaButton onClicked.");
//                    ConversationInputManager cim = mComposeMessageView.getInputManager();
//                    if(cim != null){
//                        cim.hideAllInputs(false);
//                    }
////                    mComposeMessageView.setVisibility(View.GONE);
//                    if (getMenuCount() == 2) {
////                        mTwoButtonMenu.setVisibility(View.VISIBLE);
//                        startMenuSwitchAnimation(mComposeMessageView, mTwoButtonMenu);
//                    } else if (getMenuCount() == 3) {
////                        mThreeButtonMenu.setVisibility(View.VISIBLE);
//                        startMenuSwitchAnimation(mComposeMessageView, mThreeButtonMenu);
//                    }
//                }
//            });
//
//            final View mDivider = (View)mComposeMessageView.findViewById(R.id.button_divider_margin);
//            mDivider.setVisibility(View.VISIBLE);
//        }
//        if(getMenuCount() == 2) {
//            mTwoButtonMenu = (TwoButtonPopupMenuView) view.findViewById(R.id.two_button_menu_container);
//            final View mTwoBtnSwitchButton = (ImageView) mTwoButtonMenu.findViewById(R.id.switch_to_composemsg);
//            if(mButtonMenu != null) {
//                mTwoButtonMenu.setMenu(mButtonMenu);
//            }
//            mTwoBtnSwitchButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View clickView) {
//                    // Showing the media picker is treated as starting to compose the message.
////                mInputManager.showHideMediaPicker(true /* show */, true /* animate */);
//                    LogUtil.i("junwang", "mAttachMediaButton onClicked.");
////                    mComposeMessageView.setVisibility(View.VISIBLE);
////                    mTwoButtonMenu.setVisibility(View.GONE);
//                    startMenuSwitchAnimation(mTwoButtonMenu, mComposeMessageView);
//                }
//            });
//            mComposeMessageView.setVisibility(View.GONE);
//            mTwoButtonMenu.setVisibility(View.VISIBLE);
//        }else if(getMenuCount() == 3) {
//            mThreeButtonMenu = (ThreeButtonPopupMenuView) view.findViewById(R.id.three_button_menu_container);
//            final View mThreeBtnSwitchButton = (ImageView) mThreeButtonMenu.findViewById(R.id.switch_to_composemsg);
//            if(mButtonMenu != null) {
//                mThreeButtonMenu.setMenu(mButtonMenu);
//            }
//            mThreeBtnSwitchButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View clickView) {
//                    // Showing the media picker is treated as starting to compose the message.
////                mInputManager.showHideMediaPicker(true /* show */, true /* animate */);
//                    LogUtil.i("junwang", "mAttachMediaButton onClicked.");
////                    mComposeMessageView.setVisibility(View.VISIBLE);
////                    mThreeButtonMenu.setVisibility(View.GONE);
//                    startMenuSwitchAnimation(mThreeButtonMenu, mComposeMessageView);
//                }
//            });
//            mComposeMessageView.setVisibility(View.GONE);
//            mThreeButtonMenu.setVisibility(View.VISIBLE);
//        }
//        mThreeButtonMenu.setVisibility(View.GONE);
//        mTwoButtonPopupMenuView.setVisibility(View.VISIBLE);
        //add by junwang end
        // Bind the compose message view to the DraftMessageData
        mComposeMessageView.bind(DataModel.get().createDraftMessageData(
                mBinding.getData().getConversationId()), this);

        return view;
    }

    //add by junwang
    public int getMenuCount(){
        return mMenuCount;
    }

    public void startMenuSwitchAnimation(View swithchOut, View swithchIn){
        ObjectAnimator translationY;
        translationY = ObjectAnimator.ofFloat(swithchOut, "translationY", 0f, 200.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(translationY);
        animatorSet.setDuration(100);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator translationY1;
                swithchOut.setVisibility(View.GONE);
                translationY1 = ObjectAnimator.ofFloat(swithchIn, "translationY", 200f, 0f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translationY1);
                animatorSet.setDuration(100);
                animatorSet.start();
                swithchIn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    //add by junwang
    public ComposeMessageView getComposeMessageView(){
        return mComposeMessageView;
    }

    private void scrollToPosition(final int targetPosition, final boolean smoothScroll) {
        if (smoothScroll) {
            final int maxScrollDelta = JUMP_SCROLL_THRESHOLD;

            final LinearLayoutManager layoutManager =
                    (LinearLayoutManager) mRecyclerView.getLayoutManager();
            final int firstVisibleItemPosition =
                    layoutManager.findFirstVisibleItemPosition();
            final int delta = targetPosition - firstVisibleItemPosition;
            final int intermediatePosition;

            if (delta > maxScrollDelta) {
                intermediatePosition = Math.max(0, targetPosition - maxScrollDelta);
            } else if (delta < -maxScrollDelta) {
                final int count = layoutManager.getItemCount();
                intermediatePosition = Math.min(count - 1, targetPosition + maxScrollDelta);
            } else {
                intermediatePosition = -1;
            }
            if (intermediatePosition != -1) {
                mRecyclerView.scrollToPosition(intermediatePosition);
            }
            mRecyclerView.smoothScrollToPosition(targetPosition);
        } else {
            mRecyclerView.scrollToPosition(targetPosition);
        }
    }

    private int getScrollPositionFromBottom() {
        final LinearLayoutManager layoutManager =
                (LinearLayoutManager) mRecyclerView.getLayoutManager();
        final int lastVisibleItem =
                layoutManager.findLastVisibleItemPosition();
        return Math.max(mAdapter.getItemCount() - 1 - lastVisibleItem, 0);
    }

    /**
     * Display a photo using the Photoviewer component.
     */
    @Override
    public void displayPhoto(final Uri photoUri, final Rect imageBounds, final boolean isDraft) {
        displayPhoto(photoUri, imageBounds, isDraft, mConversationId, getActivity());
    }

    public static void displayPhoto(final Uri photoUri, final Rect imageBounds,
            final boolean isDraft, final String conversationId, final Activity activity) {
        final Uri imagesUri =
                isDraft ? MessagingContentProvider.buildDraftImagesUri(conversationId)
                        : MessagingContentProvider.buildConversationImagesUri(conversationId);
        UIIntents.get().launchFullScreenPhotoViewer(
                activity, photoUri, imageBounds, imagesUri);
    }

    private void selectMessage(final ConversationMessageView messageView) {
        selectMessage(messageView, null /* attachment */);
    }

    private void selectMessage(final ConversationMessageView messageView,
            final MessagePartData attachment) {
        mSelectedMessage = messageView;
        if (mSelectedMessage == null) {
            mAdapter.setSelectedMessage(null);
            mHost.dismissActionMode();
            mSelectedAttachment = null;
            return;
        }
        mSelectedAttachment = attachment;
        mAdapter.setSelectedMessage(messageView.getData().getMessageId());
        mHost.startActionMode(mMessageActionModeCallback);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListState != null) {
            outState.putParcelable(SAVED_INSTANCE_STATE_LIST_VIEW_STATE_KEY, mListState);
        }
        mComposeMessageView.saveInputState(outState);
    }

    private void resumeWebView(){
        final ViewHolder vh = getViewHolder();
        if (vh != null) {
            final ConversationMessageView messageView = (ConversationMessageView) vh.itemView;
            if (messageView != null) {
                LinearLayout layout = messageView.mLL_webview_container;
                if(layout != null){
                    int child_count = layout.getChildCount();
                    for(int i=0; i<child_count; i++){
                        View v = layout.getChildAt(i);
                        if(v instanceof WebView){
                            ((WebView)v).resumeTimers();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //add by junwang start
//        resumeWebView();
        //add by junwang end

        if (mIncomingDraft == null) {
            mComposeMessageView.requestDraftMessage(mClearLocalDraft);
        } else {
            mComposeMessageView.setDraftMessage(mIncomingDraft);
            mIncomingDraft = null;
        }
        mClearLocalDraft = false;

        // On resume, check if there's a pending request for resuming message compose. This
        // may happen when the user commits the contact selection for a group conversation and
        // goes from compose back to the conversation fragment.
        if (mHost.shouldResumeComposeMessage()) {
            mComposeMessageView.resumeComposeMessage();
        }

        setConversationFocus();

        // On resume, invalidate all message views to show the updated timestamp.
//        mAdapter.notifyDataSetChanged();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mConversationSelfIdChangeReceiver,
                new IntentFilter(UIIntents.CONVERSATION_SELF_ID_CHANGE_BROADCAST_ACTION));
    }

    void setConversationFocus() {
        if (mHost.isActiveAndFocused()) {
            mBinding.getData().setFocus();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        closeButtonMenu();
        if (mHost.getActionMode() != null) {
            return;
        }

        inflater.inflate(R.menu.conversation_menu, menu);
//
        final ConversationData data = mBinding.getData();

        // Disable the "people & options" item if we haven't loaded participants yet.
//        menu.findItem(R.id.action_people_and_options).setEnabled(data.getParticipantsLoaded());

        // See if we can show add contact action.
        final ParticipantData participant = data.getOtherParticipant();
        final boolean addContactActionVisible = (participant != null
                && TextUtils.isEmpty(participant.getLookupKey())
                && participant.getNormalizedDestination()!= null
                && !participant.getNormalizedDestination().startsWith("sip"));
        {
            menu.findItem(R.id.action_add_contact).setVisible(addContactActionVisible);
            menu.findItem(R.id.action_call).setVisible(addContactActionVisible);
        }

        // See if we should show archive or unarchive.
//        final boolean isArchived = data.getIsArchived();
//        menu.findItem(R.id.action_archive).setVisible(!isArchived);
//        menu.findItem(R.id.action_unarchive).setVisible(isArchived);

        // Conditionally enable the phone call button.
        final boolean supportCallAction = (PhoneUtils.getDefault().isVoiceCapable() &&
                data.getParticipantPhoneNumber() != null);
        menu.findItem(R.id.action_call).setVisible(supportCallAction);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_people_and_options:
//                Assert.isTrue(mBinding.getData().getParticipantsLoaded());
//                UIIntents.get().launchPeopleAndOptionsActivity(getActivity(), mConversationId);
//                return true;

            case R.id.action_call:
                final String phoneNumber = mBinding.getData().getParticipantPhoneNumber();
                Assert.notNull(phoneNumber);
                final View targetView = getActivity().findViewById(R.id.action_call);
                Point centerPoint;
                if (targetView != null) {
                    final int screenLocation[] = new int[2];
                    targetView.getLocationOnScreen(screenLocation);
                    final int centerX = screenLocation[0] + targetView.getWidth() / 2;
                    final int centerY = screenLocation[1] + targetView.getHeight() / 2;
                    centerPoint = new Point(centerX, centerY);
                } else {
                    // In the overflow menu, just use the center of the screen.
                    final Display display = getActivity().getWindowManager().getDefaultDisplay();
                    centerPoint = new Point(display.getWidth() / 2, display.getHeight() / 2);
                }
                UIIntents.get().launchPhoneCallActivity(getActivity(), phoneNumber, centerPoint);
                return true;

//            case R.id.action_archive:
//                mBinding.getData().archiveConversation(mBinding);
//                closeConversation(mConversationId);
//                return true;
//
//            case R.id.action_unarchive:
//                mBinding.getData().unarchiveConversation(mBinding);
//                return true;

            case R.id.action_settings:
                return true;
            case R.id.my_favorite:
                ChatbotFavoriteActivity.start(getActivity());
                return true;
            case R.id.action_add_contact:
                final ParticipantData participant = mBinding.getData().getOtherParticipant();
                Assert.notNull(participant);
                final String destination = participant.getNormalizedDestination();
                final Uri avatarUri = AvatarUriUtil.createAvatarUri(participant);
                (new AddContactsConfirmationDialog(getActivity(), avatarUri, destination)).show();
                return true;

            case R.id.action_delete:
                if (isReadyForAction()) {
                    new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert)
                            .setTitle(getResources().getQuantityString(
                                    R.plurals.delete_conversations_confirmation_dialog_title, 1))
                            .setPositiveButton(R.string.delete_conversation_confirmation_button,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog,
                                                final int button) {
                                            deleteConversation();
                                        }
                            })
                            .setNegativeButton(R.string.delete_conversation_decline_button, null)
                            .show();
                } else {
                    warnOfMissingActionConditions(false /*sending*/,
                            null /*commandToRunAfterActionConditionResolved*/);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc} from ConversationDataListener
     */
    @Override
    public void onConversationMessagesCursorUpdated(final ConversationData data,
            final Cursor cursor, final ConversationMessageData newestMessage,
            final boolean isSync) {
        mBinding.ensureBound(data);
        LogUtil.i("ExpandLayout", "onConversationMessagesCursorUpdated enter");

        // This needs to be determined before swapping cursor, which may change the scroll state.
        final boolean scrolledToBottom = isScrolledToBottom();
        final int positionFromBottom = getScrollPositionFromBottom();

        // If participants not loaded, assume 1:1 since that's the 99% case
        final boolean oneOnOne =
                !data.getParticipantsLoaded() || data.getOtherParticipant() != null;
        mAdapter.setOneOnOne(oneOnOne, false /* invalidate */);

        // Ensure that the action bar is updated with the current data.
        invalidateOptionsMenu();
        final Cursor oldCursor = mAdapter.swapCursor(cursor);

        if (cursor != null && oldCursor == null) {
            if (mListState != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                // RecyclerView restores scroll states without triggering scroll change events, so
                // we need to manually ensure that they are correctly handled.
                mListScrollListener.onScrolled(mRecyclerView, 0, 0);
            }
        }

        if (isSync) {
            // This is a message sync. Syncing messages changes cursor item count, which would
            // implicitly change RV's scroll position. We'd like the RV to keep scrolled to the same
            // relative position from the bottom (because RV is stacked from bottom), so that it
            // stays relatively put as we sync.
            final int position = Math.max(mAdapter.getItemCount() - 1 - positionFromBottom, 0);
            scrollToPosition(position, false /* smoothScroll */);
        } else if (newestMessage != null) {
            // Show a snack bar notification if we are not scrolled to the bottom and the new
            // message is an incoming message.
            if (!scrolledToBottom && newestMessage.getIsIncoming()) {
                // If the conversation activity is started but not resumed (if another dialog
                // activity was in the foregrond), we will show a system notification instead of
                // the snack bar.
                if (mBinding.getData().isFocused()) {
                    UiUtils.showSnackBarWithCustomAction(getActivity(),
                            getView().getRootView(),
                            getString(R.string.in_conversation_notify_new_message_text),
                            SnackBar.Action.createCustomAction(new Runnable() {
                                @Override
                                public void run() {
                                    scrollToBottom(true /* smoothScroll */);
                                    mComposeMessageView.hideAllComposeInputs(false /* animate */);
                                }
                            },
                            getString(R.string.in_conversation_notify_new_message_action)),
                            null /* interactions */,
                            SnackBar.Placement.above(mComposeMessageView));
                }
            } else {
                // We are either already scrolled to the bottom or this is an outgoing message,
                // scroll to the bottom to reveal it.
                // Don't smooth scroll if we were already at the bottom; instead, we scroll
                // immediately so RecyclerView's view animation will take place.
                scrollToBottom(!scrolledToBottom);
            }
        }

        if (cursor != null) {
            mHost.onConversationMessagesUpdated(cursor.getCount());

            // Are we coming from a widget click where we're told to scroll to a particular item?
            final int scrollToPos = getScrollToMessagePosition();
            if (scrollToPos >= 0) {
                if (LogUtil.isLoggable(LogUtil.BUGLE_TAG, LogUtil.VERBOSE)) {
                    LogUtil.v(LogUtil.BUGLE_TAG, "onConversationMessagesCursorUpdated " +
                            " scrollToPos: " + scrollToPos +
                            " cursorCount: " + cursor.getCount());
                }
                scrollToPosition(scrollToPos, true /*smoothScroll*/);
                clearScrollToMessagePosition();
            }
        }

        mHost.invalidateActionBar();
    }

    /**
     * {@inheritDoc} from ConversationDataListener
     */
    @Override
    public void onConversationMetadataUpdated(final ConversationData conversationData) {
        mBinding.ensureBound(conversationData);

        if (mSelectedMessage != null && mSelectedAttachment != null) {
            // We may have just sent a message and the temp attachment we selected is now gone.
            // and it was replaced with some new attachment.  Since we don't know which one it
            // is we shouldn't reselect it (unless there is just one) In the multi-attachment
            // case we would just deselect the message and allow the user to reselect, otherwise we
            // may act on old temp data and may crash.
            final List<MessagePartData> currentAttachments = mSelectedMessage.getData().getAttachments();
            if (currentAttachments.size() == 1) {
                mSelectedAttachment = currentAttachments.get(0);
            } else if (!currentAttachments.contains(mSelectedAttachment)) {
                selectMessage(null);
            }
        }
        // Ensure that the action bar is updated with the current data.
        invalidateOptionsMenu();
        mHost.onConversationMetadataUpdated();
        mAdapter.notifyDataSetChanged();
    }

    public void setConversationInfo(final Context context, final String conversationId,
            final MessageData draftData) {
        // TODO: Eventually I would like the Factory to implement
        // Factory.get().bindConversationData(mBinding, getActivity(), this, conversationId));
        if (!mBinding.isBound()) {
            mConversationId = conversationId;
            mIncomingDraft = draftData;
            mBinding.bind(DataModel.get().createConversationData(context, this, conversationId));
        } else {
            Assert.isTrue(TextUtils.equals(mBinding.getData().getConversationId(), conversationId));
        }
    }

    //add by junwang
    public void destroyWebview(){
        final LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        final int pos = lm.findFirstVisibleItemPosition();
        if (pos == RecyclerView.NO_POSITION) {
            return;
        }
        final ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(pos);
        if (vh == null) {
            // This can happen if the messages update while we're dragging the thumb.
            return;
        }
        final ConversationMessageView messageView = (ConversationMessageView) vh.itemView;
        if ((messageView != null) && messageView.mMessageWebView != null) {
            messageView.mMessageWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            messageView.mMessageWebView.clearHistory();
//            messageView.mMessageWebView.clearCache(true);

            ((ViewGroup) messageView.mMessageWebView.getParent()).removeView(messageView.mMessageWebView);
            messageView.mMessageWebView.destroy();
            messageView.mMessageWebView = null;
        }
    }

    public ViewHolder getViewHolder(){
        final LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        final int pos = lm.findFirstVisibleItemPosition();
        if (pos == RecyclerView.NO_POSITION) {
            return null;
        }
        final ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(pos);
        if (vh == null) {
            // This can happen if the messages update while we're dragging the thumb.
            return null;
        }
        return vh;
    }

    public void destroyWebviewContainer(){
        final ViewHolder vh = getViewHolder();
        if (vh == null) {
            // This can happen if the messages update while we're dragging the thumb.
            return;
        }
        final ConversationMessageView messageView = (ConversationMessageView) vh.itemView;
        if (messageView != null) {
            LinearLayout layout = messageView.mLL_webview_container;
            if(layout != null){
                int child_count = layout.getChildCount();
                for(int i=0; i<child_count; i++){
                    View v = layout.getChildAt(i);
                    if(v instanceof WebView){
                        ((WebView)v).loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                        ((WebView)v).clearHistory();
                        layout.removeView(v);
                        ((WebView) v).destroy();
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //add by junwang
        destroyWebview();
        FloatingWebView.hideWebView();
        if (mComposeMessageView != null && !mSuppressWriteDraft) {
            mComposeMessageView.writeDraftMessage();
        }

        super.onDestroy();
        // Unbind all the views that we bound to data
        if (mComposeMessageView != null) {
            mComposeMessageView.unbind();
        }

        // And unbind this fragment from its data
        mBinding.unbind();
        mConversationId = null;
    }

    void suppressWriteDraft() {
        mSuppressWriteDraft = true;
    }

    private void pauseWebView(){
        final ViewHolder vh = getViewHolder();
        if (vh != null) {
            final ConversationMessageView messageView = (ConversationMessageView) vh.itemView;
            if (messageView != null) {
                LinearLayout layout = messageView.mLL_webview_container;
                if(layout != null){
                    int child_count = layout.getChildCount();
                    for(int i=0; i<child_count; i++){
                        View v = layout.getChildAt(i);
                        if(v instanceof WebView){
                            ((WebView)v).pauseTimers();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //add by junwang start
//        pauseWebView();
        //add by junwang end
//        if (mComposeMessageView != null && !mSuppressWriteDraft) {
//            mComposeMessageView.writeDraftMessage();
//        }
        mSuppressWriteDraft = false;
        mBinding.getData().unsetFocus();
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();

        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mConversationSelfIdChangeReceiver);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mRecyclerView.getItemAnimator().endAnimations();
    }

    // TODO: Remove isBound and replace it with ensureBound after b/15704674.
    public boolean isBound() {
        return mBinding.isBound();
    }

    private FragmentManager getFragmentManagerToUse() {
        return OsUtil.isAtLeastJB_MR1() ? getChildFragmentManager() : getFragmentManager();
    }

    public MediaPicker getMediaPicker() {
        return (MediaPicker) getFragmentManagerToUse().findFragmentByTag(
                MediaPicker.FRAGMENT_TAG);
    }

    @Override
    public void sendMessage(final MessageData message) {
        if (isReadyForAction()) {
            if (ensureKnownRecipients()) {
                // Merge the caption text from attachments into the text body of the messages
                message.consolidateText();

                mBinding.getData().sendMessage(mBinding, message);
                mComposeMessageView.resetMediaPickerState();
            } else {
                LogUtil.w(LogUtil.BUGLE_TAG, "Message can't be sent: conv participants not loaded");
            }
        } else {
            warnOfMissingActionConditions(true /*sending*/,
                    new Runnable() {
                        @Override
                        public void run() {
                            sendMessage(message);
                        }
            });
        }
    }

    public void setHost(final ConversationFragmentHost host) {
        mHost = host;
    }

    public String getConversationName() {
        //add by junwang
        ConversationData data = mBinding.getData();
        mConversationContactName = data.getConversationName();
        mConversationPhoneNumber = data.getParticipantPhoneNumber();

        //add by junwang for RCS chatbot
//        ChatbotUtils.queryChatbotInfo(this, data.getParticipants());
        return data.getConversationName();
    }

    @Override
    public void onComposeEditTextFocused() {
        mHost.onStartComposeMessage();
    }

    @Override
    public void onAttachmentsCleared() {
        // When attachments are removed, reset transient media picker state such as image selection.
        mComposeMessageView.resetMediaPickerState();
    }

    /**
     * Called to check if all conditions are nominal and a "go" for some action, such as deleting
     * a message, that requires this app to be the default app. This is also a precondition
     * required for sending a draft.
     * @return true if all conditions are nominal and we're ready to send a message
     */
    @Override
    public boolean isReadyForAction() {
        return UiUtils.isReadyForAction();
    }

    /**
     * When there's some condition that prevents an operation, such as sending a message,
     * call warnOfMissingActionConditions to put up a snackbar and allow the user to repair
     * that condition.
     * @param sending - true if we're called during a sending operation
     * @param commandToRunAfterActionConditionResolved - a runnable to run after the user responds
     *                  positively to the condition prompt and resolves the condition. If null,
     *                  the user will be shown a toast to tap the send button again.
     */
    @Override
    public void warnOfMissingActionConditions(final boolean sending,
            final Runnable commandToRunAfterActionConditionResolved) {
        if (mChangeDefaultSmsAppHelper == null) {
            mChangeDefaultSmsAppHelper = new ChangeDefaultSmsAppHelper();
        }
        mChangeDefaultSmsAppHelper.warnOfMissingActionConditions(sending,
                commandToRunAfterActionConditionResolved, mComposeMessageView,
                getView().getRootView(),
                getActivity(), this);
    }

    private boolean ensureKnownRecipients() {
        final ConversationData conversationData = mBinding.getData();

        if (!conversationData.getParticipantsLoaded()) {
            // We can't tell yet whether or not we have an unknown recipient
            return false;
        }

        final ConversationParticipantsData participants = conversationData.getParticipants();
        for (final ParticipantData participant : participants) {


            if (participant.isUnknownSender()) {
                UiUtils.showToast(R.string.unknown_sender);
                return false;
            }
        }

        return true;
    }

    public void retryDownload(final String messageId) {
        if (isReadyForAction()) {
            mBinding.getData().downloadMessage(mBinding, messageId);
        } else {
            warnOfMissingActionConditions(false /*sending*/,
                    null /*commandToRunAfterActionConditionResolved*/);
        }
    }

    public void retrySend(final String messageId) {
        if (isReadyForAction()) {
            if (ensureKnownRecipients()) {
                mBinding.getData().resendMessage(mBinding, messageId);
            }
        } else {
            warnOfMissingActionConditions(true /*sending*/,
                    new Runnable() {
                        @Override
                        public void run() {
                            retrySend(messageId);
                        }

                    });
        }
    }

    void deleteMessage(final String messageId) {
        if (isReadyForAction()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), /*AlertDialog.THEME_DEVICE_DEFAULT_DARK*/android.R.style.Theme_Material_Light_Dialog_Alert)
                    .setTitle(R.string.delete_message_confirmation_dialog_title)
                    .setMessage(R.string.delete_message_confirmation_dialog_text)
                    .setPositiveButton(R.string.delete_message_confirmation_button,
                            new OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            mBinding.getData().deleteMessage(mBinding, messageId);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null);
            if (OsUtil.isAtLeastJB_MR1()) {
                builder.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface dialog) {
                        mHost.dismissActionMode();
                    }
                });
            } else {
                builder.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        mHost.dismissActionMode();
                    }
                });
            }
            builder.create().show();
        } else {
            warnOfMissingActionConditions(false /*sending*/,
                    null /*commandToRunAfterActionConditionResolved*/);
            mHost.dismissActionMode();
        }
    }

    public void deleteConversation() {
        if (isReadyForAction()) {
            final Context context = getActivity();
            mBinding.getData().deleteConversation(mBinding);
            closeConversation(mConversationId);
        } else {
            warnOfMissingActionConditions(false /*sending*/,
                    null /*commandToRunAfterActionConditionResolved*/);
        }
    }

    @Override
    public void closeConversation(final String conversationId) {
        if (TextUtils.equals(conversationId, mConversationId)) {
            mHost.onFinishCurrentConversation();
            // TODO: Explicitly transition to ConversationList (or just go back)?
        }
    }

    @Override
    public void onConversationParticipantDataLoaded(final ConversationData data) {
        mBinding.ensureBound(data);
        if (mBinding.getData().getParticipantsLoaded()) {
            final boolean oneOnOne = mBinding.getData().getOtherParticipant() != null;
            mAdapter.setOneOnOne(oneOnOne, true /* invalidate */);

            // refresh the options menu which will enable the "people & options" item.
            invalidateOptionsMenu();

            mHost.invalidateActionBar();

            mRecyclerView.setVisibility(View.VISIBLE);
            mHost.onConversationParticipantDataLoaded
                (mBinding.getData().getNumberOfParticipantsExcludingSelf());
        }
    }

    @Override
    public void onSubscriptionListDataLoaded(final ConversationData data) {
        mBinding.ensureBound(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void promptForSelfPhoneNumber() {
        if (mComposeMessageView != null) {
            // Avoid bug in system which puts soft keyboard over dialog after orientation change
            ImeUtil.hideSoftInput(getActivity(), mComposeMessageView);
        }

        final FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        final EnterSelfPhoneNumberDialog dialog = EnterSelfPhoneNumberDialog
                .newInstance(getConversationSelfSubId());
        dialog.setTargetFragment(this, 0/*requestCode*/);
        dialog.show(ft, null/*tag*/);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (mChangeDefaultSmsAppHelper == null) {
            mChangeDefaultSmsAppHelper = new ChangeDefaultSmsAppHelper();
        }
        mChangeDefaultSmsAppHelper.handleChangeDefaultSmsResult(requestCode, resultCode, null);
    }

    public boolean hasMessages() {
        return mAdapter != null && mAdapter.getItemCount() > 0;
    }

    public boolean onBackPressed() {
        if (mComposeMessageView.onBackPressed()) {
            return true;
        }
        //add by junwang start
//        final LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//        final int pos = lm.findFirstVisibleItemPosition();
//        if (pos == RecyclerView.NO_POSITION) {
//            return false;
//        }
//        final ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(pos);
//        if (vh == null) {
//            // This can happen if the messages update while we're dragging the thumb.
//            return false;
//        }
//        final ConversationMessageView messageView = (ConversationMessageView) vh.itemView;
//        if((messageView != null) && (messageView.mMessageWebView != null) && (messageView.mMessageWebView.getVisibility() == View.VISIBLE)){
//            if(messageView.mMessageWebView.canGoBack()){
//                messageView.mMessageWebView.goBack();
//                return true;
//            }
//        }
        //add by junwang end
        return false;
    }

    public boolean onNavigationUpPressed() {
        return mComposeMessageView.onNavigationUpPressed();
    }

    @Override
    public boolean onAttachmentClick(final ConversationMessageView messageView,
            final MessagePartData attachment, final Rect imageBounds, final boolean longPress) {
        if (longPress) {
            selectMessage(messageView, attachment);
            return true;
        } else if (messageView.getData().getOneClickResendMessage()) {
            handleMessageClick(messageView);
            return true;
        }

        if (attachment.isImage()) {
            displayPhoto(attachment.getContentUri(), imageBounds, false /* isDraft */);
        }

        if (attachment.isVCard()) {
            UIIntents.get().launchVCardDetailActivity(getActivity(), attachment.getContentUri());
        }

        return false;
    }

    private void handleMessageClick(final ConversationMessageView messageView) {
        if (messageView != mSelectedMessage) {
            final ConversationMessageData data = messageView.getData();
            final boolean isReadyToSend = isReadyForAction();
            if (data.getOneClickResendMessage()) {
                // Directly resend the message on tap if it's failed
                retrySend(data.getMessageId());
                selectMessage(null);
            } else if (data.getShowResendMessage() && isReadyToSend) {
                // Select the message to show the resend/download/delete options
                selectMessage(messageView);
            } else if (data.getShowDownloadMessage() && isReadyToSend) {
                // Directly download the message on tap
                retryDownload(data.getMessageId());
            } else {
                // Let the toast from warnOfMissingActionConditions show and skip
                // selecting
                warnOfMissingActionConditions(false /*sending*/,
                        null /*commandToRunAfterActionConditionResolved*/);
                selectMessage(null);
            }
        } else {
            selectMessage(null);
        }
    }

    private static class AttachmentToSave {
        public final Uri uri;
        public final String contentType;
        public Uri persistedUri;

        AttachmentToSave(final Uri uri, final String contentType) {
            this.uri = uri;
            this.contentType = contentType;
        }
    }

    public static class SaveAttachmentTask extends SafeAsyncTask<Void, Void, Void> {
        private final Context mContext;
        private final List<AttachmentToSave> mAttachmentsToSave = new ArrayList<>();

        public SaveAttachmentTask(final Context context, final Uri contentUri,
                final String contentType) {
            mContext = context;
            addAttachmentToSave(contentUri, contentType);
        }

        public SaveAttachmentTask(final Context context) {
            mContext = context;
        }

        public void addAttachmentToSave(final Uri contentUri, final String contentType) {
            mAttachmentsToSave.add(new AttachmentToSave(contentUri, contentType));
        }

        public int getAttachmentCount() {
            return mAttachmentsToSave.size();
        }

        @Override
        protected Void doInBackgroundTimed(final Void... arg) {
            final File appDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES),
                    mContext.getResources().getString(R.string.app_name));
            final File downloadDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            for (final AttachmentToSave attachment : mAttachmentsToSave) {
                final boolean isImageOrVideo = ContentType.isImageType(attachment.contentType)
                        || ContentType.isVideoType(attachment.contentType);
                attachment.persistedUri = UriUtil.persistContent(attachment.uri,
                        isImageOrVideo ? appDir : downloadDir, attachment.contentType);
           }
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            int failCount = 0;
            int imageCount = 0;
            int videoCount = 0;
            int otherCount = 0;
            for (final AttachmentToSave attachment : mAttachmentsToSave) {
                if (attachment.persistedUri == null) {
                   failCount++;
                   continue;
                }

                // Inform MediaScanner about the new file
                final Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanFileIntent.setData(attachment.persistedUri);
                mContext.sendBroadcast(scanFileIntent);

                if (ContentType.isImageType(attachment.contentType)) {
                    imageCount++;
                } else if (ContentType.isVideoType(attachment.contentType)) {
                    videoCount++;
                } else {
                    otherCount++;
                    // Inform DownloadManager of the file so it will show in the "downloads" app
                    final DownloadManager downloadManager =
                            (DownloadManager) mContext.getSystemService(
                                    Context.DOWNLOAD_SERVICE);
                    final String filePath = attachment.persistedUri.getPath();
                    final File file = new File(filePath);

                    if (file.exists()) {
                        downloadManager.addCompletedDownload(
                                file.getName() /* title */,
                                mContext.getString(
                                        R.string.attachment_file_description) /* description */,
                                        true /* isMediaScannerScannable */,
                                        attachment.contentType,
                                        file.getAbsolutePath(),
                                        file.length(),
                                        false /* showNotification */);
                    }
                }
            }

            String message;
            if (failCount > 0) {
                message = mContext.getResources().getQuantityString(
                        R.plurals.attachment_save_error, failCount, failCount);
            } else {
                int messageId = R.plurals.attachments_saved;
                if (otherCount > 0) {
                    if (imageCount + videoCount == 0) {
                        messageId = R.plurals.attachments_saved_to_downloads;
                    }
                } else {
                    if (videoCount == 0) {
                        messageId = R.plurals.photos_saved_to_album;
                    } else if (imageCount == 0) {
                        messageId = R.plurals.videos_saved_to_album;
                    } else {
                        messageId = R.plurals.attachments_saved_to_album;
                    }
                }
                final String appName = mContext.getResources().getString(R.string.app_name);
                final int count = imageCount + videoCount + otherCount;
                message = mContext.getResources().getQuantityString(
                        messageId, count, count, appName);
            }
            UiUtils.showToastAtBottom(message);
        }
    }

    private void invalidateOptionsMenu() {
        final Activity activity = getActivity();
        // TODO: Add the supportInvalidateOptionsMenu call to the host activity.
        if (activity == null || !(activity instanceof BugleActionBarActivity)) {
            return;
        }
        ((BugleActionBarActivity) activity).supportInvalidateOptionsMenu();
    }

    @Override
    public void setOptionsMenuVisibility(final boolean visible) {
        setHasOptionsMenu(visible);
    }

    @Override
    public int getConversationSelfSubId() {
        final String selfParticipantId = mComposeMessageView.getConversationSelfId();
        final ParticipantData self = mBinding.getData().getSelfParticipantById(selfParticipantId);
        // If the self id or the self participant data hasn't been loaded yet, fallback to
        // the default setting.
        return self == null ? ParticipantData.DEFAULT_SELF_SUB_ID : self.getSubId();
    }

    @Override
    public void invalidateActionBar() {
        mHost.invalidateActionBar();
    }

    @Override
    public void dismissActionMode() {
        mHost.dismissActionMode();
    }

    @Override
    public void selectSim(final SubscriptionListEntry subscriptionData) {
        mComposeMessageView.selectSim(subscriptionData);
        mHost.onStartComposeMessage();
    }

    @Override
    public void onStartComposeMessage() {
        mHost.onStartComposeMessage();
    }

    @Override
    public SubscriptionListEntry getSubscriptionEntryForSelfParticipant(
            final String selfParticipantId, final boolean excludeDefault) {
        // TODO: ConversationMessageView is the only one using this. We should probably
        // inject this into the view during binding in the ConversationMessageAdapter.
        return mBinding.getData().getSubscriptionEntryForSelfParticipant(selfParticipantId,
                excludeDefault);
    }

    @Override
    public SimSelectorView getSimSelectorView() {
        return (SimSelectorView) getView().findViewById(R.id.sim_selector);
    }

    @Override
    public MediaPicker createMediaPicker() {
        return new MediaPicker(getActivity());
    }

    @Override
    public void notifyOfAttachmentLoadFailed() {
        UiUtils.showToastAtBottom(R.string.attachment_load_failed_dialog_message);
    }

    @Override
    public void warnOfExceedingMessageLimit(final boolean sending, final boolean tooManyVideos) {
        warnOfExceedingMessageLimit(sending, mComposeMessageView, mConversationId,
                getActivity(), tooManyVideos);
    }

    public static void warnOfExceedingMessageLimit(final boolean sending,
            final ComposeMessageView composeMessageView, final String conversationId,
            final Activity activity, final boolean tooManyVideos) {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity)
                    .setTitle(R.string.mms_attachment_limit_reached);

        if (sending) {
            if (tooManyVideos) {
                builder.setMessage(R.string.video_attachment_limit_exceeded_when_sending);
            } else {
                builder.setMessage(R.string.attachment_limit_reached_dialog_message_when_sending)
                        .setNegativeButton(R.string.attachment_limit_reached_send_anyway,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog,
                                            final int which) {
                                        composeMessageView.sendMessageIgnoreMessageSizeLimit();
                                    }
                                });
            }
            builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    showAttachmentChooser(conversationId, activity);
                }});
        } else {
            builder.setMessage(R.string.attachment_limit_reached_dialog_message_when_composing)
                    .setPositiveButton(android.R.string.ok, null);
        }
        builder.show();
    }

    @Override
    public void showAttachmentChooser() {
        showAttachmentChooser(mConversationId, getActivity());
    }

    public static void showAttachmentChooser(final String conversationId,
            final Activity activity) {
        UIIntents.get().launchAttachmentChooserActivity(activity,
                conversationId, REQUEST_CHOOSE_ATTACHMENTS);
    }

    protected boolean useThemestatusBarColor = true;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getActivity().getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_background_color));
            } else {
                getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getActivity().getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getActivity().getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void updateActionAndStatusBarColor(final ActionBar actionBar) {
        final int actionBarColor = ConversationDrawables.get().getActionbarColor();
//        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

        UiUtils.setStatusBarColor(getActivity(), actionBarColor);
//        setStatusBar();
    }

    public void updateActionBar(final ActionBar actionBar) {
        LogUtil.i("Junwang", "Conversation Fragment update ActionBar");
        if (mComposeMessageView == null || !mComposeMessageView.updateActionBar(actionBar)) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
            updateActionAndStatusBarColor(actionBar);
            // We update this regardless of whether or not the action bar is showing so that we
            // don't get a race when it reappears.
//            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            View customView = ((LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.actionbar_message_view, null);

//            actionBar.setDisplayShowCustomEnabled(true);
//            actionBar.setDisplayShowTitleEnabled(false);
//            ActionBar.LayoutParams layoutParams =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
//                    ActionBar.LayoutParams.MATCH_PARENT);
            actionBar.setHomeAsUpIndicator(R.drawable.back_normal);
            actionBar.setCustomView(customView/*, layoutParams*/);
//            Toolbar parent =(Toolbar) customView.getParent();
//            parent.getOverflowIcon().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
//            parent.setOverflowIcon(getResources().getDrawable(R.drawable.icon_more));
//            parent.setContentInsetsAbsolute(0,0);
            final TextView conversationNameView =
                    (TextView) customView.findViewById(R.id.actionbar_title);
            final ImageView back_icon = (ImageView)customView.findViewById(R.id.actionbar_arrow);
            back_icon.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

//            actionBar.setDisplayHomeAsUpEnabled(true);
//            // Reset the back arrow to its default
//            actionBar.setHomeAsUpIndicator(/*0*/R.drawable.ic_arrow_back_dark);
//            View customView = actionBar.getCustomView();
//            if (customView == null || customView.getId() != R.id.conversation_title_container) {
//                final LayoutInflater inflator = (LayoutInflater)
//                        getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                customView = inflator.inflate(R.layout.action_bar_conversation_name, null);
//                customView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(final View v) {
//                        onBackPressed();
//                    }
//                });
//                actionBar.setCustomView(customView);
//            }
//
//            final TextView conversationNameView =
//                    (TextView) customView.findViewById(R.id.conversation_title);
            String conversationName = getConversationName();
            //add by junwang for chatbot name
            if(mBinding.getData().getChatbotName() != null){
                conversationName = mBinding.getData().getChatbotName();
            }


            if (!TextUtils.isEmpty(conversationName)) {
                // RTL : To format conversation title if it happens to be phone numbers.
                final BidiFormatter bidiFormatter = BidiFormatter.getInstance();
                final String formattedName = bidiFormatter.unicodeWrap(
                        UiUtils.commaEllipsize(
                                conversationName,
                                conversationNameView.getPaint(),
                                conversationNameView.getWidth(),
                                getString(R.string.plus_one),
                                getString(R.string.plus_n)).toString(),
                        TextDirectionHeuristicsCompat.LTR);
                conversationNameView.setText(formattedName);
                // In case phone numbers are mixed in the conversation name, we need to vocalize it.
                final String vocalizedConversationName =
                        AccessibilityUtil.getVocalizedPhoneNumber(getResources(), conversationName);
                conversationNameView.setContentDescription(vocalizedConversationName);
                getActivity().setTitle(conversationName);
            } else {
                final String appName = getString(R.string.app_name);
                conversationNameView.setText(appName);
                getActivity().setTitle(appName);
            }

            // When conversation is showing and media picker is not showing, then hide the action
            // bar only when we are in landscape mode, with IME open.
            if (mHost.isImeOpen() && UiUtils.isLandscapeMode()) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    @Override
    public boolean shouldShowSubjectEditor() {
        return true;
    }

    @Override
    public boolean shouldHideAttachmentsWhenSimSelectorShown() {
        return false;
    }

    @Override
    public void showHideSimSelector(final boolean show) {
        // no-op for now
    }

    @Override
    public int getSimSelectorItemLayoutId() {
        return R.layout.sim_selector_item_view;
    }

    @Override
    public Uri getSelfSendButtonIconUri() {
        return null;    // use default button icon uri
    }

    @Override
    public int overrideCounterColor() {
        return -1;      // don't override the color
    }

    @Override
    public void onAttachmentsChanged(final boolean haveAttachments) {
        // no-op for now
    }

    @Override
    public void onDraftChanged(final DraftMessageData data, final int changeFlags) {
        mDraftMessageDataModel.ensureBound(data);
        // We're specifically only interested in ATTACHMENTS_CHANGED from the widget. Ignore
        // other changes. When the widget changes an attachment, we need to reload the draft.
        if (changeFlags ==
                (DraftMessageData.WIDGET_CHANGED | DraftMessageData.ATTACHMENTS_CHANGED)) {
            mClearLocalDraft = true;        // force a reload of the draft in onResume
        }
    }

    @Override
    public void onDraftAttachmentLimitReached(final DraftMessageData data) {
        // no-op for now
    }

    @Override
    public void onDraftAttachmentLoadFailed() {
        // no-op for now
    }

    @Override
    public int getAttachmentsClearedFlags() {
        return DraftMessageData.ATTACHMENTS_CHANGED;
    }

    @Override
    public void onClick(View v) {
        closeButtonMenu();
    }
}
