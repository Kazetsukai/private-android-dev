package nz.co.danieltebbutt.cloudsky;

import java.util.ArrayList;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;
import nz.co.danieltebbutt.cloudsky.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.SurfaceHolder;

public class CloudSkyWallpaper extends GLWallpaperService {

	private final Handler mHandler = new Handler();

	private ArrayList<Integer> mClouds = new ArrayList<Integer>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mClouds.add(R.drawable.cloud12);
		mClouds.add(R.drawable.cloud13);
		mClouds.add(R.drawable.cloud14);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new CloudSkyEngine();
	}

	class CloudSkyEngine extends GLEngine {
		
		private final CloudSkyRenderer mRenderer;
		
		CloudSkyEngine() {
			mRenderer = new CloudSkyRenderer(mClouds, getResources());
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
			mRenderer.setOffsets(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
		}
	}
}
