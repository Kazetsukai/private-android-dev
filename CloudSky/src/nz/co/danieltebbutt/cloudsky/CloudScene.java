package nz.co.danieltebbutt.cloudsky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rajawali.materials.TextureInfo;

import android.os.Debug;
import android.util.Log;

public class CloudScene {

	double mMaxXVelocity = 0.006;
	double mMinXVelocity = 0.004;
	double mSpeedFactor = 1;
	
	// A buffer of space so clouds don't appear or reappear on the screen
	double mBufferSpace = 1;
	
	// Cloud perspective properties
	public double mFanout = 1.7; // Difference in width between 0 and 1 depth
	public double mCloudSize = 0.5; // Cloud size as proportion of box width
	public double mScreenWidth = 0.5; // Proportion of bounding space shown on the screen
	
	ArrayList<Cloud> mClouds = new ArrayList<Cloud>();
	
	ArrayList<TextureInfo> mTextures = new ArrayList<TextureInfo>();
	
	public CloudScene(ArrayList<TextureInfo> textures) {
		
		mTextures = textures;
		
		generateScene();
		
	}
	
	public void generateScene() {
		
		if (mTextures.size() > 0) {
			// Recreate clouds to get a good distribution over screen
			mClouds.clear();
			for (int i = 0; i < 10; i++) {
				Cloud c = generateCloud();
				mClouds.add(c);
			}
			
		}
		
	}

	public boolean update(double timeElapsed) {

		//Log.d("Elapsed Time", timeElapsed + "");
		
		boolean changed = false;
		
	    int length = mClouds.size();
		for (int i = 0; i < length; i++) {
			
			Cloud cloud = mClouds.get(i);
			
			cloud.setXPosition(cloud.getXPosition() + cloud.getXVelocity() * timeElapsed);

	    	if (convertBoxToBoundingSpace((cloud.getXPosition() - mCloudSize), cloud.getZPosition()) > 1) {
	    		
	    		double mappedXPos = convertBoxToBoundingSpace(cloud.getXPosition(), cloud.getZPosition()); 
	    		System.out.println("OldPos: " + ((int)(mappedXPos*100) / 100.0));
	    		generateNewCloudPosition(cloud);
	    		
	    		mappedXPos = convertBoxToBoundingSpace(cloud.getXPosition(), cloud.getZPosition());
	    		System.out.println("NewPos: " + ((int)(mappedXPos*100) / 100.0));
	    		
	    		changed = true;
	    	}
		}
	    
		return changed;
	}

	public ArrayList<Cloud> getClouds() {
		return mClouds;
	}
	

	public double convertBoxToBoundingSpace(double x, double depth) {
		return x * fanoutAtDepth(depth);
	}
	
	public double convertBoundingSpaceToBox(double mappedX, double depth) {
		return mappedX / fanoutAtDepth(depth);
	}
	
	private double fanoutAtDepth(double depth) {
		return 1 + (1 - depth) * mFanout;
	}
	
	private Cloud generateCloud() {
		
		double z = (Math.random() * 0.8) + 0.2;
		
		return new Cloud(
				getRandomTexture(),
				convertBoundingSpaceToBox(Math.random() * 2 - 1, z),
				Math.random(),
				z,
				Math.random() * (mMaxXVelocity - mMinXVelocity) + mMinXVelocity,
				0);	
	}

	private void generateNewCloudPosition(Cloud cloud) {
		double z = (Math.random() * 0.8);
		
		cloud.setXPosition(convertBoundingSpaceToBox(-1-convertBoxToBoundingSpace(mCloudSize/2, z), z));
		cloud.setYPosition(Math.random());
		cloud.setZPosition(z);
	}
	
	private TextureInfo getRandomTexture() {
		return mTextures.get((int)(Math.random() * mTextures.size()));
	}
}
