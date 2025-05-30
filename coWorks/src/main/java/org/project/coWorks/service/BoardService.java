package org.project.coWorks.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.project.coWorks.controller.CheckResult;
import org.project.coWorks.dto.PostDetailDto;
import org.project.coWorks.model.Board;
import org.project.coWorks.model.BoardUser;
import org.project.coWorks.model.Comment;
import org.project.coWorks.model.Post;
import org.project.coWorks.model.Proposal;
import org.project.coWorks.model.UploadFile;
import org.project.coWorks.repository.BoardRepository;
import org.project.coWorks.repository.BoardUserRepository;
import org.project.coWorks.repository.CommentRepository;
import org.project.coWorks.repository.PostRepository;
import org.project.coWorks.repository.ProposalRepository;
import org.project.coWorks.repository.UploadFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {

	@Value("${file.upload-dir}") // application.properties에서 파일 업로드 경로 읽어옴
	private String fileDir;

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private UploadFileRepository uploadFileRepository;
	@Autowired
	private ProposalRepository proposalRepository;
	@Autowired
	private BoardUserRepository boardUserRepository;
	
	// 게시판 게시글 조회
	public List<Post> getPostsByBoardId(Long boardId) {
		Board board = boardRepository.findById(boardId).orElse(null);
		if(board == null) {
			return null;
		}
		return postRepository.findByBoardId(board.getBoardId());
	}

	// 게시판 유저 목록 조회
	public List<BoardUser> getUsersByBoardId(Board board) {
		return boardUserRepository.findByBoardId(board.getBoardId());
	}

	// 게시글 보기
	@Transactional
	public PostDetailDto viewPost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

		Long userId = post.getBoardUserId();

		List<UploadFile> files = uploadFileRepository.findByPostId(post.getPostId());

		List<Comment> comments = comments(postId);
		
		return new PostDetailDto(post.getPostId(), userId, files, comments);
	}
	
	private List<Comment> comments(Long postId){
		List<Comment> comments = commentRepository.findByCommentTargetId(postId);
		List<Comment> result = new ArrayList<>();
		for(Comment comment : comments) {
			result.add(comment);
			List<Comment> temp = commentRepository.findByCommentTargetId(comment.getCommentId());
			if(temp != null && temp.size() > 0) {
				for(Comment ctemp : temp) {
					result.add(ctemp);
				}
			}
		}
		return result;
	}

	// 댓글 작성
	@Transactional
	public Map<String, Object> createComment(Long userId, String content, Long targetId, String targetType) {
		Map<String, Object> result = new HashMap<>();

		try {
			BoardUser user = boardUserRepository.findById(userId).orElse(null);
			if (user == null) {
				throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
			}

			Comment comment = new Comment();
			comment.setBoardUserId(user.getBoardUserId());
			comment.setCommentContent(content);
			comment.setCommentWriteDate(LocalDateTime.now());

			if (targetType.equals("COMMENt")) {
				Comment targetComment = commentRepository.findById(targetId)
						.orElseThrow(() -> new IllegalArgumentException("대상 댓글이 존재하지 않습니다."));

				if ("COMMENT".equalsIgnoreCase(targetComment.getCommentTargetType())) {
					throw new IllegalArgumentException("대댓글 작성은 불가능합니다.");
				}

				comment.setCommentTargetId(targetComment.getCommentId());
				comment.setCommentTargetType("COMMENT");
			} else {
				comment.setCommentTargetId(targetId);
				comment.setCommentTargetType("POST");
			}

			commentRepository.save(comment);

			result.put("status", "success");
			result.put("message", "댓글이 작성되었습니다.");
		} catch (Exception e) {
			// e.printStackTrace(); // 개발 중엔 예외 로그 출력 추천
			result.put("status", "fail");
			result.put("message", e.getMessage());
		}

		return result;
	}

	// 댓글 수정
	@Transactional
	public Map<String, Object> updateComment(Long commentId, Long userId, String newContent) {
		Map<String, Object> result = new HashMap<>();
		try {
			Comment comment = commentRepository.findById(commentId)
					.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

			if (!comment.getBoardUserId().equals(userId)) {
				throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
			}

			comment.setCommentContent(newContent);
			comment.setCommentEditDate(LocalDateTime.now());
			commentRepository.save(comment);

			result.put("status", "success");
			result.put("message", "댓글이 수정되었습니다.");
		} catch (Exception e) {
			result.put("status", "fail");
			result.put("message", e.getMessage());
		}
		return result;
	}

	// 댓글 숨김
	@Transactional
	public Map<String, Object> hideComment(Long commentId, Long userId) {
		Map<String, Object> result = new HashMap<>();
		try {
			Comment comment = commentRepository.findById(commentId)
					.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

			if (!comment.getBoardUserId().equals(userId)) {
				throw new IllegalArgumentException("댓글 숨김 권한이 없습니다.");
			}

			comment.setCommentDelDate(LocalDateTime.now());
			commentRepository.save(comment);
			result.put("status", "success");
			result.put("message", "댓글이 숨김 처리되었습니다.");
			
		} catch (Exception e) {
			result.put("status", "fail");
			result.put("message", e.getMessage());
		}
		return result;
	}

	// 대댓글 작성
	@Transactional
	public Map<String, Object> createreply(Long postId, Long userId, String content, Long targetId) {
		Map<String, Object> result = new HashMap<>();

		try {
			Post post = postRepository.findById(postId)
					.orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

			BoardUser user = boardUserRepository.findById(userId).orElse(null);
			if (user == null) {
				throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
			}

			Comment comment = new Comment();
			comment.setBoardUserId(user.getBoardUserId());
			comment.setCommentContent(content);
			comment.setCommentWriteDate(LocalDateTime.now());

			if (targetId != null) {
				Comment targetComment = commentRepository.findById(targetId)
						.orElseThrow(() -> new IllegalArgumentException("대상 댓글이 존재하지 않습니다."));

				if ("COMMENT".equalsIgnoreCase(targetComment.getCommentTargetType())) {
					throw new IllegalArgumentException("대댓글 작성은 불가능합니다.");
				}

				comment.setCommentTargetId(targetComment.getCommentId());
				comment.setCommentTargetType("COMMENT");
			} else {
				comment.setCommentTargetId(post.getPostId());
				comment.setCommentTargetType("POST");
			}

			commentRepository.save(comment);

			result.put("status", "success");
			result.put("message", "댓글이 작성되었습니다.");
		} catch (Exception e) {
			// e.printStackTrace(); // 개발 중엔 예외 로그 출력 추천
			result.put("status", "fail");
			result.put("message", e.getMessage());
		}

		return result;
	}

	// 검색하기
