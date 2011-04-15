package com.ghostrun.driving;

import com.ghostrun.driving.impl.DrivingDirectionsGoogleKML;

public class DrivingDirectionsFactory
{
	public static DrivingDirections createDrivingDirections ()
	{
		return new DrivingDirectionsGoogleKML ();
	}
}
