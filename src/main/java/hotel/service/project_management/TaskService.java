package hotel.service.project_management;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.project_management.Task;
import hotel.repository.project_management.TaskRepository;

@Service
public class TaskService
{
	@Autowired
	private TaskRepository taskRepository;
	
	public List<Task> getTasks() 
	{
		List<Task> tasks = new ArrayList<>();
		taskRepository.findAll().forEach(e -> tasks.add(e));
		return tasks;
	}

	public Task getTask(long id) 
	{
		return taskRepository.findOneById(id);
	}

	public Task createTask(Task task) 
	{
		return taskRepository.save(task);
	}

	public Task updateTask(Task task) 
	{
		Task taskEntity = taskRepository.findOneById(task.getId());
		if (taskEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(task, taskEntity);	
		taskEntity = taskRepository.save(task);
		return taskEntity;
	}

	public boolean deleteTask(long id) 
	{
		Task taskEntity = taskRepository.findOneById(id);
		if (taskEntity == null) 
		{
			return false;
		}
		
		taskRepository.delete(taskEntity);
		return true;
	}


}
