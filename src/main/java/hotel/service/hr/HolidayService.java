package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Holiday;
import hotel.repository.hr.HolidayRepository;

@Service
public class HolidayService
{
	@Autowired
	private HolidayRepository holidayRepository;
	
	public List<Holiday> getHolidays() 
	{
		List<Holiday> holidays = new ArrayList<>();
		holidayRepository.findAll().forEach(e -> holidays.add(e));
		return holidays;
	}

	public Holiday getHoliday(long id) 
	{
		return holidayRepository.findOneById(id);
	}

	public Holiday createHoliday(Holiday holiday) 
	{
		return holidayRepository.save(holiday);
	}

	public Holiday updateHoliday(Holiday holiday) 
	{
		Holiday holidayEntity = holidayRepository.findOneById(holiday.getId());
		if (holidayEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(holiday, holidayEntity);	
		holidayEntity = holidayRepository.save(holiday);
		return holidayEntity;
	}

	public boolean deleteHoliday(long id) 
	{
		Holiday holidayEntity = holidayRepository.findOneById(id);
		if (holidayEntity == null) 
		{
			return false;
		}
		
		holidayRepository.delete(holidayEntity);
		return true;
	}
}
