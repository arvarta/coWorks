package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.BoardManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardManagerRepository extends JpaRepository<BoardManager, Long> {
	BoardManager findByUserIdAndBoardId(Long userId, Long boardId);

	List<BoardManager> findByBoardId(Long boardId);
}
