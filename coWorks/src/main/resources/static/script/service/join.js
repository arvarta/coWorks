// 공백 체크
export function checkNull(inputs, msgs) {
    inputs.forEach((item, idx) => {
        if (!item.value.trim()) {
            item.focus();
            throw new Error(msgs[idx]);
        }
    });

    if (inputs[1].value !== inputs[2].value) {
        focus(inputs[2]);
        throw new Error("비밀번호가 틀렸습니다.");
    }

    return true;
}

// 인증 절차
export async function verifyReq(userEmail) {
    const response = await fetch("/users/email", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userEmail),
    });

    if (response.status === 200) {
        const verifyInput = document.createElement("input");
        verifyInput.type = "text";
        verifyInput.placeholder = "인증번호를 입력하세요";

        const lastInput = document.querySelector("input:last-of-type");
        lastInput.insertAdjacentElement("afterend", verifyInput);

        const infoText = document.createElement("p");
        infoText.style.color = "#468af7";
        infoText.style.textAlign = "center";
        infoText.style.padding = "10px";
        infoText.innerText = await response.text();
        document.querySelector(".mem_wrap").appendChild(infoText);
    } else {
        // 임시
        throw new Error("서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}

// 이메일 인증코드 확인
export async function verifyEmail(verifyArgs) {
    const res = await fetch("/users/email", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(verifyArgs),
    });

    const data = await res.text();

    if (data === "인증 성공") {
        return true;
    } else if (data === "인증 실패") {
        throw new Error("이메일 인증에 실패했습니다");
    } else {
        throw new Error("잠시 후 다시 시도해주세요");
    }
}

// 회원가입
export async function join(joinArgs) {
    const response = await fetch("/users/join", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(joinArgs),
    });
	
	const data = await response.json();

    if (data.message === "Join Success") {
		location.href = "/users";
        throw new Error("회원가입 신청이 완료되었습니다.\n관리자의 승인을 기다려주세요.");
		
    } else if(data.message === "Join Fail, Not Found User") {
		throw new Error("사용자를 찾을 수 없습니다.");
		
	} else {
		throw new Error("서버에 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.");
        // return response.json();
    }
}
