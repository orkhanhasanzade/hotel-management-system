package hotel.domain.banking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import hotel.domain.hr.Employee;
import lombok.Data;

@Data
@Entity
public class Account 
{
	 @Id
	 @GeneratedValue(strategy = GenerationType.AUTO)
	 private Long id;
	 
	 private String date;
	 
	 private Long number;
	 
	 private String name;
	 
	 private String description;
	 
	 private float initialBalance;
	 
	 private float balance;
	 
	 @OneToOne
	 private Employee employee; 

}
	