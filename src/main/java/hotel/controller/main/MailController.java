package hotel.controller.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import hotel.domain.main.MailDomain;
import hotel.domain.main.MailDomainSent;
import hotel.domain.main.MailDomainTrash;
import hotel.service.main.MailDomainSentService;
import hotel.service.main.MailDomainService;
import hotel.service.main.MailDomainTrashService;
import hotel.service.main.SettingService;
import hotel.service.security.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Controller
public class MailController 
{
    @Autowired
    private SettingService settingService;

    @Autowired
    private MailDomainService mailDomainService;  
    
    @Autowired
    private MailDomainSentService mailDomainSentService;  
    
    @Autowired
    private MailDomainTrashService mailDomainTrashService;  
    
    @Autowired
	private UserService userService;
    
    @GetMapping(path = "/mail/compose") 
  	public String newMail(Model model) 
  	{
      	model.addAttribute("appTitle", settingService.getSetting(1).getTitle());
        return "/main/mail/compose";
  	}
   
    @GetMapping(path = "/mail/view/{id}") 
	public ModelAndView viewMail(@PathVariable long id) 
	{
    	int mail = 0, mailSent = 0, mailTrash = 0;
    	List<MailDomain> mails = mailDomainService.getMailDomains(); 
    	List<MailDomainSent> sentMails = mailDomainSentService.getMailDomainSents(); 
    	List<MailDomainTrash> trashMails = mailDomainTrashService.getMailDomainTrashs(); 
    	
    	for (int i = 0; i < mails.size(); i++) 
    	{
    		if(mails.get(i).getId() == id)
    		{
    			mail = 1;
    		}
    	}
    	
    	for (int i = 0; i < sentMails.size(); i++) 
    	{
    		if(sentMails.get(i).getId() == id)
    		{
    			mailSent = 1;
    		}
    	}
    	
    	for (int i = 0; i < trashMails.size(); i++) 
    	{
    		if(trashMails.get(i).getId() == id)
    		{
    			mailTrash = 1;
    		}
    	}
    	ModelAndView modelAndView = new ModelAndView();
    	if(mail == 1)
    	{ 	MailDomain mailDomain = mailDomainService.getMailDomain(id);
    		mailDomain.setReadStatus(2);
    		mailDomainService.updateMailDomain(mailDomain);
    		modelAndView.addObject("mail", mailDomainService.getMailDomain(id));    }
    	if(mailSent == 1)
    	{	MailDomainSent mailDomainSent = mailDomainSentService.getMailDomainSent(id);
    		mailDomainSent.setReadStatus(2);
			mailDomainSentService.updateMailDomainSent(mailDomainSent);
			modelAndView.addObject("mail", mailDomainSentService.getMailDomainSent(id));  }
    	if(mailTrash == 1)
    	{	MailDomainTrash mailDomainTrash = mailDomainTrashService.getMailDomainTrash(id);
			mailDomainTrash.setReadStatus(2);
			mailDomainTrashService.updateMailDomainTrash(mailDomainTrash);
			modelAndView.addObject("mail", mailDomainTrashService.getMailDomainTrash(id));  }
    	modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
        modelAndView.setViewName("/main/mail/view");
		return modelAndView;
	}

