package hotel.domain.main;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
@Entity
public class Guest 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message = "Name is mandatory")
    private String name;
    
    @NotEmpty(message = "Surname is mandatory")
    private String surname;
    
    private String title;
    
    private String nationality;
    
    private String country;
    
    private String city;
    
    @NotEmpty(message = "Middle name is mandatory")
    private String middleName;
    
    @NotEmpty(message = "Email number is mandatory")
    private String email;
    
    @NotEmpty(message = "Address is mandatory")
    private String address;
    
    private String imagePath;
    
    @OneToMany(mappedBy = "guest")
    private Set<Reservation> reservations = new HashSet<>();
    
}
