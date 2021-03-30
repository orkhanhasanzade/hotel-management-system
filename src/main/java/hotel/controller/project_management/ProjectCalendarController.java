package hotel.controller.project_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hotel.domain.project_management.Task;
import hotel.service.project_management.TaskService;

@RestController
public class ProjectCalendarController 
{
	@Autowired
	private TaskService taskService;
	
	@GetMapping(path = "/dashboard-tasks")
	public List<HashMap<String, String>> getTasksForCalendar()
	{
		List<Task> list = taskService.getTasks();
		List<HashMap<String, String>> jsonOutput = new ArrayList<>();
		int i;
		for (i = 0; i < list.size(); i++) 
		{	
			HashMap<String, String> map = new HashMap<>();
			map.put("title", list.get(i).getEmployee().getName() + " " + list.get(i).getEmployee().getSurname());
		    map.put("start", list.get(i).getStartDate());
		    map.put("end", list.get(i).getEndDate());
		    
		    if(list.get(i).getStatus().equals("New"))
		    {	map.put("backgroundColor", "#00BBE5");	}
		    else if(list.get(i).getStatus().equals("Progress"))
		    { 	map.put("backgroundColor", "#0072bb"); }
		    else if(list.get(i).getStatus().equals("Completed"))
		    {   map.put("backgroundColor", "green"); }
		    else if(list.get(i).getStatus().equals("Hold"))
		    { 	map.put("backgroundColor", "orange"); }
		    else if(list.get(i).getStatus().equals("Cancelled"))
		    {   map.put("backgroundColor", "red"); }
		    
		    
		    jsonOutput.add(map);
		}
		return jsonOutput;		
	}
	
}
