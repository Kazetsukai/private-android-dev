package nz.co.danieltebbutt.tweetsky;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService.Renderer;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class TweetSkyRenderer implements Renderer {

	private static final String VIEW_MATRIX_ATTRIBUTE = "u_ViewMatrix";
	private static final String COLOR_ATTRIBUTE = "a_Color";
	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final int BYTES_PER_FLOAT = 4;
	
	float[] mViewMatrix = new float[16];
	int[] mTexture = new int[1];
	
	/** Buffer for polygons **/
	float[] mTriangleCoords;
	FloatBuffer mCoordBuffer;
	
	/** Handle for shader program **/
	private int mProgramHandle;
	 
	/** How many elements per vertex. */
	private final int mStrideBytes = 7 * BYTES_PER_FLOAT;
	 
	/** Offset of the position data. */
	private final int mPositionOffset = 0;
	 
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	 
	/** Offset of the color data. */
	private final int mColorOffset = 3;
	 
	/** Size of the color data in elements. */
	private final int mColorDataSize = 4;
	
	private int mViewMatrixHandle;
	private int mColorHandle;
	private int mPositionHandle;
	
	
	@Override
	public void onDrawFrame(GL10 unused) {
		// TODO Auto-generated method stub

		GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		drawTriangle(mCoordBuffer);
		
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

		Matrix.orthoM(mViewMatrix, 0, 0, width, height, 0, -1, 1);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		mTriangleCoords = new float[] {
				10.0f, -50.0f, 0.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
				-50.0f, 20.0f, 0.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				
				60.0f, 20.0f, 0.0f,
				1.0f, 1.0f, 0.0f, 1.0f
		};
		
		mCoordBuffer = ByteBuffer.allocateDirect(mTriangleCoords.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		for (float f : mTriangleCoords)
			mCoordBuffer.put(f);
		
		//GLES20.glEnable(GL10.GL_TEXTURE_2D);
		//GLES20.glGenTextures(1, mTexture, 0);
		//GLES20.glBindTexture(0, mTexture[0]);
		//GLUtils.texImage2D(0, 0, Bitmap.createBitmap(200, 100, Config.ARGB_8888), 0);
		
		GLES20.glClearColor(0.2f, 0.4f, 0.2f, 1f);
		
		// Setup shader program
		int vertexShaderHandle = loadShader(vertexShader, GLES20.GL_VERTEX_SHADER);
		int fragmentShaderHandle = loadShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER);
		mProgramHandle = createProgram(vertexShaderHandle, fragmentShaderHandle);
		
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, POSITION_ATTRIBUTE);
		mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, COLOR_ATTRIBUTE);
		mViewMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, VIEW_MATRIX_ATTRIBUTE);

		checkGLError("After surface creation");
	}

	public void release() {

	}
	 
	/**
	 * Draws a triangle from the given vertex data.
	 *
	 * @param aTriangleBuffer The buffer containing the vertex data.
	 */
	private void drawTriangle(final FloatBuffer aTriangleBuffer)
	{
		checkGLError("Before drawing triangle");
		
	    // Pass in the position information
	    aTriangleBuffer.position(mPositionOffset);
	    GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aTriangleBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	    checkGLError("Enabling position vertex attribute array");
		
	    // Pass in the color information
	    aTriangleBuffer.position(mColorOffset);
	    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
	            mStrideBytes, aTriangleBuffer);
	 
	    GLES20.glEnableVertexAttribArray(mColorHandle);
		checkGLError("Enabling color vertex attribute array");
	    
	    GLES20.glUniformMatrix4fv(mViewMatrixHandle, 16, false, mViewMatrix, 0);
	    checkGLError("Set view matrix");
		
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
		checkGLError("Draw arrays");
	}
	
	public int loadShader(String shaderSource, int shaderType) {
		// Load in the vertex shader.
		int shaderHandle = GLES20.glCreateShader(shaderType);
		String errorInfo = null; 
		
		if (shaderHandle != 0)
		{
		    // Pass in the shader source.
		    GLES20.glShaderSource(shaderHandle, shaderSource);
		 
		    // Compile the shader.
		    GLES20.glCompileShader(shaderHandle);
		 
		    // Get the compilation status.
		    final int[] compileStatus = new int[1];
		    GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		 
		    // If the compilation failed, delete the shader.
		    if (compileStatus[0] == 0)
		    {
		    	errorInfo = GLES20.glGetShaderInfoLog(shaderHandle);
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
	
	final String vertexShader =
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
	
	final String fragmentShader =
		    "precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
		                                            // precision in the fragment shader.
		  + "varying vec4 v_Color;          \n"     // This is the color from the vertex shader interpolated across the
		                                            // triangle per fragment.
		  + "void main()                    \n"     // The entry point for our fragment shader.
		  + "{                              \n"
		  + "   gl_FragColor = v_Color;     \n"     // Pass the color directly through the pipeline.
		  + "}                              \n";
}
