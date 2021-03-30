package hotel.controller.main;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


import hotel.domain.main.Setting;
import hotel.service.main.SettingService;


@Controller
public class SettingsController 
{
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService; 
	
	@GetMapping(path="/list-settings")
	public ModelAndView showSettings() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		modelAndView.addObject("setting", settingService.getSetting(1));
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/main/settings/settingList");
		return modelAndView;
	}
	
	@PostMapping(path="/settings/save") 
	public String saveSettings(@Valid @ModelAttribute Setting setting) 
	{	
		settingService.updateSetting(setting); 
		String url = "list-settings?lang=" + setting.getLanguage();	
		return "redirect:/" + url;
	}
	
}
