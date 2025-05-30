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
@Table(name = "level_policy")
public class LevelPolicy {
	// Level, post_count, comment_count, visit_count, joined_at �닚�꽌
	public static final int[][] LEVEL_DEFAULT = {
		{1,0,0,0,0},
		{2,0,3,3,3},
		{3,3,5,7,14},
		{4,10,15,14,30},
		{5,30,30,30,30},
	};
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "level_policy_id", columnDefinition = "BIGINT")
	private Long levelPolicyId;

	@Column(name = "board_id", columnDefinition = "BIGINT")
	private Long boardId;

	@Column(name = "level")
	private Integer level;

	@Column(name = "write_post_count")
	private Integer writePostCount;

	@Column(name = "write_comment_count")
	private Integer writeCommentCount;

	@Column(name = "board_visit_count")
	private Integer boardVisitCount;

	@Column(name = "board_joined_at")
	private Integer boardJoinedAt;
}
