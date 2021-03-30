package hotel.repository.project_management;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.project_management.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>
{
	Task findOneById(Long id);

}
