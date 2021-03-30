package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Overtime;
import hotel.repository.hr.OvertimeRepository;

@Service
public class OvertimeService
{
	@Autowired
	private OvertimeRepository overtimeRepository;
	
	public List<Overtime> getOvertimes() 
	{
		List<Overtime> overtimes = new ArrayList<>();
		overtimeRepository.findAll().forEach(e -> overtimes.add(e));
		return overtimes;
	}

	public Overtime getOvertime(long id) 
	{
		return overtimeRepository.findOneById(id);
	}

	public Overtime createOvertime(Overtime overtime) 
	{
		return overtimeRepository.save(overtime);
	}

	public Overtime updateOvertime(Overtime overtime) 
	{
		Overtime overtimeEntity = overtimeRepository.findOneById(overtime.getId());
		if (overtimeEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(overtime, overtimeEntity);	
		overtimeEntity = overtimeRepository.save(overtime);
		return overtimeEntity;
	}

	public boolean deleteOvertime(long id) 
	{
		Overtime overtimeEntity = overtimeRepository.findOneById(id);
		if (overtimeEntity == null) 
		{
			return false;
		}
		
		overtimeRepository.delete(overtimeEntity);
		return true;
	}
}
