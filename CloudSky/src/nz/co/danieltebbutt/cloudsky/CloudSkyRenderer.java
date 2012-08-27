package nz.co.danieltebbutt.cloudsky;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService.Renderer;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class CloudSkyRenderer implements Renderer {

	private static final String VIEW_MATRIX_ATTRIBUTE = "u_ViewMatrix";
	private static final String COLOR_ATTRIBUTE = "a_Color";
	private static final String TEXTURE_ATTRIBUTE = "a_Texture";
	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String TEXTURE_UNIT_ATTRIBUTE = "u_TextureUnit";
	private static final int BYTES_PER_FLOAT = 4;
	
	ArrayList<Bitmap> mCloudBitmaps = new ArrayList<Bitmap>();
	ArrayList<Cloud> mClouds = new ArrayList<Cloud>();
	ArrayList<Cloud> mRemoveClouds = new ArrayList<Cloud>();
	ArrayList<Cloud> mAddClouds = new ArrayList<Cloud>();
	
	float[] mViewMatrix = new float[16];
	float[] mModelMatrix = new float[16];
	float[] mMVWMatrix = new float[16];
	int[] mTexture = new int[4];
	int[][] mTextureSizes = new int[4][2];
	
	/** Buffer for polygons **/
	float[] mQuadCoords;
	FloatBuffer mCoordBuffer;
	
	/** Handles for shader programs **/
	private int mProgramHandleColor;
	private int mProgramHandleTexture;

	private int mViewMatrixHandleColor;
	private int mColorHandle;
	private int mPositionHandleColor;
	private int mViewMatrixHandleTexture;
	private int mTextureHandle;
	private int mPositionHandleTexture;
	private int mTextureUnitHandle;
	
	/** How many elements per vertex. */
	private final int mStrideBytes = 7 * BYTES_PER_FLOAT;
	 
	/** Offset of the position data. */
	private final int mPositionOffset = 0;
	 
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	 
	private final int mColorOffset = 3;
	private final int mTextureOffset = 3;
	 
	private final int mColorDataSize = 4;
	private final int mTextureDataSize = 4;
	
	private int mHeight = 500;
	private int mWidth = 300;
	private float mXOffset = 0;
	private float mYOffset = 0;
	
	private CloudLogic mCloudLogic;
	
	public CloudSkyRenderer(ArrayList<Integer> clouds, Resources resources) {
		
		mCloudLogic = new CloudLogic();
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;   // No pre-scaling
		 

		for (Integer i : clouds) {
			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(resources, i, options);
			mCloudBitmaps.add(bitmap);
		}
		
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {
		
		GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		drawScene(mCoordBuffer);
		
	}

	private void checkGLError(String msg) {
		int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("DT", String.format("%s - GL error: 0x%x", msg, error));
        }
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {

		GLES20.glViewport(0, 0, width, height);

		mHeight = height;
		mWidth = width;

		// Recreate clouds to get a good distribution over screen
		mClouds.clear();
		for (int i = 0; i < 15; i++) {
			mClouds.add(Cloud.generateCloud(mCloudBitmaps.size(), -100, mWidth + 300, 0, mHeight - 150, 1, 0.5));
		}

		Matrix.orthoM(mViewMatrix, 0, 0, width, height, 0, -1, 1);
		
		float heightIncrement = height / 3.0f;
		
		mQuadCoords = new float[] {
				// Sky coords
				width, 0.0f, 0.0f,
				0.2f, 0.38f, 0.55f, 1.0f,
				
				0.0f, 0.0f, 0.0f,
				0.2f, 0.38f, 0.55f, 1.0f,
				
				width, heightIncrement, 0.0f,
				0.25f, 0.46f, 0.61f, 1.0f,
				
				0.0f, heightIncrement, 0.0f,
				0.25f, 0.46f, 0.61f, 1.0f,
				
				width, heightIncrement * 2, 0.0f,
				0.37f, 0.55f, 0.68f, 1.0f,
				
				0.0f, heightIncrement * 2, 0.0f,
				0.37f, 0.55f, 0.68f, 1.0f,
				
				width, height, 0.0f,
				0.61f, 0.64f, 0.58f, 1.0f,
				
				0.0f, height, 0.0f,
				0.61f, 0.64f, 0.58f, 1.0f,
				
				// Cloud coords
				-0.5f, -0.5f, 0.0f,
				0.0f, 0.0f, 1.0f, 1.0f,
				
				-0.5f, 0.5f, 0.0f,
				0.0f, 1.0f, 1.0f, 1.0f,
				
				0.5f, -0.5f, 0.0f,
				1.0f, 0.0f, 1.0f, 1.0f,
				
				0.5f, 0.5f, 0.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
		};
		
		mCoordBuffer = ByteBuffer.allocateDirect(mQuadCoords.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		for (float f : mQuadCoords)
			mCoordBuffer.put(f);

	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		GLES20.glClearColor(0.2f, 0.4f, 0.2f, 1f);
		loadTextures();
		loadShaders();
		
		checkGLError("After surface creation");
	}

	public void setOffsets(float xOffset, float yOffset, float xStep,
			float yStep, int xPixels, int yPixels) {
		mXOffset = xOffset;
		mYOffset = yOffset;
	}

	public void release() {

	}
	 
	/**
	 * Draws a triangle from the given vertex data.
	 *
	 * @param aTriangleBuffer The buffer containing the vertex data.
	 */
	private void drawScene(final FloatBuffer aTriangleBuffer)
	{
		checkGLError("Before draw scene");
		
	    // Draw the sky //////////////////////////////
		GLES20.glUseProgram(mProgramHandleColor);
		
	    // Pass in the position information
	    aTriangleBuffer.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandleColor, mPositionDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aTriangleBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandleColor);
		
	    // Pass in the color information
	    aTriangleBuffer.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aTriangleBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
		checkGLError("Enabling color vertex attribute array");
	    
	    GLES20.glUniformMatrix4fv(mViewMatrixHandleColor, 1, false, mViewMatrix, 0);
	    checkGLError("Set view matrix");
		
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 8);
	    checkGLError("Drawing sky");
	    
	    // Draw the clouds ///////////////////////////////

	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		GLES20.glUseProgram(mProgramHandleTexture);
		
	    // Pass in the position information
	    aTriangleBuffer.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandleTexture, mPositionDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aTriangleBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandleTexture);
		
	    // Pass in the color information
	    aTriangleBuffer.position(mTextureOffset);
	    GLES20.glVertexAttribPointer(mTextureHandle, mTextureDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aTriangleBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mTextureHandle);
		checkGLError("Enabling texture vertex attribute array");
	    
	    GLES20.glUniformMatrix4fv(mViewMatrixHandleTexture, 1, false, mViewMatrix, 0);
	    checkGLError("Set view matrix");
	    
	    GLES20.glEnable(GLES20.GL_BLEND);
	    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
	    Collections.sort(mClouds, new Comparator<Cloud>() {

			@Override
			public int compare(Cloud lhs, Cloud rhs) {
				return Double.compare(lhs.getZPosition(), rhs.getZPosition());
			}
		});
	    
	    for (Cloud cloud : mClouds) {
	    	cloud.update(0.05);
	    	if (cloud.getXPosition() > mWidth + 300){
	    		mRemoveClouds.add(cloud);
	    		mAddClouds.add(Cloud.generateCloud(mCloudBitmaps.size(), -100, -100, 0, mHeight - 150, 0.5, 1));
	    	}
	    	int textureId = cloud.getTextureId();
	    	float z = (float)cloud.getZPosition();
	    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[textureId]);
	    	Matrix.setIdentityM(mModelMatrix, 0);
	    	Matrix.translateM(mModelMatrix, 0, ((float)cloud.getXPosition() - 100 * mXOffset) * z, (float)cloud.getYPosition(), 0);
	    	Matrix.scaleM(mModelMatrix, 0, mTextureSizes[textureId][0] / 3.5f * z, mTextureSizes[textureId][1] / 3.5f * z, 1);
	    	Matrix.multiplyMM(mMVWMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	    	GLES20.glUniformMatrix4fv(mViewMatrixHandleTexture, 1, false, mMVWMatrix, 0);
	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    }
	    for (Cloud removeCloud : mRemoveClouds) {
	    	mClouds.remove(removeCloud);
	    }
	    for (Cloud addCloud : mAddClouds) {
	    	mClouds.add(addCloud);
	    }
	    mRemoveClouds.clear();
	    mAddClouds.clear();
	    GLES20.glDisable(GLES20.GL_BLEND);
		checkGLError("Drawing");
	}
	
	public int loadShader(String shaderSource, int shaderType) {
		// Load in the vertex shader.
		int shaderHandle = GLES20.glCreateShader(shaderType);
    	checkGLError("Creating shader handle");
		String errorInfo = null;
		
		if (shaderHandle != 0)
		{
		    // Pass in the shader source.
		    GLES20.glShaderSource(shaderHandle, shaderSource);
	    	checkGLError("Passing in shader source");
		 
		    // Compile the shader.
		    GLES20.glCompileShader(shaderHandle);
	    	checkGLError("Immediately post shader");
	    	
		    // Get the compilation status.
		    final int[] compileStatus = new int[1];
		    GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		 
		    // If the compilation failed, delete the shader.
		    if (compileStatus[0] == 0)
		    {
		    	errorInfo = GLES20.glGetShaderInfoLog(shaderHandle);
		    	checkGLError("After getting shader log, when shader failed");
		        GLES20.glDeleteShader(shaderHandle);
		        shaderHandle = 0;
		    }
		}
		 
		if (shaderHandle == 0)
		{
		    throw new RuntimeException("Error creating shader.");
		}
		else
		{
			return shaderHandle;
		}
	}
	
	public int createProgram(int vertexShaderHandle, int fragmentShaderHandle) {
		// Create a program object and store the handle to it.
		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0)
		{
		    // Bind the vertex shader to the program.
		    GLES20.glAttachShader(programHandle, vertexShaderHandle);

		    // Bind the fragment shader to the program.
		    GLES20.glAttachShader(programHandle, fragmentShaderHandle);

		    // Bind attributes
		    GLES20.glBindAttribLocation(programHandle, 0, POSITION_ATTRIBUTE);
		    GLES20.glBindAttribLocation(programHandle, 1, COLOR_ATTRIBUTE);

		    // Link the two shaders together into a program.
		    GLES20.glLinkProgram(programHandle);
		 
		    // Get the link status.
		    final int[] linkStatus = new int[1];
		    GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
		 
		    // If the link failed, delete the program.
		    if (linkStatus[0] == 0)
		    {
		        GLES20.glDeleteProgram(programHandle);
		        programHandle = 0;
		    }

	    	checkGLError("Creating shader program");
		}
		 
		if (programHandle == 0)
		{
		    throw new RuntimeException("Error creating program.");
		}
		else
		{
			return programHandle;
		}	
	}
	
	private void loadShaders() {
		// Setup shader program
		int vertexShaderHandle = loadShader(vertexShaderColor, GLES20.GL_VERTEX_SHADER);
		int fragmentShaderHandle = loadShader(fragmentShaderColor, GLES20.GL_FRAGMENT_SHADER);
		mProgramHandleColor = createProgram(vertexShaderHandle, fragmentShaderHandle);
		
		// Texture shader
		int texVertexShaderHandle = loadShader(vertexShaderTexture, GLES20.GL_VERTEX_SHADER);
		int texFragmentShaderHandle = loadShader(fragmentShaderTexture, GLES20.GL_FRAGMENT_SHADER);
		mProgramHandleTexture = createProgram(texVertexShaderHandle, texFragmentShaderHandle);

		mPositionHandleColor = GLES20.glGetAttribLocation(mProgramHandleColor, POSITION_ATTRIBUTE);
		mColorHandle = GLES20.glGetAttribLocation(mProgramHandleColor, COLOR_ATTRIBUTE);
		mViewMatrixHandleColor = GLES20.glGetUniformLocation(mProgramHandleColor, VIEW_MATRIX_ATTRIBUTE);
		mPositionHandleTexture = GLES20.glGetAttribLocation(mProgramHandleTexture, POSITION_ATTRIBUTE);
		mTextureHandle = GLES20.glGetAttribLocation(mProgramHandleTexture, TEXTURE_ATTRIBUTE);
		mViewMatrixHandleTexture = GLES20.glGetUniformLocation(mProgramHandleTexture, VIEW_MATRIX_ATTRIBUTE);
		mTextureUnitHandle = GLES20.glGetUniformLocation(mProgramHandleTexture, TEXTURE_UNIT_ATTRIBUTE);
		
		GLES20.glUseProgram(mProgramHandleTexture);
		GLES20.glUniform1i(mTextureUnitHandle, 0);
	}

	private void loadTextures() {
		GLES20.glGenTextures(4, mTexture, 0);
		
		for (int i = 0; i < 4; i++) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[i]);
	    	checkGLError("Binding texture name");
	    	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    	Bitmap bitmap = mCloudBitmaps.get(i);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			mTextureSizes[i][0] = bitmap.getWidth();
			mTextureSizes[i][1] = bitmap.getHeight();
	    	checkGLError("Setting texture");
		}
	}
	
	final String vertexShaderColor =
		    "uniform mat4 u_ViewMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
		 
		  + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
		  + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
		 
		  + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.
		 
		  + "void main()                    \n"     // The entry point for our vertex shader.
		  + "{                              \n"
		  + "   v_Color = a_Color;          \n"     // Pass the color through to the fragment shader.
		                                            // It will be interpolated across the triangle.
		  + "   gl_Position = u_ViewMatrix   \n"     // gl_Position is a special variable used to store the final position.
		  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
		  + "}                              \n";    // normalized screen coordinates.
	
	final String fragmentShaderColor =
		    "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
		                                            // precision in the fragment shader.
		  + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
		                                            // triangle per fragment.
		  + "void main()                    \n"     // The entry point for our fragment shader.
		  + "{                              \n"
		  + "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
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
		    		
		  + "varying vec4 v_Texture;     	\n"
		                                           
		  + "void main()                    \n"  
		  + "{                              \n"
		  + "   vec4 color = texture2D(u_TextureUnit, v_Texture.st);\n"
		  + "   gl_FragColor = color;       \n"
		  + "}                              \n";

}
