package hotel.controller.project_management;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import hotel.domain.hr.Employee;
import hotel.domain.project_management.Task;
import hotel.service.hr.EmployeeService;
import hotel.service.main.SettingService;
import hotel.service.project_management.ProjectService;
import hotel.service.project_management.TaskService;
import hotel.domain.main.MailDomain;
import hotel.service.security.UserService;
import hotel.service.main.MailDomainService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@Controller
public class TaskController 
{	
	@Autowired
	private TaskService taskService;	 
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MailDomainService mailDomainService;  
	
	@RequestMapping(path="/tasks-report", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showTasksReport( @RequestParam MultiValueMap<String, String> formInputValues)
	{			
		String employeeId = null, status = null, from = null, to = null;		
		Iterator<String> mapIterator = formInputValues.keySet().iterator();
		while (mapIterator.hasNext()) 
		{
			String key = mapIterator.next();
			if(key.equals("employeeId"))
			{
				employeeId = formInputValues.get(key).get(0); 
			}
			if(key.equals("status"))
			{				
				status = formInputValues.get(key).get(0); 
			}
			if(key.equals("from"))
			{
				from = formInputValues.get(key).get(0); 
			}
			if(key.equals("to"))
			{
				to = formInputValues.get(key).get(0); 
			}
		}
		long employeeIdLong;
		if(!employeeId.equals("All"))
		{
			 employeeIdLong = Long.parseLong(employeeId);
		}
		else
		{
			 employeeIdLong = 0;
		}
		List<Task> list = taskService.getTasks();
		List<Task> tasks = new ArrayList<>();
		int i=0;
		if(employeeIdLong == 0) 
		{
			if(status.equals("All"))
			{
				for (i = 0; i < list.size(); i++) 
				{		
					if (list.get(i).getStartDate().compareTo(from) > 0 && list.get(i).getEndDate().compareTo(to) < 0) 
					{
						tasks.add(list.get(i));
					}
				}
			}
			else
			{			
				for (i = 0; i < list.size(); i++) 
				{
					if(list.get(i).getStatus().equals(status))
					{
						if (list.get(i).getStartDate().compareTo(from) > 0 && list.get(i).getEndDate().compareTo(to) < 0) 
						{
							tasks.add(list.get(i));
						}
					}
				}
				
			}
		}
		else 
		{
			if(status.equals("All"))
			{
				
				for (i = 0; i < list.size(); i++) 
				{	
					if(list.get(i).getEmployee().getId() == employeeIdLong) 
					{
							if (list.get(i).getStartDate().compareTo(from) > 0 && list.get(i).getEndDate().compareTo(to) < 0) 
							{
								tasks.add(list.get(i));
							}
					}
				}	
			
			}
			else
			{
				for (i = 0; i < list.size(); i++) 
				{
					if(list.get(i).getStatus().equals(status))
					{
						if(list.get(i).getEmployee().getId() == employeeIdLong) 
						{							
							if (list.get(i).getStartDate().compareTo(from) > 0 && list.get(i).getEndDate().compareTo(to) < 0) 
							{
								tasks.add(list.get(i));
							}						
						}
					}
				}
			}			
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tasks", tasks) ;
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		
	    modelAndView.setViewName("/project_management/report/reportContent");	
	    return modelAndView;
	}
	
	@GetMapping(path="/list-tasks")
	public ModelAndView showTasks() 
	{	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tasks", taskService.getTasks());
		modelAndView.addObject("appTitle", settingService.getSetting(1).getTitle());
		
		List<MailDomain> mailsList = mailDomainService.getMailDomains(); 
		modelAndView.addObject("size", mailsList.size());		
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();			
        modelAndView.addObject("imagePath", userService.findUserByEmail(auth.getName()).getImagePath());
        modelAndView.addObject("userName", userService.findUserByEmail(auth.getName()).getName());
        modelAndView.addObject("userRole", userService.findUserByEmail(auth.getName()).getRoles().get(0).getName());
		modelAndView.setViewName("/project_management/tasks/taskList");	
		return modelAndView;
	}

	@RequestMapping(path = "/task/create", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView createTask() 
	{ 					
		ModelAndView modelAndView = new ModelAndView();	
		modelAndView.addObject("projects",  projectService.getProjects());
	    modelAndView.setViewName("/project_management/tasks/taskModalBodyCreate");	
	    return modelAndView;
	}
	
	@PostMapping(path="/task/create")
	public String createTask(@Valid @ModelAttribute Task task) 
	{	        
		taskService.createTask(task);
		return "redirect:/list-tasks";
	}

	@RequestMapping(path = "/projectEmployees/{id}", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView getProjectEmployees(@PathVariable long id) 
	{ 			
	    int i; 
	    List<Employee> employees = new ArrayList<>();
	    Integer[] employeeIds = projectService.getProject(id).getEmployees();
	    for (i = 0; i < employeeIds.length; i++) 
	    { 
	    	employees.add(employeeService.getEmployee(employeeIds[i])); 
	    } 	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employees);
	    modelAndView.setViewName("/project_management/tasks/employeesOfProject");	
	    return modelAndView;
	}
	
	@RequestMapping(path = "/projectEmployeesEdit/{id}", method = RequestMethod.GET) 
	@ResponseBody
	public ModelAndView getProjectEmployeesEdit(@PathVariable long id) 
	{ 			
	    int i; 
	    List<Employee> employees = new ArrayList<>();
	    Integer[] employeeIds = projectService.getProject(id).getEmployees();
	    for (i = 0; i < employeeIds.length; i++) 
	    { 
	    	employees.add(employeeService.getEmployee(employeeIds[i])); 
	    } 
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employees);
	    modelAndView.setViewName("/project_management/tasks/employeesOfProject2");	
	    return modelAndView;
	}

	@RequestMapping(path="/task/edit/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView editTask(@PathVariable long id) 
	{
		int i; 
		long id2 = taskService.getTask(id).getProject().getId();
	    List<Employee> employees = new ArrayList<>();
	    Integer[] employeeIds = projectService.getProject(id2).getEmployees();
	    for (i = 0; i < employeeIds.length; i++) 
	    { 
	    	employees.add(employeeService.getEmployee(employeeIds[i])); 
	    } 
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("employees", employees);
		modelAndView.addObject("task", taskService.getTask(id));				
		modelAndView.addObject("projects",  projectService.getProjects());
	    modelAndView.setViewName("/project_management/tasks/taskModalBodyEdit");	
	    return modelAndView;	
	}
		
	@PostMapping(path="/updateTask")
	public String editTask(Task task, @RequestParam MultiValueMap<String, String> formInputValues)
	{
		String employeeId = null;	
		int intEmployeeId = 0;
		Iterator<String> mapIterator = formInputValues.keySet().iterator();
		while (mapIterator.hasNext()) 
		{
			String key = mapIterator.next();
			if(key.equals("employeeId"))
			{
				employeeId = formInputValues.get(key).get(0);
			}
		}		
		if(employeeId != null)
		{
			intEmployeeId = Integer.parseInt(employeeId);
			task.setEmployee(employeeService.getEmployee(intEmployeeId));
		}
		taskService.updateTask(task);
		return "redirect:/list-tasks";
	}	

	@PostMapping(path="/updateTaskFromProjects")
	public ModelAndView editTaskFromProjects(Task task)      
	{			
		taskService.updateTask(task);		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/project_management/projects/message");
		return modelAndView;	
	}	
	
	@GetMapping(path="/task/delete/{id}")
	public String deleteTask(@PathVariable long id) 
	{
		if (taskService.deleteTask(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-tasks";
	}
	
	@GetMapping(path="/task/delete/fromProjects/{id}")
	public String deleteTaskFromProjects(@PathVariable long id) 
	{
		if (taskService.deleteTask(id) == false) 
		{
			//throw new ResourceNotFoundException();
			//return null;
		}
		return "redirect:/list-projects";
	}
	
	@RequestMapping(path="/tasks/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getTasks(@PathVariable long id) 
	{
		ModelAndView modelAndView = new ModelAndView();
		List<Task> list = taskService.getTasks();
		List<Task> chosenTasks = new ArrayList<>();
		int i;
		if(id == 1) 
		{
			modelAndView.addObject("chosenTasks", list);
			modelAndView.addObject("employees", employeeService.getEmployees());
			modelAndView.addObject("projects",  projectService.getProjects());
		}
		else
		{	
			for (i = 0; i < list.size(); i++) 
			{	
				if(id == 2) 
				{
					if(list.get(i).getStatus().equals("New"))
					{	
						chosenTasks.add(list.get(i));
					}		
				}
				if(id == 3) 
				{
					if(list.get(i).getStatus().equals("Progress"))
					{	
						chosenTasks.add(list.get(i));
					}		
				}
				if(id == 4)
				{
					if(list.get(i).getStatus().equals("Completed"))
					{			
						chosenTasks.add(list.get(i));
					}		
				}
				if(id == 5)  
				{
					if(list.get(i).getStatus().equals("Hold"))
					{			
						chosenTasks.add(list.get(i));
					}		
				}
				if(id == 6) 
				{
					if(list.get(i).getStatus().equals("Cancelled"))
					{			
						chosenTasks.add(list.get(i));
					}
				}
			}
			modelAndView.addObject("chosenTasks", chosenTasks);	
			modelAndView.addObject("employees", employeeService.getEmployees());
			modelAndView.addObject("projects",  projectService.getProjects());
		}
	    modelAndView.setViewName("/project_management/tasks/taskContent");	
	    return modelAndView;	
	}
	
	@RequestMapping(path = "/tasksInfo/{id}", method = RequestMethod.GET) 
    @ResponseBody
	public ModelAndView showTasks(@PathVariable long id) 
	{ 				
		List<Task> tasks = new ArrayList<>();
		for (Task temp : taskService.getTasks()) 
		 {
	           if(temp.getProject().getId() == id )
	           {
	        	   tasks.add(temp);
	           }
	     }		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("tasks", tasks);
		modelAndView.addObject("projectId", id);
	    modelAndView.setViewName("/project_management/projects/taskModalBody");
	    return modelAndView;
	}
	
}
