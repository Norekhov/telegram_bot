package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exeption.IncorrectMessageException;

import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationService implements NotificationServiceInterface {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private static final String REGEX_MESSAGE = "(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)";

    private final NotificationRepository repository;
    private final TelegramBot bot;

    public NotificationService(NotificationRepository repository, TelegramBot bot) {
        this.repository = repository;
        this.bot = bot;
    }

    @Override
    public void scheduleNotification(Notification notification, Long chatId) {
        notification.setChatId(chatId);
        Notification saveNotification = repository.save(notification);
        logger.info("Уведомление " + saveNotification + " запланировано");
    }

    @Override
    public Optional<Notification> parseMessage(String message) throws IncorrectMessageException {
        Notification notification = null;

        Pattern pattern = Pattern.compile(REGEX_MESSAGE);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String messageToSave = matcher.group(3);
            LocalDateTime notificationDateTime = LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            if (notificationDateTime.isAfter(LocalDateTime.now())) {
                notification = new Notification(messageToSave, notificationDateTime);
                logger.info("Сохранено {} в базу данных", notification);
                repository.save(notification);
            } else {
                logger.error("Дата неверна");
                throw new IncorrectMessageException("Дата неверна");
            }
        }
        return Optional.ofNullable(notification);
    }

    @Override
    public void sendNotificationMessage() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<Notification> notifications = repository.findByNotificationDate(currentTime);
        notifications.forEach(task -> {
            sendMessage(task);
            task.setAsSent();
            logger.info("Уведомление отправлено {}", task);
        });
        repository.saveAll(notifications);
        logger.info("Уведомление сохранено");
    }

    @Override
    public void sendMessage(Long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        bot.execute(sendMessage);
        logger.info("В чат {} отправлено сообщение: {}", chatId, messageText);
    }

    @Override
    public void sendMessage(Notification notification) {
        sendMessage(notification.getChatId(), notification.getMessage());
    }
}
