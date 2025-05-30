package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	List<Comment> findByBoardUserId(Long BoardUserId);
	List<Comment> findByCommentTargetIdAndCommentTargetType(Long postId, String string);
	List<Comment> findByCommentTargetId(Long postId);
	Comment findByCommentIdAndCommentDelDateIsNull(Long commentId);
}
