// 유사 세션 체크
export function checkUserSession(data) {
	if(data.responseCode === 401) {
		alert("로그인이 필요한 서비스입니다.");
		localStorage.removeItem("userInfo");
		location.href="/users";
	}
}