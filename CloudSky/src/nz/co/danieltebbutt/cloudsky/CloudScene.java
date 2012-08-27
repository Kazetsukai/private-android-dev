package nz.co.danieltebbutt.cloudsky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CloudScene {

	double mMaxXVelocity = 0.006;
	double mMinXVelocity = 0.004;
	
	// A buffer of space so clouds don't appear or reappear on the screen
	double mBufferSpace = 0.005;
	
	ArrayList<Cloud> mClouds = new ArrayList<Cloud>();
	ArrayList<Cloud> mRemoveClouds = new ArrayList<Cloud>();
	ArrayList<Cloud> mAddClouds = new ArrayList<Cloud>();
	ArrayList<Texture> mTextures = new ArrayList<Texture>();
	
	public CloudScene(ArrayList<Texture> textures) {
		
		mTextures = textures;
		generateScene();
		
	}
	
	public void generateScene() {
		
		if (mTextures.size() > 0) {
			// Recreate clouds to get a good distribution over screen
			mClouds.clear();
			for (int i = 0; i < 15; i++) {
				mClouds.add(generateCloud());
			}
			
			sortClouds();
		}
		
	}

	public void update(double timeElapsed) {
		
		for (Cloud cloud : mClouds) {
	    	cloud.update(timeElapsed);
	    	if (cloud.getXPosition() > 1 + mBufferSpace) {
	    		mRemoveClouds.add(cloud);
	    		Cloud newCloud = generateCloud();
	    		newCloud.setXPosition(-mBufferSpace);
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
	
	private Cloud generateCloud() {
		
		return new Cloud(
				mTextures.get((int)(Math.random() * mTextures.size())),
				Math.random(),
				Math.random(),
				Math.random(),
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
		
	}
}
