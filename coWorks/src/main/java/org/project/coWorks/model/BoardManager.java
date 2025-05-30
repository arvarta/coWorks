package org.project.coWorks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "board_manager")
public class BoardManager {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_manager_id", columnDefinition = "BIGINT")
	private Long boardManagerId;

	@Column(name = "board_id", columnDefinition = "BIGINT")
	private Long boardId;

	@Column(name = "user_id", columnDefinition = "BIGINT")
	private Long userId;

	@Column(name = "permission_name")
	private String permissionName;
}
