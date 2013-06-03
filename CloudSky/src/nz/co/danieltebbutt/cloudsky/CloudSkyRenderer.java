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
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;

public class CloudSkyRenderer extends RajawaliRenderer {

	private float mXOffset = 0;
	
	private CloudScene mCloudScene;
	
	private ArrayList<TextureInfo> mTextures;
	private ArrayList<Integer> mCloudResources;
	private float mBiggestTextureWidth = 1024;
	
	private Map<Cloud, Plane> mCloudPlanes;

	private Camera2D mCamera;

	private long mLastMilliseconds = 0;
	
	private class PlaneComparator implements Comparator<BaseObject3D> {

		@Override
		public int compare(BaseObject3D lhs, BaseObject3D rhs) {
			return ((Float)((Plane)lhs).getZ()).compareTo(((Plane)rhs).getZ());
		}
	
	}
	
	public CloudSkyRenderer(Context context, ArrayList<Integer> cloudResources) {

		super(context);
		
		//Debug.waitForDebugger();
		
		mTextures = new ArrayList<TextureInfo>();
		mCloudResources = cloudResources;
		mLastMilliseconds = SystemClock.uptimeMillis();
		
	}
	
	 
	private void updateScene(final CloudScene scene) {
		
		long milliseconds = SystemClock.uptimeMillis();
		double elapsedTimeSinceLastFrame = (milliseconds - mLastMilliseconds) / 1000.0;
		if (elapsedTimeSinceLastFrame < 0.0) elapsedTimeSinceLastFrame = 0.0;
		if (elapsedTimeSinceLastFrame > 0.05) elapsedTimeSinceLastFrame = 0.05;
		mLastMilliseconds = milliseconds;
		
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
	    		// Further out clouds lower logarithmically
	    		float mappedYPos = (float)(Math.log10(cloud.getZPosition() * 3 + 1) + 0.3 * cloud.getYPosition() - 0.5);
	    		// Scale accordingly
	    		float mappedScale = (float)mCloudScene.convertBoxToBoundingSpace(cloud.getTexture().getWidth() / mBiggestTextureWidth * mCloudScene.mCloudSize, cloud.getZPosition());
	    		
	    		plane.setScale(mappedScale / (float)mCloudScene.mScreenWidth);
	    		plane.setPosition(((mappedXPos / 2) / (float)mCloudScene.mScreenWidth), mappedYPos, -(float)cloud.getZPosition());
	    	}	
	    }
	}


	private void sortPlanes() {
		ArrayList<BaseObject3D> objects = new ArrayList<BaseObject3D>(getChildren().size());
		List<BaseObject3D> children = getChildren();

		int length = children.size();
		for (int i = 0; i < length; i++)
			objects.add(children.get(i));
		
		Collections.sort(objects, new PlaneComparator() { });
		
		clearChildren();
		
		length = objects.size();
		for (int i = 0; i < length; i++) {
			addChild(objects.get(i));
			System.out.println(((Plane)objects.get(i)).getZ());
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
			// Read in the resource
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; // No pre-scaling
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			final Bitmap bitmap = BitmapFactory.decodeResource(res, i, options);
			mTextures.add(mTextureManager.addTexture(bitmap));
		}

		// Create the scene that will decide where clouds go.
		mCloudScene = new CloudScene(mTextures);
		
		mCloudPlanes = new HashMap<Cloud, Plane>();
		
		for (Cloud c : mCloudScene.getClouds()) {
			createPlaneForCloud(c);
		}

		updateScene(mCloudScene);
		sortPlanes();
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
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		updateScene(mCloudScene);
		
		super.onDrawFrame(glUnused);
	}
}
