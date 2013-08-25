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
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;

public class CloudSkyWallpaper extends Wallpaper {

	private ArrayList<Integer> mClouds = new ArrayList<Integer>();

	@Override
	public Engine onCreateEngine() {
		
		mClouds.add(R.drawable.lightcloud1);
		mClouds.add(R.drawable.lightcloud2);
		mClouds.add(R.drawable.lightcloud3);
		mClouds.add(R.drawable.lightcloud4);
		mClouds.add(R.drawable.lightcloud5);
		mClouds.add(R.drawable.lightcloud6);
		
		SunPositionCalculator calc = new SunPositionCalculator(12, 235, 12, 175.2833f, -37.7833f);
		System.out.println(calc.angleFromSunToZenith() + " - " + calc.sunAzimuth() + " ST:" + calc.solarTime(12, 235, 12, 175.2833f) + " - " + calc.solarDeclination(235));
		
		return new WallpaperEngine(
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), 
			getApplicationContext(), 
			new CloudSkyRenderer(getApplicationContext(), mClouds)
		);
	}
}
