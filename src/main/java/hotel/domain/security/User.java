package hotel.domain.security;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class User 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@Column(name = "email")
	@Email(message = "*Please enter a valid email adress!")
	@NotEmpty(message = "*Please provide an email! This field can not be empty!")
	private String email;
	
	@Column(name = "password")
	@Length(min = 3, message = "*Your password can not be less than 3 characters!")
	@NotEmpty(message = "*Please provide your password! This field can not be empty!")
	@Transient
	private String password;
	
	private String real_password;
	
	@Column(name = "name")
	@NotEmpty(message = "*Please provide your name! This field can not be empty!")
	private String name;
	
	private String phone;
	
	private String mobile;
	
	private String address;
	
	@Column(name = "active")
	private int active;
	
	private String imagePath;
	
	@Column(name = "reset_token")
	private String resetToken;
		
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable( name="user_role",
	      		joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
	      		inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")} )
	private List<Role> roles;
	
}
