package nz.co.danieltebbutt.tweetsky;

public class Cloud {
	
	public Cloud(int textureId, double x, double y, double velX, double velY) {
		mTextureId = textureId;
		mXPosition = x;
		mYPosition = y;
		mXVelocity = velX;
		mYVelocity = velY;
	}
	
	private double mXPosition;
	private double mYPosition;
	private double mXVelocity;
	private double mYVelocity;
	private int mTextureId;
	
	public void update(double time) {
		mXPosition += mXVelocity * time;
		mYPosition += mYVelocity * time;
	}
	
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

	public static Cloud generateCloud(int numTextures, int minX, int maxX, int minY, int maxY) {
		return new Cloud(
				(int)(Math.random() * numTextures),
				Math.random() * (maxX - minX) + minX,
				Math.random() * (maxY - minY) + minY,
				Math.random() * 3 + 1,
				0);
				
	}
}
