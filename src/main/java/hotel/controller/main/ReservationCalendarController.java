package hotel.controller.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hotel.domain.main.Reservation;
import hotel.service.main.ReservationService;


@RestController
public class ReservationCalendarController 
{
	@Autowired
	private ReservationService reservationService;
	
	@GetMapping(path = "/projects-dashboard-reservations")
	public List<HashMap<String, String>> getReservationsForCalendar()
	{
		List<Reservation> list = reservationService.getReservations();
		List<HashMap<String, String>> jsonOutput = new ArrayList<>();
		int i;
		for (i = 0; i < list.size(); i++) 
		{	
			HashMap<String, String> map = new HashMap<>();
			map.put("title", list.get(i).getGuest().getName() + " " + list.get(i).getGuest().getSurname());
		    map.put("start", list.get(i).getFromDate());
		    map.put("end", list.get(i).getToDate());
		    if(list.get(i).getStatus().equals("Success"))
		    {	map.put("backgroundColor", "green");	}
		    else if(list.get(i).getStatus().equals("Pending"))
		    { 	map.put("backgroundColor", "gray"); }
		    else if(list.get(i).getStatus().equals("Finished"))
		    {   map.put("backgroundColor", "red"); }
		    jsonOutput.add(map);
		}
		return jsonOutput;		
	}
	
}
