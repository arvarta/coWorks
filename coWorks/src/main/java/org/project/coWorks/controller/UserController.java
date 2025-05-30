package org.project.coWorks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.coWorks.dto.CommentDto;
import org.project.coWorks.dto.PostDto;
import org.project.coWorks.dto.ProfileRequestDto;
import org.project.coWorks.model.Board;
import org.project.coWorks.model.BoardUser;
import org.project.coWorks.model.Department;
import org.project.coWorks.model.Employee;
import org.project.coWorks.model.Post;
import org.project.coWorks.model.Proposal;
import org.project.coWorks.model.UploadFile;
import org.project.coWorks.model.User;
import org.project.coWorks.service.BoardService;
import org.project.coWorks.service.BoardUserService;
import org.project.coWorks.service.CommentService;
import org.project.coWorks.service.EmployeeService;
import org.project.coWorks.service.PostService;
import org.project.coWorks.service.UploadFileService;
import org.project.coWorks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private BoardService boardService;
	@Autowired
	private PostService postService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private BoardUserService boardUserService;
	@Autowired
	private UploadFileService uploadFileService;
	@Autowired
	private EmployeeService employeeService;

	// 로그인 처리 (POST 요청)
	@PostMapping
	public Map<String, Object> login(@RequestBody Map<String, String> req, @Autowired HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		Long id = 0L;
		try {
			id = Long.parseLong(req.get("idInput"));
		} catch (Exception e) {
			result.put("responseCode", 404);
			return result;
		}
		int success = userService.login(id, req.get("pwInput"));
		ProfileRequestDto userInfo = userService.getUserInfo(id);

		switch (success) {
		case CheckResult.SUCCESS: {
			User user = userService.getUserById(id).get();
			session.setAttribute("user", user);
			if(!user.getUserStatus().equals(User.USER_STATUS[User.ADMIN])) {
				result.put("userInfo", id);
				result.put("profilePic", userInfo.getUserProfilePicture());
				result.put("userName", userInfo.getUserName());
				result.put("userDept", userInfo.getUserDepartment());
			}
			result.put("userStatus", user.getUserStatus());
			result.put("message", "Login Success");
			
			break;
		}
		case CheckResult.FAIL: {
			result.put("message", "Login Fail, No Match Password");
			result.put("idInput", id);
			result.put("pwInput", req.get("pwInput"));
			break;
		}
		case CheckResult.NO_EXIST: {
			result.put("message", "Login Fail, Not Found User");
			break;
		}
		case CheckResult.PARTIAL_SUCCESS: {
			result.put("message", "Login Fail, No Passed sign");
			break;
		}
		default: {
			result.put("message", "error");
		}
		}
		return result;
	}

	// 회원가입 요청 처리 (POST)
	// 파라미터를 Map으로 받아오기
	// @Fixed
	@PostMapping("/join")
	public Map<String, Object> joinMembership(@RequestBody Map<String, Object> json) {
		Long userId = Long.parseLong((String) json.get("id"));
		Map<String, Object> result = new HashMap<>();
		String password = (String) json.get("password");
		int process = userService.joinMembership(userId, password);
		switch (process) {
		case CheckResult.SUCCESS: {
			result.put("message", "Join Success");
			break;
		}
		case CheckResult.NO_EXIST: {
			result.put("message", "Join Fail, Not Found User");
			break;
		}
		default: {
			result.put("message", "error");
		}
		}
		return result;
	}

	// 이메일 인증 코드 발송 요청 (POST)
	@PostMapping("/email")
	public ResponseEntity<String> sendVerficationEmail(@RequestBody String email, @Autowired HttpSession session) {
		String code = userService.sendVerification(email);
		session.setAttribute("verificationCode_" + email, code);
		session.setAttribute("codeTimestamp_" + email, System.currentTimeMillis());
		return ResponseEntity.ok("인증 코드가 발송되었습니다.");
	}

	// 이메일 인증 코드 검증 요청 (PUT)
	@PutMapping("/email")
	public String verifyCode(Map<String, Object> map, @Autowired HttpSession session) {
		String email = (String) map.get("email");
		String code = (String) map.get("code");

		String storedCode = (String) session.getAttribute("verificationCode_" + email);
		Long timestamp = (Long) session.getAttribute("codeTimestamp_" + email);

		boolean isVerified = userService.verifyCode(code, storedCode, timestamp != null ? timestamp : 0);

		if (isVerified) {
			session.removeAttribute("verificationCode+" + email);
			session.removeAttribute("codeTimestamp_" + email);
			return "인증 성공";
		}
		return "인증 실패";
	}

	// 특정 게시판 조회 (GET)
	@GetMapping("/board")
	public Map<String, Object> viewBoard(@Autowired HttpSession session, @RequestParam(required = false) String boardId,
			@RequestParam(defaultValue = "1") int page) {
		// UserService에서 게시글 리스트 조회 후 반환
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		Long targetBId = 0L;
		Long userId = user.getUserId();

		if (boardId != null) {
			// 특정 게시판 지정
			targetBId = Long.parseLong(boardId);
		} else {
			// 게시판 유저 목록 찾기
			List<BoardUser> buList = boardUserService.findByUserId(userId);
			if (buList != null && !buList.isEmpty()) {
				// 특정 게시판의 id를 게시판 유저한테 받아옴
				targetBId = buList.get(0).getBoardId();
			} else {
				// 게시판 목록 전부 받아오기
				List<Board> b = boardService.getAll();
				if (b != null) {
					for (Board board : b) {
						// 게시판 유저를 사용자와 게시판 id로 찾음
						BoardUser temp = boardUserService.findByUserIdAndBoardId(userId, targetBId);
						if (temp != null) {
							// 타겟 id를 게시판의 아이디로 설정
							targetBId = board.getBoardId();
							break;
						}
					}
				}
			}
		}
		if (targetBId == 0) {
			result.put("responseCode", 400);
			return result;
		}
		try {
			result.put("targetBoard", boardService.findById(targetBId));
			result.put("responseCode", CheckResult.SUCCESS);
		} catch (Exception e) {
			result.put("responseCode", CheckResult.FAIL);
		}
		return result;
	}

	// 특정 게시판 유저 목록 받아오기
	@GetMapping("/board/userList")
	public Map<String, Object> findBoardUser(@Autowired HttpSession session,
			@RequestParam(required = false) Long boardId, 
			@RequestParam(defaultValue = "1") int page,
			@RequestParam Map<String, Object> json) {
		// UserService에서 게시글 리스트 조회 후 반환
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		result.put("userData", User.USER_STATUS[User.USER]);
		List<BoardUser> targetBUList = null;
		Board b = boardService.findById(boardId);
		if (b != null) {
			// 게시판 유저 목록
			targetBUList = boardUserService.findByBoardId(b.getBoardId());
		} else {
			List<BoardUser> bu = boardUserService.findByUserId(user.getUserId());
			if (bu != null) {
				Board temp = boardService.findById(boardId);
				if (temp != null) {
					targetBUList = boardService.getUsersByBoardId(b);
				}
			}
		}
		try {
			result.put("boardList", targetBUList);
			result.put("responseCode", CheckResult.SUCCESS);
		} catch (Exception e) {
			result.put("responseCode", CheckResult.FAIL);
		}
		return result;
	}

	// 특정 게시판 내 게시글 목록 조회 (GET)
	@GetMapping("/board/{boardId}")
	public Map<String, Object> findPostList(HttpSession session, @PathVariable String boardId,
			@RequestParam(defaultValue = "1") int page) {
		// UserService에서 게시글 리스트 조회 후 반환
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		List<Post> postList = boardService.getPostsByBoardId(Long.parseLong(boardId));
		List<PostDto> pList = new ArrayList<>();
		for(Post p : postList) {
			pList.add(postService.findByPostIdAtDto(p.getPostId()));
		}
		Board boardInfo = boardService.getBoardByBoardId(Long.parseLong(boardId));
		try {
			result.put("totalCount", pList.size());
			result.put("postList", Pagination.paging(pList, page));
			result.put("totalPage", Pagination.totalPage(postList));
			result.put("boardInfo", boardInfo);
			result.put("responseCode", CheckResult.SUCCESS);
		} catch (Exception e) {
			result.put("responseCode", CheckResult.FAIL);
		}
		return result;
	}
	
	// 특정 사용자가 이용 가능한 게시판 목록 (GET)
	// @Added
	@GetMapping("/{userId}/board")
	public Map<String, Object> getUserBoardList(HttpSession session, @PathVariable String userId) {
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		// 임시...
		List<Board> bList = boardService.getAll();
		result.put("myBoardList", bList);
		return result;
	}

	// 특정 게시판 내 사용자 목록 조회 (GET)
	@GetMapping("/board/{boardId}/users")
	public Map<String, Object> viewUserList(@Autowired HttpSession session, @PathVariable Long boardId,
			@RequestParam(defaultValue = "1") int page) {
		// UserService에서 해당 게시판 사용자 목록 조회 후 반환
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		Board board = boardService.findById(boardId);
		if (board == null) {
			result.put("responseCode", 400);// 찾을 수 없음
			return result;
		}
		List<BoardUser> buList = boardService.getUsersByBoardId(board);
		try {
			result.put("boardUserList", Pagination.paging(buList, page));
			result.put("totalPage", Pagination.totalPage(buList));
			result.put("responseCode", CheckResult.SUCCESS);
		} catch (Exception e) {
			result.put("responseCode", CheckResult.FAIL);
		}
		return result;
	}


	// 특정 게시판 내 특정 게시글 보기 (GET)
	@GetMapping("/board/post/{postId}")
	public Map<String, Object> readPost(@Autowired HttpSession session, @PathVariable Long postId) {
		// UserService에서 게시글 조회 후 반환
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		PostDto pDto = postService.findByPostIdAtDto(postId);
		List<UploadFile> files = uploadFileService.findByPostId(postId);
		int process = CheckResult.processToNull(pDto);
		process = CheckResult.processToNull(files);
		result.put("responseCode", process);
		if (process == 200) {
			result.put("post", pDto);
			result.put("fileList", files);
		}
		return result;
	}
	
	// 특정 게시판 내 특정 게시글의 댓글 보기 (GET)
	@GetMapping("/board/post/{postId}/comment")
	public Map<String, Object> readComment(@Autowired HttpSession session, @PathVariable Long postId) {
		// UserService에서 게시글 조회 후 반환
		Map<String, Object> result = new HashMap<>();
		User user = (User)session.getAttribute("user");
		if (user == null){
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		BoardUser bu = boardUserService
			.findByUserIdAndBoardId(user.getUserId()
				, postService.findById(postId).getBoardId());
		if(bu == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		
		List<CommentDto> comments = commentService.commentsDto(postId);
		int process = CheckResult.processToNull(comments);
		result.put("responseCode", process);
		if (process == 200) {
			result.put("commentList", comments);
			result.put("buId", bu.getBoardUserId());
		}
		return result;
	}

	// 게시글 작성 처리 (POST)
	@PostMapping("/board/{boardId}/post")
	public Map<String, Object> writePost(
			@PathVariable Long boardId, 
			@RequestPart(required = false) MultipartFile[] file,
			@RequestPart String title,
			@RequestPart String content,
			HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		Board board = boardService.findById(boardId);
		if (board == null) {
			result.put("responseCode", 500);// http 코드 서버 잘못
			return result;
		}
		BoardUser boardUser = boardUserService.findByUserIdAndBoardId(user.getUserId(),boardId);
		if (boardUser == null) {
			result.put("responseCode", 500);// http 코드 서버 잘못
			return result;
		}
		Long buid = boardUser.getBoardUserId();
		int process = CheckResult.checkToProcess(postService.savePost(
				boardId,title,content,file ,boardUser.getBoardUserId()));
		if(process != 200) {
			result.put("title", title);
			result.put("content", content);
			result.put("file", file);
		} else if(process == 200) {
			result.put("post", postService.findByBoardUserIdAndLastWritePost(buid));
		}
		result.put("responseCode", process);
		return result;
	}

	// 게시글 수정 처리 (PUT)
	@PutMapping("/board/post/{postId}")
	public Map<String, Object> updatePost(@PathVariable Long postId, 
			@RequestPart MultipartFile[] file,
			@RequestPart String title,
			@RequestPart String content,
			@Autowired HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		Post oldPost = postService.findById(postId);
		if (oldPost == null) {
			result.put("responseCode", 400);
			return result;
		}
		BoardUser boardUser = boardUserService.findByUserIdAndBoardId(user.getUserId(), postId);
		if (boardUser == null) {
			result.put("responseCode", 400);
			return result;
		}
		int process = CheckResult.checkToProcess(
			postService.updatePost(postId,boardUser.getBoardUserId(),title,content,file)
		);
		if(process != 200) {
			result.put("title", title);
			result.put("content", content);
			result.put("file", file);
		}else if(process == 200) {
			Long buid = boardUser.getBoardUserId();
			result.put("post", postService.findByBoardUserIdAndLastWritePost(buid));
		}
		result.put("responseCode", process);
		return result;
	}

	// 게시글 숨김 처리 (DELETE)
	@DeleteMapping("/board/post/{postId}")
	public Map<String, Object> hidePost(@PathVariable Long postId, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		// 게시글 숨김 처리 요청
		String status = postService.deletePost(postId);
		if (Post.STATUS[Post.DELETED].equals(status)) {
			result.put("responseCode", 200);
		} else {
			// 실패 시 400 Bad Request 반환
			result.put("responseCode", 500);
		}
		return result;
	}

	// 댓글 작성 처리 (POST)
		@PostMapping("/board/post/{postId}/comment")
		public Map<String, Object> addComment(@PathVariable Long postId,
			@RequestBody Map<String, Object> json,
			@Autowired HttpSession session) {
			Map<String, Object> result = new HashMap<>();
			User user = (User) session.getAttribute("user");
			if (user == null) {
				result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
				return result;
			}
			System.err.println(postId);
			BoardUser bu = boardUserService.findByUserIdAndBoardId(
				user.getUserId(),postService.findByPostId(postId).getBoardId());
			if(bu == null) {
				result.put("responseCode", 400);
			}
			result.put("userData", User.USER_STATUS[User.USER]);
			// 댓글 생성 요청
			int process = CheckResult.checkToProcess(commentService.save(json, bu.getBoardUserId()));
			result.put("responseCode", process);
			result.put("targetId", postId);
			return result;
		}

		// 댓글/대댓글 수정 (PUT)
		@PutMapping("/board/post/comment/{commentid}")
		public Map<String, Object> updateComment(@PathVariable Long commentId, @RequestBody Map<String, Object> json,
				@Autowired HttpSession session) {
			Map<String, Object> result = new HashMap<>();
			User user = (User) session.getAttribute("user");
			if (user == null) {
				result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
				return result;
			}
			BoardUser bu = (BoardUser) session.getAttribute("boardUser");
			result.put("userData", User.USER_STATUS[User.USER]);
			result.put("response", commentService.updateComment(commentId, bu.getBoardUserId(), json));
			return result;
		}

	// 댓글/대댓글 삭제(숨김)
	@DeleteMapping("/board/post/comment/{commentid}")
	public Map<String, Object> hideComment(@PathVariable Long commentId, @Autowired HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);

		BoardUser bu = (BoardUser) session.getAttribute("boardUser");
		result.put("responseCode",
				CheckResult.checkToProcess(commentService.hideComment(commentId, bu.getBoardUserId())));
		return result;
	}

	// 대댓글 작성
	@PostMapping("/board/comment/{commentId}/comment")
	public Map<String, Object> createReply(@PathVariable Long commentId, @RequestBody Map<String, Object> json,
			@Autowired HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		BoardUser bu = (BoardUser) session.getAttribute("boardUser");
		result.put("userData", User.USER_STATUS[User.USER]);
		// 댓글 생성 요청
		result.put("responseCode", CheckResult.checkToProcess(commentService.save(json, bu.getBoardUserId())));
		return result;
	}

	// 검색
	@GetMapping("/search")
	public Map<String, Object> searchPosts(HttpServletRequest request,
			@RequestParam(defaultValue = "1") int page) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		List<Post> pl = postService.searchPosts(request, user.getUserId());
		int process = CheckResult.processToNull(pl);
		if(process == 200) {
			try {
				if(request.getAttribute("total") != null) {					
					result.put("totalPostList", Pagination.paging(pl, page));
					result.put("totalPage", Pagination.totalPage(pl));
				}else {
					result.put("detailPostList", Pagination.paging(pl, page));
					result.put("totalPage", Pagination.totalPage(pl));
				}
			} catch (Exception e) {
				
			}
		}
		return result;
	}

	// 내 정보 보기 (GET /users/{id})
	@GetMapping("/{userId}")
	public Map<String, Object> getUserInfo(@PathVariable String userId, @Autowired HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
//		if (user != null) {
//			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
//			return result;
//		}
		result.put("userData", User.USER_STATUS[User.USER]);
		int process = CheckResult.processToNull(user);
		ProfileRequestDto profile = userService.getUserInfo(Long.parseLong(userId));
		if (process == 200) {
			result.put("profileInfo", profile);
		}
		result.put("responseCode", process);
		return result;
	}

	// 프로필 수정 (PUT /{id}/profile)
	@PutMapping(value = "/{userId}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object> updateProfile(@PathVariable Long userId, @RequestBody Map<String, Object> json,
			@Autowired HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user != null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		int process = CheckResult.checkToProcess(userService.updateProfile(userId, json));
		result.put("responseCode", process);
		if (process == 200) {
			session.setAttribute("user", userService.getUserById(userId));
		}
		return result;

	}

	// 비밀번호 변경 (PUT /{id}/password)
	@PutMapping("/{userId}/password")
	public Map<String, Object> changePassword(@Autowired HttpSession session, @PathVariable Long userId,
			@RequestBody Map<String, Object> json) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		String oldPassword = user.getUserPw();
		String newPassword = ((String) json.get("password"));
		String retry = ((String) json.get("retry"));
		result.put("responseCode", (userService.changePassword(userId, oldPassword, newPassword, retry)));
		return result;
	}

	// 회원 탈퇴 처리 (DELETE)
	@DeleteMapping("/{userId}")
	public Map<String, Object> deleteUser(@Autowired HttpSession session, @PathVariable Long userId) {
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		result.put("responseCode", CheckResult.checkToProcess(userService.deleteUser(userId)));

		return result;
	}

	// 내가 쓴 게시글 목록 보기
	@GetMapping("/{userId}/post")
	public Map<String, Object> getMyPosts(@Autowired HttpSession session,
		@PathVariable Long userId, @RequestParam(defaultValue = "1") int page) {
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		List<PostDto> pList = postService.getMyPosts(userId);
		
		int process = CheckResult.processToNull(pList);
		result.put("responseCode", process);
		if (process == 200) {
			try {
				result.put("postList", Pagination.paging(pList,page));
				result.put("totalPage", Pagination.totalPage(pList));
			} catch (Exception e) {
				e.printStackTrace();
				result.put("responceCode", 500);
			}
		}
		return result;
	}

	// 내가 쓴 게시글 상세 보기
	@GetMapping("/posts/{postId}")
	public Map<String, Object> readMyPost(@Autowired HttpSession session, @PathVariable Long postId,
			@RequestBody Map<String, Object> json) {
		Map<String, Object> result = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		PostDto post = postService.findByPostIdAtDto(postId);
		int process = CheckResult.processToNull(post);
		if (process == 200)
			result.put("post", post);
		// 작성자(userId)와 조회 대상 글 작성자가 같은지 검증
		if (boardUserService.findByUserIdAndBoardId(user.getUserId(), post.getBoardId()) == null) {
			result.put("responseCode", 405);
		}
		return result;
	}

