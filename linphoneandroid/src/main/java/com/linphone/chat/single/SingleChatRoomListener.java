package com.linphone.chat.single;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.linphone.chat.single.view.ChatActivity;
import com.linphone.chat.view.ChatRecordActivity;
import org.linphone.core.*;

import java.util.List;

public class SingleChatRoomListener implements ChatRoomListener
{
    public static final int STATE_CHANGED = 0;
    public static final int PARTICIPANT_REGISTRATION_UNSUBSCRIPTION_REQUESTED = 1;
    public static final int PARTICIPANT_ADMIN_STATUS_CHANGED = 2;
    public static final int PARTICIPANT_REMOVED = 3;
    public static final int EPHEMERAL_MESSAGE_TIMER_STARTED = 4;
    public static final int UNDECRYPTABLE_MESSAGE_RECEIVED = 5;
    public static final int PARTICIPANT_ADDED = 6;
    public static final int EPHEMERAL_EVENT = 7;
    public static final int CHAT_MESSAGE_RECEIVED = 8;
    public static final int CONFERENCE_ADDRESS_GENERATION = 9;
    public static final int PARTICIPANT_DEVICE_ADDED = 10;
    public static final int SECURITY_EVENT = 11;
    public static final int CONFERENCE_LEFT = 12;
    public static final int SUBJECT_CHANGED = 13;
    public static final int CHAT_MESSAGE_SENT = 14;
    public static final int CONFERENCE_JOINED = 15;
    public static final int MESSAGE_RECEIVED = 16;
    public static final int EPHEMERAL_MESSAGE_DELETED = 17;
    public static final int PARTICIPANT_REGISTRATION_SUBSCRIPTION_REQUESTED = 18;
    public static final int PARTICIPANT_DEVICE_REMOVED = 19;
    public static final int IS_COMPOSING_RECEIVED = 20;
    public static final int CHAT_MESSAGE_SHOULD_BE_STORED = 21;
    private static final ChatMessageListener messageListenr = new SingleChatMessageListener();

    @Override
    public void onStateChanged(ChatRoom chatRoom, ChatRoom.State newState)
    {
        Log.i("ChatRoomStateChanged", "state [" + newState.name() + "]");
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("state", newState.name());
        message.setData(bundle);
        message.what = STATE_CHANGED;
        if (ChatActivity.getRoomHandler() != null)
        {
            ChatActivity.getRoomHandler().sendMessage(message);
        }
    }

    @Override
    public void onParticipantRegistrationUnsubscriptionRequested(ChatRoom chatRoom, Address participantAddress) {

    }

    @Override
    public void onParticipantAdminStatusChanged(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onParticipantRemoved(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onEphemeralMessageTimerStarted(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onUndecryptableMessageReceived(ChatRoom chatRoom, ChatMessage message) {

    }

    @Override
    public void onParticipantAdded(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onEphemeralEvent(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onChatMessageReceived(ChatRoom chatRoom, EventLog eventLog)
    {
        ChatMessage message = eventLog.getChatMessage();
        message.addListener(messageListenr);
        Log.i("ChatMessageReceived", "from: " + message.getFromAddress().asString() + ", to: " + message.getToAddress().asString());
        ChatActivity.getMessages().add(message);
        if (ChatActivity.getRoomHandler() != null)
        {
            ChatActivity.getRoomHandler().sendEmptyMessage(CHAT_MESSAGE_RECEIVED);
        }
        int index = ChatRecordActivity.getRecordIndex(chatRoom);
        if (index != -1)
        {
            Message message1 = new Message();
            message1.what = CHAT_MESSAGE_RECEIVED;
            Bundle bundle = new Bundle();
            bundle.putInt("position", index);
            if (ChatRecordActivity.getHandler() != null)
            {
                ChatRecordActivity.getHandler().sendMessage(message1);
            }
        }
    }

    @Override
    public void onConferenceAddressGeneration(ChatRoom chatRoom) {

    }

    @Override
    public void onParticipantDeviceAdded(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onSecurityEvent(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onConferenceLeft(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onSubjectChanged(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onChatMessageSent(ChatRoom chatRoom, EventLog eventLog)
    {
        ChatMessage message = eventLog.getChatMessage();
        message.addListener(messageListenr);
        Log.i("ChatMessageSent", "from: " + message.getFromAddress().asString() + ", to: " + message.getToAddress().asString());
        ChatActivity.getMessages().add(message);
        if (ChatActivity.getRoomHandler() != null)
        {
            ChatActivity.getRoomHandler().sendEmptyMessage(CHAT_MESSAGE_SENT);
        }
        int index = ChatRecordActivity.getRecordIndex(chatRoom);
        if (index != -1)
        {
            Message message1 = new Message();
            message1.what = CHAT_MESSAGE_SENT;
            Bundle bundle = new Bundle();
            bundle.putInt("position", index);
            if (ChatRecordActivity.getHandler() != null)
            {
                ChatRecordActivity.getHandler().sendMessage(message1);
            }
        }
    }

    @Override
    public void onChatMessageParticipantImdnStateChanged(ChatRoom chatRoom, ChatMessage message, ParticipantImdnState state) {

    }

    @Override
    public void onConferenceJoined(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onMessageReceived(ChatRoom chatRoom, ChatMessage message) {

    }

    @Override
    public void onEphemeralMessageDeleted(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onParticipantRegistrationSubscriptionRequested(ChatRoom chatRoom, Address participantAddress) {

    }

    @Override
    public void onParticipantDeviceRemoved(ChatRoom chatRoom, EventLog eventLog) {

    }

    @Override
    public void onIsComposingReceived(ChatRoom chatRoom, Address remoteAddress, boolean isComposing) {

    }

    @Override
    public void onChatMessageShouldBeStored(ChatRoom chatRoom, ChatMessage message) {

    }
}
