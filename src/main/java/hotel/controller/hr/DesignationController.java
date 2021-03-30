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

import hotel.domain.hr.Designation;
import hotel.domain.hr.Salary;
import hotel.service.hr.DesignationService;
import hotel.service.hr.SalaryService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class DesignationController 
{	
	@Autowired
	private DesignationService designationService;	
	
	@Autowired
	private SalaryService salaryService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService; 
	
	@GetMapping(path="/list-designations")
	public ModelAndView showDesignations() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("designations", designationService.getDesignations());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/designations/designationList");
		return modelAndView;
	}
	
	@RequestMapping(path = "/designation/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createDesignation() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		List<Salary> salaries = salaryService.getSalaries();	
		modelAndView.addObject("salaries", salaries);
	    modelAndView.setViewName("/hr/designations/designationModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/designation/create")
	public String createDesignation(@Valid @ModelAttribute Designation designation)
	{	 
        designationService.createDesignation(designation);
		return "redirect:/list-designations";
	}
	
	@RequestMapping(path="/designation/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editDesignation(@PathVariable long id, Model model) 
	{				
		model.addAttribute("designation", designationService.getDesignation(id));
	
		ModelAndView modelAndView = new ModelAndView();
	    modelAndView.setViewName("/hr/designations/designationModalBodyEdit");	
	    return modelAndView;
	}
		
	@PostMapping(path="/updateDesignation")
	public String editDesignation(Designation designation)
	{	
		designationService.updateDesignation(designation);
		return "redirect:/list-designations";
	}

	@GetMapping(path="/designation/delete/{id}")
	public String deleteDesignation(@PathVariable long id) 
	{
		if (designationService.deleteDesignation(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-designations";
	}
	
}
