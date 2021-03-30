package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Designation;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long>
{
	Designation findOneById(Long id);

}
