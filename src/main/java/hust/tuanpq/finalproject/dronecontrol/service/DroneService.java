package hust.tuanpq.finalproject.dronecontrol.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mavsdk.System;
import io.mavsdk.action.Action;

@Service
public class DroneService {

	@Autowired
	public System drone;

	public void takeoffAndLand() {
		CountDownLatch latch = new CountDownLatch(1);

		drone.getAction().arm().doOnComplete(() -> java.lang.System.out.println("Arming..."))
				.doOnError(throwable -> java.lang.System.out
						.println("Failed to arm: " + ((Action.ActionException) throwable).getCode()))
				.andThen(drone.getAction().takeoff().delay(15, TimeUnit.SECONDS)
						.doOnComplete(() -> java.lang.System.out.println("Taking off..."))
						.doOnError(throwable -> java.lang.System.out
								.println("Failed to take off: " + ((Action.ActionException) throwable).getCode())))

				.andThen(drone.getAction().land().doOnComplete(() -> java.lang.System.out.println("Landing..."))
						.doOnError(throwable -> java.lang.System.out
								.println("Failed to land: " + ((Action.ActionException) throwable).getCode())))
				.subscribe(latch::countDown, throwable -> latch.countDown());

		try {
			latch.await();
		} catch (InterruptedException ignored) {
			// This is expected
		}
	}

	public void runMission(boolean isReturnToLaunch) {
		CountDownLatch latch = new CountDownLatch(1);

		drone.getMission().downloadMission().subscribe((plan) -> 
			drone.getMission().clearMission()
				.andThen(drone.getMission().setReturnToLaunchAfterMission(isReturnToLaunch).doOnComplete(() -> java.lang.System.out.println("set rtl")))
				.andThen(drone.getMission().uploadMission(plan))
				.andThen(drone.getAction().arm().onErrorComplete())
				.andThen(drone.getMission().startMission().doOnComplete(() -> java.lang.System.out.println("running mission!")))
				.andThen(drone.getMission().getMissionProgress()).subscribe(next -> {
				java.lang.System.out.println("Progress " + next.getCurrent() + "/" + next.getTotal());
				if (next.getCurrent() == next.getTotal() && !isReturnToLaunch) {
					drone.getAction().land().doOnComplete(() -> java.lang.System.out.println("land")).subscribe(latch::countDown, throwable -> latch.countDown());
				}
			}, throwable -> {
				java.lang.System.out.println("Error: " + throwable.getMessage());
				latch.countDown();
			}, () -> {
				java.lang.System.out.println("Successfully!");
				latch.countDown();
			})
		);
		
		

		try {
			latch.await();
		} catch (InterruptedException ignored) {
			// This is expected
		}
	}

	public void disconnect() {
		drone.dispose();
	}
}
