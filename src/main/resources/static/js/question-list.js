document.addEventListener("DOMContentLoaded", () => {
    // URL Query Parameter에 email이 존재하면 자동 조회
    const urlParams = new URLSearchParams(window.location.search);
    const emailParam = urlParams.get("email");

    if (emailParam) {
        document.getElementById("searchEmail").value = emailParam;
        fetchQuestions(emailParam);
    }
});

function searchQuestions(event) {
    event.preventDefault();
    const email = document.getElementById("searchEmail").value.trim();
    if (!email) return;

    fetchQuestions(email);
}

async function fetchQuestions(email) {
    try {
        const response = await fetch(`/api/questions?email=${encodeURIComponent(email)}`);

        if (!response.ok) {
            throw new Error("문의 목록을 불러올 수 없습니다.");
        }

        const questions = await response.json();
        renderQuestionTable(questions, email);
    } catch (error) {
        console.error("Fetch Questions Error:", error);
        alert(error.message);
    }
}

function renderQuestionTable(questions, email) {
    const tbody = document.getElementById("questionTableBody");
    tbody.innerHTML = "";

    if (!questions || questions.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="empty-msg">해당 이메일로 등록된 문의가 없습니다.</td></tr>`;
        return;
    }

    questions.forEach(q => {
        const tr = document.createElement("tr");
        const formattedDate = q.createdAt ? new Date(q.createdAt).toLocaleDateString() : "-";

        tr.innerHTML = `
            <td>${q.id}</td>
            <td><strong>${q.title}</strong></td>
            <td>${getStatusBadge(q.status)}</td>
            <td>${formattedDate}</td>
            <td>
                <button class="btn-outline btn-sm" onclick="location.href='/questions/${q.id}?email=${encodeURIComponent(email)}'">상세보기</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function getStatusBadge(status) {
    if (status === "ANSWERED") {
        return `<span class="badge badge-answered">답변 완료</span>`;
    }
    return `<span class="badge badge-waiting">답변 대기</span>`;
}