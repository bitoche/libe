package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BorrowStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int borrowStatusId;
    @NotNull
    String statusName;
//    @ManyToOne
//    @ToString.Exclude
//    List<Borrow> borrowWithStatusList;
}
