package ru.miit.libe.models.historicized;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ReportEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    EReportType reportType;
    LocalDateTime reportDttm;
}
