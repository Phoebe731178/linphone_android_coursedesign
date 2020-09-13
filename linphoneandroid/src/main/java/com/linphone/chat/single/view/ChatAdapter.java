package com.linphone.chat.single.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.linphone.R;
import com.linphone.util.LinphoneManager;
import org.jetbrains.annotations.NotNull;
import org.linphone.core.Address;
import org.linphone.core.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages)
    {
        this.messages = messages;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView chatLeftTextView;
        TextView chatRightTextView;
        ImageView userLeftImageView;
        ImageView userRightImageView;
        ImageView imdnLeft;
        ImageView imdnRight;
        public ViewHolder(@NonNull @NotNull View itemView)
        {
            super(itemView);
            chatLeftTextView = itemView.findViewById(R.id.chat_left_textView);
            chatRightTextView = itemView.findViewById(R.id.chat_right_textView);
            userLeftImageView = itemView.findViewById(R.id.user_left_imageView);
            userRightImageView = itemView.findViewById(R.id.user_right_imageView);
            imdnLeft = itemView.findViewById(R.id.imdn_left);
            imdnRight = itemView.findViewById(R.id.imdn_right);
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position)
    {
        ChatMessage message = messages.get(position);
        Address address = LinphoneManager.getCore().getProxyConfigList()[0].getIdentityAddress();
        if (message.getFromAddress().weakEqual(address))
        {
            holder.chatLeftTextView.setVisibility(View.GONE);
            holder.userLeftImageView.setVisibility(View.GONE);
            holder.chatRightTextView.setVisibility(View.VISIBLE);
            holder.userRightImageView.setVisibility(View.VISIBLE);
            holder.chatRightTextView.setText(message.getTextContent());
            switch (message.getState())
            {
                case NotDelivered: holder.imdnRight.setImageResource(R.drawable.imdn_error); break;
                case InProgress: holder.imdnRight.setImageResource(R.drawable.imdn_loading); break;
                default: holder.imdnRight.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            holder.chatLeftTextView.setVisibility(View.VISIBLE);
            holder.userLeftImageView.setVisibility(View.VISIBLE);
            holder.chatRightTextView.setVisibility(View.GONE);
            holder.userRightImageView.setVisibility(View.GONE);
            holder.chatLeftTextView.setText(message.getTextContent());
            switch (message.getState())
            {
                case NotDelivered: holder.imdnLeft.setImageResource(R.drawable.imdn_error); break;
                case InProgress: holder.imdnLeft.setImageResource(R.drawable.imdn_loading); break;
                default: holder.imdnLeft.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return messages == null? 0: messages.size();
    }
}
