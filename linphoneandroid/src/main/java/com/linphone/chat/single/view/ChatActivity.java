package com.linphone.chat.single.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.linphone.R;
import com.linphone.chat.single.SingleChatMessageListener;
import com.linphone.chat.single.SingleChatRoomListener;
import com.linphone.login.PhoneLoginHandler;
import com.linphone.util.LinphoneManager;
import org.linphone.core.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatActivity extends Activity
{
    private RecyclerView chatRecyclerView;
    private EditText chatEditText;
    private Button sendButton;
    private static Handler messageHandler;
    private static Handler roomHandler;
    private TextView remoteUsername;
    private ImageButton callButton;
    private static List<ChatMessage> messages = new ArrayList<>();
    private Address localAddress;
    private Address remoteAddress;
    private Core core;
    private ChatRoom chatRoom;
    private ChatRoomListener roomListener;
    private ChatAdapter chatAdapter;
    private static Map<String, Integer> messageIndices = new ConcurrentHashMap<>();

    private static class MessageHandler extends Handler
    {
        private WeakReference<Activity> mActivityReference;
        public MessageHandler(Activity activity)
        {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg)
        {
            ChatActivity mActivity = (ChatActivity) mActivityReference.get();
            if (msg.what == SingleChatMessageListener.MSG_STATE_CHANGED)
            {
                System.out.println("position: "+ msg.getData().getInt("position"));
                mActivity.chatAdapter.notifyItemChanged(msg.getData().getInt("position"));
            }
        }
    }

    private static class RoomHandler extends Handler
    {
        private WeakReference<Activity> mActivityReference;
        public RoomHandler(Activity activity)
        {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg)
        {
            ChatActivity mActivity = (ChatActivity) mActivityReference.get();
            if (msg.what == SingleChatRoomListener.STATE_CHANGED)
            {
                switch (msg.getData().getString("state"))
                {
                    case "CreationPending":
                        Toast.makeText(mActivity, "创建聊天室中", Toast.LENGTH_SHORT).show();break;
                    case "Created":
                        Toast.makeText(mActivity, "创建成功", Toast.LENGTH_SHORT).show(); break;
                    case "CreationFailed":
                        Toast.makeText(mActivity, "创建聊天室失败", Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                        break;
                }
            }
            if (msg.what == SingleChatRoomListener.CHAT_MESSAGE_SENT)
            {
                mActivity.chatAdapter.notifyItemChanged(ChatActivity.messages.size() - 1);
            }
            if (msg.what == SingleChatRoomListener.CHAT_MESSAGE_RECEIVED)
            {
                mActivity.chatAdapter.notifyItemChanged(ChatActivity.messages.size() - 1);
            }
        }
    }

    public static void addMessageIndex(String messageId, Integer index)
    {
        if (index >= 0)
        {
            messageIndices.put(messageId, index);
        }
    }

    public static Integer getMessageIndex(String messageId)
    {
        Integer index = messageIndices.get(messageId);
        if (index == null)
            return -1;
        else return index;
    }

    public static Handler getMessageHandler()
    {
        return messageHandler;
    }

    public static Handler getRoomHandler()
    {
        return roomHandler;
    }

    public static List<ChatMessage> getMessages()
    {
        return messages;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        roomListener = new SingleChatRoomListener();
        chatRecyclerView = findViewById(R.id.chat_recyclerview);
        chatEditText = findViewById(R.id.chat_editText);
        sendButton = findViewById(R.id.send_button);
        sendButton.setEnabled(false);
        core = LinphoneManager.getCore();
        remoteUsername = findViewById(R.id.remote_name);
        callButton = findViewById(R.id.call);
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        messageHandler = new MessageHandler(this);
        roomHandler = new RoomHandler(this);
        initChatRoom();
        chatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                sendButton.setEnabled(chatEditText.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendMessage();
                chatEditText.setText("");
            }
        });
    }

    /**
     * 初始化聊天室
     */
    private void initChatRoom()
    {
        String toUsername = "+8618859808506";
        remoteUsername.setText(toUsername);
        localAddress = core.getProxyConfigList()[0].getIdentityAddress();
        remoteAddress = core.createAddress(null);
        remoteAddress.setDomain(PhoneLoginHandler.DOMAIN);
        remoteAddress.setUsername(toUsername);
        chatRoom = core.getChatRoom(remoteAddress, localAddress);
        chatRoom.addListener(roomListener);
    }

    /**
     * 发送聊天消息（ChatMessage）
     */
    private void sendMessage()
    {
        ChatMessage message = chatRoom.createEmptyMessage();
        message.addTextContent(chatEditText.getText().toString());
        if (message.getContents().length > 0)
        {
            message.setToBeStored(true);
            message.send();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        messages.clear();
        messageIndices.clear();
        messageHandler.removeCallbacksAndMessages(null);
        roomHandler.removeCallbacksAndMessages(null);
    }
}