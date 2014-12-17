package com.jeezsoft.costcontrol;

import android.app.Application;
import android.content.Context;

/**
 * Created by jeezic on 17.12.2014.
 */
public class MyApp extends Application
{

    private DB db;

    public DB getDb(Context ctx) {
        if (db == null){
            db = new DB(ctx);
        }
        return db;
    }



    @Override
    public void onCreate() {
        super.onCreate();

            }
}
