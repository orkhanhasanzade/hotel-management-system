package hotel.repository.main;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel.domain.main.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long>   
{
	Setting findOneById(Long id);

}