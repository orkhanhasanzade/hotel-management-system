package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Department;
import hotel.repository.hr.DepartmentRepository;

@Service
public class DepartmentService
{
	@Autowired
	private DepartmentRepository departmentRepository;
	
	public List<Department> getDepartments() 
	{
		List<Department> departments = new ArrayList<>();
		departmentRepository.findAll().forEach(e -> departments.add(e));
		return departments;
	}

	public Department getDepartment(long id) 
	{
		return departmentRepository.findOneById(id);
	}

	public Department createDepartment(Department department) 
	{
		return departmentRepository.save(department);
	}

	public Department updateDepartment(Department department) 
	{
		Department departmentEntity = departmentRepository.findOneById(department.getId());
		if (departmentEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(department, departmentEntity);	
		departmentEntity = departmentRepository.save(department);
		return departmentEntity;
	}

	public boolean deleteDepartment(long id) 
	{
		Department departmentEntity = departmentRepository.findOneById(id);
		if (departmentEntity == null) 
		{
			return false;
		}
		
		departmentRepository.delete(departmentEntity);
		return true;
	}


}
