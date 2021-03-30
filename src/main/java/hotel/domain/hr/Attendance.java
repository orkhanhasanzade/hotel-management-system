package hotel.domain.hr;


import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class Attendance 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String date;
    
    private String inTime;
    
    private String outTime;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

}
