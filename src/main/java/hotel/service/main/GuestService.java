package hotel.service.main;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.main.Guest;
import hotel.repository.main.GuestRepository;

@Service
public class GuestService
{
	@Autowired
	private GuestRepository guestRepository;
	
	public List<Guest> getGuests() 
	{
		List<Guest> guests = new ArrayList<>();
		guestRepository.findAll().forEach(e -> guests.add(e));
		return guests;
	}

	public Guest getGuest(long id) 
	{
		return guestRepository.findOneById(id);
	}

	public Guest createGuest(Guest guest) 
	{
		return guestRepository.save(guest);
	}

	public Guest updateGuest(Guest guest) 
	{
		Guest guestEntity = guestRepository.findOneById(guest.getId());
		if (guestEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(guest, guestEntity);
		guestEntity = guestRepository.save(guest);
		return guestEntity;
	}

	public boolean deleteGuest(long id) 
	{
		Guest guestEntity = guestRepository.findOneById(id);
		if (guestEntity == null) 
		{
			return false;
		}
		
		guestRepository.delete(guestEntity);
		return true;
	}

}
