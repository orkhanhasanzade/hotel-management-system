package hotel.repository.main;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.main.MailDomainTrash;

@Repository
public interface MailDomainTrashRepository extends JpaRepository<MailDomainTrash, Long>
{
	MailDomainTrash findOneById(Long id);
}
