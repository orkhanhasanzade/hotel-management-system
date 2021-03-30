package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Attendance;
import hotel.repository.hr.AttendanceRepository;

@Service
public class AttendanceService
{
	@Autowired
	private AttendanceRepository attendanceRepository;
	
	public List<Attendance> getAttendances() 
	{
		List<Attendance> attendances = new ArrayList<>();
		attendanceRepository.findAll().forEach(e -> attendances.add(e));
		return attendances;
	}

	public Attendance getAttendance(long id) 
	{
		return attendanceRepository.findOneById(id);
	}

	public Attendance createAttendance(Attendance attendance) 
	{
		return attendanceRepository.save(attendance);
	}

	public Attendance updateAttendance(Attendance attendance) 
	{
		Attendance attendanceEntity = attendanceRepository.findOneById(attendance.getId());
		if (attendanceEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(attendance, attendanceEntity);	
		attendanceEntity = attendanceRepository.save(attendance);
		return attendanceEntity;
	} 

	public boolean deleteAttendance(long id) 
	{
		Attendance attendanceEntity = attendanceRepository.findOneById(id);
		if (attendanceEntity == null) 
		{
			return false;
		}
		
		attendanceRepository.delete(attendanceEntity);
		return true;
	}
}
