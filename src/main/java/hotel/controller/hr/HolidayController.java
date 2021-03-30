package hotel.controller.hr;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

import hotel.domain.hr.Holiday;
import hotel.service.hr.HolidayService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class HolidayController 
{	
	@Autowired
	private HolidayService holidayService;	
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService; 

	@GetMapping(path="/list-holidays")
	public ModelAndView showHolidays() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("holidays", holidayService.getHolidays());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/holidays/holidayList");
		return modelAndView;
	}
	
	@RequestMapping(path = "/holiday/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createHoliday(Model model) 
	{ 			
		ModelAndView modelAndView = new ModelAndView();
	    modelAndView.setViewName("/hr/holidays/holidayModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/holiday/create")
	public String createHoliday(@Valid @ModelAttribute Holiday holiday) 
	{	        
		holidayService.createHoliday(holiday);
		return "redirect:/list-holidays";
	}

	@RequestMapping(path="/holiday/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editHoliday(@PathVariable long id) 
	{		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("holiday", holidayService.getHoliday(id));
	    modelAndView.setViewName("/hr/holidays/holidayModalBodyEdit");	
	    return modelAndView;	
	}
	
	@RequestMapping(path="/holidays/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showHolidaysByMonth(@PathVariable long id) 
	{
		List<Holiday> holidays = new ArrayList<>();
		List<Holiday> list = holidayService.getHolidays();
		int i;
		for (i = 0; i < list.size(); i++) 
		{
		    if (list.get(i).getMonth() == id) 
		    {		    
		    	holidays.add(list.get(i));
		    }
		}		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("holidays", holidays);
		modelAndView.addObject("month", id);
	    modelAndView.setViewName("/hr/holidays/holidayContent");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateHoliday")
	public String editHoliday(Holiday holiday)
	{
		holidayService.updateHoliday(holiday);
		return "redirect:/list-holidays";
	}	

	@GetMapping(path="/holiday/delete/{id}")
	public String deleteHoliday(@PathVariable long id) 
	{
		if (holidayService.deleteHoliday(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-holidays";
	}
	
}
