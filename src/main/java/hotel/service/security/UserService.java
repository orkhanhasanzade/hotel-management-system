package hotel.service.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import hotel.domain.security.User;
import hotel.repository.security.UserRepository;
import hotel.repository.security.RoleRepository;

@Service
public class UserService
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private RoleRepository roleRepository;
	
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public List<User> getUsers() 
	{
		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(e -> users.add(e));
		return users;
	}

	public User getUser(long id) 
	{
		return userRepository.findOneById(id);
	}
	
	public User getUser(String email) 
	{
		return userRepository.findByEmail(email);
	}

	public User createUser(User user) 
	{
	    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	    user.setActive(1);            
        user.setRoles(Arrays.asList(roleRepository.findOneById(2)));     
		return userRepository.save(user);
	}

	public User updateUser(User user ) 
	{
		User userEntity = userRepository.findOneById(user.getId());
		if (userEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(user, userEntity);	
		userEntity = userRepository.save(user);
		return userEntity;
	}

	public boolean deleteUser(long id) 
	{
		User userEntity = userRepository.findOneById(id);
		if (userEntity == null) 
		{
			return false;
		}
		
		userRepository.delete(userEntity);
		return true;
	}
	
    public User findUserByEmail(String email) 
    {
        return userRepository.findByEmail(email);
    }
    
    public User findUserByResetToken(String resetToken) 
    {
        return userRepository.findByResetToken(resetToken);
    }

}

