package hotel.controller.banking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import hotel.domain.hr.Employee;
import hotel.service.banking.AccountService;
import hotel.service.banking.ExpenseService;
import hotel.service.banking.TransactionService;
import hotel.service.hr.EmployeeService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Controller
public class AccountController 
{
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private ExpenseService expenseService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;  
	
	@GetMapping(path="/banking-dashboard")
	public ModelAndView showBankingPage() throws ParseException 
	{			
		int transactionCount = 0;
		int expenseCount = 0;
		List<Transaction> transactions = transactionService.getTransactions();	
		List<Expense> expenses = expenseService.getExpenses();	
		Date date = new Date(); 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String today = formatter.format(date);
		String transactionDate, expenseDate;
		Date date1;
		Date date2;
		int i;
		for (i = 0; i < transactions.size(); i++)
		{		
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(transactions.get(i).getDate());
			transactionDate = formatter.format( date1 );
			if(today.equals(transactionDate))
			{
				transactionCount++;   
			}
		}
		for (i = 0; i < expenses.size(); i++)
		{		
			date2 = new SimpleDateFormat("yyyy-MM-dd").parse(expenses.get(i).getDate());
			expenseDate = formatter.format( date2 );
			if(today.equals(expenseDate))
			{
				expenseCount++;
			}
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("accountCount", accountService.getAccounts().size());
		modelAndView.addObject("transactionCount", transactionCount);
		modelAndView.addObject("expenseCount", expenseCount);	
		modelAndView.addObject("accounts", accountService.getAccounts());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("/banking/dashboard/dashboard");
		return modelAndView;
	}
	
	@GetMapping(path="/list-accounts")
	public ModelAndView showAccounts() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());
		modelAndView.addObject("accounts", accountService.getAccounts());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/banking/accounts/accountList");
		return modelAndView;
	}
	
	@RequestMapping(path = "/account/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createAccount() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();		
		List<Employee> employees = employeeService.getEmployees();	
		modelAndView.addObject("employees", employees);	
	    modelAndView.setViewName("/banking/accounts/accountModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/account/create")
	public String createAccount(@Valid @ModelAttribute Account account) 
	{	
		Date date = new Date(); 
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		account.setDate(formatter.format(date));
		
		account.setBalance(100);
		accountService.createAccount(account);
		return "redirect:/list-accounts";
	}
	
	@RequestMapping(path="/account/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editAccount(@PathVariable long id) 
	{		
		ModelAndView modelAndView = new ModelAndView();		
		modelAndView.addObject("account", accountService.getAccount(id));
		modelAndView.addObject("employees", employeeService.getEmployees());		
	    modelAndView.setViewName("/banking/accounts/accountModalBodyEdit");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateAccount")
	public String editAccount(Account account)
	{
		account.setBalance(accountService.getAccount(account.getId()).getBalance());
		account.setInitialBalance(accountService.getAccount(account.getId()).getInitialBalance());
		account.setDate(accountService.getAccount(account.getId()).getDate());
		
		accountService.updateAccount(account);
		return "redirect:/list-accounts";
	}	

	@GetMapping(path="/account/delete/{id}")
	public String deleteAccount(@PathVariable long id) 
	{
		if (accountService.deleteAccount(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-accounts";
	}
	
	@RequestMapping(path="/account/history/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showAccountHistory(@PathVariable long id) 
	{
		List<Transaction> transactions =  transactionService.getTransactions();
		List<Expense> expenses = expenseService.getExpenses();
		Account account = accountService.getAccount(id);

		List<LinkedHashMap<String, Object>> mapsList = new ArrayList<LinkedHashMap<String, Object>>();
		int i,j;
		long transactionAccountId=0;
		long transactionAccountId2=0;
		int a = 0;
		// put initial history data
		if(a == 0)
		{	LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("Date", account.getDate());	
			map.put("Category", "");
			map.put("Description", "");
			map.put("Money In", "");
			map.put("Money Out", ""); 
			map.put("Balance", account.getInitialBalance() );			
			mapsList.add(map);	}
		
		// check if there is transactions for this account
		int checkValueTransactions = 0;
		int checkValueExpenses = 0;
		for (i = 0 ; i < transactions.size(); i++) 
		{
			if(transactions.get(i).getFrom() != null)
			{	if(transactions.get(i).getFrom().getId() == account.getId())
				{   checkValueTransactions = 1; }  }
			if(transactions.get(i).getTo() != null)
			{	if(transactions.get(i).getTo().getId() == account.getId())
				{   checkValueTransactions = 1; } }
			if(transactions.get(i).getAccount() != null)
			{	if(transactions.get(i).getAccount().getId() == account.getId())
				{   checkValueTransactions = 1; }	}
		}
		for (j = 0 ; j < expenses.size(); j++) 
		{
			if(expenses.get(j).getEmployee() != null)
			{	if(expenses.get(j).getEmployee().getId() == account.getEmployee().getId())
				{	checkValueExpenses = 1;		}	}		
		}

		// if there is transactions and expenses at the same time then execute this part of code
		if(checkValueTransactions == 1  && checkValueExpenses == 1)
		{
		 	for (i = 0 ; i < transactions.size(); i++) 
			{			 	  
		 		 if(transactions.get(i).getCategory().equals("transfer") &&  transactions.get(i).getFrom().getId() == account.getId())
		 		 {
		 			 transactionAccountId = transactions.get(i).getFrom().getId();
		 		 }
		 		 if(transactions.get(i).getCategory().equals("transfer") &&  transactions.get(i).getTo().getId() == account.getId())
		 		 {
		 			 transactionAccountId = transactions.get(i).getTo().getId();
		 		 }
		 		 if(transactions.get(i).getCategory().equals("deposit") || transactions.get(i).getCategory().equals("withdraw"))
		 		 {
		 			transactionAccountId = transactions.get(i).getAccount().getId();
		 		 }
		 		 
		 		   if(transactionAccountId == account.getId())
		 		   {	 	 			  
			 					if(transactions.get(i).getCategory().equals("transfer"))
			 					{
			 						LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			 						map.put("Date", transactions.get(i).getDate());
			 						map.put("Category", "Transfer To");
			 						map.put("Description", transactions.get(i).getDescription());
			 						if(transactions.get(i).getFrom().getId() == account.getId() )
			 						{
			 							map.put("Money In", "" );
			 							map.put("Money Out", transactions.get(i).getAmount() ); 
			 							map.put("Balance",   transactions.get(i).getFromBalance());
			 						}
			 						if(transactions.get(i).getTo().getId() == account.getId())
			 						{
			 							map.put("Money In", transactions.get(i).getAmount() );
			 							map.put("Money Out", "" ); 
			 							map.put("Balance",  transactions.get(i).getToBalance());
			 						}
			 						mapsList.add(map);	
			 					}
			 					if(transactions.get(i).getCategory().equals("deposit"))
			 					{
			 						LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			 						map.put("Date", transactions.get(i).getDate());
			 						map.put("Category", "Deposit");
			 						map.put("Description", transactions.get(i).getDescription());		 						
			 						map.put("Money In", transactions.get(i).getAmount() );
			 						map.put("Money Out", "" );
			 						map.put("Balance",  transactions.get(i).getAccountBalance() );
			 						mapsList.add(map);	
			 					}			 					
			 					if(transactions.get(i).getCategory().equals("withdraw"))
			 					{		 	
			 						LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			 						map.put("Date", transactions.get(i).getDate());
			 						map.put("Category", "Withdraw");
			 						map.put("Description", transactions.get(i).getDescription());
			 						map.put("Money In", "" ); 
			 						map.put("Money Out", transactions.get(i).getAmount() ); 
			 						map.put("Balance", transactions.get(i).getAccountBalance() );
			 						mapsList.add(map);				 						
			 					}			 				
		 		}
			}	
			for (j = 0 ; j < expenses.size(); j++) 
 			{	
	 			if(expenses.get(j).getEmployee() == account.getEmployee())
	 			{
		 			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		 			map.put("Date", expenses.get(j).getDate());
		 			map.put("Category", "Expense");
		 			map.put("Description", expenses.get(j).getDescription());
		 			map.put("Money In", "");
		 			map.put("Money Out", expenses.get(j).getAmount());		 					
		 			map.put("Balance", expenses.get(j).getEmpAccountBalance());		
		 			mapsList.add(map);
	 			}
 			 }
		} 	
		
		// case when there is not any transactions then consider only expenses
	 	if(checkValueTransactions == 0  && checkValueExpenses == 1)
	 	{	 		
	 		for (j = 0 ; j < expenses.size(); j++) 
			{
	 			if(expenses.get(j).getEmployee() == account.getEmployee())
	 			{
	 				LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
	 				map.put("Date", expenses.get(j).getDate());
	 				map.put("Category", "Expense");
 					map.put("Description", expenses.get(j).getDescription());
 					map.put("Money In", "");
 					map.put("Money Out", expenses.get(j).getAmount());		 					
 					map.put("Balance", expenses.get(j).getEmpAccountBalance());		
 					mapsList.add(map);		 			
	 			}
			}
	 	}

	 	// case when there is not any expenses then consider only transactions
	 	if(checkValueTransactions == 1  && checkValueExpenses == 0)
	 	{	 		
	 		for(i = 0 ; i < transactions.size(); i++)
	 		{
	 			 if(transactions.get(i).getCategory().equals("transfer") &&  transactions.get(i).getFrom().getId() == account.getId())
		 		 {
		 			 transactionAccountId2 = transactions.get(i).getFrom().getId();
		 		 }
		 		 if(transactions.get(i).getCategory().equals("transfer") &&  transactions.get(i).getTo().getId() == account.getId())
		 		 {
		 			 transactionAccountId2 = transactions.get(i).getTo().getId();
		 		 }
		 		 if(transactions.get(i).getCategory().equals("deposit") || transactions.get(i).getCategory().equals("withdraw"))
		 		 {
		 			transactionAccountId2 = transactions.get(i).getAccount().getId();
		 		 }

	 			if( transactionAccountId2 == account.getId() )
		 		{	
 					if(transactions.get(i).getCategory().equals("transfer"))
 					{
 						LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
 						map.put("Date", transactions.get(i).getDate());
 						map.put("Category", "Transfer To");
 						map.put("Description", transactions.get(i).getDescription());
 						if(transactions.get(i).getFrom().getId() == account.getId() )
 						{
 							map.put("Money In", "" );
 							map.put("Money Out", transactions.get(i).getAmount() ); 
 							map.put("Balance",   transactions.get(i).getFromBalance());
 						}
 						if(transactions.get(i).getTo().getId() == account.getId())
 						{
 							map.put("Money In", transactions.get(i).getAmount() );
 							map.put("Money Out", "" ); 
 							map.put("Balance",  transactions.get(i).getToBalance());
 						}
 						mapsList.add(map);	
 					}
 					if(transactions.get(i).getCategory().equals("deposit"))
 					{
 						LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
 						map.put("Date", transactions.get(i).getDate());
 						map.put("Category", "Deposit");
 						map.put("Description", transactions.get(i).getDescription());		 						
 						map.put("Money In", transactions.get(i).getAmount() );
 						map.put("Money Out", "" );
 						map.put("Balance",  transactions.get(i).getAccountBalance() );
 						mapsList.add(map);	
 					}
 					if(transactions.get(i).getCategory().equals("withdraw"))
 					{
 						LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
 						map.put("Date", transactions.get(i).getDate());
 						map.put("Category", "Withdraw");
 						map.put("Description", transactions.get(i).getDescription());
 						map.put("Money In", "" ); 
 						map.put("Money Out", transactions.get(i).getAmount() ); 
 						map.put("Balance", transactions.get(i).getAccountBalance() );
 						mapsList.add(map);	
 					}
		 		}
	 		}
	 	}
		ModelAndView modelAndView = new ModelAndView();		
	 	modelAndView.addObject("mapsList", mapsList);		
	    modelAndView.setViewName("/banking/accounts/historyModalBody");	
	    return modelAndView;	
	}
	
	@RequestMapping(path="/accounts-report-1", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showAccountsReport1( @RequestParam MultiValueMap<String, String> formInputValues) throws ParseException
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
			
		List<Account> list = accountService.getAccounts();
		List<Account> accounts = new ArrayList<>();
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
				accounts = accountService.getAccounts();
		}	
		else
		{
			int i=0;
			for (i = 0; i < list.size(); i++) 
			{
				if(list.get(i).getEmployee().getId() == employeeIdLong)
				{
					accounts.add(list.get(i));	
				}
			}
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("accounts", accounts);	
	    modelAndView.setViewName("/banking/accounts/accountContent");	
	    return modelAndView;
	}

}
