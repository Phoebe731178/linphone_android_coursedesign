package com.linphone.chat.single;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.linphone.chat.single.view.ChatActivity;
import org.linphone.core.*;

import java.util.List;

public class SingleChatMessageListener implements ChatMessageListener
{
    public static final int PARTICIPANT_IMDN_STATE_CHANGED = 0;
    public static final int FILE_TRANSFER_RECV = 1;
    public static final int MSG_STATE_CHANGED = 2;
    public static final int FILE_TRANSFER_SEND = 3;
    public static final int EPHEMERAL_MESSAGE_TIMER_STARTED = 4;
    public static final int FILE_TRANSFER_PROGRESS_INDICATION = 5;
    public static final int FILE_TRANSFER_SEND_CHUNK = 6;
    public static final int EPHEMERAL_MESSAGE_DELETED = 7;

    @Override
    public void onParticipantImdnStateChanged(ChatMessage message, ParticipantImdnState state)
    {
    }

    @Override
    public void onFileTransferRecv(ChatMessage message, Content content, Buffer buffer) {

    }

    @Override
    public void onMsgStateChanged(ChatMessage message, ChatMessage.State state)
    {
        int position = ChatActivity.getMessageIndex(message.getMessageId());
        if (position == -1)
        {
            System.out.println("Message position cache missed");
            position = getMessageIndex(message, ChatActivity.getMessages());
            ChatActivity.addMessageIndex(message.getMessageId(), position);
        }
        System.out.println(message.getMessageId());
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        Log.i("MsgStateChanged", "position: " + position + ", state: " + state.name());
        Message handlerMessage = new Message();
        handlerMessage.setData(bundle);
        handlerMessage.what = MSG_STATE_CHANGED;
        ChatActivity.getMessageHandler().sendMessage(handlerMessage);
    }

    @Override
    public Buffer onFileTransferSend(ChatMessage message, Content content, int offset, int size) {
        return null;
    }

    @Override
    public void onEphemeralMessageTimerStarted(ChatMessage message) {

    }

    @Override
    public void onFileTransferProgressIndication(ChatMessage message, Content content, int offset, int total) {

    }

    @Override
    public void onFileTransferSendChunk(ChatMessage message, Content content, int offset, int size, Buffer buffer) {

    }

    @Override
    public void onEphemeralMessageDeleted(ChatMessage message) {

    }

    private int getMessageIndex(ChatMessage message, List<ChatMessage> messages)
    {
        for (int i = 0; i < messages.size(); i++)
        {
            if (messages.get(i).getMessageId().equals(message.getMessageId()))
                return i;
        }
        return -1;
    }
}
