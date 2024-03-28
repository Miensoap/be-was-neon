package application.db.interfaces;

import application.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDB {
    void addUser(User user) throws IllegalArgumentException;

    Optional<User> findUserById(String userId);

    Collection<User> findAll();

    void clear();
}
