import { checkUserSession } from "../util/checking.js";
// 조직도 부서별 사용자 목록 받아오기
export async function getOrgUserList(memberListDiv) {
	let departmentId = window.location.pathname.split("/").pop();
	if(!departmentId || departmentId === "organization") {
		departmentId = 1;
	}
	const res = await fetch("/users/department/" + departmentId);
	// 받아온 유저목록
	const data = await res.json();
	checkUserSession(data);
	const userList = data.profileList;
	
	const titleDiv = memberListDiv.querySelector(".title");
	if(userList !== 0) {
		titleDiv.innerText = data.departmentName;
		const ul = memberListDiv.querySelector("ul");
	
		userList.forEach(user => {
			if(!user) {
				
			} else {
				// <li>태그. 앞으로 반복될 노드. 이 안에 유저 정보가 들어있음
				const popBtn = document.createElement("li");
				popBtn.classList.add("pop_btn");
				popBtn.dataset.id = user.userId || 1;
				
				// <li>태그 안에 <a href="프로필 상세로 이동" class="user_info"> 추가
				const userInfo = document.createElement("div");
				userInfo.classList.add("user_info");
				userInfo.dataset.id = user.userId || 1;
				popBtn.appendChild(userInfo);
				
				userInfo.addEventListener("click", (e) =>{
					memberProfileController.initList(userList);
					memberProfileController.memberSelect(e)}
				);			
				
				// <a>태그 안에 <div class="user_thumb"> 추가, 프사 추가
				const userThumb = document.createElement("div");
				userThumb.classList.add("user_thumb");
				let profilePic = user.userProfilePicture;
				if(!profilePic) {
					profilePic = "/img/thumb_profile.svg";
				}
				userThumb.innerHTML = "<img src='" + profilePic + "' alt='image' style='opacity: 1;'>";
				userInfo.appendChild(userThumb);
				
				// <a>태그 안에 <div class="user_profile"> 추가
				const userProfile = document.createElement("div");
				userProfile.classList.add("user_profile");
				userInfo.appendChild(userProfile);
				
				// <span class="user_name">유저명</span>
				const userName = document.createElement("span");
				userName.classList.add("user_name");
				userName.innerText = user.userName;
				userProfile.appendChild(userName);
				
				// <span class="user_position">과장</span>
				const userPosition = document.createElement("span");
				userPosition.classList.add("user_position");
				userPosition.innerText = user.userPosition;
				userProfile.appendChild(userPosition);
				
				// <div class="user_subscript"><p>자기소개를 입력해주세요.</p></div>
				const userSubscript = document.createElement("div");
				userSubscript.classList.add("user_subscript");
				let msg = user.userMessage;
				if(!user.userMessage) {
					msg = "자기소개를 입력해주세요.";
				}
				userSubscript.innerHTML = "<p>" + msg + "</p>";
				userProfile.appendChild(userSubscript);
				
				// 반복될 노드를 listDiv에 append
				ul.appendChild(popBtn);
			}
		});	
	} else {
		titleDiv.innerText = data.departmentName;
	}
}

// 조직도 레프트 부서 목록 받아오기
export async function getDepartmentList(depMenu) {
	const res = await fetch("/users/department");
	const data = await res.json();
	checkUserSession(data);
	const deptList = data.departmentList;
	deptList.forEach(dept => {
		const li = document.createElement("li");
		li.classList.add("menu_item");
		
		const anchor = document.createElement("a");
		anchor.href = "/user/organization/" + dept.departmentId;
		anchor.innerText = dept.departmentName;
		li.appendChild(anchor);
		
		const ref = anchor.href.split("/").pop();
		if(window.location.pathname.split("/").pop() === ref) {
			li.classList.add("on");
		} else {
			li.classList.remove("on");
		}
		depMenu.appendChild(li);
	});
}

// 프로필
export const memberProfileController = {
    targetId: 0,
    profileList: [],

    popupChange() {
        const popupTag = document.querySelector(".layer_pop");
        popupTag.style.display = popupTag.style.display === "none" ? "block" : "none";
    },
	
	initList(profileList) {
		this.profileList = profileList;
	},

    memberSelect(e) {
        const id = parseInt(e.currentTarget.dataset.id);
        if (id > 0) {
            this.targetId = id;
            this.popupChange();
            this.popupValueChange(
				this.profileList.find((f) => f.userId === id)
			);
        }
    },

    popupValueChange(data) {
        const userProfileTag = document.querySelector("#user_modal_profile");
        userProfileTag.querySelector(".user_name").innerText = data.userName;
        userProfileTag.querySelector(".user_subscript p").innerText = data.userMessage;
		console.log(data);

        const forEachList = [
			data.userDepartment,
			data.userPosition,
			data.userTel,
			data.userMail,
			data.userBirth
		];

        document.querySelectorAll(".infor_list li > p").forEach((item, index) => {
            item.innerText = forEachList[index];
        });

        document.querySelector("button[type='button'].pop_close").addEventListener(
            "click", () => {
                this.targetId = 0;
                this.popupChange();
            },
            { once: true }
        );
    },
};