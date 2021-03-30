package hotel.domain.hr;


import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Salary 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String currency;
    
    private int amountPerHour;
}
