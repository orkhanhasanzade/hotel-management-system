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
import hotel.domain.hr.Leave;
import hotel.service.hr.EmployeeService;
import hotel.service.hr.LeaveService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class LeaveController 
{	
	@Autowired
	private LeaveService leaveService;	
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService; 

	@GetMapping(path="/list-leaves")
	public ModelAndView showLeaves() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());
		modelAndView.addObject("leaves", leaveService.getLeaves());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/leaves/leaveList");
		return modelAndView;
	}
	
	@RequestMapping(path = "/leave/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createLeave() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		List<Employee> employees = employeeService.getEmployees();	
		modelAndView.addObject("employees", employees);
	    modelAndView.setViewName("/hr/leaves/leaveModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/leave/create")
	public String createLeave(@Valid @ModelAttribute Leave leave) 
	{	        
		leaveService.createLeave(leave);
		return "redirect:/list-leaves";
	}

	@RequestMapping(path="/leave/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editLeave(@PathVariable long id) 
	{		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("leave", leaveService.getLeave(id));
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/hr/leaves/leaveModalBodyEdit");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateLeave")
	public String editLeave(Leave leave)
	{
		leaveService.updateLeave(leave);
		return "redirect:/list-leaves";
	}	

	@GetMapping(path="/leave/delete/{id}")
	public String deleteLeave(@PathVariable long id) 
	{
		if (leaveService.deleteLeave(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-leaves";
	}
	
}
