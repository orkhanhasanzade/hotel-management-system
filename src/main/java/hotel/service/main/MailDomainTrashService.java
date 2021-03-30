package hotel.service.main;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.main.MailDomainTrash;
import hotel.repository.main.MailDomainTrashRepository;

@Service
public class MailDomainTrashService
{
	@Autowired
	private MailDomainTrashRepository mailDomainTrashRepository;
	
	public List<MailDomainTrash> getMailDomainTrashs() 
	{
		List<MailDomainTrash> mails = new ArrayList<>();
		mailDomainTrashRepository.findAll().forEach(e -> mails.add(e));
		return mails;
	}

	public MailDomainTrash getMailDomainTrash(long id) 
	{
		return mailDomainTrashRepository.findOneById(id);
	}

	public MailDomainTrash createMailDomainTrash(MailDomainTrash mail) 
	{
		return mailDomainTrashRepository.save(mail);
	}

	public MailDomainTrash updateMailDomainTrash(MailDomainTrash mail) 
	{
		MailDomainTrash mailDomainTrashEntity = mailDomainTrashRepository.findOneById(mail.getId());
		if (mailDomainTrashEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(mail, mailDomainTrashEntity);
		mailDomainTrashEntity = mailDomainTrashRepository.save(mail);
		return mailDomainTrashEntity;
	}

	public boolean deleteMailDomainTrash(long id) 
	{
		MailDomainTrash mailDomainTrashEntity = mailDomainTrashRepository.findOneById(id);
		if (mailDomainTrashEntity == null) 
		{
			return false;
		}
		
		mailDomainTrashRepository.delete(mailDomainTrashEntity);
		return true;
	}

}
