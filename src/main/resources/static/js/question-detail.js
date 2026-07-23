let currentEmail = "";
let currentQuestionId = null;

document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    currentEmail = urlParams.get("email");

    // Path Pattern: /questions/{questionId}
    const pathSegments = window.location.pathname.split('/');
    currentQuestionId = pathSegments[pathSegments.length - 1];

    if (!currentQuestionId || !currentEmail) {
        alert("잘못된 접근입니다. 문의 ID 또는 이메일이 누락되었습니다.");
        location.href = "/questions";
        return;
    }

    fetchQuestionDetail(currentQuestionId, currentEmail);
});

async function fetchQuestionDetail(questionId, email) {
    try {
        const response = await fetch(`/api/questions/${questionId}?email=${encodeURIComponent(email)}`);

        if (!response.ok) {
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.message || "문의 정보를 불러올 수 없습니다.");
        }

        const data = await response.json();
        renderDetail(data);
    } catch (error) {
        console.error("Fetch Detail Error:", error);
        alert(error.message);
        location.href = `/questions?email=${encodeURIComponent(email)}`;
    }
}

function renderDetail(data) {
    document.getElementById("questionTitle").innerText = data.title;
    document.getElementById("questionContent").innerText = data.content;
    document.getElementById("questionCreatedAt").innerText = data.createdAt ? new Date(data.createdAt).toLocaleString() : "";
    document.getElementById("questionStatus").innerHTML = getStatusBadge(data.status);

    // ★ 답변 완료(ANSWERED) 상태일 경우 삭제 버튼 숨기기
    const deleteBtn = document.getElementById("deleteBtn");
    if (data.status === "ANSWERED") {
        deleteBtn.style.display = "none";
    } else {
        deleteBtn.style.display = "inline-block"; // 대기 중일 때는 표시
    }

    if (data.answer) {
        document.getElementById("answerContainer").style.display = "block";
        document.getElementById("answerContent").innerText = data.answer.content || data.answer.comment || "";
        document.getElementById("answerCreatedAt").innerText = data.answer.createdAt ? new Date(data.answer.createdAt).toLocaleString() : "";
    } else {
        document.getElementById("answerContainer").style.display = "none";
    }
}

// ★ 문의 삭제 처리 함수
async function handleDeleteQuestion() {
    if (!currentQuestionId || !currentEmail) {
        alert("문의 정보가 올바르지 않습니다.");
        return;
    }

    // 비밀번호 입력 받기 (QuestionAuthRequest 검증용)
    const password = prompt("문의 등록 시 입력했던 이메일을 입력해주세요:");
    if (password === null) return; // 취소 누른 경우
    if (!password.trim()) {
        alert("비밀번호를 입력해야 합니다.");
        return;
    }

    if (!confirm("정말로 이 문의를 삭제하시겠습니까?")) return;

    try {
        const response = await fetch(`/api/questions/${currentQuestionId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: currentEmail,
                password: password.trim()
            })
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.message || "문의 삭제에 실패했습니다. (비밀번호 불일치 등)");
        }

        alert("문의가 성공적으로 삭제되었습니다.");
        goBackToList();
    } catch (error) {
        console.error("Delete Question Error:", error);
        alert(error.message);
    }
}

function getStatusBadge(status) {
    if (status === "ANSWERED") {
        return `<span class="badge badge-answered">답변 완료</span>`;
    }
    return `<span class="badge badge-waiting">답변 대기</span>`;
}

function goBackToList() {
    location.href = `/questions?email=${encodeURIComponent(currentEmail)}`;
}