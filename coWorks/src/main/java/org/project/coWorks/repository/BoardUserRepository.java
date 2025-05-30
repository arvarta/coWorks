package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.BoardUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardUserRepository extends JpaRepository<BoardUser, Long>{
	List<BoardUser> findByUserId(Long userId);
	BoardUser findByUserIdAndBoardId(Long userId,Long boardId);
	List<BoardUser> findByBoardId(Long boardId);
	List<BoardUser> findByUserIdOrderByBoardId(Long userId);
}
