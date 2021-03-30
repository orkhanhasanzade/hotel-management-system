package hotel.service.banking;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.banking.Transaction;
import hotel.repository.banking.TransactionRepository;

@Service
public class TransactionService 
{
	@Autowired
	private TransactionRepository transactionRepository;
	
	public List<Transaction> getTransactions() 
	{
		List<Transaction> transactions = new ArrayList<>();
		transactionRepository.findAll().forEach(e -> transactions.add(e));
		return transactions;
	}

	public Transaction getTransaction(long id) 
	{
		return transactionRepository.findOneById(id);
	}

	public Transaction createTransaction(Transaction transaction) 
	{
		return transactionRepository.save(transaction);
	}

	public Transaction updateTransaction(Transaction transaction) 
	{
		Transaction transactionEntity = transactionRepository.findOneById(transaction.getId());
		if (transactionEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(transaction, transactionEntity);	
		transactionEntity = transactionRepository.save(transaction);
		return transactionEntity;
	}

	public boolean deleteTransaction(long id) 
	{
		Transaction transactionEntity = transactionRepository.findOneById(id);
		if (transactionEntity == null) 
		{
			return false;
		}
		
		transactionRepository.delete(transactionEntity);
		return true;
	}


}
