package hotel.repository.banking;


import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import hotel.domain.banking.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>
{
	Expense findOneById(Long id);

}
