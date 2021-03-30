package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Holiday;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long>
{
	Holiday findOneById(Long id);

}
