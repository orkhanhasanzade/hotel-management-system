package hotel.domain.main;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class MailDomainTrash 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String fromAddress;

	private String subject;

	private String toList;

	private String ccList;

	private String sentDate;
	
	private Date sentDateInDate;

	@Column(length=10000) 
	private String mailContent;
	
	private Integer readStatus;

}
