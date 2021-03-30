package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Employee;
import hotel.repository.hr.EmployeeRepository;

@Service
public class EmployeeService
{
	@Autowired
	private EmployeeRepository employeeRepository;
	
	public List<Employee> getEmployees() 
	{
		List<Employee> employees = new ArrayList<>();
		employeeRepository.findAll().forEach(e -> employees.add(e));
		return employees;
	}

	public Employee getEmployee(long id) 
	{
		return employeeRepository.findOneById(id);
	}

	public Employee createEmployee(Employee employee) 
	{
		return employeeRepository.save(employee);
	}

	public Employee updateEmployee(Employee employee) 
	{
		Employee employeeEntity = employeeRepository.findOneById(employee.getId());
		if (employeeEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(employee, employeeEntity);	
		employeeEntity = employeeRepository.save(employee);
		return employeeEntity;
	}

	public boolean deleteEmployee(long id) 
	{
		Employee employeeEntity = employeeRepository.findOneById(id);
		if (employeeEntity == null) 
		{
			return false;
		}
		
		employeeRepository.delete(employeeEntity);
		return true;
	}


}
