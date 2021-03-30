package hotel.domain.project_management;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
@Entity
public class File 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message = "Name is mandatory")
    private String name;
    
    private long size;
    
    private String type;
    
    private String filePath;
    
    @ManyToOne 
    Project project;
    
}
