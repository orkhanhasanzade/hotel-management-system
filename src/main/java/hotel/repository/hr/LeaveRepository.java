package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Leave;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long>
{
	Leave findOneById(Long id);

}
