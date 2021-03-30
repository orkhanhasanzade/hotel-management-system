package hotel.domain.hr;


import javax.persistence.*;

import lombok.Data;

@Data
@Entity
public class Holiday 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;
    
    private String description;
    
    private String fromDate;
    
    private String toDate;
    
    private Long month;
    
    private String year;

}
