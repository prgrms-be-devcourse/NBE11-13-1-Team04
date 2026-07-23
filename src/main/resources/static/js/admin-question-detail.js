let currentQuestionId = null;
let currentAnswerId = null; // 1. 삭제 호출 시 사용할 답변 ID 저장용 변수 추가

document.addEventListener("DOMContentLoaded", () => {
    // Path: /admin/questions/{questionId}
    const pathSegments = window.location.pathname.split('/');
    currentQuestionId = pathSegments[pathSegments.length - 1];

    if (!currentQuestionId || isNaN(currentQuestionId)) {
        alert("올바르지 않은 접근입니다.");
        location.href = "/admin/questions";
        return;
    }

    fetchAdminQuestionDetail(currentQuestionId);
});

async function fetchAdminQuestionDetail(id) {
    try {
        const response = await fetch(`/api/admin/questions/${id}`);

        if (!response.ok) {
            throw new Error("문의 상세 정보를 가져오는데 실패했습니다.");
        }

        const data = await response.json();
        renderDetail(data);
    } catch (error) {
        console.error("Fetch Detail Error:", error);
        alert(error.message);
        location.href = "/admin/questions";
    }
}

function renderDetail(data) {
    document.getElementById("questionEmail").innerText = data.email || "-";
    document.getElementById("questionTitle").innerText = data.title || "-";
    document.getElementById("questionContent").innerText = data.content || "-";
    document.getElementById("questionCreatedAt").innerText = data.createdAt ? new Date(data.createdAt).toLocaleString() : "-";
    document.getElementById("questionStatus").innerHTML = getStatusBadge(data.status);

    // 2. 이미 등록된 답변이 존재하는 경우 처리
    if (data.answer) {
        // 백엔드 객체 구조에 맞춰 answerId 추출 (id 또는 answerId)
        currentAnswerId = data.answer.id || data.answer.answerId;

        const answerInput = document.getElementById("answerContent");
        answerInput.value = data.answer.content || data.answer.comment || "";
        answerInput.readOnly = true; // 삭제 전용 상태이므로 수정 불가능하게 비활성화

        const btnSubmit = document.getElementById("btnSubmitAnswer");
        btnSubmit.innerText = "답변 삭제";
        btnSubmit.className = "btn-warning"; // 기존에 추가한 주황/경고 버튼 스타일로 변경
    }
}

async function handleSaveAnswer(event) {
    event.preventDefault();

    // 3. 이미 등록된 답변이 존재하는 경우 -> DELETE 요청 수행
    if (currentAnswerId) {
        if (!confirm("등록된 답변을 정말 삭제하시겠습니까?")) {
            return;
        }

        try {
            // 컨트롤러 엔드포인트: @DeleteMapping("/answers/{answerId}") -> /api/admin/answers/{answerId}
            const response = await fetch(`/api/admin/answers/${currentAnswerId}`, {
                method: "DELETE"
            });

            if (!response.ok) {
                const err = await response.json().catch(() => null);
                throw new Error(err?.message || "답변 삭제 중 오류가 발생했습니다.");
            }

            alert("답변이 성공적으로 삭제되었습니다.");
            location.href = "/admin/questions";
        } catch (error) {
            console.error("Delete Answer Error:", error);
            alert(error.message);
        }
        return;
    }

    // 4. 답변이 없는 경우 -> 기존 POST 요청 (등록) 수행
    const content = document.getElementById("answerContent").value.trim();
    if (!content) {
        alert("답변 내용을 입력해주세요.");
        return;
    }

    try {
        const response = await fetch(`/api/admin/questions/${currentQuestionId}/answers`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ content })
        });

        if (!response.ok) {
            const err = await response.json().catch(() => null);
            throw new Error(err?.message || "답변 저장 중 오류가 발생했습니다.");
        }

        alert("답변이 성공적으로 저장되었습니다.");
        location.href = "/admin/questions";
    } catch (error) {
        console.error("Save Answer Error:", error);
        alert(error.message);
    }
}

function getStatusBadge(status) {
    if (status === "ANSWERED") {
        return `<span class="badge badge-answered">답변 완료</span>`;
    }
    return `<span class="badge badge-waiting">답변 대기</span>`;
}