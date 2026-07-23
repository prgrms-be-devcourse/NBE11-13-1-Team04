async function handleCreateQuestion(event) {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();

    if (!email || !title || !content) {
        alert("모든 필수 입력 값을 채워주세요.");
        return;
    }

    const payload = { email, title, content };

    try {
        const response = await fetch("/api/questions", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.message || "문의 등록 중 오류가 발생했습니다.");
        }

        alert("문의가 정상적으로 등록되었습니다.");
        // 등록 후 본인 문의 목록 페이지로 이동 (이메일 파라미터 전달)
        location.href = `/questions?email=${encodeURIComponent(email)}`;
    } catch (error) {
        console.error("Create Question Error:", error);
        alert(error.message);
    }
}