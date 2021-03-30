package hotel.repository.project_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.project_management.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>
{
	Project findOneById(Long id);

}
