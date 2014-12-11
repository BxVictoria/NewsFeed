package com.smotrova.newsfeed.cmd;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

public abstract class Command implements Parcelable {

    protected Context context;

    public final void execute(Context context) {
        this.context = context;
        execute();
    }

    public final void start(Context context) {
        CommandService.start(context, this);
    }

    public void execute() {
        try {
            run();
        } catch (Exception e) {
            Log.e("NEWSFEED", "Error in " + this.getClass().getSimpleName(), e);
        }
    }

    protected abstract void run();

    @Override
    public int describeContents() {
        return 0;
    }
}
