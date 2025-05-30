// 공백 확인
export function checkNull(loginArgs) {
    if (!loginArgs.idInput) {
        document.querySelector(".mem_wrap input[type='text']").focus();
        throw new Error("ID를 입력해주세요.");
    }
    if (!loginArgs.pwInput) {
        document.querySelector(".mem_wrap input[type='password']").focus();
        throw new Error("비밀번호를 입력해주세요.");
    }
}

// 로그인
export async function login(loginArgs) {
    const res = await fetch("/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginArgs),
    });
    const data = await res.json();

    if (data.message === "Login Success") {
		if (data.userStatus === "admin") {
            location.href = "/admin/main";
            // sessionStorage.setItem("count", data.statistics);
        } else {
            location.href = "/user/main";
	        localStorage.setItem("userDept", data.userDept);
	        localStorage.setItem("profilePic", data.profilePic);
		}
        localStorage.setItem("userName", data.userName);
        localStorage.setItem("userInfo", data.userInfo);

    } else if (data.message === "Login Fail, No Match Password") {
        throw new Error("로그인에 실패했습니다");
		
    } else if (data.message === "Login Fail, Not Found User") {
		throw new Error("사용자를 찾을 수 없습니다");
		
	} else if (data.message === "Login Fail, No Passed sign") {
		throw new Error("승인 대기 중인 사용자입니다.\n승인이 완료될 때 까지 기다려주세요.");
		
	} else {
        throw new Error("잠시 후 다시 시도해주세요");
    }
}
/*
export async function passwordHashing(password) {
	const msgBuffer = new TextEncoder().encode(password);
	const hashBuffer = await crypto.subtle.digest("SHA-256", msgBuffer);
	const hashArray = Array.from(new Uint8Array(hashBuffer));
	return hashArray.map(b => b.toString(16).padStart(2, "0")).join("");
}
*/

