import { checkUserSession } from "../util/checking.js";

// 헤더 검색 조건 셀렉박스 클릭시 드롭다운 메뉴 노출
export function searchGroupSelect(searchGroupSelectBtn) {
	const searchGroupOptionItem = document.querySelector(".search_group_option_item");

	if (searchGroupSelectBtn && searchGroupOptionItem) {
		searchGroupSelectBtn.addEventListener("click", function () {
			const isVisible = searchGroupOptionItem.style.display === "block";
			searchGroupOptionItem.style.display = isVisible ? "none" : "block";
			searchGroupSelectBtn.classList.toggle("open", !isVisible);
		});

		document.addEventListener("click", function (e) {
			if (!searchGroupSelectBtn.contains(e.target) && !searchGroupOptionItem.contains(e.target)) {
				searchGroupOptionItem.style.display = "none";
				searchGroupSelectBtn.classList.remove("open");
			}
		});
	}
}

//상세검색창
export function searchBox(detailBtn, detailPop, searchBtn) {
	// 상세 검색 열기
	if (detailBtn && detailPop) {
		detailBtn.addEventListener("click", function (e) {
			e.stopPropagation(); // 이벤트 버블링 막기
			detailPop.style.display = "block";
		});

	    // 검색 버튼 클릭 시 제출
		if (searchBtn) {
			searchBtn.addEventListener("click", function () {
				// detailPop.style.display = "none";
			});
		}

	    // 바깥 영역 클릭 시 닫기
		document.addEventListener("click", function (e) {
			// detailPop 영역이나 버튼을 누른 경우는 제외
			if (!detailPop.contains(e.target) && !detailBtn.contains(e.target)) {
				detailPop.style.display = "none";
			}
		});
	}
};

// 프로필 열기
export function openProfile(profileArea, loginUserMenu) {

	if (profileArea && loginUserMenu) {
		profileArea.addEventListener("click", function (e) {
			e.preventDefault();
			loginUserMenu.style.display = loginUserMenu.style.display === "none" ||
				loginUserMenu.style.display === "" ? "block" : "none";
		});

		// 바깥 클릭 시 메뉴 닫기
		document.addEventListener("click", function (e) {
			if (!profileArea.contains(e.target) && !loginUserMenu.contains(e.target)) {
				loginUserMenu.style.display = "none";
			}
		});
	}
}

// 사용자 프로필 정보 받아오기
// @Fixed
export async function getMyProfile(userProfileDiv) {
	/*
	const res = await fetch();
	const userThumb = document.querySelector(".user_thumb");
	if(localStorage.getItem("profilePic") !== "null") {
		userThumb.innerHTML = '<img src="' + localStorage.getItem("profilePic") + '" alt="image" style="opacity: 1;">';
	} else {
		userThumb.innerHTML = '<img src="/img/thumb_profile.svg" alt="image" style="opacity: 1" />';
	}
	userProfileDiv.children[0].innerText = localStorage.getItem("userName");
	userProfileDiv.children[1].innerText = localStorage.getItem("userDept");
	*/
	const id = localStorage.getItem("userInfo");
	const res = await fetch("/users/" + id , {
			method: "GET",
			headers: {"Content-Type": "application/json"}
		});
	const data = await res.json();
	checkUserSession(data);
	const userThumb = document.querySelector(".user_thumb");
	const userProfile = await data.profileInfo;
	
	userThumb.innerHTML = '<img src="/img/thumb_profile.svg" alt="image" style="opacity: 1" />';
	userProfileDiv.children[0].innerText = userProfile.userName;
	userProfileDiv.children[1].innerText = userProfile.userDepartment;
	
}

// 로그아웃
export function logout(logoutBtn) {
	logoutBtn.addEventListener('click', async () => {
		if(confirm("로그아웃하시겠습니까?")) {
			const res = await fetch("/users/logout"	, {
		        method: "POST",
		        headers: { "Content-Type": "application/json" },
		    });
			if(res.ok) {
				location.href = "/logout";
				localStorage.removeItem("userInfo");
			}
			
		}
	});
}

// 사용자 마이페이지 레프트
export function leftUiOn(menu) {
	const lists = menu.querySelectorAll("li");
	lists.forEach(li => {
		const ref = li.children[0].href.split("/").pop();
		if(window.location.pathname.split("/").pop() === ref) {
			li.classList.add("on");
		} else {
			li.classList.remove("on");
		}
	});
}