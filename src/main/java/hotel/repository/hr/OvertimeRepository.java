package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Overtime;

@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, Long>
{
	Overtime findOneById(Long id);

}
