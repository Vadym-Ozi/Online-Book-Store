package example.repository.role;

import example.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByName(Role.RoleName roleName);
}
