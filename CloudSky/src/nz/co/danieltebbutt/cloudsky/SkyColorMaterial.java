package nz.co.danieltebbutt.cloudsky;

import android.opengl.GLES20;
import rajawali.materials.AMaterial;
import rajawali.materials.SimpleMaterial;

public class SkyColorMaterial extends AMaterial {

	protected static final String mVShader = 
		"#define NUM_COLORS 5.0\n" +
		"uniform mat4 uMVPMatrix;\n" +
		"uniform vec4 uColor0;\n" +
		"uniform vec4 uColor1;\n" +
		"uniform vec4 uColor2;\n" +
		"uniform vec4 uColor3;\n" +
		"uniform vec4 uColor4;\n" +
				
		"attribute vec4 aPosition;\n" +
		"attribute vec2 aTextureCoord;\n" +
		"attribute vec4 aColor;\n" +

		"varying vec4 vColor;\n" +
		
		"void main() {\n" +
		"	gl_Position = uMVPMatrix * aPosition;\n" +
		"	float pos = aPosition.x;\n" +
		"	float num = NUM_COLORS - 1.0;\n" +
		"	vColor = uColor0 * min(max(1.0 - abs(pos * num), 0.0), 1.0) +\n" +
		"			 uColor1 * min(max(1.0 - abs(pos * num - 1.0), 0.0), 1.0) +\n" +
		"			 uColor2 * min(max(1.0 - abs(pos * num - 2.0), 0.0), 1.0) +\n" +
		"			 uColor3 * min(max(1.0 - abs(pos * num - 3.0), 0.0), 1.0) +\n" +
		"			 uColor4 * min(max(1.0 - abs(pos * num - 4.0), 0.0), 1.0);\n" +
		"}\n";
		
	protected static final String mFShader = 
		"precision mediump float;\n" +
		
		"varying vec4 vColor;\n" +

		"void main() {\n" +
		"   gl_FragColor = vColor;\n" +
		"}\n";
	
	public SkyColorMaterial() {
		super(mVShader, mFShader, false);
		setShaders();
		getUniformLocation("uColor0");
		GLES20.glUniform4f(getUniformLocation("uColor0"), 1, 0, 0, 1);
		GLES20.glUniform4f(getUniformLocation("uColor1"), 1, 1, 1, 1);
		GLES20.glUniform4f(getUniformLocation("uColor2"), 1, 1, 0, 1);
		GLES20.glUniform4f(getUniformLocation("uColor3"), 1, 1, 1, 1);
		GLES20.glUniform4f(getUniformLocation("uColor4"), 1, 0, 1, 1);
	}
}
