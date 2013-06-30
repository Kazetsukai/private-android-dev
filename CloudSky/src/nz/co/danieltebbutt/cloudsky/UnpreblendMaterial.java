package nz.co.danieltebbutt.cloudsky;

import rajawali.materials.AMaterial;
import rajawali.materials.SimpleMaterial;

public class UnpreblendMaterial extends AMaterial {

	protected static final String mVShader = 
			"uniform mat4 uMVPMatrix;\n" +

			"attribute vec4 aPosition;\n" +
			"attribute vec2 aTextureCoord;\n" +
			"attribute vec4 aColor;\n" +

			"varying vec2 vTextureCoord;\n" +
			
			"void main() {\n" +
			"	gl_Position = uMVPMatrix * aPosition;\n" +
			"	vTextureCoord = aTextureCoord;\n" +
			"}\n";
		
		protected static final String mFShader = 
			"precision mediump float;\n" +

			"varying float fZPos;\n" +		
			"varying vec2 vTextureCoord;\n" +
			"uniform sampler2D uDiffuseTexture;\n" +

			"void main() {\n" +
			"	gl_FragColor = texture2D(uDiffuseTexture, vTextureCoord);\n" +
			"   gl_FragColor.rgb = (gl_FragColor.rgb * 0.9) / gl_FragColor.a;\n" +
			"}\n";
		
		public UnpreblendMaterial() {
			super(mVShader, mFShader, false);
			setShaders();
		}
}
