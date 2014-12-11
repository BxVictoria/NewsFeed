package com.smotrova.newsfeed.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.smotrova.newsfeed.R;
import com.smotrova.newsfeed.store.NewsProvider;
import com.smotrova.newsfeed.store.NewsProvider.NewsTable;
import com.squareup.picasso.Picasso;

public class NewsDetailsActivity extends ActionBarActivity {

    private static final String NEWS_KEY = NewsDetailsActivity.class.getCanonicalName() + ".NEWS_KEY";
    private static final int NEWS_LOADER_ID = 0;

    private ImageView pic;
    private TextView title;
    private WebView body;

    public static void start(Context context, long newsId) {
        context.startActivity(new Intent(context, NewsDetailsActivity.class).putExtra(NEWS_KEY, newsId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        pic = (ImageView) findViewById(R.id.pic);
        title = (TextView) findViewById(R.id.title);
        body = (WebView) findViewById(R.id.body);

        body.setBackgroundColor(0x00000000);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportLoaderManager().initLoader(NEWS_LOADER_ID, getIntent().getExtras(), callbacks);
    }

    private LoaderManager.LoaderCallbacks<Cursor> callbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(NewsDetailsActivity.this, NewsProvider.getNewsContentUri(args.getLong(NEWS_KEY)),
                    null, null, null, NewsTable.POSITION);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.moveToFirst()) {
                Picasso.with(NewsDetailsActivity.this)
                        .load(data.getString(data.getColumnIndex(NewsTable.IMG_URL)))
                        .placeholder(android.R.color.background_dark)
                        .error(android.R.color.holo_red_dark)
                        .into(pic);
                title.setText(data.getString(data.getColumnIndex(NewsTable.TITLE)));
                body.getSettings().setJavaScriptEnabled(true);
                body.loadDataWithBaseURL("", data.getString(data.getColumnIndex(NewsTable.BODY)), "text/html", "UTF-8", "");
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // no op
        }
    };
}
