package hotel.repository.banking;


import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import hotel.domain.banking.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>
{
	Account findOneById(Long id);
}
