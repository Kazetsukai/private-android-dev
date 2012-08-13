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

	private ArrayList<Drawable> mClouds = new ArrayList<Drawable>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		android.os.Debug.waitForDebugger();
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		
		Resources res = getResources();
		mClouds.add(res.getDrawable(R.drawable.cloud1));
		mClouds.add(res.getDrawable(R.drawable.cloud2));
		mClouds.add(res.getDrawable(R.drawable.cloud3));
		mClouds.add(res.getDrawable(R.drawable.cloud4));
		
		return new TweetSkyEngine();
	}

	class TweetSkyEngine extends GLEngine {
		
		private final TweetSkyRenderer mRenderer;
		
		TweetSkyEngine() {
			mRenderer = new TweetSkyRenderer(mClouds);
			setRenderer(mRenderer);
			setRenderMode(RENDERMODE_CONTINUOUSLY);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
		}


		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
		}
	}
}
