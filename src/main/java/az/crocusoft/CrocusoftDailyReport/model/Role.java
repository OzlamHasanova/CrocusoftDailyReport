package az.crocusoft.CrocusoftDailyReport.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;



    public Role() {

    }

    public Role(Role superAdminRole) {
    }
}
