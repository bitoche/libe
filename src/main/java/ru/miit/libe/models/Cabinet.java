package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name="cabinet")
public class Cabinet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int cabinetId;
    int cabinetNumber;
    String cabinetName;
    @OneToMany
    @ToString.Exclude
    //@OnDelete(action = OnDeleteAction.SET_NULL)
    List<Bookshelf> shelves;
}
