package kr.co.loopz.user.repository;

import kr.co.loopz.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);
    void deleteByEmail(String email);
    boolean existsByNickName(String nickName);
}
