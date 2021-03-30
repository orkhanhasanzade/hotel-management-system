package hotel.service.security;


import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotel.domain.security.Role;
import hotel.repository.security.RoleRepository;

@Service
public class RoleService
{
	@Autowired
	private RoleRepository roleRepository;
	
	public List<Role> getRoles() 
	{
		List<Role> roles = new ArrayList<>();
		roleRepository.findAll().forEach(e -> roles.add(e));
		return roles;
	}

	public Role getRole(Integer id) 
	{
		return roleRepository.findOneById(id);
	}

	public Role createRole(Role role) 
	{
		return roleRepository.save(role);
	}

	public Role updateRole(Role role) 
	{
		Role roleEntity = roleRepository.findOneById(role.getId());
		if (roleEntity == null) 
		{
			return null;
		}
		
		BeanUtils.copyProperties(role, roleEntity);	
		roleEntity = roleRepository.save(role);
		return roleEntity;
	}

	public boolean deleteRole(Integer id) 
	{
		Role roleEntity = roleRepository.findOneById(id);
		if (roleEntity == null) 
		{
			return false;
		}
		
		roleRepository.delete(roleEntity);
		return true;
	}
}