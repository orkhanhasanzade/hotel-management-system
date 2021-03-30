package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Salary;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long>
{
	Salary findOneById(Long id);

}
