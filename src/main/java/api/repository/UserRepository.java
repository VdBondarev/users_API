package api.repository;

import api.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    @Query("FROM User user LEFT JOIN FETCH user.roles WHERE user.email = :email")
    Optional<User> findByEmailWithRoles(String email);

    @Query("FROM User user WHERE user.email = :email")
    Optional<User> findByEmailWithoutRoles(String email);
}
