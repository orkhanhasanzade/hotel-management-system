package hotel.service.project_management;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.project_management.File;
import hotel.repository.project_management.FileRepository;

@Service
public class FileService
{
	@Autowired
	private FileRepository fileRepository;
	
	public List<File> getFiles() 
	{
		List<File> files = new ArrayList<>();
		fileRepository.findAll().forEach(e -> files.add(e));
		return files;
	}

	public File getFile(long id) 
	{
		return fileRepository.findOneById(id);
	}

	public File createFile(File file) 
	{
		return fileRepository.save(file);
	}

	public File updateFile(File file) 
	{
		File fileEntity = fileRepository.findOneById(file.getId());
		if (fileEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(file, fileEntity);	
		fileEntity = fileRepository.save(file);
		return fileEntity;
	}

	public boolean deleteFile(long id) 
	{
		File fileEntity = fileRepository.findOneById(id);
		if (fileEntity == null) 
		{
			return false;
		}
		
		fileRepository.delete(fileEntity);
		return true;
	}

}
