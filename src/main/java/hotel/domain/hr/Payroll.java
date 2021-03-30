package hotel.domain.hr;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class Payroll 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
   
    private int year;
    
    private int month;
    
    private double grossSalary;
    
    private double deductions;
    
    private double netSalary;
    
    private String status;   
    
    @OneToOne
    Employee employee;  
}
