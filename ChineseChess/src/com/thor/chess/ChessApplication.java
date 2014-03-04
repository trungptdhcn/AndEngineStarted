package com.thor.chess;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ChessApplication extends Application {
	private static Context context;
	private final static String CONFIG_FILE = "chess.config"; 

    public void onCreate(){
//        VMRuntime.getRuntime().setMinimumHeapSize(2*1024*1024);
        super.onCreate();
        ChessApplication.context = getApplicationContext();
    }
    
    public static Context getAppContext() {
        return context;
    }
    
    public static void setSetting(String key, String value) {
    	SharedPreferences p = 
    			context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
    	Editor e = p.edit();
    	e.putString(key, value);
    	e.commit();
    }
    
    public static String getSetting(String key, String defValue) {
    	SharedPreferences p = 
    			context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
    	return p.getString(key, defValue);
    }
}
