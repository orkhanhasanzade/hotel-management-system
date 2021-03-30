package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Salary;
import hotel.repository.hr.SalaryRepository;

@Service
public class SalaryService
{
	@Autowired
	private SalaryRepository salaryRepository;
	
	public List<Salary> getSalaries() 
	{
		List<Salary> salaries = new ArrayList<>();
		salaryRepository.findAll().forEach(e -> salaries.add(e));
		return salaries;
	}

	public Salary getSalary(long id) 
	{
		return salaryRepository.findOneById(id);
	}

	public Salary createSalary(Salary salary) 
	{
		return salaryRepository.save(salary);
	}

	public Salary updateSalary(Salary salary) 
	{
		Salary salaryEntity = salaryRepository.findOneById(salary.getId());
		if (salaryEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(salary, salaryEntity);	
		salaryEntity = salaryRepository.save(salary);
		return salaryEntity;
	}

	public boolean deleteSalary(long id) 
	{
		Salary salaryEntity = salaryRepository.findOneById(id);
		if (salaryEntity == null) 
		{
			return false;
		}
		
		salaryRepository.delete(salaryEntity);
		return true;
	}


}
