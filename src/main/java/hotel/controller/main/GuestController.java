package hotel.controller.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.main.Guest;
import hotel.domain.main.Reservation;
import hotel.domain.main.Room;
import hotel.service.main.GuestService;
import hotel.service.main.ReservationService;
import hotel.service.main.RoomService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;


@Controller
public class GuestController 
{
	private static String UPLOADED_FOLDER = "";  // add folder
	
	@Autowired
	private GuestService guestService;
	
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
	
	@GetMapping(path="/list-guests")
	public ModelAndView showGuests() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		List<Room> rooms = new ArrayList<Room>();
		rooms = roomService.getRooms();	
		modelAndView.addObject("rooms", rooms );		
		
		modelAndView.addObject("guests", guestService.getGuests());
		modelAndView.addObject("reservations", reservationService.getReservations());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/main/guests/guestList");
		return modelAndView;
	}	
	
	@PostMapping(path="/guest/create")
	public String createGuest(@Valid @ModelAttribute Guest guest, @RequestParam("file") MultipartFile file) throws IOException 
	{	
		if(file.getSize() != 0)
		{       
	        byte[] bytes = file.getBytes();
	        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
	        Files.write(path, bytes);
	        guest.setImagePath(file.getOriginalFilename());   
		}else {  guest.setImagePath("avatar.jpg");   }
        
        guestService.createGuest(guest);
		return "redirect:/list-guests";
	}
	
	@GetMapping(path="/guest/show/{id}")
	public ModelAndView showGuest(@PathVariable long id) 
	{
		double amount = 0;
		double payedAmount = 0;	
		double debtAmount = 0;
		List<Reservation> list = new ArrayList<Reservation>();
		for (Reservation temp : reservationService.getReservations()) 
		{
	           if(temp.getGuest().getId() == id)
	           {
	        	   amount = amount + temp.getRoom().getRent();
	        	   if(temp.isPaymentStatus())
	        	   { 
	        	   		payedAmount = payedAmount + temp.getRoom().getRent();
	        	   }
	        	   if(!temp.isPaymentStatus())
	        	   { 
	        	   		debtAmount = debtAmount + temp.getRoom().getRent();
	        	   }
	        	   list.add(temp);
	           }
	    }		
		Guest guest = guestService.getGuest(id);	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("amount", amount);
		modelAndView.addObject("payedAmount", payedAmount);
		modelAndView.addObject("debtAmount", debtAmount);
		modelAndView.addObject("guest", guest);
		modelAndView.addObject("reservations", list);
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("/main/guests/guestShow");
		return modelAndView;
	}
	
	@RequestMapping(path="/guest/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Guest editGuest(@PathVariable long id) 
	{
		if (guestService.getGuest(id) == null) 
		{
			throw new RuntimeException("Something went wrong");
			//return null;
			//throw new ResourceNotFoundException();
		}
		return guestService.getGuest(id);
	}
	
	@PostMapping(path="/updateGuest")
	public String editGuest(Guest guest, @RequestParam("file") MultipartFile file) throws IOException 
	{
		if(file.getSize() != 0)
		{
			byte[] bytes = file.getBytes();
	        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
	        Files.write(path, bytes);
	        guest.setImagePath(file.getOriginalFilename());  
		}else { guest.setImagePath(guestService.getGuest(guest.getId()).getImagePath()); }
        
		guestService.updateGuest(guest);
		return "redirect:/list-guests";
	}

	@GetMapping(path="/guest/delete/{id}")
	public String deleteGuest(@PathVariable long id) 
	{
		 for (Reservation temp : reservationService.getReservations()) 
		 {
	           if(temp.getGuest().getId() == id)
	           {
	        	   reservationService.deleteReservation(temp.getId());
	           }
	     }

		if (guestService.deleteGuest(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-guests";
	}
	
	@RequestMapping(path="/guests-report", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showGuestsReport( @RequestParam MultiValueMap<String, String> formInputValues)
	{			
		String name = null, surname = null, middlename = null;		
		Iterator<String> mapIterator = formInputValues.keySet().iterator();
		while (mapIterator.hasNext()) 
		{
			String key = mapIterator.next();
			if(key.equals("name"))
			{
				name = formInputValues.get(key).get(0); 
			}
			if(key.equals("surname"))
			{				
				surname = formInputValues.get(key).get(0); 
			}
			if(key.equals("middlename"))
			{
				middlename = formInputValues.get(key).get(0); 
			}
		}
		List<Guest> list = guestService.getGuests();
		List<Guest> guests = new ArrayList<>();
		int i;
		if(name.equals(""))
		{
			if(surname.equals(""))
			{				
				for (i = 0; i < list.size(); i++) 
				{	
					if(list.get(i).getMiddleName().equals(middlename))
					{	
						guests.add(list.get(i));	
					}
				}
			}
		}
		if(surname.equals(""))
		{
			if(middlename.equals(""))
			{
				for (i = 0; i < list.size(); i++) 
				{
					if(list.get(i).getName().equals(name))
					{	
						guests.add(list.get(i));	
					}
				}
			}			
		}
		if(name.equals(""))
		{
			if(middlename.equals(""))
			{
				for (i = 0; i < list.size(); i++) 
				{
					if(list.get(i).getSurname().equals(surname))
					{	
						guests.add(list.get(i));	
					}
				}				
			}			
		}
		if(name.equals(""))
		{
			if(!middlename.equals("") && !surname.equals(""))
			{
				for (i = 0; i < list.size(); i++) 
				{
					if( list.get(i).getSurname().equals(surname) && list.get(i).getMiddleName().equals(middlename) )
					{
							guests.add(list.get(i));
					}
				}
			}
		}
		if(surname.equals(""))
		{
			if(!middlename.equals("") && !name.equals(""))
			{
				for (i = 0; i < list.size(); i++) 
				{
					if( list.get(i).getName().equals(name) && list.get(i).getMiddleName().equals(middlename) )
					{	
						guests.add(list.get(i));
					}
				}
			}
		}
		if(middlename.equals(""))
		{
			if(!name.equals("") && !surname.equals(""))
			{
				for (i = 0; i < list.size(); i++) 
				{
					if( list.get(i).getName().equals(name) && list.get(i).getSurname().equals(surname) )
					{	
						guests.add(list.get(i));
					}
				}
			}
		}		
		if(!name.equals("") && !middlename.equals("") && !surname.equals(""))
		{
			for (i = 0; i < list.size(); i++) 
			{
				if( list.get(i).getName().equals(name) && list.get(i).getSurname().equals(surname) && list.get(i).getMiddleName().equals(middlename) )
				{
					guests.add(list.get(i));
				}
			}
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("guests", guests);
		modelAndView.addObject("reservations", reservationService.getReservations());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
        
	    modelAndView.setViewName("/main/report/reportContent");	
	    return modelAndView;
	}
	
}