//		//게시판 추가 요청하기
//		@PostMapping("/proposals")
//	    public ResponseEntity<String> createProposal(@RequestBody ProposalRequestDto dto) {
//	        boardService.createProposal(dto);
//	        return ResponseEntity.ok("게시판 추가 요청이 접수되었습니다.");
//	    }
	//
//		//게시판 추가 요청 목록
//		@GetMapping("/proposals")
//		public ResponseEntity<List<Proposal>> getProposals(@RequestParam Long userId) {
//			ProposalBoardUser userList = proposalBoardUserRepository.findById(userId)
//					.orElseThrow(() -> new IllegalArgumentException("게시판 요청 유저 정보가 존재하지 않습니다."));
//			List<Proposal> list = proposalRepository.findBy(userList);
//			return ResponseEntity.ok(list);
//		}

	// 게시판 추가 요청 상세 보기
	@GetMapping("/proposals/{id}")
	public Map<String, Object> getProposalDetail(@Autowired HttpSession session, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		Proposal proposal = boardService.getProposalDetail(id);
		int process = CheckResult.processToNull(proposal);
		if (process == 200)
			result.put("proposal", proposal);
		result.put("responseCode", process);
		return result;
	}

	// 전체 조직도 보기
	@GetMapping("/department")
	public Map<String, Object> getAllDepartments(@Autowired HttpSession session,
			@RequestParam Map<String, Object> json, @RequestParam(defaultValue = "1")int page) {
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		List<Department> dList = userService.getAllDepartments();
		try {
			result.put("departmentList", Pagination.paging(dList, page));
			result.put("perPage", Pagination.totalPage(dList));
			result.put("responseCode", CheckResult.SUCCESS);
		} catch (Exception e) {
			result.put("responseCode", CheckResult.FAIL);
		}
		return result;
	}

	// 부서 정보 보기 수정
	@GetMapping("/department/{departmentId}")
	public Map<String, Object> getEmployeesByDepartment(@Autowired HttpSession session,
			@PathVariable String departmentId, @RequestParam(defaultValue = "1")int page) {
		Map<String, Object> result = new HashMap<>();
		if (!CheckResult.isLoggedIn(session)) {
			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.USER]);
		List<Employee> eList = employeeService.findByDepartmentId(Integer.parseInt(departmentId));
		List<ProfileRequestDto> profileList = new ArrayList<>();
		for(Employee e : eList) {
			profileList.add(userService.getUserInfo(e.getEmployeeId()));
		}
		try {
			result.put("departmentName", userService.getDepartmentById(Integer.parseInt(departmentId)).getDepartmentName());
			result.put("profileList", Pagination.paging(profileList, page));
			result.put("perPage", Pagination.totalPage(profileList));
			result.put("responseCode", CheckResult.SUCCESS);
		} catch (Exception e) {
			result.put("responseCode", CheckResult.FAIL);
		}
		return result;
	}

	// 부서 내 사용자의 정보 보기 수정
	@GetMapping("/department/users/{userId}")
	public Map<String, Object> getEmployeeDetail(@PathVariable Long userId) {
		Map<String, Object> result = new HashMap<>();
		Employee employee = employeeService.findByEmployeeId(userId);
		if (employee == null) {
			result.put("responseCode", 400);
			return result;
		}
		result.put("responseCode", 200);
		return result;
	}

	// 로그아웃
	@PostMapping("/logout")
	public Map<String, Object> logout(@Autowired HttpSession session) {
		Map<String, Object> result = new HashMap<>();
//		User user = (User) session.getAttribute("user");
//		if (user == null) {
//			result.put("responseCode", 401);// http 코드 로그인 하지 않은 유저의 접근 시도
//			return result;
//		}
		session.invalidate();
		result.put("responseCode", 200);// http 코드 로그인 하지 않은 유저의 접근 시도
		result.put("message", "logout success!");
		return result;
	}

}