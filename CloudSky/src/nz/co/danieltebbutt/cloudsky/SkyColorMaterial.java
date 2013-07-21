package nz.co.danieltebbutt.cloudsky;

import rajawali.materials.AMaterial;
import rajawali.materials.SimpleMaterial;

public class SkyColorMaterial extends AMaterial {

	protected static final String mVShader = 
		"uniform mat4 uMVPMatrix;\n" +
		"uniform vec3 uColor0;\n" +
		"uniform vec3 uColor1;\n" +
		"uniform vec3 uColor2;\n" +
		"uniform vec3 uColor3;\n" +
		"uniform vec3 uColor4;\n" +
				
		"attribute vec4 aPosition;\n" +
		"attribute vec2 aTextureCoord;\n" +
		"attribute vec4 aColor;\n" +

		"varying vec4 vColor;\n" +
		
		"void main() {\n" +
		"	gl_Position = uMVPMatrix * aPosition;\n" +
		"	vColor = ;\n" +
		"}\n";
		
	protected static final String mFShader = 
		"precision mediump float;\n" +
		
		"varying vec4 vColor;\n" +

		"void main() {\n" +
		"   gl_FragColor = vColor\n" +
		"}\n";
	
	public SkyColorMaterial() {
		super(mVShader, mFShader, false);
		setShaders();
	}
}
