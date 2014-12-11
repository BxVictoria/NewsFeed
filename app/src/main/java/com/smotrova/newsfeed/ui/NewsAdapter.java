package com.smotrova.newsfeed.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.smotrova.newsfeed.R;
import com.smotrova.newsfeed.store.NewsProvider;
import com.squareup.picasso.Picasso;

public class NewsAdapter extends CursorAdapter {

    private int idxTitle;
    private int idxImgUrl;
    private Picasso picasso;

    public NewsAdapter(Context context) {
        super(context, null, false);
        picasso = Picasso.with(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(cursor.getString(idxTitle));
        picasso.load(cursor.getString(idxImgUrl))
                .placeholder(android.R.color.background_dark)
                .error(android.R.color.holo_red_dark)
                .into(holder.pic);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        setIndexes(cursor);
        super.changeCursor(cursor);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        setIndexes(newCursor);
        return super.swapCursor(newCursor);
    }

    private void setIndexes(Cursor c) {
        if (null != c) {
            idxImgUrl = c.getColumnIndex(NewsProvider.NewsTable.IMG_URL);
            idxTitle = c.getColumnIndex(NewsProvider.NewsTable.TITLE);
        }
    }

    private static class ViewHolder {
        ImageView pic;
        TextView title;

        ViewHolder(View view) {
            pic = (ImageView) view.findViewById(R.id.pic);
            title = (TextView) view.findViewById(R.id.title);
        }
    }
}
