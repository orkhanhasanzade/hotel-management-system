package hotel.service.banking;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.banking.Expense;
import hotel.repository.banking.ExpenseRepository;

@Service
public class ExpenseService
{
	@Autowired
	private ExpenseRepository expenseRepository;
	
	public List<Expense> getExpenses() 
	{
		List<Expense> expenses = new ArrayList<>();
		expenseRepository.findAll().forEach(e -> expenses.add(e));
		return expenses;
	}

	public Expense getExpense(long id) 
	{
		return expenseRepository.findOneById(id);
	}

	public Expense createExpense(Expense expense) 
	{
		return expenseRepository.save(expense);
	}

	public Expense updateExpense(Expense expense) 
	{
		Expense expenseEntity = expenseRepository.findOneById(expense.getId());
		if (expenseEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(expense, expenseEntity);	
		expenseEntity = expenseRepository.save(expense);
		return expenseEntity;
	}

	public boolean deleteExpense(long id) 
	{
		Expense expenseEntity = expenseRepository.findOneById(id);
		if (expenseEntity == null) 
		{
			return false;
		}
		
		expenseRepository.delete(expenseEntity);
		return true;
	}


}
