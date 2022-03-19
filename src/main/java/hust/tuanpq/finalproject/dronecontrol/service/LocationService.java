package hust.tuanpq.finalproject.dronecontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hust.tuanpq.finalproject.dronecontrol.model.Location;
import io.mavsdk.System;
import io.mavsdk.telemetry.Telemetry.Position;
import io.reactivex.Flowable;

@Service
public class LocationService {
	@Autowired
	public System drone;
	
	public Location getCurrentLocation() {
		Flowable<Position> altitudeFlowable = drone.getTelemetry().getPosition();
		Position p = altitudeFlowable.blockingFirst();
		return new Location(p.getRelativeAltitudeM(), p.getLatitudeDeg(), p.getLongitudeDeg());
	}
}
