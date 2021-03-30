package hotel.service.main;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.main.Setting;
import hotel.repository.main.SettingRepository;

@Service
public class SettingService
{
	@Autowired
	private SettingRepository settingRepository;
	
	public List<Setting> getSettings() 
	{
		List<Setting> settings = new ArrayList<>();
		settingRepository.findAll().forEach(e -> settings.add(e));
		return settings;
	}

	public Setting getSetting(long id) 
	{
		return settingRepository.findOneById(id);
	}

	public Setting createSetting(Setting setting) 
	{
		return settingRepository.save(setting);
	}

	public Setting updateSetting(Setting setting) 
	{
		Setting settingEntity = settingRepository.findOneById(setting.getId());
		if (settingEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(setting, settingEntity);	
		settingEntity = settingRepository.save(setting);
		return settingEntity;
	}

	public boolean deleteSetting(long id) 
	{
		Setting settingEntity = settingRepository.findOneById(id);
		if (settingEntity == null) 
		{
			return false;
		}
		
		settingRepository.delete(settingEntity);
		return true;
	}
}
