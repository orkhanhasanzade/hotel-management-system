package hotel.domain.main;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "guest", "room"})  
public class Reservation 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	String creationDate;

	String fromDate;
	
	String toDate;

	private String status;
	
	private String meal;
	
	private boolean paymentStatus;
	
	private double amount;

	private String checkinDate;

	private String checkoutDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "reservations_guest", joinColumns = {
			@JoinColumn(name = "reservation_id") }, inverseJoinColumns = { @JoinColumn(name = "guest_id") })
	private Guest guest;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "reservation_room", joinColumns = { @JoinColumn(name = "reservation_id") }, inverseJoinColumns = {
			@JoinColumn(name = "room_id") })
	private Room room;

}
