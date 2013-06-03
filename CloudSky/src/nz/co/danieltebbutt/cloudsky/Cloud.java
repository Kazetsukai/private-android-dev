package nz.co.danieltebbutt.cloudsky;

import java.util.ArrayList;

import rajawali.materials.TextureInfo;

public class Cloud {
	
	public Cloud(TextureInfo texture, double x, double y, double z, double velX, double velY) {
		mXPosition = x;
		mYPosition = y;
		mZPosition = z;
		mXVelocity = velX;
		mYVelocity = velY;
		mTexture = texture;
	}
	
	private double mXPosition;
	private double mYPosition;
	private double mZPosition;
	private double mXVelocity;
	private double mYVelocity;
	private TextureInfo mTexture;
	
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
	public TextureInfo getTexture() {
		return mTexture;
	}
}
