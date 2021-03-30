package hotel.repository.main;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import hotel.domain.main.Guest;
import hotel.domain.main.Reservation;
import hotel.domain.main.Room;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>
{
	Reservation findOneById(Long id);
	
	Reservation findByGuest(Guest guest);
	
	Reservation findByRoom(Room room);	
		
    boolean existsByGuestAndRoom(String guest, String room);	 	 	  
}
