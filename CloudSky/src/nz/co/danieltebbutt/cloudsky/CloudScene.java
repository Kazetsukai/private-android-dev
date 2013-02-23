package nz.co.danieltebbutt.cloudsky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CloudScene {

	double mMaxXVelocity = 0.006;
	double mMinXVelocity = 0.004;
	
	// A buffer of space so clouds don't appear or reappear on the screen
	double mBufferSpace = 1;
	
	ArrayList<Cloud> mClouds = new ArrayList<Cloud>();
	ArrayList<Cloud> mRemoveClouds = new ArrayList<Cloud>();
	ArrayList<Cloud> mAddClouds = new ArrayList<Cloud>();
	
	public CloudScene() {
		
		generateScene();
		
	}
	
	public void generateScene() {
		
		/*if (mTextures.size() > 0) {
			// Recreate clouds to get a good distribution over screen
			mClouds.clear();
			for (int i = 0; i < 10; i++) {
				mClouds.add(generateCloud());
			}
			
			sortClouds();
		}*/
		
	}

	public void update(double timeElapsed) {
		
		for (Cloud cloud : mClouds) {
	    	cloud.update(timeElapsed);
	    	if (getMappedX(cloud.getXPosition(), cloud.getZPosition(), 1) > 1 + mBufferSpace) {
	    		mRemoveClouds.add(cloud);
	    		Cloud newCloud = generateCloud();
	    		newCloud.setXPosition(getReverseMappedX(-mBufferSpace, newCloud.getZPosition(), 0));
	    		mAddClouds.add(newCloud);
	    	}
		}
		
	    for (Cloud removeCloud : mRemoveClouds) {
	    	mClouds.remove(removeCloud);
	    }
	    mRemoveClouds.clear();
	    
	    for (Cloud addCloud : mAddClouds) {
	    	mClouds.add(addCloud);
	    }
	    mAddClouds.clear();
	    
	    sortClouds();
	    
	}
	
	public ArrayList<Cloud> getClouds() {
		return mClouds;
	}
	

	public double getMappedX(double x, double z, double xOffset) {
		return (x - 0.5 - (xOffset - 0.5) / 20) * (5 - z * 4) + 0.5;
	}
	
	public double getReverseMappedX(double mappedX, double z, double xOffset) {
		return (mappedX - 0.5) / (5 - z * 4) + (xOffset - 0.5) / 20 + 0.5;
	}
	
	private Cloud generateCloud() {
		
		double z = (Math.random() * 0.8);
		
		return new Cloud(
				getReverseMappedX(Math.random() * mBufferSpace * 2 - mBufferSpace, z, Math.random()),
				Math.random(),
				z,
				Math.random() * (mMaxXVelocity - mMinXVelocity) + mMinXVelocity,
				0);
		
	}
	
	private void sortClouds() {
		
		Collections.sort(mClouds, new Comparator<Cloud>() {
			@Override
			public int compare(Cloud lhs, Cloud rhs) {
				return Double.compare(lhs.getZPosition(), rhs.getZPosition());
			}
		});
		Collections.reverse(mClouds);
		
	}
}
