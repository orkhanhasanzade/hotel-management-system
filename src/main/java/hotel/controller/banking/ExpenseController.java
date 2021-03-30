package hotel.controller.banking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import hotel.domain.banking.Account;
import hotel.domain.banking.Expense;
import hotel.domain.banking.Transaction;
import hotel.service.banking.AccountService;
import hotel.service.banking.ExpenseService;
import hotel.service.banking.TransactionService;
import hotel.service.hr.DepartmentService;
import hotel.service.hr.EmployeeService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class ExpenseController 
{	
	@Autowired
	private ExpenseService expenseService;	
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService; 

	@GetMapping(path="/list-expenses")
	public ModelAndView showExpenses() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());
		modelAndView.addObject("departments", departmentService.getDepartments());
		modelAndView.addObject("expenses", expenseService.getExpenses());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());   	
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/banking/expenses/expenseList");
		return modelAndView;
	}
	
	@RequestMapping(path="/expenses/all", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showAllExpenses() 
	{		
		ModelAndView modelAndView = new ModelAndView();	
		modelAndView.addObject("chosenExpenses", expenseService.getExpenses());
	    modelAndView.setViewName("/banking/expenses/expenseContent");	
	    return modelAndView;	
	}	
	
	@RequestMapping(path="/expenses/pending", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showPendingExpenses() 
	{
		List<Expense> expenses = expenseService.getExpenses();
		List<Expense> chosenExpenses = new ArrayList<>();
		int i;
		for (i = 0; i < expenses.size(); i++) 
		{
			if(expenses.get(i).getStatus().equals("Submitted"))
			{			
				chosenExpenses.add(expenses.get(i));
			}		
		}		
		ModelAndView modelAndView = new ModelAndView();		
		modelAndView.addObject("chosenExpenses", chosenExpenses);		
	    modelAndView.setViewName("/banking/expenses/expenseContent");	
	    return modelAndView;	
	}
	
	@RequestMapping(path="/expenses/approved", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showApprovedExpenses() 
	{
		List<Expense> expenses = expenseService.getExpenses();
		List<Expense> chosenExpenses = new ArrayList<>();
		int i;
		for (i = 0; i < expenses.size(); i++) 
		{
			if(expenses.get(i).getStatus().equals("Approved"))
			{			
				chosenExpenses.add(expenses.get(i));
			}		
		}		
		ModelAndView modelAndView = new ModelAndView();	
		modelAndView.addObject("chosenExpenses", chosenExpenses);	
	    modelAndView.setViewName("/banking/expenses/expenseContent");	
	    return modelAndView;	
	}
	
	
	@RequestMapping(path="/expenses/declined", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showDeclinedExpenses() 
	{
		List<Expense> expenses = expenseService.getExpenses();
		List<Expense> chosenExpenses = new ArrayList<>();
		int i;
		for (i = 0; i < expenses.size(); i++) 
		{
			if(expenses.get(i).getStatus().equals("Declined"))
			{			
				chosenExpenses.add(expenses.get(i));
			}		
		}		
		ModelAndView modelAndView = new ModelAndView();		
		modelAndView.addObject("chosenExpenses", chosenExpenses);	
	    modelAndView.setViewName("/banking/expenses/expenseContent");	
	    return modelAndView;	
	}
	
	@RequestMapping(path="/expenses/paid", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showPaidExpenses() 
	{
		List<Expense> expenses = expenseService.getExpenses();
		List<Expense> chosenExpenses = new ArrayList<>();
		int i;
		for (i = 0; i < expenses.size(); i++) 
		{
			if(expenses.get(i).getStatus().equals("Paid"))
			{			
				chosenExpenses.add(expenses.get(i));
			}		
		}		
		ModelAndView modelAndView = new ModelAndView();		
		modelAndView.addObject("chosenExpenses", chosenExpenses);		
	    modelAndView.setViewName("/banking/expenses/expenseContent");	
	    return modelAndView;	
	}
	
	@RequestMapping(path = "/expense/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createExpense() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("departments", departmentService.getDepartments());
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/banking/expenses/expenseModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/expense/create")
	public String createExpense(@Valid @ModelAttribute Expense expense)
	{	     
		List<Account> accounts = accountService.getAccounts();
		int i;
		float accountBalance;
		Account account = new Account();
		for (i = 0; i < accounts.size(); i++) 
		{
			if(accounts.get(i).getEmployee().getId() == expense.getEmployee().getId())
			{
				account = accounts.get(i);
			}
		}	
		accountBalance = account.getBalance() - expense.getAmount();
		account.setBalance(accountBalance);		
		expense.setEmpAccountBalance(accountBalance);		
		accountService.updateAccount(account);
		
		Date date = new Date(); 
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		expense.setDate(formatter.format(date));
		
		expenseService.createExpense(expense);
		return "redirect:/list-expenses";
	}

	@RequestMapping(path="/expense/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editExpense(@PathVariable long id) 
	{		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("expense", expenseService.getExpense(id));
		modelAndView.addObject("departments", departmentService.getDepartments());
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/banking/expenses/expenseModalBodyEdit");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateExpense")
	public String editExpense(Expense expense)
	{
		expense.setEmployee(expenseService.getExpense(expense.getId()).getEmployee());
		expense.setDepartment(expenseService.getExpense(expense.getId()).getDepartment());
		expense.setDate(expenseService.getExpense(expense.getId()).getDate());		
		expenseService.updateExpense(expense);
		return "redirect:/list-expenses";
	}	

	@GetMapping(path="/expense/delete/{id}")
	public String deleteExpense(@PathVariable long id) 
	{	
		Expense expenseTemp = new Expense();
		Expense expense = expenseService.getExpense(id);
		List<Account> accounts =  accountService.getAccounts();
		List<Expense> expenses =  expenseService.getExpenses();
		Account account = new Account();
		int i;
		for(i=0; i<accounts.size();i++)
		{
			if( accounts.get(i).getEmployee() == expense.getEmployee() )
			{
				account = accounts.get(i);
			}
		}
		account.setBalance( account.getBalance() + expense.getAmount());
		accountService.updateAccount(account);
		List<Transaction> transactions =  transactionService.getTransactions();
		for(i=0; i<transactions.size(); i++)
		{
			Transaction transaction = new Transaction();
			if(transactions.get(i).getTo() != null)
			{	if(transactions.get(i).getTo().getId() == account.getId() )					 
				{		
					if((expense.getDate()).compareTo(transactions.get(i).getDate()) < 0)
					{	transaction = transactions.get(i);
						transaction.setToBalance(transactions.get(i).getToBalance() + expense.getAmount());  }
				}
			}
			if(transactions.get(i).getFrom() != null)
			{	if(transactions.get(i).getFrom().getId() == account.getId() )
				{	
					if((expense.getDate()).compareTo(transactions.get(i).getDate()) < 0)
					{	transaction = transactions.get(i);
						transaction.setFromBalance(transactions.get(i).getFromBalance() + expense.getAmount());  }
				}
			}
			if(transactions.get(i).getAccount() != null)
			{	if( transactions.get(i).getAccount().getId() == account.getId())
				{		
					if((expense.getDate()).compareTo(transactions.get(i).getDate()) < 0)
					{	transaction = transactions.get(i);
						transaction.setAccountBalance(transactions.get(i).getAccountBalance() + expense.getAmount()); }
				}
			}	
			transactionService.updateTransaction(transaction);
		}
		for(i=0; i<expenses.size(); i++)
		{		
			if(expenses.get(i).getEmployee().getId() == account.getEmployee().getId() )	
			{			
				if((expenses.get(i).getDate()).compareTo(expense.getDate()) > 0)
				{	
					expenseTemp = expenses.get(i);
					expenseTemp.setEmpAccountBalance(expenses.get(i).getEmpAccountBalance() + expense.getAmount());   
					expenseService.updateExpense(expenseTemp);  }
			}			
		}
		if (expenseService.deleteExpense(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-expenses";
	}
	
	@RequestMapping(path="/expenses-report-1", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showExpenseReportByEmployee( @RequestParam MultiValueMap<String, String> formInputValues) throws ParseException
	{		
		String employeeId = null;
		Iterator<String> mapIterator = formInputValues.keySet().iterator();
		while (mapIterator.hasNext()) 
		{
			String key = mapIterator.next();
			if(key.equals("employeeId"))
			{
				employeeId = formInputValues.get(key).get(0); 
			}
		}
			
		List<Expense> list = expenseService.getExpenses();
		List<Expense> expenses = new ArrayList<>();
		long employeeIdLong;
		if(!employeeId.equals("all"))
		{
			employeeIdLong = Long.parseLong(employeeId);
		}
		else
		{
			employeeIdLong = 0;
		}
		
		if(employeeIdLong == 0) 
		{
				expenses = expenseService.getExpenses();
		}	
		else
		{
			int i=0;
			for (i = 0; i < list.size(); i++) 
			{
				if(list.get(i).getEmployee().getId() == employeeIdLong)
				{
					expenses.add(list.get(i));	
				}
			}
		}		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("chosenExpenses", expenses);
	    modelAndView.setViewName("/banking/expenses/expenseContent");	
	    return modelAndView;
	}
	
	@RequestMapping(path="/expenses-report-2", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showExpenseReportByDepartment( @RequestParam MultiValueMap<String, String> formInputValues) throws ParseException
	{		
		String departmentId = null;
		Iterator<String> mapIterator = formInputValues.keySet().iterator();
		while (mapIterator.hasNext()) 
		{
			String key = mapIterator.next();
			if(key.equals("departmentId"))
			{
				departmentId = formInputValues.get(key).get(0); 
			}
		}
			
		List<Expense> list = expenseService.getExpenses();
		List<Expense> expenses = new ArrayList<>();
		long departmentIdLong;
		if(!departmentId.equals("all"))
		{
			departmentIdLong = Long.parseLong(departmentId);
		}
		else
		{
			departmentIdLong = 0;
		}
		
		if(departmentIdLong == 0) 
		{
				expenses = expenseService.getExpenses();
		}	
		else
		{
			int i=0;
			for (i = 0; i < list.size(); i++) 
			{
				if(list.get(i).getDepartment().getId() == departmentIdLong)
				{
					expenses.add(list.get(i));	
				}
			}
		}		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("chosenExpenses", expenses);
	    modelAndView.setViewName("/banking/expenses/expenseContent");	
	    return modelAndView;
	}
	
}
