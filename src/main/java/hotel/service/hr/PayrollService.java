package hotel.service.hr;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.hr.Payroll;
import hotel.repository.hr.PayrollRepository;

@Service
public class PayrollService
{
	@Autowired
	private PayrollRepository payrollRepository;
	
	public List<Payroll> getPayrolls() 
	{
		List<Payroll> payrolls = new ArrayList<>();
		payrollRepository.findAll().forEach(e -> payrolls.add(e));
		return payrolls;
	}

	public Payroll getPayroll(long id) 
	{
		return payrollRepository.findOneById(id);
	}

	public Payroll createPayroll(Payroll payroll) 
	{
		return payrollRepository.save(payroll);
	}

	public Payroll updatePayroll(Payroll payroll) 
	{
		Payroll payrollEntity = payrollRepository.findOneById(payroll.getId());
		if (payrollEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(payroll, payrollEntity);	
		payrollEntity = payrollRepository.save(payroll);
		return payrollEntity;
	}

	public boolean deletePayroll(long id) 
	{
		Payroll payrollEntity = payrollRepository.findOneById(id);
		if (payrollEntity == null) 
		{
			return false;
		}
		
		payrollRepository.delete(payrollEntity);
		return true;
	}


}
