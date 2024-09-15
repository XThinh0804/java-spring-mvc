package vn.hoidanit.laptopshop.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hoidanit.laptopshop.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    List<User> findAllByEmail(String email);

    List<User> findAll();

    User findById(long id);

    void deleteById(long id);

    boolean existsByEmail(String email);// Tìm xem trong database có xuất hiện email nhập vào hay không

    User findByEmail(String email);
}
