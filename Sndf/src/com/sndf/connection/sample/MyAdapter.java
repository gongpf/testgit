package com.sndf.connection.sample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class MyAdapter extends BaseAdapter
{
    private Context mContext;
    private final List<DeviceInfo> mList;

    public MyAdapter(Context context)
    {
        mList = new ArrayList<DeviceInfo>();
        mContext = context;
    }

    public void addInfo(DeviceInfo info)
    {
        mList.add(info);
        notifyDataSetChanged();
    }

    public void clear()
    {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (null == convertView)
        {
            convertView = new Button(mContext);
        }

        Button button = (Button) convertView;
        button.setText(mList.get(position).mName + ":" + mList.get(position).getRemoteAddress());
        return button;
    }
}
