package org.project.coWorks.repository;

import java.util.List;
import java.util.Optional;

import org.project.coWorks.model.Position;
import org.project.coWorks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Integer> {
	List<Position> findAllByOrderByPositionIdAsc();

}
