package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NotEmpty private String name;
    @NotEmpty private String email;
    @NotEmpty private String contact;
    @NotEmpty private String password;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private Membership membership;

    @OneToMany(mappedBy = "user")
    private List<Product> sellingProducts = new ArrayList<>();
}
