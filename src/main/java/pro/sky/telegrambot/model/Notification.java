package pro.sky.telegrambot.model;

import pro.sky.telegrambot.status.NotificationStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    private String message;

    private LocalDateTime notificationDate;

    private LocalDateTime notificationSent;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.SCHEDULED;

    public Notification() {
    }

    public Notification(String message, LocalDateTime notificationDate) {
        this.message = message;
        this.notificationDate = notificationDate;
    }


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }


    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }


    public LocalDateTime getNotificationSent() {
        return notificationSent;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setAsSent() {
        this.status = NotificationStatus.SENT;
        this.notificationSent = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId) && Objects.equals(message, that.message) && Objects.equals(notificationDate, that.notificationDate) && Objects.equals(notificationSent, that.notificationSent) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, message, notificationDate, notificationSent, status);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                ", notificationDate=" + notificationDate +
                ", notificationSent=" + notificationSent +
                ", status=" + status +
                '}';
    }
}
