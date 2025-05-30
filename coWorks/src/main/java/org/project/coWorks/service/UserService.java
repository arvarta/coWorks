package org.project.coWorks.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.project.coWorks.controller.CheckResult;
import org.project.coWorks.dto.PostDetailDto;
import org.project.coWorks.dto.ProfileRequestDto;
import org.project.coWorks.model.Board;
import org.project.coWorks.model.Comment;
import org.project.coWorks.model.Department;
import org.project.coWorks.model.Employee;
import org.project.coWorks.model.Post;
import org.project.coWorks.model.UploadFile;
import org.project.coWorks.model.User;
import org.project.coWorks.repository.BoardRepository;
import org.project.coWorks.repository.BoardUserRepository;
import org.project.coWorks.repository.CommentRepository;
import org.project.coWorks.repository.DepartmentRepository;
import org.project.coWorks.repository.EmployeeRepository;
import org.project.coWorks.repository.PositionRepository;
import org.project.coWorks.repository.PostRepository;
import org.project.coWorks.repository.UploadFileRepository;
import org.project.coWorks.repository.UserRepository;
import org.project.coWorks.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Value("${file.upload-dir}") // application.properties에서 파일 업로드 경로 읽어옴
	private String fileDir;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BoardUserRepository boardUserRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private UploadFileRepository uploadFileRepository;
	@Autowired
	private UploadFileService uploadFileService;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private ProposalService proposalService;
	
	@Autowired
	private PositionRepository positionRepository;

	// 로그인 성공 여부 확인하는 메서드
	public int login(Long userId, String userPw) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			return CheckResult.NO_EXIST; // 유저 없음
		}
		if (user.getUserStatus().equals(User.USER_STATUS[User.STAND])) {
			return CheckResult.PARTIAL_SUCCESS; // 신청 대기 상태의 유저
		}
		if (passwordEncoder.matches(userPw, user.getUserPw())) {
			return CheckResult.SUCCESS;
		} else {
			return CheckResult.FAIL; // 비번 불일치
		}
	}

	// 회원가입 요청 (관리자 승인 필요)
	@Transactional
	public int joinMembership(Long userId, String password) {
		// 사원번호가 존재하는지 확인
		Employee e = employeeRepository.findById(userId).orElse(null);
		if (e == null) {
			return CheckResult.NO_EXIST; // 존재하지 않는 사원
		}
		User user = new User();
		user.setUserId(userId);
		user.setUserPw(passwordEncoder.encode(password));
		user.setUserStatus("stand");
		if (userRepository.save(user) != null) {
			return CheckResult.SUCCESS;
		} else {
			return CheckResult.FAIL;
		}
	}

	public int savePost(Map<String, Object> json, User user) {
		String title = (String)json.get("title");
		Long boardId = (Long)json.get("boardUserId");
		Long boardUserId = boardUserRepository
			.findByUserIdAndBoardId(boardId, user.getUserId()).getBoardUserId();
		String content = (String)json.get("content");
		if(title == null || boardId == null ||
			boardUserId == null || content == null) {
			return CheckResult.NO_EXIST;
		}
		Post newPost = new Post();
		newPost.setPostTitle(title);
		newPost.setBoardUserId(boardUserId);
		newPost.setBoardId(boardId);
		newPost.setPostContent(content);
		newPost.setPostStatus(Post.STATUS[Post.POSTTING]);
		newPost.setPostCreateDate(LocalDateTime.now());
		Post oldPost = postRepository.findById(newPost.getPostId()).orElse(null);
		MultipartFile[] files = (MultipartFile[])json.get("file");
		if(oldPost != null) {
			return CheckResult.EXIST;
		}
		newPost = postRepository.save(newPost);
		if(files != null) {
			if(uploadFileService.save(newPost.getPostId(),files) != null)
				return CheckResult.SUCCESS;
			return CheckResult.PARTIAL_SUCCESS;
		}
		return CheckResult.SUCCESS;
	}

	// 사용자 프로필
	public ProfileRequestDto getUserInfo(Long userId) {
		ProfileRequestDto result = new ProfileRequestDto();
		Employee e = employeeRepository.findById(userId).orElse(null);
		User u = userRepository.findById(userId).orElse(null);
		if(e == null || u == null) {
			return null;
		}
		result.setUserId(userId);
		result.setUserBirth(e.getEmployeeBirth());
		result.setUserDepartment(
			departmentRepository
			.findById(e.getDepartmentId()).get()
			.getDepartmentName()
		);
		result.setUserMail(e.getEmployeeMail());
		result.setUserMessage(u.getUserMessage());
		result.setUserName(e.getEmployeeName());
		result.setUserPosition(
			positionRepository
			.findById(e.getPositionId()).get()
			.getPositionName()
		);
		if(u.getProfileId() != null) {
			result.setUserProfilePicture(uploadFileService.getPicture(u.getProfileId()));
		} else {
			result.setUserProfilePicture(null);
		}
		result.setUserTel(e.getEmployeeTel());
		return result;
	}
	
	// 부서명 가져오기
	public Department getDepartmentById(Integer deptId) {
		return departmentRepository.findById(deptId).orElse(null);
	}

	// 이메일 인증 코드 생성 및 Redis 저장, 이메일 발송
	// redis 오류때문에 session으로 수정
	// @Fixed
	public String sendVerification(String toEmail) {
		String code = CodeGenerator.generateCode(); // 6자리 난수 코드 생성
//			template.delete(toEmail); // Redis 기존 코드 삭제
//			template.opsForValue().set(toEmail, code, 300, TimeUnit.SECONDS); // 5분간 저장
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(toEmail);
		message.setSubject("이메일 인증 코드입니다.");
		message.setText("인증번호는 " + code + "입니다. 5분 내에 입력해주세요.");
		message.setFrom("coWorks@org.ac.kr");
		mailSender.send(message);

		System.out.println("인증 코드 발송: " + code);

		return code;
	}

	// 인증 확인
	// redis 오류때문에 session으로 수정
	// @Fixed
	public boolean verifyCode(String storedCode, String inputCode, long timestamp) {
		// String storedCode = template.opsForValue().get(email);
		if (storedCode == null || timestamp == 0) {
			return false;
		}
		long currentTime = System.currentTimeMillis();
		if (currentTime - timestamp > 300000) {
			return false;
		}
		return storedCode.equals(inputCode);
	}

	// 내 정보 보기
	public Optional<User> getUserById(Long userId) {
		return userRepository.findById(userId);
	}

	// 비밀번호 변경
	public boolean changePassword(Long userId, String oldPassword, 
			String newPassword, String retryPassword) {
		if (!newPassword.equals(retryPassword))
			return false;

		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty())
			return false;

		User user = userOpt.get();

		// userPw는 평문인지 암호화된건지에 따라 다르지만 보통 암호화 필요
		if (!passwordEncoder.matches(oldPassword, user.getUserPw())) {
			return false; // 기존 비밀번호 불일치
		}

		user.setUserPw(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		return true;
	}


	// 내가 쓴 게시글 상세보기
	@Transactional
	public PostDetailDto viewMyPost(Long postId, Long userId) {
		Post post = postRepository.findById(postId).orElse(null);

		// 게시글 작성자와 요청한 userId가 같은지 검사
		if (!post.getBoardUserId().equals(userId)) {
			throw new IllegalArgumentException("권한이 없습니다. 본인의 글만 조회할 수 있습니다.");
		}

		List<UploadFile> files = uploadFileRepository.findByPostId(post.getPostId());
		List<Comment> comments = comments(postId);

		return new PostDetailDto(post.getPostId(), userId, files, comments);
	}

	private List<Comment> comments(Long postId) {
		List<Comment> comments = commentRepository.findByCommentTargetId(postId);
		List<Comment> result = new ArrayList<>();
		for (Comment comment : comments) {
			result.add(comment);
			List<Comment> temp = commentRepository.findByCommentTargetId(comment.getCommentId());
			if (temp != null && temp.size() > 0) {
				for (Comment ctemp : temp) {
					result.add(ctemp);
				}
			}
		}
		return result;
	}

	// 회원 탈퇴
	@Transactional
	public int deleteUser(Long userId) {
		User user = userRepository.findByUserId(userId);
		if (user == null) {
			return CheckResult.NO_EXIST;
		}
		user.setUserStatus("unvisible");
		userRepository.save(user);
		return CheckResult.SUCCESS;
	}

	// 전제 조직도 열람
	public List<Department> getAllDepartments() {
		return departmentRepository.findAll();
	}

	// 특정 부서의 구성원 목록
	public List<Employee> getEmployeesByDepartmentId(Long departmentId) {
		List<Employee> list = departmentRepository.findByDepartmentId(departmentId);
		if (list == null) {
			return null;
		}
		return list;
	}

	// 특정 부서 내 특정 사용자 상세 정보
	public Employee getEmployeeDetail(Long employeeId) {
		return employeeRepository.findById(employeeId).orElse(null);
	}

	// 상태로 사용자 목록 가져오기
	public List<User> findByUserStatus(String status) {
		List<User> result = null;
		if(status != null) {
			result = userRepository.findByUserStatus(status);
		}else {
			result = userRepository.findAllByOrderByUserId();
		}
		return result;
	}



