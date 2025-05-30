package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByUserId(Long userId);
	List<User> findByUserStatus(String userStatus);
	Long countByUserStatus(String userStatus);
	boolean existsByUserId(Long UserId);
	List<User> findAllByOrderByUserId();
}
