package hotel.domain.hr;


import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
public class Designation 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message = "Name is mandatory")
    private String name;
    
    @NotEmpty(message = "Description is mandatory")
    private String description;
    
    @OneToOne
    Salary salary;
    
    @OneToMany( mappedBy = "designation", cascade = CascadeType.ALL, 
    		    fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Employee> employees;
    
}
