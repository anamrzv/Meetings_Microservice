package ifmo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Data
@Entity
@AllArgsConstructor
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @NotNull
    @Column(name = "role_id")
    private Integer id;

    private String name;

    @Transient
    @OneToMany(mappedBy = "role")
    private Set<UserEntity> users = new HashSet<>();
    @Override
    public String getAuthority() {
        return name;
    }

}
