package nz.co.danieltebbutt.cloudsky;

public class SkyColourCalculator {

	float _theta;
	float _thetaSun;
	float _gamma;
	float _turbidity;
	
	public SkyColourCalculator() {
		_theta = 0;
		_thetaSun = 0;
		_gamma = 0;
		_turbidity = 1;
	}
	
	public SkyColourCalculator(float theta, float gamma, float thetaSun, float turbidity) {
		_theta = theta;
		_thetaSun = thetaSun;
		_gamma = gamma;
		_turbidity = turbidity;
	}
	
	public void setDirections(float theta, float gamma){
		_theta = theta;
		_gamma = gamma;
	}
	
	public void setSunAngle(float thetaSun){
		_thetaSun = thetaSun;
	}
	
	public void setTurbidity(float turbidity) {
		_turbidity = turbidity;
	}
	
	public float getLuminance() {
		float t = _turbidity;
		float num = perezCalc(_theta, _gamma, 
				 0.1787f*t + -1.4630f, 
				-0.3554f*t + 0.4275f, 
				-0.0227f*t + 5.3251f, 
				 0.1206f*t + -2.5771f, 
				-0.0670f*t + 0.3703f
		);
		float den = perezCalc(0, _thetaSun, 
				 0.1787f*t + -1.4630f, 
				-0.3554f*t + 0.4275f, 
				-0.0227f*t + 5.3251f, 
				 0.1206f*t + -2.5771f, 
				-0.0670f*t + 0.3703f
		);
		
		return num/den * zenithLuminance();
	}
	
	public float getCIEx() {
		float t = _turbidity;
		return perezCalc(_theta, _gamma, 
				-0.0193f*t + -0.2592f, 
				-0.0665f*t + 0.0008f, 
				-0.0004f*t + 0.2125f, 
				-0.0641f*t + -0.8989f, 
				-0.0033f*t + 0.0452f 
		);
	}
	
	public float getCIEy() {
		float t = _turbidity;
		return perezCalc(_theta, _gamma, 
				-0.0167f*t + -0.2608f, 
				-0.0950f*t + 0.0092f, 
				-0.0079f*t + 0.2102f, 
				-0.0441f*t + -1.6537f, 
				-0.0109f*t + 0.0529f
		);
	}

	private float zenithLuminance() {
		return (float)(
				(4.0453 * _turbidity * Math.tan((4.0 / 9.0 - _turbidity / 120.0) * (Math.PI - 2 * _thetaSun))) -
				(0.2155 * _turbidity) +
				(2.4192)
				);
	}
	
	private float perezCalc(float theta, float gamma, float A, float B, float C, float D, float E) {
		return (float)(
			(
				1 + 
				A * Math.pow(Math.E, B / Math.cos(theta))
			) 
			*	
			(
				1 + 
				C * Math.pow(Math.E, D * gamma) + 
				E * Math.cos(Math.cos(gamma))
			)
		);
	}
}
