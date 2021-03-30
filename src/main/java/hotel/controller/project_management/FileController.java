package hotel.controller.project_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.main.MailDomain;
import hotel.domain.project_management.File;
import hotel.service.main.MailDomainService;
import hotel.service.main.SettingService;
import hotel.service.project_management.FileService;
import hotel.service.project_management.ProjectService;
import hotel.service.security.UserService;


@Controller
public class FileController 
{	
	private static String UPLOADED_FOLDER = "";  // add folder
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MailDomainService mailDomainService; 

	@GetMapping(path="/list-files")
	public ModelAndView showFiles() 
	{	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ModelAndView modelAndView = new ModelAndView();
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());
		modelAndView.addObject("files", fileService.getFiles());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/project_management/files/fileList");
		return modelAndView;
	}
	
	@RequestMapping(path = "/file/create", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView createFile() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("projects", projectService.getProjects());
	    modelAndView.setViewName("/project_management/files/fileModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/file/create")
	public String createFile(@Valid @ModelAttribute File file, @RequestParam("file") MultipartFile fileVariable) throws IOException 
	{	
		  byte[] bytes = fileVariable.getBytes();
	      Path path = Paths.get(UPLOADED_FOLDER + fileVariable.getOriginalFilename());
	      Files.write(path, bytes);		
	    
		    file.setFilePath(fileVariable.getOriginalFilename()); 	    
		    file.setType(fileVariable.getContentType());
		    file.setSize(fileVariable.getSize());   
		
		fileService.createFile(file);
		return "redirect:/list-files";
	}

	@RequestMapping(path="/file/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editFile(@PathVariable long id) 
	{		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("file", fileService.getFile(id));
		modelAndView.addObject("projects", projectService.getProjects());
	    modelAndView.setViewName("/project_management/files/fileModalBodyEdit");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateFile")
	public String editFile(File file)
	{
		File file2 = fileService.getFile(file.getId());
	    file2.setName(file.getName());
	    file2.setProject(file.getProject());
		
		fileService.updateFile(file2);
		return "redirect:/list-files";
	}	

	@GetMapping(path="/file/delete/{id}")
	public String deleteFile(@PathVariable long id) 
	{
		if (fileService.deleteFile(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-files";
	}
	
}
