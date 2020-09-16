package com.linphone.chat.view;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.linphone.R;
import com.linphone.addressbook.AddressBookModel;
import com.linphone.addressbook.AddressBookModelImpl;
import com.linphone.chat.single.view.ChatActivity;
import com.linphone.vo.Contact;
import org.jetbrains.annotations.NotNull;
import org.linphone.core.ChatRoom;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatRecordAdapter extends RecyclerView.Adapter<ChatRecordAdapter.ViewHolder>
{
    private List<ChatRoom> records;
    private AddressBookModel addressBookModel;
    private View contentView;
    private Activity activity;

    public ChatRecordAdapter(List<ChatRoom> records, Activity activity)
    {
        this.records = records;
        this.activity = activity;
        addressBookModel = new AddressBookModelImpl(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView userNameTextView;
        TextView lastMessageTextView;
        ImageView userImageImageView;
        TextView unreadCountImageView;
        TextView lastMessageTimeTextView;
        public ViewHolder(@NonNull @NotNull View itemView)
        {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_record);
            lastMessageTextView = itemView.findViewById(R.id.last_message);
            userImageImageView = itemView.findViewById(R.id.user_image);
            userImageImageView.setImageResource(R.drawable.user_left);
            unreadCountImageView = itemView.findViewById(R.id.unread_count);
            lastMessageTimeTextView = itemView.findViewById(R.id.last_message_time);
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_record_item, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position)
    {
        ChatRecordActivity.getLock().readLock().lock();
        final ChatRoom record = records.get(position);
        ChatRecordActivity.getLock().readLock().unlock();
        holder.userNameTextView.setText(addressBookModel.findNameFromPhone(record.getPeerAddress().getUsername().substring(3)).getName());
        holder.lastMessageTextView.setText(record.getLastMessageInHistory().getTextContent());
        holder.unreadCountImageView.setText(record.getUnreadMessagesCount() + "");
        holder.lastMessageTimeTextView.setText(getDateText(record.getLastUpdateTime()));
        contentView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String phoneNumber = record.getPeerAddress().getUsername().substring(3);
                Contact contact = addressBookModel.findNameFromPhone(phoneNumber);
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("contact", contact);
                activity.startActivity(intent);
            }
        });
        if (record.getUnreadMessagesCount() == 0)
        {
            holder.unreadCountImageView.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount()
    {
        ChatRecordActivity.getLock().readLock().lock();
        int count = records == null? 0: records.size();
        ChatRecordActivity.getLock().readLock().unlock();
        return count;
    }

    public void setOnItemClickListener(View.OnClickListener listener)
    {
        contentView.setOnClickListener(listener);
    }

    /**
     *
     * @param timestamp 以秒为单位
     * @return 日期对应的文字描述
     */
    private String getDateText(long timestamp)
    {
        String result = "";
        Calendar messageDate = Calendar.getInstance();
        Date msgDate = new Date(timestamp * 1000);
        messageDate.setTime(msgDate);
        Date curDate = new Date();
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(curDate);
        int dayInterval = getDayInterval(msgDate, curDate);
        if (dayInterval == 0)
        {
            if (messageDate.get(Calendar.AM_PM) == Calendar.AM)
            {
                result += "上午 " + messageDate.get(Calendar.HOUR_OF_DAY) + ":" + messageDate.get(Calendar.MINUTE);
                return result;
            }
            if (messageDate.get(Calendar.AM_PM) == Calendar.PM)
            {
                result += "下午 " + (messageDate.get(Calendar.HOUR_OF_DAY) - 12) + ":" + messageDate.get(Calendar.MINUTE);
                return result;
            }
        }
        else if (dayInterval == 1)
        {
            result += "昨天";
            return result;
        }
        else if (dayInterval < 7)
        {
            return getWeekString(messageDate.get(Calendar.DAY_OF_WEEK));
        }
        else return new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp));
        return "";
    }

    private int getDayInterval(Date date1, Date date2)
    {
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    private String getWeekString(int day)
    {
        switch (day)
        {
            case Calendar.MONDAY: return "星期一";
            case Calendar.TUESDAY: return "星期二";
            case Calendar.WEDNESDAY: return "星期三";
            case Calendar.THURSDAY: return "星期四";
            case Calendar.FRIDAY: return "星期五";
            case Calendar.SATURDAY: return "星期六";
            case Calendar.SUNDAY: return "星期日";
            default: throw new IllegalArgumentException();
        }
    }
}