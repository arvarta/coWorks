package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByBoardId(Long boardId);
	
	List<Post> findByBoardIdAndPostDelDateIsNullOrderByPostCreateDateDesc(Long boardId);

	List<Post> findByBoardUserIdOrderByPostCreateDateDesc(Long boardUserId);
	List<Post> findByBoardUserIdAndPostDelDateIsNullOrderByPostCreateDateDesc(Long boardUserId);

	List<Post> findByPostContentLikeAndPostDelDateIsNullOrderByPostCreateDateDesc(String content);
	List<Post> findByPostTitleLikeAndPostDelDateIsNullOrderByPostCreateDateDesc(String title);

	boolean existsByPostIdAndBoardIdOrderByPostCreateDateDesc(Long postId, Long boardId);

	List<Post> findByBoardIdAndPostDelDateIsNotNullOrderByPostCreateDateDesc(Long boardId);

	List<Post> findByBoardIdAndPostStatusNotOrderByPostCreateDateDesc(Long boardId, String string);

	List<Post> findByBoardIdAndPostTitleLikeAndPostDelDateIsNullOrderByPostCreateDateDesc(Long boardId, String keyword);

	List<Post> findByBoardIdAndPostContentLikeAndPostDelDateIsNullOrderByPostCreateDateDesc(Long boardId, String keyword);

	Post findByPostIdAndPostDelDateIsNullOrderByPostCreateDateDesc(Long postId);

	List<Post> findByBoardIdAndPostStatusNot(Long boardId, String string);
}