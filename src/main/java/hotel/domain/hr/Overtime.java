package hotel.domain.hr;


import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class Overtime 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String date;
    
    private int hours;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

}
