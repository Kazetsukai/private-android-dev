package nz.co.danieltebbutt.tweetsky;

public class Cloud {
	
	public Cloud(int textureId, int x, int y, int velX, int velY) {
		mTextureId = textureId;
		mXPosition = x;
		mYPosition = y;
		mXVelocity = velX;
		mYVelocity = velY;
	}
	
	double mXPosition;
	double mYPosition;
	double mXVelocity;
	double mYVelocity;
	int mTextureId;
	
	public double getXPosition() {
		return mXPosition;
	}
	public void setXPosition(double mXPosition) {
		this.mXPosition = mXPosition;
	}
	public double getYPosition() {
		return mYPosition;
	}
	public void setYPosition(double mYPosition) {
		this.mYPosition = mYPosition;
	}
	public double getXVelocity() {
		return mXVelocity;
	}
	public void setXVelocity(double mXVelocity) {
		this.mXVelocity = mXVelocity;
	}
	public double getYVelocity() {
		return mYVelocity;
	}
	public void setYVelocity(double mYVelocity) {
		this.mYVelocity = mYVelocity;
	}
	public int getTextureId() {
		return mTextureId;
	}
	public void setTextureId(int mTextureId) {
		this.mTextureId = mTextureId;
	}
}
