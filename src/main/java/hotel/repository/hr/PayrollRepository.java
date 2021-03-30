package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Payroll;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long>
{
	Payroll findOneById(Long id);

}
