package hotel.repository.banking;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.banking.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>
{
	Transaction findOneById(Long id);

}
