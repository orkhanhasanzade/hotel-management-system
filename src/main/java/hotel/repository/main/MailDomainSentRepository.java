package hotel.repository.main;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import hotel.domain.main.MailDomainSent;

@Repository
public interface MailDomainSentRepository extends JpaRepository<MailDomainSent, Long>
{
	MailDomainSent findOneById(Long id);
}
