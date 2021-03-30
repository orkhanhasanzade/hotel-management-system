package hotel.controller.project_management;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.project_management.Project;
import hotel.domain.project_management.Task;
import hotel.service.hr.EmployeeService;
import hotel.service.main.ReservationService;
import hotel.service.main.SettingService;
import hotel.service.project_management.ProjectService;
import hotel.service.project_management.TaskService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class ProjectController 
{	
	@Autowired
	private ProjectService projectService;	 
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;  
	
	@GetMapping(path="/project-report")
	public ModelAndView showProjectReport() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tasks", taskService.getTasks());
		modelAndView.addObject("employees", employeeService.getEmployees());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
        modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
    	modelAndView.setViewName("/project_management/report/report");
		return modelAndView;
	}
	
	@GetMapping(path="/projects-dashboard")
	public ModelAndView showDashboard() throws ParseException 
	{	
		List<Task> list = taskService.getTasks();
		int i;
		int newCount=0, inProgressCount=0, completedCount=0, onHoldCount=0, cancelledCount=0;
		for (i = 0; i < list.size(); i++) 
		{	
			if(list.get(i).getStatus().equals("New"))
			{	
				newCount++;
			}	
			if(list.get(i).getStatus().equals("Progress"))
			{	
				inProgressCount++;
			}	
			if(list.get(i).getStatus().equals("Completed"))
			{	
				completedCount++;
			}	
			if(list.get(i).getStatus().equals("Hold"))
			{	
				onHoldCount++;
			}	
			if(list.get(i).getStatus().equals("Cancelled"))
			{	
				cancelledCount++;
			}	
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("newCount", newCount);
		modelAndView.addObject("inProgressCount", inProgressCount);
		modelAndView.addObject("completedCount", completedCount);
		modelAndView.addObject("onHoldCount", onHoldCount);
		modelAndView.addObject("cancelledCount", cancelledCount);
		modelAndView.addObject("projectsCount", projectService.getProjects().size());
		modelAndView.addObject("tasksCount", taskService.getTasks().size());
		modelAndView.addObject("reservations", reservationService.getReservations());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
		modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
		modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/project_management/dashboard/dashboard");
		return modelAndView;
	}
	
	@GetMapping(path="/list-projects")
	public ModelAndView showProjects() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("projects", projectService.getProjects());
		modelAndView.addObject("employees", employeeService.getEmployees());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());   
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
    	modelAndView.setViewName("/project_management/projects/projectList");
		return modelAndView;
	}

	@RequestMapping(path = "/project/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createProject() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/project_management/projects/projectModalBodyCreate");	
	    return modelAndView;
	}

	@PostMapping(path="/project/create")
	public String createProject(@Valid @ModelAttribute Project project) 
	{	        
		projectService.createProject(project);
		return "redirect:/list-projects";
	}
	
	@RequestMapping(path="/project/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editProject(@PathVariable long id) 
	{				
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("project",  projectService.getProject(id));
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/project_management/projects/projectModalBodyEdit");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateProject")
	public String editProject(Project project)
	{
		projectService.updateProject(project);
		return "redirect:/list-projects";
	}	

	@GetMapping(path="/project/delete/{id}")
	public String deleteProject(@PathVariable long id) 
	{
		if (projectService.deleteProject(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-projects";
	}
	
	@RequestMapping(path="/projects/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getProjects(@PathVariable long id) 
	{
		ModelAndView modelAndView = new ModelAndView();
		List<Project> list = projectService.getProjects();
		List<Project> chosenProjects = new ArrayList<>();
		int i;
		if(id == 1) // all projects
		{
			modelAndView.addObject("chosenProjects", list);
			modelAndView.addObject("employees", employeeService.getEmployees());
		}
		else
		{	
			for (i = 0; i < list.size(); i++) 
			{	
				if(id == 2) // projects in progress
				{
					if(list.get(i).getStatus().equals("Progress"))
					{	
						chosenProjects.add(list.get(i));
					}		
				}
				if(id == 3) // completed projects 
				{
					if(list.get(i).getStatus().equals("Completed"))
					{			
						chosenProjects.add(list.get(i));
					}		
				}
				if(id == 4) // cancelled projects 
				{
					if(list.get(i).getStatus().equals("Cancelled"))
					{			
						chosenProjects.add(list.get(i));
					}
				}
			}
			modelAndView.addObject("chosenProjects", chosenProjects);	
			modelAndView.addObject("employees", employeeService.getEmployees());
		}
	    modelAndView.setViewName("/project_management/projects/projectContent");	
	    return modelAndView;	
	}
	
    @RequestMapping(path = "/projectInfo/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public ModelAndView showProjectDetails(@PathVariable long id) 
	{ 	
		Project project = projectService.getProject(id);				
		List<Task> tasks = new ArrayList<>();
		int i=0;
		for (Task temp : taskService.getTasks()) 
		{
	           if(temp.getProject().getId() == project.getId())
	           {
	        	  if(i <= 3) 
	        	  {	i++;
	        	   	tasks.add(temp); }	           		
	           }
	     }		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tasks", tasks);
		modelAndView.addObject("project", project);
		modelAndView.addObject("employees", employeeService.getEmployees());
	    modelAndView.setViewName("/project_management/tasks/projectModalBody");
	    return modelAndView;
	}
    
	@RequestMapping(path = "/getProject/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public ModelAndView showProjectInfo(@PathVariable long id) 
	{ 						
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("project", projectService.getProject(id));
	    modelAndView.setViewName("/project_management/files/projectModalBodyShow.html");
	    return modelAndView;
	}
	
}
