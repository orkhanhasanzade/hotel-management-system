package hotel.repository.project_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.project_management.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long>
{
	File findOneById(Long id);

}
