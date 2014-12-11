package com.smotrova.newsfeed.cmd;

import java.util.List;
import retrofit.RestAdapter;
import retrofit.http.GET;

public abstract class RetrofitCommand extends Command {

    private NewsApi api;

    private interface NewsApi {
        @GET("/news")
        List<News> getNewsFeed();
    }

    public RetrofitCommand() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.innogest.ru/api/v3/amobile")
                .build();
        api = restAdapter.create(NewsApi.class);
    }

    protected List<News> getNews() {
        return api.getNewsFeed();
    }

    /*package*/ static class News {
        long nid;
        long create;
        String title;
        String img_url;
        String type;
        String body;
        String url;
        int position;
        long goods_id;
        String words2;
    }
}