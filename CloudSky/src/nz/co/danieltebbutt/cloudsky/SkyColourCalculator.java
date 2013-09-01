package nz.co.danieltebbutt.cloudsky;

public class SkyColourCalculator {
	public SkyColourCalculator() {
		
	}
	
	private float perezCalc(float theta, float gamma, float turbidity) {
		float T = turbidity;
		
		return (float)(
			(
				1 + 
				A(T) * Math.pow(Math.E, B(T) / Math.cos(theta))
			) 
			*	
			(
				1 + 
				C(T) * Math.pow(Math.E, D(T) * gamma) + 
				E(T) * Math.cos(Math.cos(gamma))
			)
		);
	}
	
	private float A(float turbidity) { return 1; }
	private float B(float turbidity) { return 1; }
	private float C(float turbidity) { return 1; }
	private float D(float turbidity) { return 1; }
	private float E(float turbidity) { return 1; }
}
