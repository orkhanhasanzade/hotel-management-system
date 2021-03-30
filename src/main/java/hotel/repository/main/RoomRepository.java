package hotel.repository.main;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.main.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>
{
	Room findOneById(Long id);

}
