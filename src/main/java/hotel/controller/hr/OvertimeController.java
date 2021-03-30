package hotel.controller.hr;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.hr.Employee;
import hotel.domain.hr.Overtime;
import hotel.service.hr.EmployeeService;
import hotel.service.hr.OvertimeService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class OvertimeController 
{	
	@Autowired
	private OvertimeService overtimeService;	
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService; 

	@GetMapping(path="/list-overtimes")
	public ModelAndView showOvertimes() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());
		modelAndView.addObject("overtimes", overtimeService.getOvertimes());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/overtimes/overtimeList");
		return modelAndView;
	}
	
	@RequestMapping(path = "/overtime/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createOvertime() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		List<Employee> employees = employeeService.getEmployees();	
		modelAndView.addObject("employees", employees);
	    modelAndView.setViewName("/hr/overtimes/overtimeModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/overtime/create")
	public String createOvertime(@Valid @ModelAttribute Overtime overtime) 
	{	        
		overtimeService.createOvertime(overtime);
		return "redirect:/list-overtimes";
	}

	@RequestMapping(path="/overtime/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editOvertime(@PathVariable long id) 
	{		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("overtime", overtimeService.getOvertime(id));
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/hr/overtimes/overtimeModalBodyEdit");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateOvertime")
	public String editOvertime(Overtime overtime)
	{
		overtimeService.updateOvertime(overtime);
		return "redirect:/list-overtimes";
	}	

	@GetMapping(path="/overtime/delete/{id}")
	public String deleteOvertime(@PathVariable long id) 
	{
		if (overtimeService.deleteOvertime(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-overtimes";
	}
	
}
