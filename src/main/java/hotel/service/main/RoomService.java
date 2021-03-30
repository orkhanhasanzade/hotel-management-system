package hotel.service.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.main.Room;
import hotel.repository.main.RoomRepository;

@Service
public class RoomService
{
	@Autowired
	private RoomRepository roomRepository;
	
	public List<Room> getRooms() 
	{
		List<Room> rooms = new ArrayList<>();
		roomRepository.findAll().forEach(e -> rooms.add(e));
		return rooms;
	}

	public Room getRoom(long id) 
	{
		return roomRepository.findOneById(id);
	}

	public Room createRoom(Room room) 
	{
		return roomRepository.save(room);
	}
	
    public Room findById(Long roomId) 
    {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if(!roomOptional.isPresent()){
            throw new RuntimeException("Room Not Found!");
        }
        return roomOptional.get();      
    }

	public Room updateRoom(Long roomId, Room roomDetails) 
	{		
	     Room room = findById(roomId);
	     room.setId(roomDetails.getId());
	     room.setFloor(roomDetails.getFloor());
	     room.setRoomNumber(roomDetails.getRoomNumber());
	     room.setType(roomDetails.getType());
	     room.setRent(roomDetails.getRent());
	     room.setStatus(roomDetails.getStatus());
	        
	     roomRepository.save(room);
	     return room;
	}

	public boolean deleteRoom(long id) 
	{
		Room roomEntity = roomRepository.findOneById(id);
		if (roomEntity == null) 
		{
			return false;
		}
		
		roomRepository.delete(roomEntity);
		return true;
	}

}
