package org.spon.uspehliftbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "settings_table")
@Entity
public class Settings {

    @Id
    private Integer id;

    private Long alarmChatId;
    private Integer alarmThreadId;

    private int secondsToReminder;
    private int secondsToAlarm;

    private String alarmPhone;

    private Date lastModifiedDate;

}
