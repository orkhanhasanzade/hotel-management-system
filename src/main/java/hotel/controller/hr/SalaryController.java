package hotel.controller.hr;

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

import hotel.domain.hr.Salary;
import hotel.service.hr.SalaryService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class SalaryController 
{	
	@Autowired
	private SalaryService salaryService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;  
	
	@GetMapping(path="/list-salaries")
	public ModelAndView showSalaries() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("salaries", salaryService.getSalaries());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/salaries/salaryList");
		return modelAndView;
	}
	
	@RequestMapping(path = "/salary/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createSalary(Model model) 
	{		
		ModelAndView modelAndView = new ModelAndView();
	    modelAndView.setViewName("/hr/salaries/salaryModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/salary/create")
	public String createSalary(@Valid @ModelAttribute Salary salary) 
	{	        
		salaryService.createSalary(salary);
		return "redirect:/list-salaries";
	}
	
	@RequestMapping(path="/salary/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editSalary(@PathVariable long id) 
	{						
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("salary", salaryService.getSalary(id));
	    modelAndView.setViewName("/hr/salaries/salaryModalBodyEdit");	
	    return modelAndView;
	}
		
	@PostMapping(path="/updateSalary")
	public String editSalary(Salary salary)
	{
		salaryService.updateSalary(salary);
		return "redirect:/list-salaries";
	}	

	@GetMapping(path="/salary/delete/{id}")
	public String deleteSalary(@PathVariable long id) 
	{
		if (salaryService.deleteSalary(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-salaries";
	}
	
}
