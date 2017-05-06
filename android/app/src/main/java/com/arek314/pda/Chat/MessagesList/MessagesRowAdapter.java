package com.arek314.pda.Chat.MessagesList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.arek314.pda.R;

import java.util.ArrayList;


public class MessagesRowAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<MessageRowBean> list = new ArrayList<>();
    private Context context;

    public MessagesRowAdapter(ArrayList<MessageRowBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MessageRowBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final RowBeanHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.message_row, null);

            holder = new RowBeanHolder();
            holder.date = (TextView) view.findViewById(R.id.message_date);
            holder.sender = (TextView) view.findViewById(R.id.message_sender);
            holder.content = (TextView) view.findViewById(R.id.message_content);

            view.setTag(holder);
        } else {
            holder = (RowBeanHolder) view.getTag();
        }

        final MessageRowBean messageRowBean = list.get(position);
        if (messageRowBean.getUserId() < 100000) {
            view.setBackgroundColor(context.getResources().getColor(R.color.backgroundLight));
            holder.date.setTextColor(context.getResources().getColor(R.color.background));
            holder.sender.setTextColor(context.getResources().getColor(R.color.background));
            holder.content.setTextColor(context.getResources().getColor(R.color.background));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.background));
            holder.date.setTextColor(context.getResources().getColor(R.color.backgroundLight));
            holder.sender.setTextColor(context.getResources().getColor(R.color.backgroundLight));
            holder.content.setTextColor(context.getResources().getColor(R.color.backgroundLight));
        }
        holder.date.setText(messageRowBean.getDate());
        holder.sender.setText(messageRowBean.getSender());
        holder.content.setText(messageRowBean.getContent());

        return view;
    }

    private static class RowBeanHolder {
        TextView date;
        TextView sender;
        TextView content;
    }
}