// 회원 활성화 및 비활성화
	public int updateUser(Long userId, String method) {
		int result = CheckResult.FAIL;
		User user = getUserById(userId).orElse(null);
		// 존재하지 않는 유저일 시
		if (user == null) {
			result = CheckResult.NO_EXIST;
//				LogService.addLog("updateUser", userId, result);	// 시스템 로그 확인
			return result;
		} else if (method.equals("POST")) {
			user.setUserStatus(User.USER_STATUS[User.USER]);
			user.setEmployeeId(userId);
			userRepository.save(user);
			List<Board> board = boardRepository.findAll();
			for(Board b : board) {
			proposalService.saveBoardUser(b, user);
			}
		} else if (method.equals("DELETE")) {
			user.setUserStatus(User.USER_STATUS[User.DELETE]);
		}
		result = CheckResult.SUCCESS;
//			LogService.addLog("updateUser", userId, result);	//시스템 로그 확인
		return result;
	}

	// 회원 DB에서 제거
	public int dropUser(Long userId) {
		int result = CheckResult.FAIL;
		User user = getUserById(userId).orElse(null);
		if (user == null) {
			result = CheckResult.NO_EXIST;
//			LogService.addLog("deleteUser", id, result);	// 시스템 로그 확인
			return result;
		} else {
			userRepository.delete(user);
			result = CheckResult.SUCCESS;
//			LogService.addLog("deleteUser", id, result);	//시스템 로그 확인
		}
		return 0;
	}

	// 회원가입 요청 수
	public long userCount(String userStatus) {
		return userRepository.countByUserStatus(userStatus);
	}
	
	@Transactional
	public int updateProfile(Long userId, Map<String, Object> json) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null)
			return CheckResult.NO_EXIST;
		MultipartFile newProfilePicture = (MultipartFile)json.get("picture");
		// 프로필 사진 저장
		if (newProfilePicture != null && !newProfilePicture.isEmpty()) {
			UploadFile uploadFile = new UploadFile();
			uploadFile.setFileName(newProfilePicture.getOriginalFilename());
			uploadFile.setFileDate(LocalDateTime.now());
			user.setProfileId(uploadFile.getFileId());
		}else {
			user.setProfileId(uploadFileRepository.findByFileName("default").getFileId());
		}
		String newUserMessage = (String)json.get("message");
		user.setUserMessage(newUserMessage);

		userRepository.save(user);
		return CheckResult.SUCCESS;
	}

}
