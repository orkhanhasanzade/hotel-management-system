package hotel.controller.security;


import javax.mail.PasswordAuthentication;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hotel.service.security.UserService;

import hotel.domain.security.User;

@Controller
public class LoginController 
{	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping(value={"/login"}, method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", defaultValue = "false") boolean loginError,
					    @RequestParam(value = "invalid-session", defaultValue = "false") boolean invalidSession)
	{
		ModelAndView modelAndView = new ModelAndView();
		if(loginError)
		{
			modelAndView.addObject("message", "Wrong username and password");
		}
		if(invalidSession)
		{
			modelAndView.addObject("message", "Session expired, please re-login");
		}		
		
		modelAndView.setViewName("/security/login.html");
		return modelAndView;
	}
	
	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration()
	{
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("/security/registration.html");
		return modelAndView;
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) 
	{
		
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.getUser(user.getEmail());
		user.setReal_password(user.getPassword());
		if (bindingResult.hasErrors())
		{
			modelAndView.setViewName("/security/registration.html");
		} 
		else
		{
			if (userExists != null) 
			{
				modelAndView.addObject("errorMessage", "Email has already been taken!");
				modelAndView.addObject("user", new User());
				modelAndView.setViewName("/security/registration.html");
			}
			else
			{   userService.createUser(user);
				modelAndView.addObject("successMessage", "You've successfully registered to app!");
				modelAndView.addObject("user", new User());
				modelAndView.setViewName("/security/registration.html");
			}
		}	
		return modelAndView;
	}
	
	@RequestMapping(value="/forgot-password", method = RequestMethod.GET)
	public ModelAndView forgotUserPassword()
	{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/security/forgotPassword.html");
		return modelAndView;
	}
	
    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
	public ModelAndView processForgotPasswordForm(ModelAndView modelAndView, @RequestParam("email") String userEmail, HttpServletRequest request)
    {
		User user = userService.findUserByEmail(userEmail);
		if (user == null) 
		{
			modelAndView.addObject("errorMessage", "We didn't find an account for that e-mail address.");
		} else 
		  {				
				user.setResetToken(UUID.randomUUID().toString()); 			
				userService.updateUser(user);  
				String appUrl = request.getScheme() + "://" + request.getServerName() + ":8080";			
				sendMail(user.getEmail(), 
							"Password Reset Request", "To reset your password, click the link below:\n" + 
									appUrl + "/reset?token=" + user.getResetToken());			
				modelAndView.addObject("successMessage", "A password reset link has been sent to " + userEmail);
			}

			modelAndView.setViewName("/security/forgotPassword.html");
			return modelAndView;
    }

	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public ModelAndView displayResetPasswordPage( @RequestParam("token") String token) 
	{	
		ModelAndView modelAndView = new ModelAndView();
		User user = userService.findUserByResetToken(token);
		if (user != null) 
		{ 
			modelAndView.addObject("resetToken", token);
		} else // Token not found in DB
		  {  modelAndView.addObject("errorMessage", "Oops!  This is an invalid password reset link."); }

		modelAndView.setViewName("/security/resetPassword.html");
		return modelAndView;
	}
	
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public ModelAndView setNewPassword(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) 
	{
		User user = userService.findUserByResetToken(requestParams.get("resetToken"));

		if (user != null) 
		{						
			user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));		   			
			user.setResetToken(null);					
			userService.updateUser(user);	  

			redir.addFlashAttribute("successMessage", "You have successfully reset your password.  You may now login.");
			modelAndView.setViewName("redirect:login");
			return modelAndView;				
		} else {
				 	modelAndView.addObject("errorMessage", "Oops!  This is an invalid password reset link.");
				 	modelAndView.setViewName("/security/resetPassword.html");
			   }			
		return modelAndView;
	 }

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) 
	{
		return new ModelAndView("redirect:login");
	}
	
	// https://javapapers.com/spring/spring-email-sending-using-gmail-smtp/
	public String sendMail(String to, String subject, String messageContent) 
	{ 		 
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
	             message.setFrom(new InternetAddress(""));            // add email address
	             message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
	             message.setSentDate(new Date());
	             message.setSubject(subject);
	             message.setText(messageContent); 
	             
	             Transport.send(message);	
	         } catch (Exception e) {
	             e.printStackTrace();
	         }
	   return null;
	 } 
	    
}
