package ru.miit.libe.models;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class EntryCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long codeId;
    String code;
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    User user;
    LocalDateTime expireDateTime;

    public EntryCode(@NotNull User user){
        int sixDigitNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
        this.code = String.valueOf(sixDigitNumber);
        this.user = user;
        this.expireDateTime = LocalDateTime.now().plusMinutes(20);
    }
}
