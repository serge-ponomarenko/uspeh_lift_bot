package org.spon.uspehliftbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users_table")
@Entity
public class User {

    @Id
    private Long chatId;

    private String userName;
    private String name;

    private int userSection;

    private Date joinDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<UserAction> userActions;

}
