package hotel.repository.hr;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.hr.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>
{
	Department findOneById(Long id);

}
