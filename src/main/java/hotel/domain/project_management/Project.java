package hotel.domain.project_management;


import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
@Entity
public class Project 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message = "Name is mandatory")
    private String name;
    
    @NotEmpty(message = "Description is mandatory")
    private String description;
    
    private String status;
    
    private Integer[] employees;

    @OneToMany
    private Set<Task> tasks;
    
}
