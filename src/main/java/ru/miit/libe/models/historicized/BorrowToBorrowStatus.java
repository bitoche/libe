package ru.miit.libe.models.historicized;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import ru.miit.libe.models.Borrow;
import ru.miit.libe.models.EBorrowStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class BorrowToBorrowStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long entryId;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Borrow borrow;
    EBorrowStatus status;
    LocalDateTime assignDttm;
}
