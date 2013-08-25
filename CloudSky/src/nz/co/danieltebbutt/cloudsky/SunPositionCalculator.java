package nz.co.danieltebbutt.cloudsky;

public class SunPositionCalculator {

	private float _standardTime;
	private float _standardMeridian;
	private float _longitude;
	private float _latitude;
	private int _dayOfYear;
	
	public SunPositionCalculator(float standardTime, int dayOfYear, float standardMeridian, float longitude, float latitude) {
		_standardTime=(standardTime);
		_dayOfYear=(dayOfYear);
		_standardMeridian=(standardMeridian);
		_longitude=(longitude);
		_latitude=(latitude);
		
		System.out.println("AngleToZenith: " + calc.angleFromSunToZenith() + " - SunAzimuth: " + calc.sunAzimuth());
	}

	/**
	 * @return The angle from the sun to the zenith in radians.
	 */
	public float angleFromSunToZenith() {
		return angleFromSunToZenith(
				solarTime(_standardTime, _dayOfYear, _standardMeridian, _longitude), 
				solarDeclination(_dayOfYear),
				_latitude
		);
	}
	
	/**
	 * @return The angle between a line to south, and then sun.
	 */
	public float sunAzimuth() {
		return sunAzimuth(
				solarTime(_standardTime, _dayOfYear, _standardMeridian, _longitude),
				solarDeclination(_dayOfYear), 
				_latitude
		);
	}
	
	/// Returns the solar time at a certain geographic place, day of year and standard time.
	public float solarTime(float standardTime, int dayOfYear, float standardMeridian, float longitude) {
		return (float)(
				standardTime + 
				0.17 * Math.sin(4 * Math.PI * (dayOfYear - 80) / 373.0) - 
				0.129 * Math.sin(2 * Math.PI * (dayOfYear - 8) / 355.0) + 
				12 * (standardMeridian - longitude) / Math.PI);
	}
	  	
  	/// Returns the solar declination. Solar declination is the angle between the rays of the sun and the
	/// plane of the earth's equator.
	public float solarDeclination(int dayOfYear) {
	    return (float)(0.4093 * Math.sin(2 * Math.PI * (dayOfYear - 81) / 368.0));
	}

	/// Returns the angle from the sun to the zenith in rad.
	private float angleFromSunToZenith(float solarTime, float solarDeclination, float latitude) {
	    return (float)(
    		Math.PI / 2 - 
    		Math.asin(
				Math.sin(latitude) * Math.sin(solarDeclination) - 
				Math.cos(latitude) * Math.cos(solarDeclination) * Math.cos(Math.PI * solarTime / 12.0)
    		)
	    );
	}

	/// Returns the azimuth of the sun in rad. Azimuth is the angle between a line to south and the sun.
	private float sunAzimuth(float solarTime, float solarDeclination, float latitude) {
	   	return -(float)(
	   		Math.atan2(
	   			(
	   				-Math.cos(solarDeclination) * Math.sin(Math.PI * solarTime / 12.0)
	   			),
	   			(
	   				Math.cos(latitude) * Math.sin(solarDeclination) - 
	   				Math.sin(latitude) * Math.cos(solarDeclination) * Math.cos(Math.PI * solarTime / 12.0)
	   			)
	   		)
	   	);
	}
	

	public void set_dayOfYear(int _dayOfYear) { this._dayOfYear = _dayOfYear; }
	public void set_longitude(float _longitude) { this._longitude = _longitude; }
	public void set_latitude(float _latitude) { this._latitude = _latitude; }
	public void set_standardMeridian(float _standardMeridian) { this._standardMeridian = _standardMeridian; }
	public void set_standardTime(float _standardTime) { this._standardTime = _standardTime;	}
	public int get_dayOfYear() { return _dayOfYear; }
	public float get_longitude() { return _longitude; }
	public float get_latitude() { return _latitude; }
	public float get_standardMeridian() { return _standardMeridian; }
	public float get_standardTime() { return _standardTime; }
}
