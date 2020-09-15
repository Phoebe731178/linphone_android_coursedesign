package com.linphone.chat.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.linphone.R;
import com.linphone.addressbook.AddressBookModel;
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.addressbook.AddressBookPresenter;
import com.linphone.chat.single.SingleChatRoomListener;
import com.linphone.login.PhoneLoginHandler;
import com.linphone.util.LinphoneManager;
import com.linphone.vo.Contact;
import org.linphone.core.Address;
import org.linphone.core.ChatRoom;
import org.linphone.core.ChatRoomListener;
import org.linphone.core.Core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatRecordActivity extends Activity
{
    private TextView usernameTextView;
    private static final List<ChatRoom> records = new ArrayList<>();
    private ChatRecordAdapter adapter;
    private RecyclerView chatRecordRecyclerView;
    private Core core;
    private ChatRoomListener chatRoomListener;
    private static Handler handler;
    private Address localAddress;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private static final ReadWriteLock threadLock = new ReentrantReadWriteLock(true);
    private static final Map<String, Integer> roomIndices = new ConcurrentHashMap<>();
    private boolean isRunning = false;
    private boolean isThreadRunning = false;
    private AddressBookModel addressBookModel;
    private ImageView refreshImageView;
    private static class RefreshThread extends Thread
    {
        private WeakReference<Activity> mActivityReference;
        public RefreshThread(Activity activity)
        {
            mActivityReference = new WeakReference<>(activity);
        }
        @Override
        public void run() {
            ChatRecordActivity mActivity = (ChatRecordActivity) mActivityReference.get();
            mActivity.getChatRecords();
        }
    }


    private static class ChatRecordHandler extends Handler
    {
        private WeakReference<Activity> mActivityReference;
        public ChatRecordHandler(Activity activity)
        {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg)
        {
            super.handleMessage(msg);
            ChatRecordActivity mActivity = (ChatRecordActivity) mActivityReference.get();
            String state = msg.getData().getString("state", "");
            if (state.equals("PrepareRecords"))
            {
                lock.readLock().lock();
                mActivity.adapter.notifyItemInserted(msg.getData().getInt("position"));
                lock.readLock().unlock();
                return;
            }
            if (state.equals("MessageRead"))
            {
                lock.readLock().lock();
                mActivity.adapter.notifyItemChanged(msg.getData().getInt("position"));
                lock.readLock().unlock();
                return;
            }
            if (state.equals("ClearRecords"))
            {
                lock.writeLock().lock();
                mActivity.adapter.notifyDataSetChanged();
                lock.writeLock().unlock();
                return;
            }
            switch (msg.what)
            {
                case SingleChatRoomListener.CHAT_MESSAGE_RECEIVED:
                case SingleChatRoomListener.CHAT_MESSAGE_SENT:
                    int index = msg.getData().getInt("position");
                    mActivity.adapter.notifyItemChanged(index);
                    break;
                case AddressBookPresenter.ON_CHANGE:
                    mActivity.getChatRecords();
                    mActivity.adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    public static List<ChatRoom> getRecords()
    {
        return records;
    }

    public static ReadWriteLock getLock()
    {
        return lock;
    }

    public static int getRecordIndex(ChatRoom chatRoom)
    {
        Integer index = roomIndices.get(chatRoom.getPeerAddress().asString().substring(3));
        if (index == null)
        {
            lock.readLock().lock();
            for (int i = 0; i < records.size(); i++)
            {
                if (records.get(i).getPeerAddress().getUsername().equals(chatRoom.getPeerAddress().getUsername()))
                {
                    System.out.println(chatRoom.getPeerAddress().asString());
                    roomIndices.put(chatRoom.getPeerAddress().getUsername().substring(3), i);
                    return i;
                }
            }
            return -1;
        }
        else return index;
    }

    public static Handler getHandler()
    {
        return handler;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_record);
        isRunning = true;
        usernameTextView = findViewById(R.id.user_name);
        adapter = new ChatRecordAdapter(records, this);
        chatRecordRecyclerView = findViewById(R.id.chat_record_list);
        chatRecordRecyclerView.setLayoutManager(new CustomLinearLayoutManager(this));
        chatRecordRecyclerView.setAdapter(adapter);
        core = LinphoneManager.getCore();
        chatRoomListener = new SingleChatRoomListener();
        addressBookModel = new AddressBookModelImpl(this);
        refreshImageView = findViewById(R.id.refresh_record);
        refreshImageView.setImageResource(R.drawable.refresh);
        handler = new ChatRecordHandler(this);
        localAddress = core.getProxyConfigList()[0].getIdentityAddress();
        String displayName = localAddress.getDisplayName();
        if (displayName == null || "".equals(displayName))
        {
            usernameTextView.setText(localAddress.getUsername().substring(3));
        }
        else
        {
            usernameTextView.setText(localAddress.getDisplayName());
        }
        refreshImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("clicked");
                new RefreshThread(ChatRecordActivity.this).start();
            }
        });
    }

    /**
     * 加载聊天记录
     */
    private void getChatRecords()
    {
        threadLock.readLock().lock();
        if (isThreadRunning)
        {
            threadLock.readLock().unlock();
            return;
        }
        threadLock.readLock().unlock();
        threadLock.writeLock().lock();
        isThreadRunning = true;
        threadLock.writeLock().unlock();
        lock.writeLock().lock();
        records.clear();
        Message message1 = new Message();
        Bundle bundle1 = new Bundle();
        bundle1.putString("status", "ClearRecords");
        message1.setData(bundle1);
        handler.sendMessage(message1);
        lock.writeLock().unlock();
        roomIndices.clear();
        Map<String, Contact> contactMap = addressBookModel.getAddressBookInfo();
        System.out.println("size of contact: " + contactMap.size());
        for (Map.Entry<String, Contact> entry: contactMap.entrySet())
        {
            if (isRunning)
            {
                Contact contact = entry.getValue();
                Address remoteAddress = core.createAddress(null);
                remoteAddress.setDomain(PhoneLoginHandler.DOMAIN);
                remoteAddress.setUsername("+" + PhoneLoginHandler.COUNTRY_CODE + contact.getPhones().get(0));
                ChatRoom chatRoom = core.getChatRoom(remoteAddress, localAddress);
                Log.i("threadRemote", remoteAddress.getUsername());
                if (chatRoom != null)
                {
                    if (chatRoom.getHistorySize() > 0)
                    {
                        Log.i("record", "remoteAddress: " + remoteAddress.asString());
                        chatRoom.addListener(chatRoomListener);
                        lock.writeLock().lock();
                        records.add(chatRoom);
                        String subject = chatRoom.getPeerAddress().getUsername().substring(3);
                        int index = 0;
                        if (!(subject == null || "".equals(subject)))
                        {
                            index = records.size() - 1;
                            roomIndices.put(subject, index);
                        }
                        lock.writeLock().unlock();
                        Bundle bundle = new Bundle();
                        bundle.putString("state", "PrepareRecords");
                        bundle.putInt("position", index);
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            }
        }
        threadLock.writeLock().lock();
        isThreadRunning = false;
        threadLock.writeLock().unlock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        Log.i("resume", "running");
        new RefreshThread(ChatRecordActivity.this).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        Log.i("pause", "paused");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        lock.writeLock().lock();
        for (ChatRoom chatRoom: records)
        {
            chatRoom.removeListener(chatRoomListener);
        }
        records.clear();
        lock.writeLock().unlock();
        handler.removeCallbacksAndMessages(null);
        roomIndices.clear();
        isRunning = false;
    }
}
