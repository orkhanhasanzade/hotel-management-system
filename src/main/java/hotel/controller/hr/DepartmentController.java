package hotel.controller.hr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import hotel.domain.banking.Expense;
import hotel.domain.hr.Department;
import hotel.domain.hr.Employee;
import hotel.domain.hr.Leave;
import hotel.domain.hr.Payroll;
import hotel.service.banking.ExpenseService;
import hotel.service.hr.DepartmentService;
import hotel.service.hr.EmployeeService;
import hotel.service.hr.LeaveService;
import hotel.service.hr.OvertimeService;
import hotel.service.hr.PayrollService;
import hotel.service.main.SettingService;
import hotel.service.project_management.ProjectService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class DepartmentController 
{	
	@Autowired
	private DepartmentService departmentService;	
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private LeaveService leaveService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private OvertimeService overtimeService;
	
	@Autowired
	private PayrollService payrollService;
	
	@Autowired
	private ExpenseService expenseService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;
	
	@GetMapping(path="/list-departments")
	public ModelAndView showDepartments() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("departments", departmentService.getDepartments());
		modelAndView.addObject("employees", employeeService.getEmployees());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/departments/departmentList");
		return modelAndView;
	}
		
	@GetMapping(path="/department/create")
	public String createDepartment(Model model) 
	{
		model.addAttribute("department", new Department());
		return "/hr/departments/departmentCreate";
	}

	@PostMapping(path="/department/create")
	public String createDepartment(@Valid @ModelAttribute Department department) 
	{	        
		departmentService.createDepartment(department);
		return "redirect:/list-departments";
	}

	@RequestMapping(path="/department/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Department editDepartment(@PathVariable long id) 
	{
		if (departmentService.getDepartment(id) == null) 
		{
			throw new RuntimeException("Something went wrong");		
			//return null;
			//throw new ResourceNotFoundException();
		}
		return departmentService.getDepartment(id);
	}
		
	@PostMapping(path="/updateDepartment")
	public String editDepartment(Department department)
	{
		departmentService.updateDepartment(department);
		return "redirect:/list-departments";
	}	

	@GetMapping(path="/department/delete/{id}")
	public String deleteDepartment(@PathVariable long id) 
	{
		if (departmentService.deleteDepartment(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-departments";
	}
	
	@GetMapping(path="/hr-dashboard")
	public ModelAndView hrDashboard() throws ParseException  
	{	
		DateFormat dtf = new SimpleDateFormat("MM-dd-yyyy");
		Date dateToday = new Date();
		Calendar calendarToday = new GregorianCalendar();		
		int year, monthFrom, monthTo, i, j, k, y;
		List<Leave> leaves = new ArrayList<>();
		List<Leave> leaveList = leaveService.getLeaves();
		List<Employee> employeesVacation = new ArrayList<>();
		List<Employee> employeesAtWork = new ArrayList<>();
		List<Employee> employeeList = employeeService.getEmployees();
		List<Employee> employeesBirthday = new ArrayList<>();
		String[] returnDates = new String[100];
		/* Leaves today */
		for (i = 0; i < leaveList.size(); i++) 
		{
			if (!leaveList.get(i).getCategory().equals("Vacation")) 
			{
			    if (leaveList.get(i).getFromDate().equals(dtf.format(dateToday))) 
			    {		   
				    	leaves.add(leaveList.get(i));		    	
				}
			}
		}		
		/* Employees who have birthdays today */
		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");  
		String strDate = dateFormat.format(dateToday);  
		for (j = 0; j < employeeList.size(); j++) 
		{ 
			 if ( strDate.compareTo(employeeList.get(j).getBirthdate()) == 0) 
			 {					 						 
				 	Employee employee = employeeList.get(j);
				 	String birthdate = employee.getBirthdate();
				 	
				 	SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
					Date birthDate = formatter.parse(birthdate);
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(birthDate);						     						     
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");		
					
					employee.setBirthdate( sdf.format(calendar.getTime()) );
					employeesBirthday.add(employee);
			 }
		}		
		/* Employees in vacation today */
		for (k = 0; k < leaveList.size(); k++) 
		{
			 if (leaveList.get(k).getCategory().equals("Vacation")) 
			 {				  	 	
			     SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			     
			     Date vacationBeginDate = formatter.parse(leaveList.get(k).getFromDate());  // begin date of vacation
			     Calendar calendar = new GregorianCalendar();
			     calendar.setTime(vacationBeginDate);
			     
			     Date vacationEndDate = formatter.parse(leaveList.get(k).getToDate());   // end date of vacation
			     Calendar calendar2 = new GregorianCalendar();
			     calendar2.setTime(vacationEndDate);
			    	 	
			    
			     
			     year = calendar.get(Calendar.YEAR);
			     monthFrom = calendar.get(Calendar.MONTH) + 1;
			     monthTo  = calendar2.get(Calendar.MONTH) + 1;

			     if (year == calendarToday.get(Calendar.YEAR))  
			     {
			         if (monthFrom == calendarToday.get(Calendar.MONTH) + 1 || monthTo == calendarToday.get(Calendar.MONTH) + 1)   
			    	 {  
			        	if( dateToday.after(vacationBeginDate) && dateToday.before(vacationEndDate) )		        	 			        	 			        	 
			        	{    
					    		   employeesVacation.add(leaveList.get(k).getEmployee());				    		   
					    		   y = leaveList.get(k).getEmployee().getId().intValue();				 // get id of employee
					    		   
					    		   // finding return date
					    		   SimpleDateFormat formatter2 = new SimpleDateFormat("MM-dd-yyyy");
								   Date vacationDate3 = formatter2.parse(leaveList.get(k).getToDate());  // format end date of vacation
								   Calendar calendar3 = new GregorianCalendar();
								   calendar3.setTime(vacationDate3);						     						     
								   SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		
								   returnDates[y] = sdf.format(calendar3.getTime());                     // add end date to array
			        	}
			    	 }
			         
			      }	    			      
			   }    	
		}		
		for (j = 0; j < employeeList.size(); j++) 		 
		{	
	    		if(!employeesVacation.contains(employeeList.get(j))) 
	    		{
	    			employeesAtWork.add(employeeList.get(j));
	    		}	    				    		
		}
		double salaries = 0;
		int z;
		List<Payroll> payrolls =  payrollService.getPayrolls();
		for (z = 0; z < payrolls.size(); z++) 
		{
			salaries = salaries + payrolls.get(z).getGrossSalary();
		}	
		double expenses = 0;
		z = 0;
		List<Expense> expenseList =  expenseService.getExpenses();
		for (z = 0; z < expenseList.size(); z++) 
		{
			if( expenseList.get(z).getStatus() == "Paid")
			{
				expenses = expenses + expenseList.get(z).getAmount();
			}
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("leaves", leaves);											  // employees that are have leave today
		modelAndView.addObject("employeesBirthday", employeesBirthday);						  // employees that have birthday today
		modelAndView.addObject("employeesVacation", employeesVacation);						  // employees that are in vacation today	
		modelAndView.addObject("returnDates", returnDates);									  // return dates of employees's that are in vacation today		
		modelAndView.addObject("departmentCount", departmentService.getDepartments().size()); // department count
		modelAndView.addObject("employeeCount", employeeService.getEmployees().size());		  // employee count	
		modelAndView.addObject("employeesAtWorkCount",  employeesAtWork.size()); 			  // present today
		modelAndView.addObject("empInVacCount",  employeesVacation.size()); 				  // employees in vacation count
		modelAndView.addObject("leavesCount",  leaves.size()); 							 	  // leaves today
		modelAndView.addObject("expenses",  expenses);   									  // total expences
		modelAndView.addObject("salaries",  salaries);     									  // total salaries paid		
		modelAndView.addObject("overtimeCount",  overtimeService.getOvertimes().size());      // overtime count
		modelAndView.addObject("projectCount",  projectService.getProjects().size());    	  // project count
		modelAndView.addObject("birthdayCount", employeesBirthday.size());  				  // birthday today count
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
	    modelAndView.setViewName("/hr/dashboard/dashboard");
		return modelAndView;
	}
}
