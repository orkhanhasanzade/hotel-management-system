package hotel.controller.main;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.banking.Expense;
import hotel.domain.main.Guest;
import hotel.domain.main.MailDomain;
import hotel.domain.main.Reservation;
import hotel.domain.main.Room;
import hotel.domain.security.User;
import hotel.service.banking.ExpenseService;
import hotel.service.main.GuestService;
import hotel.service.main.MailDomainService;
import hotel.service.main.ReservationService;
import hotel.service.main.RoomService;
import hotel.service.main.SettingService;
import hotel.service.security.UserService;



@Controller
public class HomeController 
{	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private GuestService guestService;
	
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private ExpenseService expenseService;
	
	@Autowired
	private MailDomainService mailDomainService;  

	@RequestMapping(value={"/", "dashboard"}, method = RequestMethod.GET)
	public ModelAndView showDashboardPage(HttpServletRequest request)   
	{
		int count_of_rooms = 0;
		int count_of_guests = 0;
		int count_of_reservations = 0;
		int count_of_users = 0;
		double total_income = 0;
		List<Room> rooms = new ArrayList<Room>();
		List<Guest> guests = new ArrayList<Guest>();
		List<Reservation> reservations = new ArrayList<Reservation>();	
		List<User> users = new ArrayList<User>();
		List<Expense> expenses = new ArrayList<Expense>();
		rooms = roomService.getRooms();		
		guests = guestService.getGuests();
		reservations = reservationService.getReservations();
		users = userService.getUsers();
		expenses = expenseService.getExpenses();
		for (Room room : rooms) 
		{ 		      
	         count_of_rooms = count_of_rooms+1;
	    }
		for (Guest guest : guests) 
		{ 		      
	         count_of_guests = count_of_guests+1;
	    }
		for (Reservation reservation : reservations) 
		{ 		      
	         count_of_reservations = count_of_reservations+1;
	         total_income = total_income + reservation.getAmount();
	    }
		for (User user : users) 
		{ 		      
	         count_of_users = count_of_users+1;
	    }
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("count_of_rooms", count_of_rooms);
		modelAndView.addObject("count_of_guests", count_of_guests);
		modelAndView.addObject("count_of_reservations", count_of_reservations);
		modelAndView.addObject("count_of_users", count_of_users);
		modelAndView.addObject("total_income", total_income);
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		float amount = 0; 
		for (Expense expense : expenses) 
		{ 		      
	         amount =  expense.getAmount();
	    }
		modelAndView.addObject("amount", amount);
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("dashboard");
		return modelAndView;
	}
}
