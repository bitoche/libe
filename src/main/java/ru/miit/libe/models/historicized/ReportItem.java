package ru.miit.libe.models.historicized;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class ReportItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Integer calcId;
    String reportName;
    String metric;
    @Nullable
    String description;
    Integer amount;
    @Timestamp
    LocalDateTime loadDateTime;
}
