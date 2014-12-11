package com.smotrova.newsfeed.cmd;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommandService extends IntentService {

    private final static String EXTRA_COMMAND = CommandService.class.getName() + ".EXTRA_COMMAND";

    /*package*/
    static void start(Context context, Command command) {
        context.startService(new Intent(context, CommandService.class)
                .putExtra(EXTRA_COMMAND, command));
    }

    public CommandService() {
        super(CommandService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Command command = intent.getParcelableExtra(EXTRA_COMMAND);
        if (null != command) {
            command.execute(this);
        } else {
            Log.w("NEWSFEED", "No command supplied: " + intent);
        }
    }

}
