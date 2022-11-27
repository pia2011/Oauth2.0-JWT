package oauth2jwt.demo.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oauth2jwt.demo.user.enumerate.Provider;
import oauth2jwt.demo.user.enumerate.RoleType;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column
    private String providerId;

    public void updateName(String name){
        this.name = name;
    }
}
