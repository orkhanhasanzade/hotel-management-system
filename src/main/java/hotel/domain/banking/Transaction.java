package hotel.domain.banking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;


@Data
@Entity
public class Transaction 
{
	 @Id
	 @GeneratedValue(strategy = GenerationType.AUTO)
	 private Long id;
	 
	 private String date;
	 
	 private float amount;
	 
	 private String category;
	 
	 private String description;	
	 
	 @ManyToOne
	 private Account from;
	 
	 private float fromBalance;
	 
	 @ManyToOne
	 private Account to;
	 
	 private float toBalance;
	 
	 @ManyToOne
	 private Account account;
	 
	 private float accountBalance;

}
