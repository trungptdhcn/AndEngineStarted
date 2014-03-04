package com.thor.chess;

import android.app.Activity;
import android.util.DisplayMetrics;

class Size {
	public int width;
	public int height;
	public Size() {
		width = 0;
		height = 0;
	}
	public Size(int w, int h) {
		width = w;
		height = h;
	}
}
public final class Helper {
	public static Size getScreenSize(Activity act) {
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        return new Size(width, height);
	}
}
