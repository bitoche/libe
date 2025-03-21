package ru.miit.libe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

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
    @JsonIgnore
    //@OnDelete(action = OnDeleteAction.SET_NULL)
    List<Bookshelf> shelves;
    public int getShelvesCount(){
        if(shelves != null){
            return this.shelves.size();
        }
        return 0;
    }
    @Nullable
    @ToString.Exclude
    Boolean isActive = true; // чтобы при создании записи было сразу true
}
