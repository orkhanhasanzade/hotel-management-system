package hotel.service.project_management;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.project_management.Project;
import hotel.repository.project_management.ProjectRepository;

@Service
public class ProjectService
{
	@Autowired
	private ProjectRepository projectRepository;
	
	public List<Project> getProjects() 
	{
		List<Project> projects = new ArrayList<>();
		projectRepository.findAll().forEach(e -> projects.add(e));
		return projects;
	}

	public Project getProject(long id) 
	{
		return projectRepository.findOneById(id);
	}

	public Project createProject(Project project) 
	{
		return projectRepository.save(project);
	}

	public Project updateProject(Project project) 
	{
		Project projectEntity = projectRepository.findOneById(project.getId());
		if (projectEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(project, projectEntity);	
		projectEntity = projectRepository.save(project);
		return projectEntity;
	}

	public boolean deleteProject(long id) 
	{
		Project projectEntity = projectRepository.findOneById(id);
		if (projectEntity == null) 
		{
			return false;
		}
		
		projectRepository.delete(projectEntity);
		return true;
	}


}
