package com.smotrova.newsfeed.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.smotrova.newsfeed.R;
import com.smotrova.newsfeed.cmd.GetNewsCommand;
import com.smotrova.newsfeed.store.NewsProvider;

public class NewsListActivity extends ActionBarActivity {

    private static final int NEWS_LOADER_ID = 0;
    private ListView newsList;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        newsList = (ListView) findViewById(R.id.news_list);
        progressView = findViewById(R.id.progress_view);

        newsList.setAdapter(new NewsAdapter(this));
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor news = ((CursorAdapter) parent.getAdapter()).getCursor();
                NewsDetailsActivity.start(NewsListActivity.this,
                        news.getLong(news.getColumnIndex(NewsProvider.NewsTable._ID)));
            }
        });

        getSupportLoaderManager().initLoader(NEWS_LOADER_ID, Bundle.EMPTY, callbacks);
    }

    private LoaderCallbacks<Cursor> callbacks = new LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(NewsListActivity.this, NewsProvider.NEWS_CONTENT_URI,
                    null, null, null, NewsProvider.NewsTable.POSITION);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() > 0) {
                showProgress(false);
                ((CursorAdapter) newsList.getAdapter()).swapCursor(data);
            } else {
                new GetNewsCommand().start(NewsListActivity.this);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            ((CursorAdapter) newsList.getAdapter()).swapCursor(null);
        }
    };

    private void showProgress(boolean show) {
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
