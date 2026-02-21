package org.spon.uspehliftbot;

import lombok.Getter;
import org.spon.uspehliftbot.entity.Settings;
import org.spon.uspehliftbot.repository.SettingsRepository;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Getter
public class SettingsManager {

    private final SettingsRepository settingsRepository;

    private final Integer SETTINGS_ID = 0;

    private Long alarmChatId = 0L;
    private Integer alarmThreadId = null;

    private int secondsToReminder = 300;
    private int secondsToAlarm = 360;

    private String alarmPhone = "+380481234567";

    public SettingsManager(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
        settingsRepository.findById(SETTINGS_ID).ifPresentOrElse(settings -> {
            alarmChatId = settings.getAlarmChatId();
            alarmThreadId = settings.getAlarmThreadId();
            secondsToReminder = settings.getSecondsToReminder();
            secondsToAlarm = settings.getSecondsToAlarm();
            alarmPhone = settings.getAlarmPhone();
        }, () -> {
            Settings settings = Settings.builder()
                    .id(SETTINGS_ID)
                    .alarmChatId(alarmChatId)
                    .alarmThreadId(alarmThreadId)
                    .secondsToReminder(secondsToReminder)
                    .secondsToAlarm(secondsToAlarm)
                    .alarmPhone(alarmPhone)
                    .build();
            settingsRepository.save(settings);
        });
    }

    public void setAlarmChatId(Long alarmChatId) {
        this.alarmChatId = alarmChatId;
        settingsRepository.findById(SETTINGS_ID).ifPresent(settings -> {
            settings.setAlarmChatId(alarmChatId);
            settings.setLastModifiedDate(new Date());
            settingsRepository.save(settings);
        });
    }

    public void setAlarmThreadId(Integer alarmThreadId) {
        this.alarmThreadId = alarmThreadId;
        settingsRepository.findById(SETTINGS_ID).ifPresent(settings -> {
            settings.setAlarmThreadId(alarmThreadId);
            settings.setLastModifiedDate(new Date());
            settingsRepository.save(settings);
        });
    }

    public void setSecondsToReminder(int secondsToReminder) {
        this.secondsToReminder = secondsToReminder;
        settingsRepository.findById(SETTINGS_ID).ifPresent(settings -> {
            settings.setSecondsToReminder(secondsToReminder);
            settings.setLastModifiedDate(new Date());
            settingsRepository.save(settings);
        });
    }

    public void setSecondsToAlarm(int secondsToAlarm) {
        this.secondsToAlarm = secondsToAlarm;
        settingsRepository.findById(SETTINGS_ID).ifPresent(settings -> {
            settings.setSecondsToAlarm(secondsToAlarm);
            settings.setLastModifiedDate(new Date());
            settingsRepository.save(settings);
        });
    }

    public void setAlarmPhone(String alarmPhone) {
        this.alarmPhone = alarmPhone;
        settingsRepository.findById(SETTINGS_ID).ifPresent(settings -> {
            settings.setAlarmPhone(alarmPhone);
            settings.setLastModifiedDate(new Date());
            settingsRepository.save(settings);
        });
    }


}
