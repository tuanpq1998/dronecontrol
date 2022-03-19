package hust.tuanpq.finalproject.dronecontrol.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hust.tuanpq.finalproject.dronecontrol.model.Location;
import hust.tuanpq.finalproject.dronecontrol.model.Mission;
import io.mavsdk.System;
import io.mavsdk.mission.Mission.MissionItem;
import io.mavsdk.mission.Mission.MissionPlan;
import io.reactivex.CompletableSource;
import io.mavsdk.mission.Mission.MissionItem.CameraAction;

@Service
public class MissionService {

	@Autowired
	public System drone;
	
	public void clearMission() {
		CountDownLatch latch = new CountDownLatch(1);
		drone.getMission().clearMission()
			.doOnComplete(() -> java.lang.System.out.println("clearing"))
			.subscribe();
		try {
	      latch.await();
	    } catch (InterruptedException ignored) {
	      // This is expected
	    }
	}
	
	public void uploadMission(Mission mission) {
		List<Location> locations = mission.getLocations();
		List<MissionItem> list = new ArrayList<MissionItem>();

		for(Location l : locations) {
			list.add(new MissionItem(l.getLatitude(), l.getLongitude(), 10f, 10f, true, Float.NaN, Float.NaN,
					CameraAction.NONE, Float.NaN, 1.0, Float.NaN, Float.NaN, Float.NaN));
		}
		CountDownLatch latch = new CountDownLatch(1);
		MissionPlan plan = new MissionPlan(list);
		
		drone.getMission().setReturnToLaunchAfterMission(false)
			.andThen(drone.getMission().uploadMission(plan)
					.doOnComplete(() -> java.lang.System.out.println("Upload done!")))
			.andThen(drone.getMission().downloadMission()
					 .doOnSubscribe(disposable -> java.lang.System.out.println("Downloading mission"))
			            .doAfterSuccess(disposable -> java.lang.System.out.println("Mission downloaded")))
			.ignoreElement()
	        .andThen((CompletableSource) cs -> latch.countDown())
	        .subscribe();
		try {
	      latch.await();
	    } catch (InterruptedException ignored) {
	      // This is expected
	    }
		
	}
}
