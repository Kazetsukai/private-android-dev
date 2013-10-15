package nz.co.danieltebbutt.cloudsky;

import android.opengl.GLES20;
import rajawali.BaseObject3D;

public class SkyPlane extends BaseObject3D {

	protected float mWidth;
	protected float mHeight;
	protected int mSegmentsW;
	protected int mSegmentsH;
	
	float[] _vertices;
	float[] _textureCoords;
	float[] _normals;
	float[] _colors;
	int[] _indices;
	
	SunPositionCalculator _calc;
	SkyColourCalculator _colourCalc;
	
	public SkyPlane(float width, float height, int segmentsW, int segmentsH) {
		super();
		mWidth = width;
		mHeight = height;
		mSegmentsW = segmentsW;
		mSegmentsH = segmentsH;
		init();
	}
	
	private void init() {
		_calc = new SunPositionCalculator(12, 235, 12, 175.2833f, -37.7833f);
		_colourCalc = new SkyColourCalculator(0, 0, _calc.angleFromSunToZenith(), 2);
		
		int i, j;
		int numVertices = (mSegmentsW + 1) * (mSegmentsH + 1);
		_vertices = new float[numVertices * 3];
		_textureCoords = new float[numVertices * 2];
		_normals = new float[numVertices * 3];
		_colors = new float[numVertices * 4];
		_indices = new int[mSegmentsW * mSegmentsH * 6];
		int vertexCount = 0;
		int texCoordCount = 0;
	
		for (i = 0; i <= mSegmentsW; i++) {
			for (j = 0; j <= mSegmentsH; j++) {
				_vertices[vertexCount] = ((float) i / (float) mSegmentsW - 0.5f) * mWidth;
				_vertices[vertexCount + 1] = ((float) j / (float) mSegmentsH - 0.5f) * mHeight;
				_vertices[vertexCount + 2] = 0;
	
				_textureCoords[texCoordCount++] = (float) i / (float) mSegmentsW;
				_textureCoords[texCoordCount++] = 1.0f - (float) j / (float) mSegmentsH;
	
				_normals[vertexCount] = 0;
				_normals[vertexCount + 1] = 0;
				_normals[vertexCount + 2] = 1;
	
				vertexCount += 3;
			}
		}
	
		int colspan = mSegmentsH + 1;
		int indexCount = 0;
	
		for (int col = 0; col < mSegmentsW; col++) {
			for (int row = 0; row < mSegmentsH; row++) {
				int ul = col * colspan + row;
				int ll = ul + 1;
				int ur = (col + 1) * colspan + row;
				int lr = ur + 1;
	
				_indices[indexCount++] = (int) ul;
				_indices[indexCount++] = (int) ur;
				_indices[indexCount++] = (int) lr;
	
				_indices[indexCount++] = (int) ul;
				_indices[indexCount++] = (int) lr;
				_indices[indexCount++] = (int) ll;
			}
		}
	
		setColors(1);
		
		setData(_vertices, _normals, _textureCoords, _colors, _indices);
		
		// Dynamic color buffer
		getGeometry().changeBufferUsage(getGeometry().getColorBufferInfo(), GLES20.GL_DYNAMIC_DRAW);
	}

	public void setColors(float turbidity) {
		
		int i = 0;
		int j = 0;
		int colNum = 0;
		
		_colourCalc.setTurbidity(turbidity);

		for (i = 0; i <= mSegmentsW; i++) {
			for (j = 0; j <= mSegmentsH; j++) {
				
				float theta = j / (float)mSegmentsH;
				float gamma = (i - mSegmentsW / 3) / ((float)mSegmentsW * 3);
				_colourCalc.setDirections(theta, gamma);
				
				float lum = _colourCalc.getLuminance() / 60.f;
				if (lum >= 1.0f) lum = 1.0f;
				if (lum >= 1.0f) lum = 1.0f;
				
				_colors[colNum] = lum;
				_colors[colNum + 1] = lum;
				_colors[colNum + 2] = lum;
				_colors[colNum + 3] = 1.0f;
				
				colNum += 4;
			}
		}
	}
	
	public void updateColors() {
		mGeometry.setColors(_colors);
	}
}
