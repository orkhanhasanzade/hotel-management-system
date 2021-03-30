package hotel.domain.project_management;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import hotel.domain.hr.Employee;
import lombok.Data;

@Data
@Entity
public class Task 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message = "Name is mandatory")
    private String name;
    
    @NotEmpty(message = "Description is mandatory")
    private String description;
    
    private String status;   
    
    private String startDate;
    
    private String endDate;

    @ManyToOne 
    @JoinColumn(name = "employee_id", nullable = true)
    Employee employee;
    
    @ManyToOne
    private Project project;
 
}
