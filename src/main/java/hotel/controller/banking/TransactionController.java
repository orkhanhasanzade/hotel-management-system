package hotel.controller.banking;

import java.text.DateFormat;
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
import hotel.domain.banking.Transaction;
import hotel.service.banking.AccountService;
import hotel.service.banking.TransactionService;
import hotel.service.hr.EmployeeService;
import hotel.service.main.SettingService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Controller
public class TransactionController 
{
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;  
	
	@GetMapping(path="/list-transactions")
	public ModelAndView showTransactions() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());
		modelAndView.addObject("accounts", accountService.getAccounts());
		modelAndView.addObject("transactions", transactionService.getTransactions());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/banking/transactions/transactionList");
		return modelAndView;
	}

	@RequestMapping(path = "/transaction/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createTransaction() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("accounts", accountService.getAccounts());
	    modelAndView.setViewName("/banking/transactions/transactionModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/transaction/create") 
	public String createTransaction(@Valid @ModelAttribute Transaction transaction) 
	{	        		
		Date date = new Date(); 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		transaction.setDate(formatter.format(date));
		
		Account from = transaction.getFrom();
		Account to = transaction.getTo();
		Account account = transaction.getAccount();
			
		// setting transaction balances part
		if(transaction.getCategory().equals("transfer"))
		{	
			transaction.setFromBalance(from.getBalance() - transaction.getAmount());
			transaction.setToBalance(to.getBalance() + transaction.getAmount());
		}
		if(transaction.getCategory().equals("deposit"))
		{     transaction.setAccountBalance(account.getBalance() + transaction.getAmount());	}
		if(transaction.getCategory().equals("withdraw"))
		{	  transaction.setAccountBalance(account.getBalance() - transaction.getAmount());    }
		
		// updating accounts's balances
		if(transaction.getCategory().equals("transfer"))
		{	
			from.setBalance(from.getBalance() - transaction.getAmount());
			to.setBalance(to.getBalance() + transaction.getAmount());
		}
		if(transaction.getCategory().equals("deposit"))
		{
			account.setBalance(account.getBalance() + transaction.getAmount());
		}
		if(transaction.getCategory().equals("withdraw"))
		{
			account.setBalance(account.getBalance() - transaction.getAmount());
		}
		
		// updating account's
		if(transaction.getCategory().equals("transfer"))
		{
			accountService.updateAccount(from);
			accountService.updateAccount(to);
		}
		if(transaction.getCategory().equals("deposit") || transaction.getCategory().equals("withdraw"))
		{
			accountService.updateAccount(account);
		}		
		transactionService.createTransaction(transaction);
		return "redirect:/list-transactions";
	}
	
	@RequestMapping(path="/getTransactions/{id}", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView getTransactions(@PathVariable long id) 
	{
		List<Transaction> transactions = new ArrayList<>();
		List<Transaction> list = transactionService.getTransactions();
		int i;
		for (i = 0; i < list.size(); i++) 
		{	
			if(list.get(i).getFrom().getId() == id || list.get(i).getTo().getId() == id)
			{	transactions.add(list.get(i));   }
		}		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("transactions", transactions);
	    modelAndView.setViewName("/banking/dashboard/transactionsShowModalBody");	
	    return modelAndView;	
	}
	
	@RequestMapping(path="/transactions-report", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showTransactionsReport( @RequestParam MultiValueMap<String, String> formInputValues) throws ParseException
	{		
		String category = null, accountId = null, fromDate = null, toDate = null;		
		Iterator<String> mapIterator = formInputValues.keySet().iterator();
		while (mapIterator.hasNext()) 
		{
			String key = mapIterator.next();
			if(key.equals("category"))
			{
				category = formInputValues.get(key).get(0); 
				System.out.println(category);
			}
			if(key.equals("accountId"))
			{
				accountId = formInputValues.get(key).get(0); 
				System.out.println(accountId);
			}
			if(key.equals("fromDate"))
			{
				fromDate = formInputValues.get(key).get(0); 
				System.out.println(fromDate);
			}
			if(key.equals("toDate"))
			{
				toDate = formInputValues.get(key).get(0); 
				System.out.println(toDate);
			}
		}
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");		
		List<Transaction> list = transactionService.getTransactions();
		List<Transaction> transactions = new ArrayList<>();
		long accountIdLong;
		if(!accountId.equals("all"))
		{
			accountIdLong = Long.parseLong(accountId);
		}
		else
		{
			accountIdLong = 0;
		}
		if(accountIdLong == 0) 
		{
			int i=0;
			for (i = 0; i < list.size(); i++) 
			{		
					Date date = new SimpleDateFormat("yyyy-MM-dd").parse(list.get(i).getDate());  
					String strDate = formatter.format(date);  
					if (strDate.compareTo(fromDate) > 0 && strDate.compareTo(toDate) < 0) 
					{										
						if(category.equals("all"))
						{
							transactions.add(list.get(i));	
						}
						else
						{
							if(list.get(i).getCategory().equals(category))
							{
								transactions.add(list.get(i));	
							}
						}									
					}
			}
		}
		else
		{
			int i=0;
			for (i = 0; i < list.size(); i++) 
			{
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(list.get(i).getDate());  
				String strDate = formatter.format(date);  
				if (strDate.compareTo(fromDate) > 0 && strDate.compareTo(toDate) < 0) 
				{	
					if(list.get(i).getFrom() != null )
					{	
							if(list.get(i).getFrom().getId() == accountIdLong)
							{
								if(category.equals("all"))
								{
									transactions.add(list.get(i));	
								}
								else
								{
									if(list.get(i).getCategory().equals(category))
									{
										transactions.add(list.get(i));	
									}
								}
							}	
					}
					if(list.get(i).getTo() != null )
					{	
							if(list.get(i).getTo().getId() == accountIdLong)
							{
								if(category.equals("all"))
								{
									transactions.add(list.get(i));	
								}
								else
								{
									if(list.get(i).getCategory().equals(category))
									{
										transactions.add(list.get(i));	
									}
								}
							}	
					}
					if(list.get(i).getAccount() != null )
					{	
							if(list.get(i).getAccount().getId() == accountIdLong)
							{
								if(category.equals("all"))
								{
									transactions.add(list.get(i));	
								}
								else
								{
									if(list.get(i).getCategory().equals(category))
									{
										transactions.add(list.get(i));	
									}
								}
							}	
					}					
					
				}
			}
		}	

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("transactions", transactions);
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
	    modelAndView.setViewName("/banking/transactions/reportContent");	
	    return modelAndView;
	}

}
