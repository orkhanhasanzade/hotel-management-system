package hotel.controller.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import hotel.domain.main.MailDomain;
import hotel.domain.main.Reservation;
import hotel.domain.main.Room;
import hotel.service.main.MailDomainService;
import hotel.service.main.ReservationService;
import hotel.service.main.RoomService;
import hotel.service.main.SettingService;
import hotel.service.security.UserService;


@Controller
public class RoomController 
{
	private static String UPLOADED_FOLDER = "";   // add folder
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;
	
	@RequestMapping(value="/calendar", method = RequestMethod.GET)
	public ModelAndView newTask()
	{
		List<Room> rooms = new ArrayList<Room>();
		rooms = roomService.getRooms();	
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("rooms", rooms );
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		
		modelAndView.addObject("size", mailsList.size() );
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();	
		modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath() );
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		
		modelAndView.setViewName("/main/calendar/calendar");
		return modelAndView;
	}	
	
	@GetMapping(path="/list-rooms")
	public ModelAndView showRooms() 
	{
		List<Room> rooms = new ArrayList<Room>();
		rooms = roomService.getRooms();	
		
		ModelAndView modelAndView = new ModelAndView();		
		modelAndView.addObject("rooms", rooms );
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
        modelAndView.setViewName("/main/rooms/roomList");
        return modelAndView;
	}
	
	@GetMapping(path="/list-dashboardrooms")
	public ModelAndView showDashboardRooms() 
	{
		List<Room> rooms = new ArrayList<Room>();
		rooms = roomService.getRooms();	
		ModelAndView modelAndView = new ModelAndView();	
		
		modelAndView.addObject("rooms", rooms );
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("/main/rooms/dashboard/roomDashboardList");
	    return modelAndView;
	}
	
	@GetMapping(path="/list-avaliablerooms")
	public ModelAndView showAvaliableRooms() 
	{
		List<Room> rooms = roomService.getRooms();
		List<Room> avaliableRooms = new ArrayList<Room>();
		for (Room room : rooms) 
		{ 		      
			  if(room.getStatus() != null)	
			  {	  if(room.getStatus().equals("on"))
				  {
					  avaliableRooms.add(room);
				  }		
			  }
	    }		
		ModelAndView modelAndView = new ModelAndView();	
		modelAndView.addObject("avaliableRooms", avaliableRooms);
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("/main/rooms/roomAvaliableList");
	    return modelAndView;
	}
	
	@GetMapping(path="/list-bookedrooms")
	public ModelAndView showBookedRooms() 
	{
		List<Room> rooms = roomService.getRooms();
		List<Room> bookedRooms = new ArrayList<Room>();
		for (Room room : rooms) 
		{ 		  
	          if(room.getStatus() == null)		
		      {	 
		           bookedRooms.add(room);
		      }
	    }		
		ModelAndView modelAndView = new ModelAndView();	
		modelAndView.addObject("bookedRooms", bookedRooms);
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("/main/rooms/roomBookedList");
	    return modelAndView;
	}

	@PostMapping(path="/room/create")
	public String createRoom(Room room) throws IOException
	{	    	
        roomService.createRoom(room);
		return "redirect:/list-rooms";
	}
    
    @RequestMapping(path = "/room/edit/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public Room editRoom(@PathVariable long id) 
	{
		return roomService.getRoom(id);
	}

    @PostMapping(path="/updateRoom")
	public String editRoom(Room room) throws IOException 
	{
	    roomService.updateRoom(room.getId() ,room);
	    
	    List<Reservation> reservations = reservationService.getReservations();
	    for (Reservation reservation : reservations) 
		{ 		      
			  if(reservation.getRoom().getId() == room.getId())	
			  {	  
				  reservation.setStatus("Finished");
				  reservationService.updateReservation(reservation);
			  }
	    }
	    
		return "redirect:/list-rooms";
	}

	@GetMapping(path="/room/delete/{id}")
	public String deleteRoom(@PathVariable long id) 
	{		
		 for (Reservation temp : reservationService.getReservations()) 
		 {
	           if(temp.getRoom().getId() == id)
	           {
	        	   reservationService.deleteReservation(temp.getId());
	           }
	     }
		 roomService.deleteRoom(id);			
		 return "redirect:/list-rooms";
	}
    
    @RequestMapping(path = "/room/showRoom/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public ModelAndView showRoomDetails(@PathVariable long id) 
	{ 			
		ModelAndView modelAndView = new ModelAndView();
		Room room = roomService.getRoom(id);
		modelAndView.addObject("room", room);
	    modelAndView.setViewName("/main/rooms/roomModalBody");
	    return modelAndView;
	}
    
	@GetMapping(path="/list-housekeeping")
	public ModelAndView showHousekeeping() 
	{
		List<Room> rooms = roomService.getRooms();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("rooms", rooms );
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
        modelAndView.setViewName("/main/rooms/housekeeping/housekeepingList");
		return modelAndView;
	}
	
    @RequestMapping(path = "/housekeeping/edit/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public Room editHousekeeping(@PathVariable long id) 
	{
		return roomService.getRoom(id);
	}

    @PostMapping(path="/housekeeping/update")
	public String editHousekeeping(Room room) throws IOException 
	{
    	Room tempRoom = roomService.getRoom(room.getId());
    	tempRoom.setReadiness(room.getReadiness());
	    roomService.updateRoom(tempRoom.getId() ,tempRoom);
		return "redirect:/list-housekeeping";
	}

}


