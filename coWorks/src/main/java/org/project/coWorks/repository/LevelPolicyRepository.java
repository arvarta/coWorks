package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.LevelPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelPolicyRepository extends JpaRepository<LevelPolicy, Long>{

	LevelPolicy findByBoardIdAndLevel(Long boardId, int level);

	List<LevelPolicy> findByBoardId(Long boardId);
}
