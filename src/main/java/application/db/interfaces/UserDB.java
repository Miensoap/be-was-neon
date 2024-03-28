package application.db.interfaces;

import application.model.User;

import java.util.Collection;

public interface UserDB {
    void addUser(User user);

    User findUserById(String userId);

    Collection<User> findAll();

    void clear();
}
