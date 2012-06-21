package nz.co.danieltebbutt.tweetsky;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class TweetSkyWallpaper extends WallpaperService {

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

		private final Paint mSkyPaint = new Paint();
		private final Paint mCloudPaint = new Paint();
		private float mWidth = 400;
		private float mHeight = 300;

		private ArrayList<Bitmap> mSkyBitmaps = new ArrayList<Bitmap>();
		private ArrayList<Bitmap> mCloudBitmaps = new ArrayList<Bitmap>();
		
		private final Runnable mDrawRunnable = new Runnable() {
			public void run() {
				drawFrame(true);
			}
		};
		private boolean mVisible;

		TweetSkyEngine() {
			mVisible = true;

			mSkyPaint.setARGB(255, 255, 255, 255);
			mCloudPaint.setARGB(255, 255, 255, 255);
		}

		@Override
		public void onCreate(SurfaceHolder holder) {
			holder.setFormat(PixelFormat.RGBA_8888);
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
			
			float biggestDimension = mHeight > mWidth ? mHeight : mWidth;
			float ratio = 100 / biggestDimension;
			
			// Scale all bitmaps to the appropriate size for new resolution and cache
			mSkyBitmaps.clear();
			for (Drawable sky : mSkies) {
				Bitmap bitmap = Bitmap.createScaledBitmap(((BitmapDrawable)sky).getBitmap(), (int)mWidth, (int)mHeight, false);
				mSkyBitmaps.add(bitmap);
			}			
			mCloudBitmaps.clear();
			for (Drawable cloud : mClouds) {
				Bitmap bitmap = Bitmap.createScaledBitmap(
									((BitmapDrawable)cloud).getBitmap(), 
									(int)(cloud.getIntrinsicWidth() * ratio),
									(int)(cloud.getIntrinsicHeight() * ratio),
									false);
				mCloudBitmaps.add(bitmap);
			}
			
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

			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					Bitmap sky = mSkyBitmaps.get(0);
					c.drawBitmap(sky, 0, 0, mSkyPaint);
					
					for (int i = 0; i < 4; i++) {
						Bitmap cloud = mCloudBitmaps.get(i);
						int x = i * 50;
						int y = i * 100 % 230;
						
						c.drawBitmap(cloud, x, y, mCloudPaint);
					}
				}
			} finally {
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			// Reschedule the next redraw
			mHandler.removeCallbacks(mDrawRunnable);
			if (mVisible) {
				mHandler.postDelayed(mDrawRunnable, 1000 / mTimeStep);
			}
		}
	}
}
