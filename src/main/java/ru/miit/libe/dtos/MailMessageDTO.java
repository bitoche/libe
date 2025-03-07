package ru.miit.libe.dtos;

import ru.miit.libe.models.EntryCode;

public class MailMessageDTO {
    String mailTitle;
    String sender = "tszyuconstantin@yandex.ru";
    String receiver;
    String textMessage;
    //конструктор для создания сообщения для входа
    public MailMessageDTO(EntryCode ec, Boolean isRegister){
        mailTitle = (isRegister)
                ? "Завершение регистрации E-Library"
                : "Код для входа E-Library";
        receiver = ec.getUser().getEmail();
        textMessage = isRegister
                ? "Ваш код для завершения регистрации на сайте:\n" +
                makeECBeatiful(ec)
                : "Ваш код для входа на сайт:\n" +
                makeECBeatiful(ec)
                ;
    }
    String makeECBeatiful(EntryCode ec){
        return ec.getCode()+"\n"+
                "Этот код действителен в течение 20 минут (до " + ec.getExpireDateTime() + ")";
    }

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
