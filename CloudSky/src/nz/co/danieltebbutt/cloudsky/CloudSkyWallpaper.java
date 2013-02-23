package nz.co.danieltebbutt.cloudsky;

import java.util.ArrayList;

import rajawali.wallpaper.Wallpaper;

import nz.co.danieltebbutt.cloudsky.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.SurfaceHolder;

public class CloudSkyWallpaper extends Wallpaper {

	private ArrayList<Integer> mClouds = new ArrayList<Integer>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mClouds.add(R.drawable.cloud12);
		mClouds.add(R.drawable.cloud13);
		mClouds.add(R.drawable.cloud14);
	}

	@Override
	public Engine onCreateEngine() {
		
		SharedPreferences prefs = getSharedPreferences("Preferences", 0);
		
		Context context = getApplicationContext();
		
		return new WallpaperEngine(prefs, context, new CloudSkyRenderer(context, mClouds));
	}
}
