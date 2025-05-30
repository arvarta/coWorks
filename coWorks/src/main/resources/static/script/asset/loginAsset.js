import { login, checkNull } from "../service/login.js";


document.querySelector("button.btn_core").addEventListener('click', async function() {
	try {
		// 비밀번호는 암호화 예정...
		const idInput = document.querySelector(".mem_wrap input[type='text']");
		const password = document.querySelector(".mem_wrap input[type='password']");
		
		// const pwInput = await passwordHashing(password);
		
		const loginArgs = {
			idInput:idInput.value.trim(),
			pwInput:password.value.trim()
		}
		checkNull(loginArgs);
		
		// ID 기억하기, 로그인 유지
		const keepId = document.querySelector("#keep_id");
		const keepLogin = document.querySelector("#keep_login");
		
		if(keepId.checked) {
			localStorage.setItem("keepId", true);
			localStorage.setItem("savedId", idInput.value.trim());
		}
		
		if(keepLogin.checked) {
			localStorage.setItem("keepLogin", true);
		}
		
		await login(loginArgs);
		
	} catch(err) {
		alert(err.message);
	}
});

// ID 기억하기
if(localStorage.getItem("keepId") === true) {
	idInput.value = localStorage.getItem("savedId");
}

// 로그인 유지
if(localStorage.getItem("keepLogin") === true) {
	// 따로 로그아웃을 누르기 전까지 localStorage에 유지되어 있는 정보를 쭉 사용
	location.href = "/user/main";
}