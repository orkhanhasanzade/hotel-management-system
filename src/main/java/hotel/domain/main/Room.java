package hotel.domain.main;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;


@Data
@Entity
public class Room 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull(message = "Floor is mandatory")
    private int floor;
	
    @NotNull(message = "Room number is mandatory")
    private int roomNumber;
    
    @NotNull(message = "Type is mandatory")
    private String type;
    
    @NotNull(message = "Rent is mandatory")
    private double rent;
   
    private String status;
    
    private String readiness;

}
