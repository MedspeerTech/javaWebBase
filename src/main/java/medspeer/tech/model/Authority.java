package medspeer.tech.model;

import medspeer.tech.constants.UserAuthorities;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private UserAuthorities name;
    @ManyToMany(mappedBy = "authorities",cascade = CascadeType.ALL)
    private Collection<Role> roles;

    public Authority() {
    }

    public Authority(UserAuthorities name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAuthorities getName() {
        return name;
    }

    public void setName(UserAuthorities name) {
        this.name = name;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String getAuthority() {
        return name.toString();
    }
}
