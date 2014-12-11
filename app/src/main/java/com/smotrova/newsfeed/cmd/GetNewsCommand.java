package com.smotrova.newsfeed.cmd;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import com.smotrova.newsfeed.store.NewsProvider;
import com.smotrova.newsfeed.store.NewsProvider.NewsTable;
import java.util.List;

public class GetNewsCommand extends RetrofitCommand {

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    protected void run() {
        List<News> newsArray = getNews();
        int length = newsArray.size();
        int index = 0;
        ContentValues[] valueses = new ContentValues[length];
        for (News news : newsArray) {
            valueses[index++] = parseNews(news);
        }
        context.getContentResolver().bulkInsert(NewsProvider.NEWS_CONTENT_URI, valueses);
    }

    private ContentValues parseNews(News news) {
        ContentValues values = new ContentValues(10);
        values.put(NewsTable._ID, news.nid);
        values.put(NewsTable.CREATE, news.create);
        values.put(NewsTable.TITLE, news.title);
        values.put(NewsTable.IMG_URL, news.img_url);
        values.put(NewsTable.TYPE, news.type);
        values.put(NewsTable.BODY, news.body);
        values.put(NewsTable.URL, news.url);
        values.put(NewsTable.POSITION, news.position);
        values.put(NewsTable.GOODS_ID, news.goods_id);
        values.put(NewsTable.WORDS2, news.words2);
        return values;
    }

    public final static Parcelable.Creator<GetNewsCommand> CREATOR = new Parcelable.Creator<GetNewsCommand>() {

        @Override
        public GetNewsCommand createFromParcel(Parcel source) {
            return new GetNewsCommand();
        }

        @Override
        public GetNewsCommand[] newArray(int size) {
            return new GetNewsCommand[size];
        }
    };

}
