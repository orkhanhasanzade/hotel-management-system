package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Leave;
import hotel.repository.hr.LeaveRepository;

@Service
public class LeaveService
{
	@Autowired
	private LeaveRepository leaveRepository;
	
	public List<Leave> getLeaves() 
	{
		List<Leave> leaves = new ArrayList<>();
		leaveRepository.findAll().forEach(e -> leaves.add(e));
		return leaves;
	}

	public Leave getLeave(long id) 
	{
		return leaveRepository.findOneById(id);
	}

	public Leave createLeave(Leave leave) 
	{
		return leaveRepository.save(leave);
	}

	public Leave updateLeave(Leave leave) 
	{
		Leave leaveEntity = leaveRepository.findOneById(leave.getId());
		if (leaveEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(leave, leaveEntity);	
		leaveEntity = leaveRepository.save(leave);
		return leaveEntity;
	}

	public boolean deleteLeave(long id) 
	{
		Leave leaveEntity = leaveRepository.findOneById(id);
		if (leaveEntity == null) 
		{
			return false;
		}
		
		leaveRepository.delete(leaveEntity);
		return true;
	}
}
