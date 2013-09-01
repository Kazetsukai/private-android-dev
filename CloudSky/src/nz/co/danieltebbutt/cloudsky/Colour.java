package nz.co.danieltebbutt.cloudsky;

public class Colour {
	int _r, _g, _b, _a;
	
	public Colour(int r, int g, int b, int a) {
		_r = r;
		_g = g;
		_b = b;
		_a = a;
	}
	
	public static Colour fromCIE(float Y, float y, float x) {
		return null; // TODO
	}
}
