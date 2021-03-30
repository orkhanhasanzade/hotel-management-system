package hotel.controller.hr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

import java.time.YearMonth;
import hotel.domain.hr.Payroll;
import hotel.domain.hr.Employee;
import hotel.domain.hr.Overtime;
import hotel.service.hr.PayrollService;
import hotel.service.main.SettingService;
import hotel.service.hr.EmployeeService;
import hotel.service.hr.OvertimeService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class PayrollController
{	
	@Autowired
	private PayrollService payrollService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private OvertimeService overtimeService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;  
	
	@GetMapping(path="/list-payrolls")
	public ModelAndView showPayrolls() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("payrolls", payrollService.getPayrolls());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/hr/payroll/payrollList");
		return modelAndView;
	}
	
	@RequestMapping(path = "/payroll/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createPayroll() 
	{ 			
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/hr/payroll/payrollModalBodyCreate");
	    return modelAndView;
	}

	@PostMapping(path="/payroll/create")
	public String createPayroll(@Valid @ModelAttribute Payroll payroll) 
	{	        
        payrollService.createPayroll(payroll);
		return "redirect:/list-payrolls";
	}

	@RequestMapping(path="/payroll/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editPayroll(@PathVariable long id) 
	{	
		int month = payrollService.getPayroll(id).getMonth();
		String smonth = "";
		switch (month) 
		{
	        case 1:
	        	smonth = "January";
	        	break;
	        case 2:
	        	smonth = "February";
	        	break;
	        case 3:
	        	smonth = "March";
	        	break;
	        case 4:
	        	smonth = "April";
	        	break;
	        case 5:
	        	smonth = "May";
	        	break;
	        case 6:
	        	smonth = "June";
	        	break;
	        case 7:
	        	smonth = "July";
	        	break;
	        case 8:
	        	smonth = "August";
	        	break;
	        case 9:
	        	smonth = "September";
	        	break;
	        case 10:
	        	smonth = "October";
	        	break;
	        case 11:
	        	smonth = "November";
	        	break;
	        case 12:
	        	smonth = "December";
	        	break;	
	    }		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("month", smonth);
		modelAndView.addObject("payroll", payrollService.getPayroll(id));
	    modelAndView.setViewName("/hr/payroll/payrollModalBodyEdit");	
	    return modelAndView;
	}
		
	@PostMapping(path="/updatePayroll")
	public String editPayroll(Payroll payroll)
	{	
		payroll.setMonth(payrollService.getPayroll(payroll.getId()).getMonth());
		payroll.setEmployee(payrollService.getPayroll(payroll.getId()).getEmployee());
		payrollService.updatePayroll(payroll);
		return "redirect:/list-payrolls";
	}	
	
	@GetMapping(path="/payrolls/{year}/{month}")
	public ModelAndView getPayrollByDateId(@PathVariable("year") int year, @PathVariable("month") int month) 
	{	
		List<Payroll> payrolls = new ArrayList<>();
		List<Payroll> list = payrollService.getPayrolls();
		int i;
		String smonth = "";
		for (i = 0; i < list.size(); i++) 
		{		 					
		    if (list.get(i).getYear() == year && list.get(i).getMonth() == month ) 
		    {		    	
		    	payrolls.add(list.get(i));
		    }
		}			
		switch (month) 
		{
	        case 1:
	        	smonth = "January";
	        	break;
	        case 2:
	        	smonth = "February";
	        	break;
	        case 3:
	        	smonth = "March";
	        	break;
	        case 4:
	        	smonth = "April";
	        	break;
	        case 5:
	        	smonth = "May";
	        	break;
	        case 6:
	        	smonth = "June";
	        	break;
	        case 7:
	        	smonth = "July";
	        	break;
	        case 8:
	        	smonth = "August";
	        	break;
	        case 9:
	        	smonth = "September";
	        	break;
	        case 10:
	        	smonth = "October";
	        	break;
	        case 11:
	        	smonth = "November";
	        	break;
	        case 12:
	        	smonth = "December";
	        	break;	
	    }		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("payrolls", payrolls);
		modelAndView.addObject("year", year);
		modelAndView.addObject("month", smonth);
	    modelAndView.setViewName("/hr/payroll/payrollContent");	
	    return modelAndView;
	}	
	
	@GetMapping(path="/payroll/delete/{id}")
	public String deletePayroll(@PathVariable long id) 
	{
		if (payrollService.deletePayroll(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-payrolls";
	}
	
	@GetMapping(path="/generate/{year}/{month}")
	public ModelAndView generatePayrollByDate(@PathVariable("year") int year, @PathVariable("month") int month) 
	{	
		List<Employee> list = employeeService.getEmployees();
		List<Overtime> overtimeList  = overtimeService.getOvertimes();
		int i, j, k, m, grossSalary , amountPerHour;
		int day;
		int countOfWeekDays = 0;
		int countOfWeekends = 0;
		int hoursOfOvertime = 0;
		int hoursOfMonth = 0;
		int hoursOfWeekdays = 0;
		int deductionAmount = 0;
		int netAmount = 0;
		int continueId = 0;
		String status = "";	
		
		List<Payroll> payrollList1 = payrollService.getPayrolls();
		for (m = 0; m < payrollList1.size(); m++) 
		{
			if(payrollList1.get(m).getYear() == year)				
			{	
				if(payrollList1.get(m).getMonth() == month)
				{
					continueId=1;
				}				
			}
		}

		if(continueId == 0)
		{
			for (i = 0; i < list.size(); i++) 
			{	 		
				 Payroll payroll = new Payroll();				 
				 payroll.setYear(year);
				 payroll.setMonth(month);				 
				 
				 /* ------------------- GROSS SALARY ------------------- */			 
				 amountPerHour = list.get(i).getDesignation().getSalary().getAmountPerHour();  // Amount that employee get per hour
				 			
				 YearMonth yearMonth = YearMonth.of(year, month);
				 int lengthOfMonth = yearMonth.lengthOfMonth();	
				 
				 for (j=1; j<=lengthOfMonth; j++) 
				 {  
					 Calendar calendar = new GregorianCalendar(year, month, j);  // 7->SATURDAY,1->SUNDAY
					 day = calendar.get(Calendar.DAY_OF_WEEK);		
					 if (day == 7 || day == 1)
					 {				
						 countOfWeekends = countOfWeekends + 1;
					 }
					 else
					 {
						 countOfWeekDays = countOfWeekDays + 1;
					 }			 
				 }
				 hoursOfWeekdays = countOfWeekDays*8;	
				 			 
				 
				 /* ------------------- OVERTIMES ------------------- */	
				 for (k = 0; k < overtimeList.size(); k++) 
				 {
					    if (overtimeList.get(k).getEmployee().getId()  == i) 
					    {		    	
					    	 hoursOfOvertime = hoursOfOvertime + overtimeList.get(k).getHours();		
					    }
				 }	
				 hoursOfOvertime = hoursOfOvertime * 2;					 
				 
				 /* ------------------- BONUS(only in December) ------------------- */
				 if(month == 12)
				 {
					 hoursOfWeekdays = hoursOfWeekdays * 2;
				 }			 
				 hoursOfMonth = hoursOfWeekdays + hoursOfOvertime;
				 grossSalary = amountPerHour * hoursOfMonth;
				 payroll.setGrossSalary(grossSalary);
				 
				 
				 /* ------------------- DEDUCTIONS ------------------- */	
				 deductionAmount = grossSalary * 18 / 100;			 
				 payroll.setDeductions(deductionAmount);
				 
				 
				 /* ------------------- NET SALARY ------------------- */	
				 netAmount = grossSalary - deductionAmount;
				 payroll.setNetSalary(netAmount);			 			 
				 payroll.setStatus("Unpaid");
				 payroll.setEmployee(list.get(i));		 		    	
				 payrollService.createPayroll(payroll);
			}	
			
			List<Payroll> payrollList2 = payrollService.getPayrolls();
			for (m = 0; m < payrollList2.size(); m++) 
			{
				if(payrollList2.get(m).getYear() == year)				
				{	
					if(payrollList2.get(m).getMonth() == month)
					{
						status = "Success";
					}			
				}
			}	
		}

		if(continueId == 1)
		{
			status = "Unsuccessful";
		}	
		ModelAndView modelAndView = new ModelAndView();	
		modelAndView.addObject("status", status );
		modelAndView.addObject("year", year );
		modelAndView.addObject("month", month );
		
	    modelAndView.setViewName("/hr/payroll/payrollStatus");	
	    return modelAndView;
	}	
}
