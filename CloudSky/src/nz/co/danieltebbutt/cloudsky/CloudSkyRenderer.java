package nz.co.danieltebbutt.cloudsky;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;

import rajawali.BaseObject3D;
import rajawali.Camera2D;
import rajawali.wallpaper.Wallpaper;
import rajawali.materials.AMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.materials.TextureInfo;
import rajawali.materials.TextureManager;
import rajawali.math.Number3D;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;

public class CloudSkyRenderer extends RajawaliRenderer {

	private float mXOffset = 0;
	
	private CloudScene mCloudScene;
	
	private ArrayList<TextureInfo> mCloudTextures;
	private ArrayList<Integer> mCloudResources;
	private float mBiggestTextureWidth = 1024;
	private TextureInfo mBackgroundTexture;
	
	private Map<Cloud, Plane> mCloudPlanes;
	private SkyPlane mBackgroundPlane;

	private Camera2D mCamera;

	private long mLastMilliseconds = 0;

	private class BaseObject3DComparator implements Comparator<BaseObject3D> {

		@Override
		public int compare(BaseObject3D lhs, BaseObject3D rhs) {
			return ((Float)((BaseObject3D)lhs).getZ()).compareTo(((BaseObject3D)rhs).getZ());
		}
	
	}
	
	public CloudSkyRenderer(Context context, ArrayList<Integer> cloudResources) {

		super(context);
		
		//Debug.waitForDebugger();
		
		mCloudTextures = new ArrayList<TextureInfo>();
		mCloudResources = cloudResources;
		mLastMilliseconds = SystemClock.uptimeMillis();
		
	}
	
	 
	private void updateScene(final CloudScene scene) {
		
		long milliseconds = SystemClock.uptimeMillis();
		double elapsedTimeSinceLastFrame = (milliseconds - mLastMilliseconds) / 1000.0;
		if (elapsedTimeSinceLastFrame < 0.0) elapsedTimeSinceLastFrame = 0.0;
		if (elapsedTimeSinceLastFrame > 0.05) elapsedTimeSinceLastFrame = 0.05;
		mLastMilliseconds = milliseconds;
		
		mBackgroundPlane.getSunPositionCalculator().set_standardTime(((SystemClock.uptimeMillis() / 10) % 100) / 100.0f);
		mBackgroundPlane.setColors(2);
		mBackgroundPlane.updateColors();
		
	    if (mCloudScene.update(elapsedTimeSinceLastFrame)) {
			// Sort the clouds
	    	sortPlanes();
	    }
	    
	    // Cache this so all clouds use the same offset
	    double cloudOffset = (mXOffset - 0.5) * (1 - mCloudScene.mScreenWidth);
	    
	    ArrayList<Cloud> clouds = scene.getClouds();
	    int length = clouds.size();
	    for (int i = 0; i < length; i++) {
	    	
	    	Cloud cloud = clouds.get(i);
	    	
	    	// Update the cloud
	    	Plane plane = mCloudPlanes.get(cloud);
	    	if (plane == null) 
	    		mCloudPlanes.remove(cloud);
	    	else
	    	{
	    		float mappedXPos = (float)mCloudScene.convertBoxToBoundingSpace(cloud.getXPosition() - cloudOffset, cloud.getZPosition());
	    		// Further out clouds lower
	    		float mappedYPos = (float)(cloud.getYPosition() * 0.3 - cloud.getZPosition() * 0.5 + 0.1);
	    		// Scale accordingly
	    		float mappedScale = (float)(mCloudScene.convertBoxToBoundingSpace(cloud.getTexture().getWidth() / mBiggestTextureWidth * mCloudScene.mCloudSize, cloud.getZPosition()) / mCloudScene.mScreenWidth);
	    		
	    		float aspectRatio = 1;
	    		if (mViewportHeight != 0)
	    			aspectRatio = mViewportWidth / (float)mViewportHeight;
	    		
	    		if (aspectRatio > 1)
	    			mappedScale /= aspectRatio;
	    		
	    		plane.setScale(mappedScale, mappedScale * aspectRatio, mappedScale);
	    		plane.setPosition(((mappedXPos / 2) / (float)mCloudScene.mScreenWidth), mappedYPos, -(float)cloud.getZPosition());
	    		
	    		float zFactor = (float) (cloud.getZPosition() - 0.2) / 2;// * 2;
	    		plane.setColor(Color.argb((int)(zFactor * 255), 0, 0, 0));
	    	}	
	    }
	}


	private void sortPlanes() {
		ArrayList<BaseObject3D> objects = new ArrayList<BaseObject3D>(getChildren().size());
		List<BaseObject3D> children = getChildren();

		int length = children.size();
		for (int i = 0; i < length; i++)
			objects.add(children.get(i));
		
		Collections.sort(objects, new BaseObject3DComparator() { });
		
		clearChildren();
		
		length = objects.size();
		for (int i = 0; i < length; i++) {
			addChild(objects.get(i));
			System.out.println(objects.get(i).getZ());
		}
	}
	
	@Override
	public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
							float yStep, int xPixels, int yPixels) {
		mXOffset = xOffset;
	}

	@Override
	public void initScene() {
		
		mCamera = new Camera2D();
		setCamera(mCamera);
		setFrameRate(30);

		Resources res = mContext.getResources();
		
		for (Integer i : mCloudResources) {
			TextureInfo texture = addTextureFromResource(res, i);
			mCloudTextures.add(texture);
		}

		mBackgroundTexture = addTextureFromResource(res, R.drawable.background1);
		
		// Create the scene that will decide where clouds go.
		mCloudScene = new CloudScene(mCloudTextures);
		
		createBackgroundPlane();
		
		mCloudPlanes = new HashMap<Cloud, Plane>();
		
		for (Cloud c : mCloudScene.getClouds()) {
			createPlaneForCloud(c);
		}

		
		updateScene(mCloudScene);
		sortPlanes();
	}


	private TextureInfo addTextureFromResource(Resources contextResources,
			Integer resource) {
		// Read in the resource
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false; // No pre-scaling
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		final Bitmap bitmap = BitmapFactory.decodeResource(contextResources, resource, options);
		return mTextureManager.addTexture(bitmap);
	}


	private void createPlaneForCloud(Cloud c) {
		TextureInfo texture = c.getTexture();
		Plane plane = new Plane(texture.getWidth() / 2000f, texture.getHeight() / 2000f, 1, 1);
		AMaterial material = new UnpreblendMaterial();
		plane.setMaterial(material);
		plane.setTransparent(true);
		plane.addTexture(texture);
		mCloudPlanes.put(c, plane);
		addChild(plane);
	}
	
	private void createBackgroundPlane() {
		SkyPlane plane = new SkyPlane(1, 1, 40, 40);
		mBackgroundPlane = plane;
		plane.setPosition(0, 0, -2);
		AMaterial material = new SimpleMaterial();
		material.setUseColor(true);
		plane.setMaterial(material);
		addChild(plane);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		updateScene(mCloudScene);
		
		super.onDrawFrame(glUnused);
	}
}