    @GetMapping(path = "/mailbox")
    public ModelAndView showMailbox() throws Exception 
    {
        downloadEmails(1); 
        downloadEmails(2); 
        downloadEmails(3);     

        int newCount=0, openCount=0;
        List<MailDomain> mails = mailDomainService.getMailDomains(); 
        List<MailDomain> mails2 = new ArrayList();
        for (int i = mails.size()-1; i >= 0; i--) 
		{ 	
        	mails2.add(mails.get(i));
		}

        for (int i = 0; i < mails.size(); i++) 
        {
            if(mails.get(i).getReadStatus() == 1)
            {
                newCount++;
            }
            if(mails.get(i).getReadStatus() == 2)
            {
                openCount++;
            }
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("size", mails.size());
        modelAndView.addObject("newCount", newCount);
        modelAndView.addObject("open", openCount);       
        modelAndView.addObject("mails", mails2);
        modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
        
        List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("/main/mail/mailbox");
		return modelAndView;
    }
    
    // get inbox mails
    @RequestMapping(path = "/mail/inbox", method = RequestMethod.GET) 
    @ResponseBody   
    public ModelAndView getInboxMails() 
    {  	
    	List<MailDomain> mails = mailDomainService.getMailDomains();
    	List<MailDomain> mails2 = new ArrayList();
        for (int i = mails.size()-1; i >= 0; i--) 
 		{ 	
         	mails2.add(mails.get(i));
 		}   	
    	// ModelAndView holds both to make it possible for a controller to return both model and view in a single return value.
    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.addObject("mails", mails2);
        modelAndView.setViewName("/main/mail/mailBoxBody2");
    	return modelAndView;
    }
    
    // get sent mails
    @RequestMapping(path = "/mail/sent", method = RequestMethod.GET) 
    @ResponseBody   
    public ModelAndView getSentMails() 
    {  	
    	List<MailDomainSent> mails = mailDomainSentService.getMailDomainSents();
    	List<MailDomainSent> mails2 = new ArrayList();
        for (int i = mails.size()-1; i >= 0; i--) 
 		{ 	
         	mails2.add(mails.get(i));
 		}
    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.addObject("mails", mails2);
        modelAndView.setViewName("/main/mail/mailBoxBody2");
    	return modelAndView;
    }

    // get trash mails
    @RequestMapping(path = "/mail/trash", method = RequestMethod.GET) 
    @ResponseBody   
    public ModelAndView getTrashMails() 
    {  	   	
    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.addObject("mails", mailDomainTrashService.getMailDomainTrashs());
        modelAndView.setViewName("/main/mail/mailBoxBody2");
    	return modelAndView;
    }
    
    // to delete chosen mails   
    @RequestMapping(path = "/mails/{mails}", method = RequestMethod.GET) 
    @ResponseBody   
    public ModelAndView deleteMail( @PathVariable List<Integer> mails) throws MessagingException 
    {  	  
    	int emailType = 0;
    	int id = 0;
    	for (int j = 0; j < mails.size(); j++) 
       	{ 
    		id = mails.get(j);   	         	   
        }       
    	if(mailDomainService.getMailDomain(id) != null)
        {	emailType=1;	}   
    	if(mailDomainSentService.getMailDomainSent(id) != null)
        {	emailType=2;	}   
    	if(mailDomainTrashService.getMailDomainTrash(id) != null)
        {	emailType=3;	}   
    	
    	
    	deleteChosenMailFromGmailmails(mails, emailType);  
    	for (int i = 0; i < mails.size(); i++) 
    	{		
    		if(emailType == 1)
    		{ 	            
	            mailDomainService.deleteMailDomain(mails.get(i));
    		}
    		if(emailType == 2)
    		{ 	            
	            mailDomainSentService.deleteMailDomainSent(mails.get(i));
    		}
    		if(emailType == 3)	
    		{	
    			mailDomainTrashService.deleteMailDomainTrash(mails.get(i)); 
    		} 		 
    	}  
    	int newCount=0, openCount=0;
        List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
        for (int i = 0; i < mailsList.size(); i++) 
        {
            if(mailsList.get(i).getReadStatus() == 1)
            {
                newCount++;
            }
            if(mailsList.get(i).getReadStatus() == 2)
            {
                openCount++;
            }
        }
    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.addObject("size", mailsList.size());
    	modelAndView.addObject("newCount", newCount);
    	modelAndView.addObject("open", openCount);
        if(emailType == 1)	
        {	List<MailDomain> mails1 = mailDomainService.getMailDomains();
	    	List<MailDomain> mails2 = new ArrayList();
	        for (int i = mails1.size()-1; i >= 0; i--) 
	 		{ 	
	         	mails2.add(mails1.get(i));
	 		}       	
	        modelAndView.addObject("mails", mails2);	
	    }
        if(emailType == 2)	
        {	List<MailDomainSent> mails1 = mailDomainSentService.getMailDomainSents();
	    	List<MailDomainSent> mails2 = new ArrayList();
	        for (int i = mails1.size()-1; i >= 0; i--) 
	 		{ 	
	         	mails2.add(mails1.get(i));
	 		}       	
	        modelAndView.addObject("mails", mails2);	
	    }
        if(emailType == 3)	
        {	modelAndView.addObject("mails", mailDomainTrashService.getMailDomainTrashs());	}	
        modelAndView.setViewName("/main/mail/mailBoxBody");
    	return modelAndView;
    }
    
    // to mark mails as read  
    @RequestMapping(path = "/markasread/{mails}", method = RequestMethod.GET) 
    @ResponseBody 
    public ModelAndView markMailAsRead( @PathVariable List<Integer> mails) throws MessagingException 
    {  	   	
    	int temp = 0;
    	for (int i = 0; i < mails.size(); i++) 
    	{
    		if(mailDomainService.getMailDomain(mails.get(i)) != null )
    		{	MailDomain mail =  mailDomainService.getMailDomain(mails.get(i));
	    		if(mail.getReadStatus() == 1)
	    		{	mail.setReadStatus(2);    		 
	    			mailDomainService.updateMailDomain(mail);   } 
	    		temp = 1;
	    	}
    		if(mailDomainSentService.getMailDomainSent(mails.get(i)) != null)
    		{ 	MailDomainSent mail =  mailDomainSentService.getMailDomainSent(mails.get(i));
	    		if(mail.getReadStatus() == 1)
	    		{	mail.setReadStatus(2);    		 
	    			mailDomainSentService.updateMailDomainSent(mail);   } 
	    		temp = 2;
	    	}
    		if(mailDomainTrashService.getMailDomainTrash(mails.get(i)) != null)
    		{ 	MailDomainTrash mail =  mailDomainTrashService.getMailDomainTrash(mails.get(i));
	    		if(mail.getReadStatus() == 1)
	    		{	mail.setReadStatus(2);    		 
	    			mailDomainTrashService.updateMailDomainTrash(mail);   } 
	    		temp = 3;
	    	}
    	}  
    	ModelAndView modelAndView = new ModelAndView();
    	if(temp == 1) 
    	{ 
    		List<MailDomain> mails1 = mailDomainService.getMailDomains();
	    	List<MailDomain> mails2 = new ArrayList();
	        for (int i = mails1.size()-1; i >= 0; i--) 
	 		{ 	
	         	mails2.add(mails1.get(i));
	 		}       	
	        modelAndView.addObject("mails", mails2);
    	}
    	if(temp == 2) 
    	{ 
    		List<MailDomainSent> mails1 = mailDomainSentService.getMailDomainSents();
	    	List<MailDomainSent> mails2 = new ArrayList();
	        for (int i = mails1.size()-1; i >= 0; i--) 
	 		{ 	
	         	mails2.add(mails1.get(i));
	 		}       	
	        modelAndView.addObject("mails", mails2);
    	}
    	if(temp == 3) 
    	{ 	modelAndView.addObject("mails", mailDomainTrashService.getMailDomainTrashs());		}
    	int newCount=0, openCount=0;
        List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
        for (int i = 0; i < mailsList.size(); i++) 
        {
            if(mailsList.get(i).getReadStatus() == 1)
            {
                newCount++;
            }
            if(mailsList.get(i).getReadStatus() == 2)
            {
                openCount++;
            }
        }
        modelAndView.addObject("size", mailsList.size());
        modelAndView.addObject("newCount", newCount);
        modelAndView.addObject("open", openCount);       	
        modelAndView.setViewName("/main/mail/mailBoxBody");
    	return modelAndView;
    }
    
    @RequestMapping(path = "/get-mails-status/{mails}", method = RequestMethod.GET) 
    @ResponseBody 
    public Integer mailExistenceCheck(@PathVariable List<Integer> mails) throws Exception 
    {
    	int status = 0;
    	for (int i = 0; i < mails.size(); i++) 
    	{
    		if( mailDomainService.getMailDomain(mails.get(i)) != null )
    		{
    			status = 1;
    		}
    	}    	
    	return status;
    }
    
    // method mails from gmail imap
    public void downloadEmails(Integer input) throws Exception
    {
    	int size = 0;
    	String host = "imap.gmail.com";       
        String port = "993";
        String protocol = "imap";  
        String userName = "";         // add username
        String password = "";         // add password
        
        Properties properties = new Properties();
        
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
 
        // SSL setting
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),"javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol),"false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol),String.valueOf(port));
        
        Session session = Session.getDefaultInstance(properties);

        try
        {
        	System.out.println("Connecting please wait....");
            Store store = session.getStore(protocol);
            
            store.connect(userName, password);
            System.out.println("Connected to mail via "+host);
            
            System.out.println("Getting INBOX..");
            
	        Folder[] folderList = store.getFolder("[Gmail]").list();
	        for (int i = 0; i < folderList.length; i++) 
	        {
	              System.out.println(folderList[i].getFullName());
	        }
            
            Folder folderInbox = null;
            if(input == 1)
            {   
	            folderInbox = store.getFolder("INBOX");   }
            
            if(input == 2)
            {  
	           folderInbox = store.getFolder("[Gmail]/Sent Mail");   }

            if(input == 3)
            {  
  	           folderInbox = store.getFolder("[Gmail]/Trash");   }

            folderInbox.open(Folder.READ_ONLY); 

            Message[] messages = folderInbox.getMessages();

            System.out.println("You have "+messages.length+" mails in your INBOX");                		   
	        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm a");            
	         
	        List<MailDomain> mails1 = null;
	        List<MailDomainSent> mails2 = null;
	        List<MailDomainTrash> mails3 = null;
	        if(input == 1)
	        {		mails1 = mailDomainService.getMailDomains(); 
	            	size = mails1.size(); }	            
	        if(input == 2)
	        {		mails2 = mailDomainSentService.getMailDomainSents();  
	            	size = mails2.size(); }	            
	        if(input == 3)
	        {		mails3 = mailDomainTrashService.getMailDomainTrashs();  
	            	size = mails3.size(); }
	            
	        int tempExists;  
	        Message msg = null;
	        Address[] fromAddress;
	        String from="", subject="", toList="",  ccList="", sentDate="", contentType="", messageContent="";
	        Date sentDateInDate; 
	        if(size == 0)
            {	     	
		        for (int i = 0; i < messages.length ; i++)
				{
				      tempExists = 0;
				      msg = messages[i];
				      fromAddress = msg.getFrom();
				      from = fromAddress[0].toString();
				      subject = msg.getSubject();
				      toList = parseAddresses(msg.getRecipients(Message.RecipientType.TO));
				      ccList = parseAddresses(msg.getRecipients(Message.RecipientType.CC));
				      sentDateInDate = msg.getSentDate();          
				      sentDate = formatter.format(sentDateInDate);                
				      contentType = msg.getContentType();
				
				      // get content of message
				      messageContent = getText(msg);		            				               				                	
				      if(input == 1)
				      {  // create mails in database  
					     MailDomain mail  = new MailDomain();              
					     mail.setFromAddress(from);
					     mail.setSubject(subject);
					     mail.setToList(toList); 
					     mail.setCcList(ccList);
					     mail.setSentDate(sentDate);                    
					     mail.setSentDateInDate(sentDateInDate);                   
					     mail.setMailContent(messageContent); 
					     mail.setReadStatus(1);
					     mailDomainService.createMailDomain(mail);   }
				       if(input == 2)
				       {  // create mails in database
					      MailDomainSent mail  = new MailDomainSent();              
					      mail.setFromAddress(from);
					      mail.setSubject(subject);
					      mail.setToList(toList); 
					      mail.setCcList(ccList);
					      mail.setSentDate(sentDate);                    
					      mail.setSentDateInDate(sentDateInDate);                   
					      mail.setMailContent(messageContent); 
					      mail.setReadStatus(1);
					      mailDomainSentService.createMailDomainSent(mail);   }
				        if(input == 3)
				        {  // create mails in database
					       MailDomainTrash mail  = new MailDomainTrash();              
					       mail.setFromAddress(from);
					       mail.setSubject(subject);
					       mail.setToList(toList); 
					       mail.setCcList(ccList);
					       mail.setSentDate(sentDate);                    
					       mail.setSentDateInDate(sentDateInDate);                   
					       mail.setMailContent(messageContent); 
					       mail.setReadStatus(1);
					       mailDomainTrashService.createMailDomainTrash(mail);   }	            
	            }    
        	}     	    
	        if(size != messages.length && size != 0 )
		    {		 	        
		          for (int i = size; i < messages.length ; i++)
		     	  {
		     			   tempExists = 0;
		     			   msg = messages[i];
		     			   fromAddress = msg.getFrom();
		     			   from = fromAddress[0].toString();
		     			   subject = msg.getSubject();
		     			   toList = parseAddresses(msg.getRecipients(Message.RecipientType.TO));
		     			   ccList = parseAddresses(msg.getRecipients(Message.RecipientType.CC));
		     			   sentDateInDate = msg.getSentDate();          
		     			   sentDate = formatter.format(sentDateInDate);                
		     			   contentType = msg.getContentType();
		     			
		     			   // get content of message
		     			   messageContent = getText(msg);
		     			   
		     			   if(input == 1)
		                   {    for (MailDomain mailDomain : mails1) 
			                    {             
			                         if(mailDomain.getSentDate().equals(sentDate))  
			                         {
			                             tempExists = 1;                                        
			                         }                       
			                    }   
		                	}
		                	if(input == 2)
		                	{   for (MailDomainSent mailDomain : mails2) 
			                    {             
			                         if(mailDomain.getSentDate().equals(sentDate))  
			                         {
			                             tempExists = 1;                                        
			                         }                       
			                    }   
		                	}
		                	if(input == 3)
		                	{   for (MailDomainTrash mailDomain : mails3) 
			                    {             
			                         if(mailDomain.getSentDate().equals(sentDate))  
			                         {
			                             tempExists = 1;                                        
			                         }                       
			                    }   
		                	}
		                	
		                    if(tempExists != 1)
		                    {                            
			                     if(input == 1)
			                   	 {  // create mails in database 	
			   	                    MailDomain mail  = new MailDomain();              
			   	                    mail.setFromAddress(from);
			   	                    mail.setSubject(subject);
			   	                    mail.setToList(toList); 
			   	                    mail.setCcList(ccList);
			   	                    mail.setSentDate(sentDate);                    
			   	                    mail.setSentDateInDate(sentDateInDate);                   
			   	                    mail.setMailContent(messageContent); 
			   	                    mail.setReadStatus(1);
			   	                    mailDomainService.createMailDomain(mail);   }
			                   	 if(input == 2)
			                   	 {  // create sent mails in database			                   		
			   	                    MailDomainSent mail  = new MailDomainSent();              
			   	                    mail.setFromAddress(from);
			   	                    mail.setSubject(subject);
			   	                    mail.setToList(toList); 
			   	                    mail.setCcList(ccList);
			   	                    mail.setSentDate(sentDate);                    
			   	                    mail.setSentDateInDate(sentDateInDate);                   
			   	                    mail.setMailContent(messageContent); 
			   	                    mail.setReadStatus(1);
			   	                    mailDomainSentService.createMailDomainSent(mail);   }
			                   	 if(input == 3)
			                   	 {  // create trash mails in database
			   	                    MailDomainTrash mail  = new MailDomainTrash();              
			   	                    mail.setFromAddress(from);
			   	                    mail.setSubject(subject);
			   	                    mail.setToList(toList); 
			   	                    mail.setCcList(ccList);
			   	                    mail.setSentDate(sentDate);                    
			   	                    mail.setSentDateInDate(sentDateInDate);                   
			   	                    mail.setMailContent(messageContent); 
			   	                    mail.setReadStatus(1);
			   	                    mailDomainTrashService.createMailDomainTrash(mail);   }  
		                    }        	
			                // print out details of each message
			                System.out.println("Message #" + (i + 1) + ":");
			                System.out.println("\t From: " + from);
			                System.out.println("\t To: " + toList);
			                System.out.println("\t CC: " + ccList);
			                System.out.println("\t Subject: " + subject);
			                System.out.println("\t Sent Date: " + sentDate);
			                System.out.println("\t Message: " +  messageContent);
			                System.out.println("\t Content type: " +  contentType); 	
		     	  }
		    }	    
            // disconnect
            folderInbox.close(false);
            store.close();
        }
        catch (NoSuchProviderException ex)
        {
            System.out.println("No provider for protocol: " + "pop");
            ex.printStackTrace();
        }
        catch (MessagingException ex)
        {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }     
    }
    
    private static String parseAddresses(Address[] address) 
    {
        String listAddress = "";
 
        if (address != null)
        {
            for (int i = 0; i < address.length; i++) 
            {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) 
        {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }
 
        return listAddress;
    }
    

 	public static String getText(Part p) throws MessagingException, IOException 
 	{
 		if (p.isMimeType("text/*")) 
 		{
 			String s = (String) p.getContent();
 			return s;
 		}

 		if (p.isMimeType("multipart/alternative")) 
 		{
 			Multipart mp = (Multipart) p.getContent();
 			String text = null;
 			for (int i = 0; i < mp.getCount(); i++) 
 			{
 				Part bp = mp.getBodyPart(i);
 				if (bp.isMimeType("text/plain")) 
 				{
 					if (text == null)
 						text = getText(bp);
 					continue;
 				} 
 				else 
 				if (bp.isMimeType("text/html")) 
 				{
 					String s = getText(bp);
 					String result="";
 					if (s != null)					 
 					{	result = org.jsoup.Jsoup.parse(s).text();					
 						return result;
 					}
 				} 
 				else 
 				{
 					return getText(bp);
 				}
 			}
 			return text;
 		} 
 		else
 		if (p.isMimeType("multipart/*")) 
 		{
 			Multipart mp = (Multipart) p.getContent();
 			for (int i = 0; i < mp.getCount(); i++) 
 			{
 				String s = getText(mp.getBodyPart(i));
 				if (s != null)
 				{	return s;  }
 			}
 		}
 		return null;
 	}
 	
    MailSender mailSender;  
 	
 	@RequestMapping(value = "/mail/send/", method = RequestMethod.GET)
 	@ResponseBody
  	public String sendMail(@RequestParam MultiValueMap<String, String> formInputValues) 
  	{ 		 
 		String toList = null, subject = null, messageContent = null;		
		Iterator<String> mapIterator = formInputValues.keySet().iterator();
		while (mapIterator.hasNext()) 
		{
			String key = mapIterator.next();
			if(key.equals("to"))
			{
				toList = formInputValues.get(key).get(0); 
				System.out.println(toList);
			}
			if(key.equals("subject"))
			{				
				subject = formInputValues.get(key).get(0); 
				System.out.println(subject);
			}
			if(key.equals("message"))
			{
				messageContent = formInputValues.get(key).get(0); 
				System.out.println(messageContent);
			}
		}
         Properties properties = new Properties();
         properties.put("mail.smtp.host", "smtp.gmail.com");
         properties.put("mail.smtp.port", "587");
         properties.put("mail.smtp.auth", "true");
         properties.put("mail.smtp.starttls.enable", "true");
         
         try 
         {
             Authenticator auth = new Authenticator() 
             {
                 public PasswordAuthentication getPasswordAuthentication() 
                 {
                     return new PasswordAuthentication("", "");  // add username and password
                 }
             };
             Session session = Session.getInstance(properties, auth);
             
             Message message = new MimeMessage(session);
             message.setFrom(new InternetAddress(""));    // add email address        
             message.setRecipient(Message.RecipientType.TO, new InternetAddress(toList));
             message.setSentDate(new Date());
             message.setSubject(subject);
             message.setText(messageContent); 
             
             Transport.send(message);	
         } catch (Exception e) {
             e.printStackTrace();
         }
         return "<div class=\"mailbox-header d-flex justify-content-between\" style=\"border-bottom: 1px solid #e8e8e8;\"><h5 class=\"inbox-title\" style=\"color:green;\">Success! Mail send.</h5></div>";
  	} 
 	
 	
    public void deleteChosenMailFromGmailmails(List<Integer> mails, int emailType) throws MessagingException
    {
    	String host = "imap.gmail.com";
        String port = "993";
        String protocol = "imap";  
        String userName = "";           // add username
        String password = "";           // add password     
        
        Properties properties = new Properties();
        
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
 
        // SSL setting
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),"javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol),"false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol),String.valueOf(port));
        
        Session session = Session.getDefaultInstance(properties);

       try
       {
           System.out.println("Connecting please wait....");
           Store store = session.getStore(protocol);
           
           store.connect(userName, password);
           System.out.println("Connected to mail via "+host);
           
           System.out.println("Getting INBOX..");
           
           Folder folderInbox = null;
           if(emailType == 1)
           {	 folderInbox = store.getFolder("INBOX");	}
           if(emailType == 2)
           {     folderInbox = store.getFolder("[Gmail]/Sent Mail");   }
           if(emailType == 3)
           {     folderInbox = store.getFolder("[Gmail]/Trash");    }
           
           folderInbox.open(Folder.READ_WRITE);

           Message[] messages = folderInbox.getMessages();
           System.out.println("You have "+messages.length+" mails in your INBOX");

           for (int j = 0; j < mails.size(); j++) 
       	   {
           		for (int i = 0; i < messages.length; i++)
           		{    
           			if(emailType == 1)			          			
           			{   if(messages[i].getSentDate().equals(mailDomainService.getMailDomain(mails.get(j)).getSentDateInDate()))
					    {
	        			   messages[i].setFlag(Flags.Flag.DELETED, true);
					    }
           			}
           			if(emailType == 2)	
           			{    if(messages[i].getSentDate().equals(mailDomainSentService.getMailDomainSent(mails.get(j)).getSentDateInDate()))
           				 {
           					messages[i].setFlag(Flags.Flag.DELETED, true); 	
           				 }
           			}
           			if(emailType == 3)	
           			{    if(messages[i].getSentDate().equals(mailDomainTrashService.getMailDomainTrash(mails.get(j)).getSentDateInDate()))
           				 {
           					messages[i].setFlag(Flags.Flag.DELETED, true); 	
           				 }
           			}      		   
           	   }      	         	   
           }           
		   // disconnect
		   folderInbox.close(true);   // Close this Folder.
		   store.close();	
       }
       catch (NoSuchProviderException ex)
       {
           System.out.println("No provider for protocol: " + "pop");
           ex.printStackTrace();
       }  
    }	
}
