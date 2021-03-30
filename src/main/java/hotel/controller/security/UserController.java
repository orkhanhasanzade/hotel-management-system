package hotel.controller.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import hotel.service.main.MailDomainService;
import hotel.service.main.SettingService;
import hotel.service.security.UserService;
import hotel.domain.main.MailDomain;
import hotel.domain.security.User;

@Controller
@RequestMapping("/profile")
public class UserController 
{
	private static String UPLOADED_FOLDER = "";   // add folder
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private MailDomainService mailDomainService; 
	
	@Autowired
	private SettingService settingService;

	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public ModelAndView showProfile() 
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();	
		
		ModelAndView modelAndView = new ModelAndView();		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());				
		modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		modelAndView.addObject("choice", "INFO");
		modelAndView.addObject("user", userService.findUserByEmail(auth.getName())); 	
		modelAndView.setViewName("/security/profile");
		return modelAndView;
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public ModelAndView getProfileContent(@PathVariable long id) 
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();	
		ModelAndView modelAndView = new ModelAndView();
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());				
		modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		modelAndView.addObject("choice", "EDIT");
		modelAndView.addObject("user", userService.getUser(id));
		modelAndView.setViewName("/security/profile");
		return modelAndView;
	}
	
	@RequestMapping(value = "/changepass/{id}", method = RequestMethod.GET)
	public ModelAndView getProfilePassword(@PathVariable long id) 
	{
		User user = new User(); 	
		user.setId(id);
		user.setName("");
		user.setPassword("");
		
		ModelAndView modelAndView = new ModelAndView();
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
        modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		modelAndView.addObject("choice", "PASS");
		modelAndView.addObject("user", user);
		modelAndView.setViewName("/security/profile");
		return modelAndView;
	}
	
	@PostMapping(path="/savepass")
	public ModelAndView savePassword(User user)
	{		
	    User userTemp = userService.getUser(user.getId());
	    if(passwordEncoder.matches(user.getName(), userTemp.getPassword()))
	    {   
	    	userTemp.setReal_password(user.getPassword());
	    	userTemp.setActive(1);
	    	userTemp.setPassword( bCryptPasswordEncoder.encode(user.getPassword()));		    	
		    userService.updateUser(userTemp);		    

		    ModelAndView modelAndView = new ModelAndView();
		    modelAndView.addObject("choice", "PASS");
		    modelAndView.addObject("process", "SUCCESS");
		    modelAndView.addObject("success", "You successfully changed your password.");
			modelAndView.setViewName("/security/profile");
			return modelAndView;
	    }
	    else		
	    {
	    	 System.out.println("Old password do not match");	    	 	    	 
	    	 ModelAndView modelAndView = new ModelAndView();
	    	 modelAndView.addObject("choice", "PASS");
	    	 modelAndView.addObject("process", "ERROR");
	    	 modelAndView.addObject("error", "Error : Check your old password!");
		     modelAndView.setViewName("/security/profile");
		     return modelAndView;
	    }
	}

	@PostMapping(path="/save")
	public ModelAndView saveUser(@Valid User user)
	{
		if(userService.getUser(user.getId()).getImagePath() != null)
		{
			user.setImagePath(userService.getUser(user.getId()).getImagePath());
		}
		
		user.setActive(1);
		user.setReal_password(userService.getUser(user.getId()).getReal_password());
		user.setPassword(userService.getUser(user.getId()).getPassword());
		
		userService.updateUser(user);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("choice", "INFO");
		modelAndView.setViewName("/security/profile");
	    return modelAndView;
	}
	
	@PostMapping(path="/image/save")
	public String saveUserImage(@RequestParam("file") MultipartFile file) throws IOException 
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();	
		User user = userService.findUserByEmail(auth.getName());
		
		byte[] bytes = file.getBytes();
		Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());    
		Files.write(path, bytes);
		user.setImagePath(file.getOriginalFilename());
		
		userService.updateUser(user);
		return "redirect:/profile/info";
	}

}
