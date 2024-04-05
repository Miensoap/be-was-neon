package application.db.memoryDB;

import application.db.interfaces.UserDB;
import application.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemUserDB implements UserDB {
    private Map<String, User> users = new HashMap<>();

    public MemUserDB(){
        addUser(new User("tester" , "1234" , "테스트" , "test@naver.com"));
    }

    public void addUser(User user){
        if(contains(user)) throw new IllegalArgumentException("이미 존재하는 회원 id");
        users.put(user.getUserId(), user);
    }

    public Optional<User> findUserById(String userId){
        return Optional.ofNullable(users.get(userId));
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public void clear(){
        users = new HashMap<>();
    }

    private boolean contains(User user){
        return users.containsKey(user.getUserId());
    }
}
