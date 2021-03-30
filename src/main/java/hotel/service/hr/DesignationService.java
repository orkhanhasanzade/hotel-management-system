package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Designation;
import hotel.repository.hr.DesignationRepository;

@Service
public class DesignationService
{
	@Autowired
	private DesignationRepository designationRepository;
	
	public List<Designation> getDesignations() 
	{
		List<Designation> designations = new ArrayList<>();
		designationRepository.findAll().forEach(e -> designations.add(e));
		return designations;
	}

	public Designation getDesignation(long id) 
	{
		return designationRepository.findOneById(id);
	}

	public Designation createDesignation(Designation designation) 
	{
		return designationRepository.save(designation);
	}

	public Designation updateDesignation(Designation designation) 
	{
		Designation designationEntity = designationRepository.findOneById(designation.getId());
		if (designationEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(designation, designationEntity);	
		designationEntity = designationRepository.save(designation);
		return designationEntity;
	}

	public boolean deleteDesignation(long id) 
	{
		Designation designationEntity = designationRepository.findOneById(id);
		if (designationEntity == null) 
		{
			return false;
		}
		
		designationRepository.delete(designationEntity);
		return true;
	}
}
