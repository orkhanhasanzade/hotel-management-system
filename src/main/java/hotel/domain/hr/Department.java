package hotel.domain.hr;


import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
@Entity
public class Department 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message = "Name is mandatory")
    private String name;
    
    @NotEmpty(message = "Description is mandatory")
    private String description;
    
    @OneToMany( mappedBy = "department", cascade = CascadeType.ALL, 
    		    fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Employee> employees;
    
}
