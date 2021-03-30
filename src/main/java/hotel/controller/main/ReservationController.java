package hotel.controller.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.main.Guest;
import hotel.domain.main.MailDomain;
import hotel.domain.main.Reservation;
import hotel.domain.main.Room;
import hotel.service.main.GuestService;
import hotel.service.main.MailDomainService;
import hotel.service.main.ReservationService;
import hotel.service.main.RoomService;
import hotel.service.main.SettingService;
import hotel.service.security.UserService;


@Controller
public class ReservationController 
{
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private GuestService guestService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;
	
	@GetMapping(path="/list-reservations")
	public ModelAndView showReservations() 
	{
		ModelAndView modelAndView = new ModelAndView();

		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		
        modelAndView.addObject("reservations", reservationService.getReservations());
        modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		modelAndView.setViewName("/main/reservations/reservationModal/reservationList");
		return modelAndView;
	}

	@RequestMapping(path = "/reservation/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createReservation() 
	{ 			
		ModelAndView modelAndView = new ModelAndView();
		List<Room> rooms = roomService.getRooms();
		
		List<Guest> guests = guestService.getGuests();	
		List<Guest> list = guestService.getGuests();	
		List<Reservation> reservations = reservationService.getReservations();		
		long a[] = new long[guests.size()];
		for (int i = 0; i < reservations.size(); i++) 
		{
			a[i] = reservations.get(i).getGuest().getId();
			rooms.remove( reservations.get(i).getRoom());
		}	
		for (int i = 0; i < guests.size(); i++) 
		{
			for (int j = 0; j < a.length; j++) 
			{
				if(a[j] == guests.get(i).getId())
				{
					list.remove(guests.get(i));
				}
			}
		}	
		modelAndView.addObject("rooms", rooms);
		modelAndView.addObject("guests", list);
	    modelAndView.setViewName("/main/reservations/reservationModal/reservationModalBodyCreate");	
	    return modelAndView;
	}
	
	@RequestMapping(path = "/reservation/createFromRoom/{id}", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createReservationFromRoom(@PathVariable long id) 
	{
		List<Guest> guests = guestService.getGuests();	
		List<Guest> list = guestService.getGuests();	
		List<Reservation> reservations = reservationService.getReservations();		
		long a[] = new long[guests.size()];
		for (int i = 0; i < reservations.size(); i++) 
		{
			a[i] = reservations.get(i).getGuest().getId();
		}	
		for (int i = 0; i < guests.size(); i++) 
		{
			for (int j = 0; j < a.length; j++) 
			{
				if(a[j] == guests.get(i).getId())
				{
					list.remove(guests.get(i));
				}
			}
		}		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("guests", list);	
		modelAndView.addObject("room" ,  roomService.getRoom(id));
	    modelAndView.setViewName("/main/reservations/reservationModalFromRoom/reservationModalBodyCreateFromRoom");	
	    return modelAndView;
	}
	
	@PostMapping(path="/reservation/create/fromReservation")
	public String createReservationFromReservation(Reservation reservation)
	{	   
		List<Reservation> list = reservationService.getReservations();
		Room room = reservation.getRoom();
		// change status of room to booked room with making value of status field null
		room.setStatus(null);
		roomService.updateRoom(room.getId(), room);
		
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		
		reservation.setCreationDate(sdformat.format(today));
		reservationService.createReservation(reservation);
		return "redirect:/list-reservations"; 
	}
	
	@PostMapping(path="/reservation/create/fromRoom")
	public String createReservationFromRoom(Reservation reservation)
	{	   
		List<Reservation> list = reservationService.getReservations();
		Room room = reservation.getRoom();
		room.setStatus(null);
		roomService.updateRoom(room.getId(), room);
		
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		
		reservation.setCreationDate(sdformat.format(today));
		reservationService.createReservation(reservation);
        return "redirect:/list-rooms"; 
	}
	
    @RequestMapping(path = "/reservation/edit/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public Reservation editReservation(@PathVariable long id) 
	{
		return reservationService.getReservation(id);
	}

    @PostMapping(path="/updateReservation")
	public String editReservation(Reservation reservation) throws IOException 
	{
    	Reservation reservationTemp = reservationService.getReservation(reservation.getId());
    	reservationTemp.setFromDate(reservation.getFromDate());
    	reservationTemp.setToDate(reservation.getToDate());
    	reservationTemp.setStatus(reservation.getStatus());  	
    	reservationService.updateReservation(reservationTemp);
		return "redirect:/list-reservations";
	}
    
	@RequestMapping(path = "/reservation/show/{id}", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView showReservation(@PathVariable long id) 
	{ 			
		ModelAndView modelAndView = new ModelAndView();
		Reservation reservation = reservationService.getReservation(id);
		modelAndView.addObject("reservation", reservation);
	    modelAndView.setViewName("/main/reservations/reservationModal/reservationModalBodyShow");
	    return modelAndView;
	}

	@RequestMapping(path="/reservation/delete/{id}", method = RequestMethod.GET)
	public String deleteReservation(@PathVariable long id) 
	{	
		Room room = reservationService.getReservation(id).getRoom();		
		room.setStatus("on");
		roomService.updateRoom(room.getId(), room);
		if (reservationService.deleteReservation(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-reservations";
	}	
	
	
	/********************* Dashboard reservation part *********************/
	@RequestMapping(path = "/reservation/createFromDash/{id}", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createReservationFromDash(@PathVariable long id) 
	{
	    ModelAndView modelAndView = new ModelAndView();
		Room room = roomService.getRoom(id);
		modelAndView.addObject("room" , room );  
		
		Reservation reservation = new Reservation();
		reservation.setStatus("Pending");  					
		modelAndView.addObject("reservation", reservation);		
		if(room.getStatus() != null)
		{
			List<Guest> guests = guestService.getGuests();	
			List<Guest> list = guestService.getGuests();	
			List<Reservation> reservations = reservationService.getReservations();				
			long a[] = new long[reservations.size()];   
			for (int i = 0; i < reservations.size(); i++) 
			{
				a[i] = reservations.get(i).getGuest().getId();
			}	
			for (int i = 0; i < guests.size(); i++) 
			{
				for (int j = 0; j < a.length; j++) 
				{
					if(a[j] == guests.get(i).getId())
					{
						if(!reservations.get(i).getStatus().equals("Finished"))
						{   list.remove(guests.get(i));   }
					}
				}
			}
			modelAndView.addObject("guests", list);		
			
			modelAndView.setViewName("/main/reservations/reservationModalFromRoomDash/reservationModalBodyCreateFromDash");	
			return modelAndView;		
		}
		else														
		{
			List<Reservation> list = reservationService.getReservations();
			for (int i = 0; i < list.size(); i++) 
			{	    			    
			        if (list.get(i).getRoom().getId() == room.getId() ) 
			    	{  
			        	modelAndView.addObject("reservationCreated", list.get(i));
			    	}
			}
			modelAndView.setViewName("/main/reservations/reservationModalFromRoomDash/reservationModalBodyResFromDash");	
			return modelAndView;		
		}
	}
	
	@PostMapping(path="/reservation/createFromDash")
	public String createReservationFromDash(@Valid @ModelAttribute Reservation reservation)
	{	   
		List<Reservation> list = reservationService.getReservations();
		Guest guest = reservation.getGuest();
		Room room = reservation.getRoom();
		int i;
		for (i = 0; i < list.size(); i++) 
		{
		    if (list.get(i).getGuest().getId() == guest.getId() ) 
		    {		    	
		    	if (list.get(i).getRoom().getId() == room.getId() ) 
		    	{  
		    		//return "reservations/reservationError";
		    	}
		    }
		}			
		room.setStatus(null);
		roomService.updateRoom(room.getId(), room);
		
		double amount;
		double mealAmount = 0;
		if(reservation.getMeal() == "breakfast")
		{ mealAmount = 4.80; } 
		else if(reservation.getMeal() == "lunch")
			 {  mealAmount = 10.24; }		 
			 else if(reservation.getMeal() == "dinner")
			 	  {  mealAmount = 16.00; }		
		amount = mealAmount + room.getRent();
		reservation.setAmount(amount);
		
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		
		reservation.setCreationDate(sdformat.format(today));
		reservationService.createReservation(reservation);
		return "redirect:/list-dashboardrooms";
	}
	
	// Dashboard check-in part
	@RequestMapping(path = "/reservation/dashCheckin/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public ModelAndView getReservationCheckin(@PathVariable long id) 
	{
		// ModelAndView holds both to make it possible for a controller to return both model and view in a single return value.
		ModelAndView modelAndView = new ModelAndView();
		Reservation reservation = reservationService.getReservation(id);
		modelAndView.addObject("reservation", reservation );
		modelAndView.setViewName("/main/reservations/reservationModalFromRoomDash/reservationDashCheckin");	
		return modelAndView;
	}	
    @PostMapping(path="/checkin/updateDash")
	public String updateForCheckinFromDash(Reservation reservation) throws IOException 
	{
    	Reservation tempReservation = reservationService.getReservation(reservation.getId());
    	tempReservation.setStatus("Success");
    	tempReservation.setCheckinDate(reservation.getCheckinDate());
    	  	
    	reservationService.updateReservation(tempReservation);
    	return "redirect:/list-dashboardrooms";
	}
    
	// Dashboard check-out part
	@RequestMapping(path = "/reservation/dashCheckout/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public ModelAndView getReservationCheckout(@PathVariable long id) 
	{
		// ModelAndView holds both to make it possible for a controller to return both model and view in a single return value.
		ModelAndView modelAndView = new ModelAndView();
		Reservation reservation = reservationService.getReservation(id);
		modelAndView.addObject("reservation", reservation );
		modelAndView.setViewName("/main/reservations/reservationModalFromRoomDash/reservationDashCheckout");	
		return modelAndView;
	}	
    @PostMapping(path="/checkout/updateDash")
	public String updateForCheckoutFromDash(Reservation reservation) throws IOException 
	{
    	Reservation tempReservation = reservationService.getReservation(reservation.getId());
    	tempReservation.setStatus("Finished");
    	tempReservation.setCheckoutDate(reservation.getCheckoutDate());    	
    	reservationService.updateReservation(tempReservation);    	
    	return "redirect:/list-dashboardrooms";
	}

	// Dashboard payment part
	@RequestMapping(path = "/reservation/payment/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public ModelAndView getReservationPayment(@PathVariable long id) 
	{	       
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("id", id );	 
		modelAndView.setViewName("/main/reservations/reservationModalFromRoomDash/reservationDashPayment");	
		return modelAndView;
	}	
    @PostMapping(path="/paymentRes/{id}")
	public String reservationPayment(@PathVariable long id) throws IOException 
	{ 	
    	Reservation tempReservation = reservationService.getReservation(id);
    	tempReservation.setPaymentStatus(true);  	  	
    	reservationService.updateReservation(tempReservation);
    	Room room = tempReservation.getRoom();
    	room.setStatus("on");
    	roomService.updateRoom(room.getId(), room);
    	return "redirect:/list-dashboardrooms";
	}
	
    /********************* Check-In Check-Out part *********************/
	@GetMapping(path="/list-checkins")
	public ModelAndView checkinList() 
	{
		List<Reservation> reservations = reservationService.getReservations();
		List<Reservation> reservationsCheckins =  new ArrayList<>();
		for (Reservation reservation : reservations) 
		{ 		   
			  if( reservation.getStatus().equals("Pending"))	
			  {	
				  reservationsCheckins.add(reservation);
			  }
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("reservationsCheckins", reservationsCheckins );
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
        modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		modelAndView.setViewName("/main/reservations/checkIn_checkOut/checkInList");
		return modelAndView;
	}
	
	@GetMapping(path="/list-checkouts")
	public ModelAndView checkoutList() 
	{
		List<Reservation> reservations = reservationService.getReservations();
		List<Reservation> reservationsCheckouts =  new ArrayList<>();
		for (Reservation reservation : reservations) 
		{ 		   
			  if(reservation.getStatus().equals("Success"))	
			  {	
				  reservationsCheckouts.add(reservation);
			  }
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("reservationsCheckouts", reservationsCheckouts );
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
        modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		modelAndView.setViewName("/main/reservations/checkIn_checkOut/checkOutList");
		return modelAndView;
	}
	
	@GetMapping(path="/list-finished")
	public ModelAndView finishedList() 
	{
		List<Reservation> reservations = reservationService.getReservations();
		List<Reservation> reservationsFinished =  new ArrayList<>();
		for (Reservation reservation : reservations) 
		{ 		   
			  if(reservation.getStatus().equals("Finished"))	
			  {	
				  reservationsFinished.add(reservation);
			  }
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("reservationsFinished", reservationsFinished );
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
        modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());	
		modelAndView.setViewName("/main/reservations/checkIn_checkOut/finishedList");
		return modelAndView;
	}
	
	// Check-In Check-Out part
    @RequestMapping(path = "/checkin/edit/{id}", method = RequestMethod.GET) 	
    @ResponseBody
	public Reservation editForCheckin(@PathVariable long id) 
	{
		return reservationService.getReservation(id);
	}

    @PostMapping(path="/checkin/update")
	public String updateForCheckin(Reservation reservation) throws IOException 
	{
    	Reservation tempReservation = reservationService.getReservation(reservation.getId());
    	tempReservation.setStatus(reservation.getStatus());
    	tempReservation.setCheckinDate(reservation.getCheckinDate());
    	  	
    	reservationService.updateReservation(tempReservation);
		return "redirect:/list-checkins";
	}   

    @RequestMapping(path = "/checkout/edit/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public Reservation editForCheckout(@PathVariable long id) 
	{
		return reservationService.getReservation(id);
	}

    @PostMapping(path="/checkout/update")
	public String updateForCheckout(Reservation reservation) throws IOException 
	{
    	Reservation tempReservation = reservationService.getReservation(reservation.getId());
    	tempReservation.setStatus(reservation.getStatus());
    	tempReservation.setCheckoutDate(reservation.getCheckoutDate());
    	
    	reservationService.updateReservation(tempReservation);
		return "redirect:/list-checkouts";
	}
	
}
