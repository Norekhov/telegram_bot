package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exeption.IncorrectMessageException;
import pro.sky.telegrambot.model.Notification;
import pro.sky.telegrambot.service.NotificationService;

import javax.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final String START = "/start";
    private static final String WELCOME = "Привет, ";
    private static final String HELP = "Введите напоминание в формате ДД.ММ.ГГГГ чч.мм напоминание ";
    private static final String INVALID_MESSAGE = "Неверное сообщение или команда!";
    private final NotificationService notificationService;
    private final TelegramBot telegramBot;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationService notificationService) {
        this.notificationService = notificationService;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void setNotificationMessage() {
        notificationService.sendNotificationMessage();
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {

            logger.info("Обработка обновления: {}", update);
            Message message = update.message();

            if (message.text().startsWith(START)) {
                logger.info(START + " " + LocalDateTime.now());
                notificationService.sendMessage(getChatId(message), WELCOME + message.from().firstName() + " ");
                notificationService.sendMessage(getChatId(message), HELP);
            } else {
                try {
                    notificationService
                            .parseMessage(message.text())
                            .ifPresentOrElse(
                                    task -> scheduledNotification(getChatId(message), task),
                                    () -> notificationService.sendMessage(getChatId(message), INVALID_MESSAGE)
                            );
                } catch (IncorrectMessageException e) {
                    notificationService.sendMessage(getChatId(message), "Сообщение не соответствует требуемому формату");
                }
            }
        });
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void scheduledNotification(Long chatId, Notification notification) {
        notificationService.scheduleNotification(notification, chatId);
        notificationService.sendMessage(chatId, "Задача: " + notification.getMessage() + " создана");
    }

    private Long getChatId(Message message) {
        return message.chat().id();
    }
}
