package nz.co.danieltebbutt.cloudsky;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

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
import rajawali.materials.SimpleMaterial;
import rajawali.materials.TextureInfo;
import rajawali.materials.TextureManager;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;

public class CloudSkyRenderer extends RajawaliRenderer {

	private int mHeight = 500;
	private int mWidth = 300;
	private float mXOffset = 0;
	private float mYOffset = 0;
	
	private float mTime = 0;
	
	private boolean mStarted = false;
	
	private CloudScene mCloudScene;
	
	private SimpleMaterial mMaterial;
	private TextureManager mTextureManager;
	private ArrayList<TextureInfo> mTextures;

	
	public CloudSkyRenderer(Context context, ArrayList<Integer> cloudResources) {

		super(context);
		
		Debug.waitForDebugger();
		
		Resources res = context.getResources();
		
		mTextureManager = new TextureManager();
		mTextures = new ArrayList<TextureInfo>();
		
		for (Integer i : cloudResources) {
			// Read in the resource
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; // No pre-scaling
			final Bitmap bitmap = BitmapFactory.decodeResource(res, i, options);
			mTextures.add(mTextureManager.addTexture(bitmap));
		}
		
		
		setCamera(new Camera2D());
		setFrameRate(30);
		
		// Create the scene that will decide where clouds go.
		mCloudScene = new CloudScene();
	}
	
	 
	private void drawScene(final CloudScene scene, final FloatBuffer aTriangleBuffer) {
		
	    mCloudScene.update(0.02);
		
	    // Cache this so all clouds use the same offset
	    double xOffset = mXOffset;
	    
	    for (Cloud cloud : scene.getClouds()) {
	    	
	    	// Draw the cloud
	    	
	    }
	    
	}
	
	@Override
	public void initScene() {
		super.initScene();
		
		mMaterial = new SimpleMaterial();
		
		Plane plane = new Plane(1,1,1,1,1);
		plane.setMaterial(mMaterial);
		plane.addTexture(mTextures.get(0));
		addChild(plane);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
	}
	
	
	final String vertexShaderColor =
		    "uniform mat4 u_ViewMatrix;     \n"     // A constant representing the combined model/view/projection matrix.
		 
		  + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
		  + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
		 
		  + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.
		 
		  + "void main()                    \n"     // The entry point for our vertex shader.
		  + "{                              \n"
		  + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
		                                            // It will be interpolated across the triangle.
		  + "   gl_Position = u_ViewMatrix  \n"     // gl_Position is a special variable used to store the final position.
		  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
		  + "}                              \n";    // normalized screen coordinates.
	
	final String fragmentShaderColor =
		    "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
		                                            // precision in the fragment shader.
		  + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
		                                            // triangle per fragment.
		  + "void main()                    \n"     // The entry point for our fragment shader.
		  + "{                              \n"
		  + "   gl_FragColor = v_Color;     \n"
		  + "}                              \n";	
	
	final String vertexShaderTexture =
		    "uniform mat4 u_ViewMatrix;     \n"

		  + "attribute vec4 a_Position;     \n"
		  + "attribute vec4 a_Texture;     	\n"
		  
		  + "varying vec4 v_Texture;        \n"
		 
		  + "void main()                    \n"
		  + "{                              \n"
		  + "   v_Texture = a_Texture;      \n"
		  
		  + "   gl_Position = u_ViewMatrix  \n"
		  + "               * a_Position;   \n"
		  + "}                              \n";  
	
	final String fragmentShaderTexture =
		    "precision mediump float;       \n"
		  + "uniform sampler2D u_TextureUnit;\n"
		  + "uniform float u_Factor;        \n"
		    		
		  + "varying vec4 v_Texture;     	\n"
		                                           
		  + "void main()                    \n"  
		  + "{                              \n"
		  + "   vec4 color = texture2D(u_TextureUnit, v_Texture.st);\n"
		  + "   color.rgb = color.rgb / color.a;\n"
		  + "   gl_FragColor = color * u_Factor;\n"
		  + "}                              \n";

}
