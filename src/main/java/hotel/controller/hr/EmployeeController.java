package hotel.controller.hr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import hotel.domain.hr.Department;
import hotel.domain.hr.Designation;
import hotel.domain.hr.Employee;
import hotel.domain.hr.Leave;
import hotel.service.hr.DepartmentService;
import hotel.service.hr.DesignationService;
import hotel.service.hr.EmployeeService;
import hotel.service.hr.LeaveService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class EmployeeController 
{
	private static String UPLOADED_FOLDER = "";   // add folder
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private DesignationService designationService;
	
	@Autowired
	private LeaveService leaveService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;  

	
	@GetMapping(path="/list-employees")
	public ModelAndView showEmployees() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());	
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/employees/employeeList");
		return modelAndView;
	}

	@RequestMapping(path = "/employee/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createEmployee() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		List<Department> departments = departmentService.getDepartments();	
		List<Designation> designations = designationService.getDesignations();	
		modelAndView.addObject("departments", departments);
		modelAndView.addObject("designations", designations);
	    modelAndView.setViewName("/hr/employees/employeeModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/employee/create")
	public String createEmployee(@Valid @ModelAttribute Employee employee, BindingResult result, @RequestParam("file") MultipartFile file) throws IOException 
	{	
		if(file.getSize() != 0)
        { byte[] bytes = file.getBytes();
          Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
          Files.write(path, bytes);
        employee.setImagePath(file.getOriginalFilename());    }  
        
        employeeService.createEmployee(employee);
		return "redirect:/list-employees";
	}
	
	@GetMapping(path="/employee/show/{id}")
	public ModelAndView showEmployee(@PathVariable long id,  Model model) 
	{
		List<Leave> leaves = new ArrayList<>();
		List<Leave> list2 = leaveService.getLeaves();	
		Employee employee = employeeService.getEmployee(id);		
		int i=0;		
		for (i = 0; i < list2.size(); i++) 
		{
		    if (list2.get(i).getEmployee().getId()  == id) 
		    {		    	
		    	leaves.add(list2.get(i));		    	
		    }
		}		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employee", employee);
		modelAndView.addObject("leaves", leaves);
		
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("/hr/employees/employeeShow");
		return modelAndView;
	}
	
	@GetMapping(path="/employees/{id}")
	public ModelAndView getEmployeeByDepartmentId(@PathVariable long id) 
	{	
		List<Employee> employees = new ArrayList<>();
		List<Employee> list = employeeService.getEmployees();
		int i;
		for (i = 0; i < list.size(); i++) 
		{
		    if (list.get(i).getDepartment().getId() == id) 
		    {		    	
		    	employees.add(list.get(i));
		    }
		}				
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employees);	
	    modelAndView.setViewName("/hr/departments/departmentContent");	
	    return modelAndView;
	}
	
	@GetMapping(path="/employees/des/{id}")
	public ModelAndView getEmployeeByDesignationId(@PathVariable long id) 
	{	
		List<Employee> employees = new ArrayList<>();
		List<Employee> list = employeeService.getEmployees();
		int i;
		for (i = 0; i < list.size(); i++) 
		{
		    if (list.get(i).getDesignation().getId() == id) 
		    {		    	
		    	employees.add(list.get(i));
		    }
		}					
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employees);	
	    modelAndView.setViewName("/hr/designations/designationContent");	
	    return modelAndView;
	}
		
	@RequestMapping(path="/employee/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editEmployee(@PathVariable long id) 
	{			
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("designations", designationService.getDesignations());
		modelAndView.addObject("employee", employeeService.getEmployee(id));
	    modelAndView.setViewName("/hr/employees/employeeModalBodyEdit");	
	    return modelAndView;
	}
		
	@PostMapping(path="/updateEmployee")
	public String editEmployee(Employee employee, @RequestParam("file") MultipartFile file) throws IOException 
	{			
		employee.setDepartment(employeeService.getEmployee(employee.getId()).getDepartment());		
		if(file.getSize() != 0)
		{	
			byte[] bytes = file.getBytes();
	        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
	        Files.write(path, bytes);
	        employee.setImagePath(file.getOriginalFilename());  
		}
		employeeService.updateEmployee(employee);
		return "redirect:/list-employees";
	}	

	@GetMapping(path="/employee/delete/{id}")
	public String deleteEmployee(@PathVariable long id) 
	{
		if (employeeService.deleteEmployee(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-employees";
	}
	
}
