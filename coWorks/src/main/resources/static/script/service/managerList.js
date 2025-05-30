// 게시판 회원 목록 받아오기
export async function getBoardUserList(listUl, countEm) {
	const res = await fetch("/users/board/{boardId}/users",{
		method: "GET",
		headers: {"Content-Type": "application/json"}
	});
	// 받아온 게시판 회원 목록
	const data = await res.json();
	const boardUsers = await data.userList;
	countEm.innerText = boardUsers.length;
	
	boardUsers.forEach(user => {
		// <li>태그. 앞으로 반복될 노드. 이 안에 유저 정보가 들어있음
		const userInfo = document.createElement("li");
		
		// <li>태그 안에 <a href="프로필 상세로 이동" class="user_info"> 추가
		const userAnchor = userInfo.appendChild("a");
		userAnchor.classList.add("user_info");
		
		// <a>태그 안에 <div class="user_thumb"> 추가, 프사 추가
		const userThumb = userAnchor.appendChild("div");
		userThumb.classList.add("user_info");
		userThumb.innerHTML = "<img src='" + user.profile_pic + "' alt='image' style='opacity: 1;'>";
		
		// <a>태그 안에 <div class="user_profile"> 추가
		const userProfile = userAnchor.appendChild("div");
		userProfile.classList.add("user_profile");
		
		// <span class="user_name">유저명</span>
		const userName = userProfile.appendChild("span");
		userName.classList.add("user_name");
		userName.innerText = user.name;
		
		// <span class="user_position">과장</span>
		const userPosition = userProfile.appendChild("span");
		userPosition.classList.add("user_position");
		userPosition.innerText = user.position;
		
		// <div class="user_subscript"><p>자기소개를 입력해주세요.</p></div>
		const userSubscript = userProfile.appendChild("div");
		userSubscript.classList.add("user_subscript");
		userSubscript.innerHTML = "<p>" + user.subScript + "</p>";
		
		// 반복될 노드를 listDiv에 append
		listUl.appendChild(userInfo);
	});
}

//게시판 운영자 목록 받아오기
export async function getManagerList(listUl, countEm) {
	const res = await fetch("/managers/board/{id}/managers",{
		method: "GET",
		headers: {"Content-Type": "application/json"}
	});
	// 받아온 게시판 회원 목록
	const data = await res.json();
	const managers = await data.boardManagerList;
	countEm.innerText = managers.length;
	
	managers.forEach(user => {
		const userInfo = document.createElement("li");
		
		const userAnchor = userInfo.appendChild("a");
		userAnchor.classList.add("user_info");
		
		const userThumb = userAnchor.appendChild("div");
		userThumb.classList.add("user_info");
		userThumb.innerHTML = "<img src='" + user.profile_pic + "' alt='image' style='opacity: 1;'>";
		
		const userProfile = userAnchor.appendChild("div");
		userProfile.classList.add("user_profile");
		
		const userName = userProfile.appendChild("span");
		userName.classList.add("user_name");
		userName.innerText = user.name;
		
		const userPosition = userProfile.appendChild("span");
		userPosition.classList.add("user_position");
		userPosition.innerText = user.position;
		
		const userSubscript = userProfile.appendChild("div");
		userSubscript.classList.add("user_subscript");
		userSubscript.innerHTML = "<p>" + user.subScript + "</p>";
		
		// 반복될 노드를 listDiv에 append
		listUl.appendChild(userInfo);
	});
}

//게시판 숨겨진 게시글, 숨겨진 댓글 받아오기
export async function hiddenPostList(listUl, countEm) {
	const res = await fetch("/managers/board/post/{id}/hidden",{
		method: "GET",
		headers: {"Content-Type": "application/json"}
	});
	// 받아온 게시판 회원 목록
	const data = await res.json();
	const hiddenPosts = await data.userList;
	countEm.innerText = managerList.length;
}