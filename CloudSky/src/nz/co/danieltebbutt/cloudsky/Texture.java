package nz.co.danieltebbutt.cloudsky;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {
	
	private Bitmap mBitmap;
	private int mId = -1;
	private int mWidth = 0;
	private int mHeight = 0;

	public Texture(Bitmap bitmap){
		mBitmap = bitmap;
	}
	
	public void generateGlTexture() {

		int[] textureId = new int[1];
		GLES20.glGenTextures(1, textureId, 0);
		mId = textureId[0];
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mId);
    	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
    	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
		mWidth = mBitmap.getWidth();
		mHeight = mBitmap.getHeight();
		
	}

	public int getId() {
		return mId;
	}
	
	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

}