//	public List<Post> searchPosts(Long boardId, Map<String, String> keywords) {
//		String title = keywords.get("title");
//		String content = keywords.get("content");
//		String writer = keywords.get("writer");
//		if(title != null)
//			return postRepository.findByPostTitle
//		return postRepository.findByConditions(boardId, title, content, writer);
//	}

	// 게시판 추가 요청하기
//	public Proposal createProposal(ProposalRequestDto dto) {
//		Proposal proposal = new Proposal();
//		proposal.setProposalAnonymous(dto.getAnonymous().equals("anonymous"));
//		proposal.setProposalBoardInfo(dto.getBoardInfo());
//		proposal.setProposalBoardName(dto.getBoardName());
//		proposal.setProposalReason(dto.getReason());
//		
//		List<ProposalBoardUser> userList = proposalBoardUserRepository.findByProposal(proposal);
//		if (userList == null) {
//			throw new IllegalArgumentException("사용자 정보를 찾을 수 없습니다.");
//		}
//
//		Proposal proposal = new Proposal();
//		proposal.setProposalBoardUser(userList);
//		proposal.setProposalBoardName(dto.getBoardName());
//		proposal.setProposalBoardInfo(dto.getBoardInfo());
//		proposal.setProposalAnonymous(Boolean.parseBoolean(dto.getAnonymous()));
//		proposal.setProposalReason(dto.getReason());
//
//		return proposalRepository.save(proposal);
//	}
//
//	// 게시판 추가 요청자 목록
//	@Transactional
//	public void addUserToProposal(Long proposalId, Long userId, String status) {
//		Proposal proposal = proposalRepository.findById(proposalId)
//				.orElseThrow(() -> new IllegalArgumentException("요청 정보 없음"));
//
//		User user = proposalRepository.findByUserId(userId)
//				.orElseThrow(() -> new IllegalArgumentException("직원 정보 없음"));
//
//		// 중복 참여 방지
//		boolean exists = proposalBoardUserRepository.existsByProposalAndUser(proposal, user);
//		if (exists) {
//			throw new IllegalStateException("이미 참여한 사용자입니다.");
//		}
//
//		ProposalBoardUser userList = new ProposalBoardUser();
//		userList.setProposal(proposal);
//		userList.setUser(user);
//		userList.setProposalBoardUserStatus(status);
//
//		proposalBoardUserRepository.save(userList);
//	}

