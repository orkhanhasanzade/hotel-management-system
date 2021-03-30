package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>
{
	Attendance findOneById(Long id);

}
