package hotel.service.main;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.main.MailDomain;
import hotel.repository.main.MailDomainRepository;

@Service
public class MailDomainService
{
	@Autowired
	private MailDomainRepository mailDomainRepository;
	
	public List<MailDomain> getMailDomains() 
	{
		List<MailDomain> mails = new ArrayList<>();
		mailDomainRepository.findAll().forEach(e -> mails.add(e));
		return mails;
	}

	public MailDomain getMailDomain(long id) 
	{
		return mailDomainRepository.findOneById(id);
	}

	public MailDomain createMailDomain(MailDomain mail) 
	{
		return mailDomainRepository.save(mail);
	}

	public MailDomain updateMailDomain(MailDomain mail) 
	{
		MailDomain mailDomainEntity = mailDomainRepository.findOneById(mail.getId());
		if (mailDomainEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(mail, mailDomainEntity);
		mailDomainEntity = mailDomainRepository.save(mail);
		return mailDomainEntity;
	}

	public boolean deleteMailDomain(long id) 
	{
		MailDomain mailDomainEntity = mailDomainRepository.findOneById(id);
		if (mailDomainEntity == null) 
		{
			return false;
		}
		
		mailDomainRepository.delete(mailDomainEntity);
		return true;
	}

}
