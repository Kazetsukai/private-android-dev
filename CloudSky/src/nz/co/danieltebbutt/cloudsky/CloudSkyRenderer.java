package nz.co.danieltebbutt.cloudsky;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.util.Log;

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
	
	private Map<Cloud, Plane> mCloudPlanes;

	
	public CloudSkyRenderer(Context context, ArrayList<Integer> cloudResources) {

		super(context);
		
		Debug.waitForDebugger();
		
		mTextures = new ArrayList<TextureInfo>();
		mCloudResources = cloudResources;
		
	}
	
	 
	private void updateScene(final CloudScene scene) {
		
	    mCloudScene.update(0.02);
		
	    // Cache this so all clouds use the same offset
	    double xOffset = mXOffset;
	    
	    for (Cloud cloud : scene.getClouds()) {
	    	
	    	// Update the cloud
	    	Plane plane = mCloudPlanes.get(cloud);
	    	if (plane == null) 
	    		mCloudPlanes.remove(cloud);
	    	else
	    	{
	    		// Take a wedge out of the 1.0x1.0 box that clouds are in to simulate perspective
	    		float mappedXPos = (float)((cloud.getXPosition() - 0.5 - (xOffset - 0.5) / 20) * (10 - cloud.getZPosition() * 8) + 0.5);
	    		// Further out clouds lower logarithmically
	    		float mappedYPos = (float)(Math.log10(cloud.getZPosition() * 3 + 1) + 0.3 * cloud.getYPosition() - 0.5);
	    		// Scale accordingly
	    		float mappedScale = (float)(1 / Math.log10(cloud.getZPosition() + 2.2));

	    		plane.setScale(mappedScale);
	    		plane.setPosition(mappedXPos, mappedYPos, (float)cloud.getZPosition());
	    	}
	    	
	    }
	    
	}
	
	@Override
	public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
							float yStep, int xPixels, int yPixels) {
			 		
			mXOffset = xOffset;
			
	}

	@Override
	public void initScene() {
		
		setCamera(new Camera2D());
		setFrameRate(30);
		setBackgroundColor(0.2f, 0.3f, 0.7f, 1.0f);

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
			TextureInfo texture = c.getTexture();
			AMaterial material = new UnpreblendMaterial();
			Plane plane = new Plane(texture.getWidth() / 2000f, texture.getHeight() / 2000f, 1, 1, 1);
			plane.setMaterial(material);
			plane.setTransparent(true);
			plane.addTexture(texture);
			mCloudPlanes.put(c, plane);
			addChild(plane);
		}
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		updateScene(mCloudScene);
		
		super.onDrawFrame(glUnused);
	}
}
