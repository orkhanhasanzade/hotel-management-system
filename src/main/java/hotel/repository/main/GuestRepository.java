package hotel.repository.main;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.main.Guest;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long>
{
	Guest findOneById(Long id);

}
