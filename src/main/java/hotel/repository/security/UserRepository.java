package hotel.repository.security;

import hotel.domain.security.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> 
{
	 User findByEmail(String email);
	 User findOneById(Long id);	
	 User findByName(String name);
	 User findByResetToken(String resetToken);
	 
}
