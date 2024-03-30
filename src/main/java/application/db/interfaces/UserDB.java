package application.db.interfaces;

import application.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDB {

    /**
     * DB에 유저를 저장
     * 이미 등록된 ID라면 예외 발생
     * @param user
     * @throws IllegalArgumentException 이미 존재하는 회원 ID
     */
    void addUser(User user) throws IllegalArgumentException;

    /**
     * DB에 저장되 유저를 id로 조회
     * @param userId
     * @return
     */
    Optional<User> findUserById(String userId);

    /**
     * DB에 저장된 모든 유저를 반환
     * @return
     */
    Collection<User> findAll();

    /**
     * DB에서 모든 유저를 삭제
     */
    void clear();
}
