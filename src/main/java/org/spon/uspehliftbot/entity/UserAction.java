package org.spon.uspehliftbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "actions_table")
@Entity
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    @ToString.Exclude
    private User user;

    private Action action;
    private Boolean isPassengerLift;
    private Date actionDate;

    public enum Action {
        ENTER,
        EXIT,
        STUCK
    }

}

