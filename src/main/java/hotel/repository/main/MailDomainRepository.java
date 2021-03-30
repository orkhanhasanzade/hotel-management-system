package hotel.repository.main;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.main.MailDomain;

@Repository
public interface MailDomainRepository extends JpaRepository<MailDomain, Long>
{
	MailDomain findOneById(Long id);
}
