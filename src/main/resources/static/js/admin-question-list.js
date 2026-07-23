let allQuestions = [];

document.addEventListener("DOMContentLoaded", () => {
    fetchAdminQuestions();
});

async function fetchAdminQuestions() {
    try {
        const response = await fetch("/api/admin/questions");

        if (!response.ok) {
            throw new Error("관리자 문의 목록을 불러올 수 없습니다.");
        }

        allQuestions = await response.json();
        renderTable(allQuestions);
    } catch (error) {
        console.error("Fetch Admin Questions Error:", error);
        alert(error.message);
    }
}

function renderTable(questions) {
    const tbody = document.getElementById("adminQuestionTableBody");
    tbody.innerHTML = "";

    if (!questions || questions.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="empty-msg">등록된 문의가 없습니다.</td></tr>`;
        return;
    }

    questions.forEach(q => {
        const tr = document.createElement("tr");
        const formattedDate = q.createdAt ? new Date(q.createdAt).toLocaleString() : "-";

        tr.innerHTML = `
            <td>${q.id}</td>
            <td><strong>${escapeHtml(q.email)}</strong></td>
            <td class="text-left">${escapeHtml(q.title)}</td>
            <td>${getStatusBadge(q.status)}</td>
            <td>${formattedDate}</td>
            <td>
                <button class="btn-outline btn-sm" onclick="location.href='/admin/questions/${q.id}'">
                    ${q.status === 'ANSWERED' ? '상세 보기' : '답변 작성'}
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function filterQuestions() {
    const status = document.getElementById("statusFilter").value;
    if (status === "ALL") {
        renderTable(allQuestions);
    } else {
        const filtered = allQuestions.filter(q => q.status === status);
        renderTable(filtered);
    }
}

function getStatusBadge(status) {
    if (status === "ANSWERED") {
        return `<span class="badge badge-answered">답변 완료</span>`;
    }
    return `<span class="badge badge-waiting">답변 대기</span>`;
}

function escapeHtml(text) {
    if (!text) return "";
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}