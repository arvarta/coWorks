package org.project.coWorks.service;

import java.util.List;

import org.project.coWorks.model.Board;
import org.project.coWorks.model.BoardUser;
import org.project.coWorks.model.Post;
import org.project.coWorks.repository.BoardRepository;
import org.project.coWorks.repository.BoardUserRepository;
import org.project.coWorks.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardUserService {
	
	@Autowired
	private BoardUserRepository buRepository;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private PostRepository postRepository;

	public BoardUser findByUserIdAndBoardId(Long userId, Long boardId) {
		return buRepository.findByUserIdAndBoardId(userId, boardId);
	}
	// 게시판 게시글 조회
	public List<Post> getPostsByBoardId(Long boardId) {
		Board board = boardRepository.findById(boardId).orElse(null);
		if(board == null) {
			return null;
		}
		List<Post> posts = postRepository
			.findByBoardIdAndPostStatusNot(
				board.getBoardId(), Post.STATUS[Post.DELETED]);
		return posts;
	}
	
	public List<BoardUser> findByUserId(Long userId) {
		return buRepository.findByUserIdOrderByBoardId(userId);
	}
//	게시판별 밴된 유저 목록 추출
//	public List<BoardUser> getBanUsers(Long boardId) {
//		List<BoardUser> tempList = buRepository.getByBoardId(boardId);
//		if(tempList == null)
//			return null;
//		List<BoardUser> buList = new ArrayList<>();
//		for(BoardUser bu : tempList) {
//			if(blRepository.findByBoardUserId(bu.getBoardUserId())!= null) {
//				buList.add(bu);
//			}
//		}
//		return buList;
//	}
	public List<BoardUser> findByBoardId(Long boardId) {
		return buRepository.findByBoardId(boardId);
	}
}
