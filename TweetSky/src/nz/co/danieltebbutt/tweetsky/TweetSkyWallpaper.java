package nz.co.danieltebbutt.tweetsky;

import java.util.ArrayList;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.SurfaceHolder;

public class TweetSkyWallpaper extends GLWallpaperService {

	private final Handler mHandler = new Handler();

	// Time step in milliseconds
	private final int mTimeStep = 100;

	private ArrayList<Drawable> mSkies = new ArrayList<Drawable>();
	private ArrayList<Drawable> mClouds = new ArrayList<Drawable>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		android.os.Debug.waitForDebugger();
		
		Resources res = getResources();

		mSkies.add(res.getDrawable(R.drawable.sky1));

		mClouds.add(res.getDrawable(R.drawable.cloud1));
		mClouds.add(res.getDrawable(R.drawable.cloud2));
		mClouds.add(res.getDrawable(R.drawable.cloud3));
		mClouds.add(res.getDrawable(R.drawable.cloud4));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new TweetSkyEngine();
	}

	class TweetSkyEngine extends Engine {
		
		private final Runnable mDrawRunnable = new Runnable() {
			public void run() {
				drawFrame(true);
			}
		};
		private boolean mVisible;

		TweetSkyEngine() {
			mVisible = true;
		}

		@Override
		public void onCreate(SurfaceHolder holder) {
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			mHandler.removeCallbacks(mDrawRunnable);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				drawFrame(false);
			} else {
				mHandler.removeCallbacks(mDrawRunnable);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			mHeight = height;
			mWidth = width;
			
			drawFrame(false);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawRunnable);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
		}

		void update() {
		}

		void drawFrame(boolean doUpdate) {

			if (doUpdate)
				update();

			// Reschedule the next redraw
			mHandler.removeCallbacks(mDrawRunnable);
			if (mVisible) {
				mHandler.postDelayed(mDrawRunnable, 1000 / mTimeStep);
			}
		}
	}
}
