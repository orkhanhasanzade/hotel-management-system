package hotel.controller.hr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.hr.Attendance;
import hotel.domain.hr.Employee;
import hotel.service.hr.AttendanceService;
import hotel.service.hr.EmployeeService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class AttendanceController 
{	
	@Autowired
	private AttendanceService attendanceService;	
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	 @Autowired
	 private MailDomainService mailDomainService;

	@GetMapping(path="/list-attendances")
	public ModelAndView showAttendances() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());
		modelAndView.addObject("attendances", attendanceService.getAttendances());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/attendance/attendanceList");
        return modelAndView;
	}
	
	@RequestMapping(path = "/attendance/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createAttendance() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		List<Employee> employees = employeeService.getEmployees();	
		modelAndView.addObject("employees", employees);
	    modelAndView.setViewName("/hr/attendance/attendanceModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/attendance/create")
	public String createAttendance(@Valid @ModelAttribute Attendance attendance) 
	{	        
		attendanceService.createAttendance(attendance);
		return "redirect:/list-attendances";
	}

	@RequestMapping(path="/attendance/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editAttendance(@PathVariable long id) 
	{		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("attendance", attendanceService.getAttendance(id));
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/hr/attendance/attendanceModalBodyEdit");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateAttendance")
	public String editAttendance(Attendance attendance)
	{
		attendanceService.updateAttendance(attendance);
		return "redirect:/list-attendances";
	}	

	@GetMapping(path="/attendance/delete/{id}")
	public String deleteAttendance(@PathVariable long id) 
	{
		if (attendanceService.deleteAttendance(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-attendances";
	}
	
	@RequestMapping(path="/attendance-report", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showAttendanceReport( @RequestParam MultiValueMap<String, String> formInputValues)
	{			
		String employeeId = null, from = null, to = null;		
		Iterator<String> mapIterator = formInputValues.keySet().iterator();
		while (mapIterator.hasNext()) 
		{
			String key = mapIterator.next();
			if(key.equals("employeeId"))
			{
				employeeId = formInputValues.get(key).get(0); 
			}
			if(key.equals("from"))
			{
				from = formInputValues.get(key).get(0); 
			}
			if(key.equals("to"))
			{
				to = formInputValues.get(key).get(0); 
			}
		}
		long employeeIdLong;
		if(!employeeId.equals("All"))
		{
			 employeeIdLong = Long.parseLong(employeeId);
		}
		else
		{
			 employeeIdLong = 0;
		}
		List<Attendance> list = attendanceService.getAttendances();
		List<Attendance> attendances = new ArrayList<>();
		int i=0;
		if(employeeIdLong == 0) 
		{
			for (i = 0; i < list.size(); i++) 
			{		
				if (list.get(i).getDate().compareTo(from) > 0 && list.get(i).getDate().compareTo(to) < 0) 
				{
					attendances.add(list.get(i));
				}
			}
			
		}
		else 
		{
			for (i = 0; i < list.size(); i++) 
			{	
				if(list.get(i).getEmployee().getId() == employeeIdLong) 
				{
					if (list.get(i).getDate().compareTo(from) > 0 && list.get(i).getDate().compareTo(to) < 0) 
					{
						attendances.add(list.get(i));
					}
				}
			}	
		}		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("attendances", attendances) ;
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
	    modelAndView.setViewName("/hr/attendance/reportContent");	
	    return modelAndView;
	}
	
}
