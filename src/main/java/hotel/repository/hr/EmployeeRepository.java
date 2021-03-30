package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>
{
	Employee findOneById(Long id);

}
