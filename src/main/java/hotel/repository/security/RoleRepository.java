package hotel.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hotel.domain.security.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>
{
	Role findOneById(Integer id);
}
