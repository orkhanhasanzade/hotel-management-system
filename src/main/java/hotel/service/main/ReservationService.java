package hotel.service.main;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.main.Reservation;
import hotel.repository.main.ReservationRepository;

@Service
public class ReservationService
{
	@Autowired
	private ReservationRepository reservationRepository;
	
	public List<Reservation> getReservations() 
	{
		List<Reservation> reservations = new ArrayList<>();
		reservationRepository.findAll().forEach(e -> reservations.add(e));
		return reservations;
	}

	public Reservation getReservation(long id) 
	{
		return reservationRepository.findOneById(id);
	}

	public Reservation createReservation(Reservation reservation) 
	{      		
		return reservationRepository.save(reservation);
	}

	public Reservation updateReservation(Reservation reservation) 
	{
		Reservation reservationEntity = reservationRepository.findOneById(reservation.getId());
		if (reservationEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(reservation, reservationEntity);
		reservationEntity = reservationRepository.save(reservation);
		return reservationEntity;
	}

	public boolean deleteReservation(long id) 
	{
		Reservation reservationEntity = reservationRepository.findOneById(id);
		if (reservationEntity == null) 
		{
			return false;
		}
		
		reservationRepository.delete(reservationEntity);
		return true;
	}

}
