package api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private RoleName name = RoleName.USER;

    public Role(Long id) {
        this.id = id;
        if (id == 2) {
            name = RoleName.ADMIN;
        }
    }

    @Override
    public String getAuthority() {
        return name.name();
    }
}
