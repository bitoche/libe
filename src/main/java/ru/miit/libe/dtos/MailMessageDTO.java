package ru.miit.libe.dtos;

import ru.miit.libe.models.EntryCode;
import ru.miit.libe.models.Notification;

public class MailMessageDTO {
    private String mailTitle;
    private String sender = "tszyuconstantin@yandex.ru";
    private String receiver;
    private String textMessage;

    // Конструктор для создания сообщения для входа
    public MailMessageDTO(EntryCode ec, boolean isRegister) {
        this.mailTitle = isRegister
                ? "Завершение регистрации E-Library"
                : "Код для входа E-Library";
        this.receiver = ec.getUser().getEmail();
        this.textMessage = convertToHtml(
                addNoReplyFooter(
                        isRegister
                                ? "Ваш код для завершения регистрации на сайте:\n" + makeECBeautiful(ec)
                                : "Ваш код для входа на сайт:\n" + makeECBeautiful(ec)
                )
        );
    }

    // Конструктор для уведомлений
    public MailMessageDTO(Notification notification) {
        this.mailTitle = "Уведомление от E-Library";
        this.receiver = notification.getUser().getEmail();
        this.textMessage = convertToHtml(
                addNoReplyFooter(
                        buildNotificationHtml(notification.getTitle(), notification.getText())
                )
        );
    }

    // Форматирование кода и времени
    private String makeECBeautiful(EntryCode ec) {
        String date = ec.getExpireDateTime().toLocalDate().toString();
        String time = String.format("%02d ч. %02d мин. %02d сек.",
                ec.getExpireDateTime().toLocalTime().getHour(),
                ec.getExpireDateTime().toLocalTime().getMinute(),
                ec.getExpireDateTime().toLocalTime().getSecond()
        );
        String datetime = date + ", " + time;

        return "<table style=\"width:100%; border-collapse: collapse;\">" +
                "<tr><td style=\"text-align:center\"><b style=\"font-size:20px\">" + escapeHtml(ec.getCode()) + "</b></td></tr>" +
                "<tr><td style=\"text-align:center\">Действителен до: " + escapeHtml(datetime) + "</td></tr>" +
                "</table>";
    }

    // Построение HTML для уведомления
    private String buildNotificationHtml(String title, String text) {
        return "<table style=\"width:100%; border-collapse: collapse;\">" +
                "<tr><td style=\"text-align:center; font-size:18px; font-family:Arial, Helvetica, sans-serif\">" +
                "<h2 style=\"text-align:center\">" + escapeHtml(title) + "</h2>" +
                "</td></tr>" +
                "<tr><td style=\"text-align:center; font-size:16px; font-family:Arial, Helvetica, sans-serif\">" +
                escapeHtml(text) +
                "</td></tr>" +
                "</table>";
    }

    // Добавление футера
    private String addNoReplyFooter(String message) {
        return message +
                "<p style=\"text-align:center;color:gray\">Это сообщение сформировано автоматически, вам не нужно на него отвечать.</p>" +
                "<a style=\"text-align:center\" href=\"https://bitoche.cloudpub.ru/\">E-Library</a>";
    }

    // Преобразование текста в HTML
    private String convertToHtml(String text) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset=\"UTF-8\"></head>" +
                "<body>" + text + "</body>" +
                "</html>";
    }

    // Экранирование HTML-символов
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // Геттеры и сеттеры
    public String getMailTitle() {
        return mailTitle;
    }

    public void setMailTitle(String mailTitle) {
        this.mailTitle = mailTitle;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }
}