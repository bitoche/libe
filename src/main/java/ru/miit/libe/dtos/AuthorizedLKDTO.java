package ru.miit.libe.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.miit.libe.models.Borrow;
import ru.miit.libe.models.RequestBooks;
import ru.miit.libe.models.User;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizedLKDTO {
    User user;
}
