package hotel.domain.hr;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "leaves")
public class Leave 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String category;
    
    private String fromDate;
    
    private String fromTime;
    
    private String toDate;
    
    private String toTime;
    
    private String hours;
    
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

}
