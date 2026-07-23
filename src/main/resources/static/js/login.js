const form = document.getElementById("loginForm");
form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const res = await fetch("/admin/auth/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({username, password})
    });
    if (res.ok) {
        location.href = "/admin/orders";
    } else {
        document.getElementById("error").innerText = "아이디 또는 비밀번호가 올바르지 않습니다.";
    }
});