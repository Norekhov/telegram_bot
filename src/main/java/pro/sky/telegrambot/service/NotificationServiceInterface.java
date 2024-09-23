package pro.sky.telegrambot.service;

import pro.sky.telegrambot.exeption.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;

import java.util.Optional;


public interface NotificationServiceInterface {

    void scheduleNotification(Notification notification, Long chtId);

    Optional<Notification> parseMessage(String notificationBotMessage) throws IncorrectMessageException;

    void sendNotificationMessage();

    void sendMessage(Long chatId, String messageText);

    void sendMessage(Notification notification);

}
