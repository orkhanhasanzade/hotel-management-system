package hotel.domain.hr;


import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.domain.project_management.Task;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "department" })  
public class Employee 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message = "Name is mandatory")
    private String name;
    
    @NotEmpty(message = "Surname is mandatory")
    private String surname;
    
    @NotEmpty(message = "Middle name is mandatory")
    private String middleName;
    
    private String birthdate;
    
    private String gender;
    
    private String staff;
    
    private String shift;
    
    private String status;
    
    private int bonusGrade;
    
    @NotEmpty(message = "Mobile number is mandatory")
    private String mobileNumber;
    
    @NotEmpty(message = "Address is mandatory")
    private String address;
    
    private String imagePath;
    
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;
    
    @ManyToOne
    @JoinColumn(name = "salary_id", nullable = true)
    private Salary salary;
    
    @ManyToOne
    @JoinColumn(name = "designation_id", nullable = true)
    private Designation designation;
    
    @OneToMany( mappedBy = "employee", cascade = CascadeType.ALL, 
	    		fetch = FetchType.LAZY, orphanRemoval = false)
    private Set<Leave> leaves;
    
    @OneToMany( mappedBy = "employee", cascade = CascadeType.ALL, 
    		fetch = FetchType.LAZY, orphanRemoval = false)
    private Set<Overtime> overtimes;
    
    @OneToMany( mappedBy = "employee", cascade = CascadeType.ALL, 
    		fetch = FetchType.LAZY, orphanRemoval = false)
    private Set<Attendance> attendances;
    
    @OneToMany( mappedBy = "employee", cascade = CascadeType.ALL, 
    		fetch = FetchType.LAZY, orphanRemoval = false)
    private Set<Task> tasks;

}
