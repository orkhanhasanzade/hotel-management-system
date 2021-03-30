package hotel.domain.banking;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import hotel.domain.hr.Department;
import hotel.domain.hr.Employee;
import lombok.Data;

@Data
@Entity
public class Expense 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String date;
    
    private String category;
    
    @NotEmpty(message = "Name is mandatory")
    private String currency;
    
    private float amount;
    
    private String description;  
    
    private String status;
    
    private float empAccountBalance;
    
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;
    
}
