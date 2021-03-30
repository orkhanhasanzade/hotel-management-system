package hotel.service.main;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.main.MailDomainSent;
import hotel.repository.main.MailDomainSentRepository;

@Service
public class MailDomainSentService
{
	@Autowired
	private MailDomainSentRepository mailDomainSentRepository;
	
	public List<MailDomainSent> getMailDomainSents() 
	{
		List<MailDomainSent> mails = new ArrayList<>();
		mailDomainSentRepository.findAll().forEach(e -> mails.add(e));
		return mails;
	}

	public MailDomainSent getMailDomainSent(long id) 
	{
		return mailDomainSentRepository.findOneById(id);
	}

	public MailDomainSent createMailDomainSent(MailDomainSent mail) 
	{
		return mailDomainSentRepository.save(mail);
	}

	public MailDomainSent updateMailDomainSent(MailDomainSent mail) 
	{
		MailDomainSent mailDomainSentEntity = mailDomainSentRepository.findOneById(mail.getId());
		if (mailDomainSentEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(mail, mailDomainSentEntity);
		mailDomainSentEntity = mailDomainSentRepository.save(mail);
		return mailDomainSentEntity;
	}

	public boolean deleteMailDomainSent(long id) 
	{
		MailDomainSent mailDomainSentEntity = mailDomainSentRepository.findOneById(id);
		if (mailDomainSentEntity == null) 
		{
			return false;
		}
		
		mailDomainSentRepository.delete(mailDomainSentEntity);
		return true;
	}

}
