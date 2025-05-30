import { checkUserSession } from "../util/checking.js";
import { verifyEmail } from "../service/join.js";
const id = localStorage.getItem("userInfo");

// 사용자 정보 받기
async function getUserInfo() {
	const res = await fetch("/users/" + id , {
			method: "GET",
			headers: {"Content-Type": "application/json"}
		});
	const data = await res.json();
	checkUserSession(getUserInfo);
	const userProfile = await data.profileInfo;
	return userProfile;
}

// 프로필 수정 : 프로필 편집
export async function updateMyProfile() {
	
}

// 프로필 수정 : 비밀번호 변경
export async function changeMyPassword(pwInputs) {
	const changePw = document.querySelector("#change_pw");
	const uid = localStorage.getItem("userInfo");
	changePw.addEventListener('click', async () => {
		try {
			pwInputs.forEach(item => {
				if(!item.value.trim()) {
					throw new Error("비밀번호를 입력해주세요.");
				}
			});
			if(pwInputs[1].value !== pwInputs[2].value) {
				throw new Error("새 비밀번호가 일치하지 않습니다.");
			}
			const res = await fetch("/users/" + uid + "/password");
			const data = await res.json();
			if(data.responseCode === true) {
				throw new Error("비밀번호가 변경되었습니다.");
			}
		} catch(Err) {
			alert(Err.message);
		}
	});
}

// 내가 쓴 글 확인
export async function getMypostList(myPostList) {
	const id = localStorage.getItem("userInfo");
	const res = await fetch("/users/" + id + "/post", {
	    method: "GET",
	    headers: { "Content-Type": "application/json" },
	});

	const data = await res.json();
	checkUserSession(data);
	const posts = data.postList;

	if (Array.isArray(posts) && posts.length > 0) {
	    posts.forEach((post) => {
	        const row = myPostList.insertRow();
	        // 게시판 이름
	        row.insertCell(0).textContent = post.boardName;
	        // 제목
	        row.insertCell(1).textContent = post.postTitle;
	        // 날짜
	        row.insertCell(2).textContent = post.postSimpleDate;

	        row.addEventListener("click", () => {
	            location.href = "/user/post/" + post.postId;
	        });
	    });
	} else {
	    const row = myPostList.insertRow();
	    const cell = row.insertCell(0);
	    cell.colSpan = 3;
		
	    const errDiv = document.createElement("div");
		errDiv.classList.add("err_message");
		cell.append(errDiv);
		
		const nodataP = document.createElement("p");
		nodataP.classList.add("nodata");
		nodataP.innerText = "게시글이 존재하지 않습니다.";
		
		errDiv.append(nodataP);
	}	
}

// 게시판 생성 신청 내역
export async function getMyPurposeList() {
	
}

// 회원탈퇴
export function userGoodBye(withdrawConfirm) {
	const isAgree = document.querySelector("#agree");
	withdrawConfirm.addEventListener('click', () => {
		try {
			if(isAgree.checked === false) {
				throw new Error("안내사항에 동의해주세요");
			} 
			if(confirm("정말로 탈퇴하시겠습니까?")) {
				location.href = "/user/account";
			}
		} catch(err) {
			alert(err.message);
		}
	});
}

// faq
export function checkFaq(questionDiv, answerDiv) {
	questionDiv.forEach((item, idx) => {
		item.addEventListener('click', () => {
			if(answerDiv[idx].style.display === "block") {
				answerDiv[idx].style.display = "none";
				
			} else {
				answerDiv[idx].style.display = "block";
			}
		});
	});
}

// 회원탈퇴 절차
export async function verifyBeforeBye(withdrawSubmit) {
	let authProgress = "대기중";
	
	withdrawSubmit.addEventListener('click', async () => {
		const mailInput = document.querySelector("#withdraw_mail");
		const userEmail = mailInput.value;
		try {
			if(!userEmail) {
				throw new Error("이메일을 입력해주세요.");
			}
			const info = await getUserInfo();
			if(userEmail !== info.userMail) {
				throw new Error("이메일이 일치하지 않습니다.");
			}
			
			if(authProgress === "대기중") {
				await verifying();
				authProgress = "전송됨";
				
			} else if(authProgress === "전송됨") {
				const codeInput = document.querySelector("input[type='text']:last-of-type");
				const verifyArgs = {
				    email: document.querySelector("#withdraw_mail").value.trim(),
				    code: codeInput.value.trim()
				};
				codeInput.addEventListener('change', async () => {
					await verifyEmail(verifyArgs);
				});
				
				const res = await fetch("/users/" + id, {
			        method: "DELETE",
			        headers: { "Content-Type": "application/json" }
				});
				
				if(res.ok) {
					localStorage.removeItem("userInfo");
					alert("회원탈퇴 신청이 완료되었습니다.\n이용해주셔서 감사합니다.");
					const res = await fetch("/users/logout"	, {
					    method: "POST",
					    headers: { "Content-Type": "application/json" },
					});
					location.href = "/logout";
				}
			}
		} catch(err) {
			alert(err.message);
		}
	});
}
// 회원탈퇴 절차 : 이메일 전송 요청
async function verifying() {
	const mailInput = document.querySelector("#withdraw_mail");
	const userEmail = mailInput.value;	
	const res = await fetch("/users/email", {
	    method: "POST",
	    headers: { "Content-Type": "application/json" },
	    body: JSON.stringify(userEmail),
	});
	
	if (res.status === 200) {
	    const verifyInput = document.createElement("input");
	    verifyInput.type = "text";
	    verifyInput.placeholder = "인증번호를 입력하세요";
	    mailInput.after(verifyInput);

	    const infoText = document.createElement("p");
	    infoText.style.color = "#468af7";
	    infoText.style.textAlign = "center";
	    infoText.style.padding = "10px";
	    infoText.innerText = await res.text();
	    document.querySelector(".mem_wrap").appendChild(infoText);
						
	} else {
	    throw new Error("서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
	}	
}
// 마이페이지 내 정보
export async function getMyProfile(profile, inforList) {
	const userInfo = await getUserInfo();
	let profilePic = userInfo.userProfilePicture;
	if(!profilePic) {
		profilePic = "<img src='/img/thumb_profile.svg' alt='image' style='opacity: 1;'>";
	}
	profile.children[0].innerHTML = "<img src='/img/thumb_profile.svg' alt='image' style='opacity: 1;'>";
	
	const profileInfo = profile.children[1];
	profileInfo.children[0].innerText = userInfo.userName;
	
	if(!userInfo.userMessage) {
		profileInfo.children[1].innerText
			= "상태 메시지를 입력해주세요.";
	} else {
		profileInfo.children[1].innerText = userInfo.userMessage;
	}
		
	const listInputs = [
		userInfo.userDepartment,
		userInfo.userPosition,
		userInfo.userTel,
		userInfo.userMail,
		userInfo.userBirth
	];
	
	inforList.forEach((info, idx) => {
		info.lastElementChild.innerText = listInputs[idx];
	});
}
