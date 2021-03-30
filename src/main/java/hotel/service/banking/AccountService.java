package hotel.service.banking;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.banking.Account;
import hotel.repository.banking.AccountRepository;

@Service
public class AccountService 
{
	@Autowired
	private AccountRepository accountRepository;
	
	public List<Account> getAccounts() 
	{
		List<Account> accounts = new ArrayList<>();
		accountRepository.findAll().forEach(e -> accounts.add(e));
		return accounts;
	}

	public Account getAccount(long id) 
	{
		return accountRepository.findOneById(id);
	}

	public Account createAccount(Account account) 
	{
		return accountRepository.save(account);
	}

	public Account updateAccount(Account account) 
	{
		Account accountEntity = accountRepository.findOneById(account.getId());
		if (accountEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(account, accountEntity);	
		accountEntity = accountRepository.save(account);
		return accountEntity;
	}

	public boolean deleteAccount(long id) 
	{
		Account accountEntity = accountRepository.findOneById(id);
		if (accountEntity == null) 
		{
			return false;
		}
		
		accountRepository.delete(accountEntity);
		return true;
	}

}
