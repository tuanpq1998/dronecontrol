package hust.tuanpq.finalproject.dronecontrol.controller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import hust.tuanpq.finalproject.dronecontrol.model.Location;
import hust.tuanpq.finalproject.dronecontrol.model.Mission;
import hust.tuanpq.finalproject.dronecontrol.service.DroneService;
import hust.tuanpq.finalproject.dronecontrol.service.LocationService;
import hust.tuanpq.finalproject.dronecontrol.service.MissionService;


@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class DroneControlController {
    
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private DroneService droneService;
	
	@Autowired
	private MissionService missionService;
	
	@MessageMapping("/getLocation")
    @SendTo("/chat/location")
	public Location getLocation() {
		return locationService.getCurrentLocation();
	}
	
	
	@MessageMapping("/send")
    @SendTo("/chat/sendMessage")
    public Location sendMessage() {
        return getLocation();
    }
	
	@MessageMapping("/takeoffandland")
    public void takeoffAndLand() throws IOException {
		droneService.takeoffAndLand();
    }
	
	@MessageMapping("/uploadmission")
    public void uploadMisson(@RequestBody Mission mission) throws Exception {
		missionService.uploadMission(mission);
    }
	
	@MessageMapping("/runmission")
    public void flyMission(@RequestBody boolean isReturnToLaunch) throws IOException {
		java.lang.System.out.println(isReturnToLaunch);
        droneService.runMission(isReturnToLaunch);
    }
	
	@MessageMapping("/clearmission")
    public void clearMission()throws IOException {
        missionService.clearMission();
    }
	
	@MessageMapping("/disconnect")
	public void disconnect() {
		droneService.disconnect();
	}

	
}
