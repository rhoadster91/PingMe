package beit.skn.pingmeuser;

import java.io.Serializable;

public class LocationPoint implements Serializable
{
	private static final long serialVersionUID = -7750655758611441671L;
	protected double latitude;
	protected double longitude;
	protected double radius;
	protected String label;	
	
	LocationPoint(String pointlabel, double lat, double lng, double precision)
	{
		latitude = lat;
		longitude = lng;
		label = pointlabel;
		radius = precision;
	}
}