//	// 게시판 추가 요청 유저 목록
//	public List<Proposal> getProposalsByUserList(ProposalBoardUser userList) {
//		return proposalRepository.findByproposalBoardUser(userList);
//	}

	public List<Board> findByUserId(Long userId) {
		List<Board> result = new ArrayList<>();
		List<BoardUser> buList = boardUserRepository.findByUserId(userId);
		if(buList != null) {
			for(BoardUser bu : buList) {
				Board b = boardRepository.findById(bu.getBoardId()).orElse(null);
				if(b != null) {
					result.add(b);
				}
			}
		}
		return result;
	}
	
	// 게시판 추가 요청 상세 정보 열람
	public Proposal getProposalDetail(Long proposalId) {
		Proposal proposal = proposalRepository.findByProposalId(proposalId);
		if (proposal == null) {
			throw new NoSuchElementException("요청이 존재하지 않습니다.");
		}
		return proposal;
	}
	
	//관리자 부분
	//게시판 목록 받아오기
	public List<Board> getAll() {
		return boardRepository.findAll();
	}

	//게시판 찾기
	public Board findById(Long id) {
		return boardRepository.findById(id).orElse(null);
	}

	//게시판 생성
	public int save(Board board) {
		int result = CheckResult.FAIL;
		Board b = boardRepository.save(board);
		if (b != null) {
			result = CheckResult.SUCCESS;
		}
		return result;
	}

	// 보이지 않는 게시판 물리 삭제 또는 보이는 상태로 전환
	public int visibleReverseBoard(Long id, String method) {
		int result = CheckResult.FAIL;
		Board board = findById(id);
		if (board == null) {
			return CheckResult.NO_EXIST;
		}
		if ("POST".equals(method)) {
			if (board.getBoardStatus().equals(Board.STATUS[Board.VISIBLE_ANONYMOUS])) {
				board.setBoardStatus(Board.STATUS[Board.UNVISIBLE_ANONYMOUS]);
				result = CheckResult.SUCCESS;
			} else if (board.getBoardStatus().equals(Board.STATUS[Board.VISIBLE])) {
				board.setBoardStatus(Board.STATUS[Board.UNVISIBLE]);
				result = CheckResult.SUCCESS;
			}
		} else if ("DELETE".equals(method)) {
			boardRepository.delete(board);
			result = CheckResult.SUCCESS;
//			LogService.addLog("Board", id, result, method);
		}
		if (result == CheckResult.SUCCESS) {
//			LogService.addLog("Board", id, result);
		}
		return result;
	}

	// 게시판 정보 업데이트
	public int updateBoard(long id, Board board) {
		int result = CheckResult.FAIL;
		if (board == null)
			return CheckResult.NO_EXIST_CHAINGE_INFO;
		Board oldBoard = boardRepository.findById(id).orElse(null);
		if (oldBoard == null)
			return CheckResult.NO_EXIST;
		else if (!oldBoard.getBoardId().equals(board.getBoardId()))
			return CheckResult.NO_MATCH;
		if (boardRepository.save(board) != null)
			result = CheckResult.SUCCESS;
		return result;
	}

	public Board getBoardByBoardId(long boardId) {
		return boardRepository.findById(boardId).orElse(null);
	}
}