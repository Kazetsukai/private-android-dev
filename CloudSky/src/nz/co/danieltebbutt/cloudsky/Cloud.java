package nz.co.danieltebbutt.cloudsky;

public class Cloud {
	
	public Cloud(int textureId, double x, double y, double z, double velX, double velY) {
		mTextureId = textureId;
		mXPosition = x;
		mYPosition = y;
		mZPosition = z;
		mXVelocity = velX;
		mYVelocity = velY;
	}
	
	private double mXPosition;
	private double mYPosition;
	private double mZPosition;
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
	public double getZPosition() {
		return mZPosition;
	}
	public void setZPosition(double mZPosition) {
		this.mZPosition = mZPosition;
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

	public static Cloud generateCloud(int numTextures, double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
		return new Cloud(
				(int)(Math.random() * numTextures),
				Math.random() * (maxX - minX) + minX,
				Math.random() * (maxY - minY) + minY,
				Math.random() * (maxZ - minZ) + minZ,
				Math.random() * 3 + 1,
				0);
				
	}
}
