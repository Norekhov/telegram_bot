package pro.sky.telegrambot.exeption;

public class IncorrectMessageException extends Exception{
    public IncorrectMessageException(String name) {
        super("Неверное сообщение: " + name);
    }
}
