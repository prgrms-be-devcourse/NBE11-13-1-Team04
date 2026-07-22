document.addEventListener("DOMContentLoaded", () => {
    fetchOrders();
});

// REST API 호출 및 테이블 렌더링
async function fetchOrders(queryParams = "") {
    try {
        const response = await fetch(`/api/admin/orders${queryParams}`);
        if (!response.ok) {
            throw new Error("주문 목록을 불러오는 데 실패했습니다.");
        }
        const orders = await response.json();
        renderOrderTable(orders);
    } catch (error) {
        console.error("Fetch Orders Error:", error);
        alert(error.message);
    }
}

function renderOrderTable(orders) {
    const tbody = document.getElementById("orderTableBody");
    tbody.innerHTML = "";

    if (!orders || orders.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">조회된 주문 내역이 없습니다.</td></tr>`;
        return;
    }

    orders.forEach(order => {
        const tr = document.createElement("tr");

        // Row 전체 클릭 시 상세 정보 페이지 이동
        tr.onclick = (e) => {
            if (e.target.tagName === 'BUTTON') return; // 버튼 클릭 시 이벤트 전파 차단
            goDetail(order.orderId);
        };

        const statusClass = getStatusBadgeClass(order.status);
        const formattedPrice = order.totalPrice ? order.totalPrice.toLocaleString() + "원" : "0원";
        const formattedDate = order.orderedAt ? order.orderedAt.replace("T", " ").substring(0, 16) : "-";

        tr.innerHTML = `
            <td><strong>${order.orderCode}</strong></td>
            <td>${order.email}</td>
            <td>${order.totalAmount}개</td>
            <td>${formattedPrice}</td>
            <td>${formattedDate}</td>
            <td><span class="status-badge ${statusClass}">${order.statusDescription}</span></td>
            <td>
                <button class="btn-sm btn-outline" onclick="goDetail(${order.orderId})">상세보기</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// 필터 조회 제출 시
function searchOrders(event) {
    event.preventDefault();
    const status = document.getElementById("statusSelect").value;
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    const params = new URLSearchParams();
    if (status) params.append("status", status);
    if (startDate) params.append("startDate", startDate);
    if (endDate) params.append("endDate", endDate);

    const queryString = params.toString() ? `?${params.toString()}` : "";
    fetchOrders(queryString);
}

// 필터 초기화
function resetFilters() {
    document.getElementById("searchForm").reset();
    fetchOrders();
}

// 상세 페이지 이동
function goDetail(orderId) {
    location.href = `/admin/orders/${orderId}`;
}

// 상태에 따른 뱃지 CSS 클래스 매핑
function getStatusBadgeClass(status) {
    switch (status) {
        case "CONFIRMED": return "available";
        case "ORDERED": return "ordered";
        case "CANCEL_REQUESTED": return "warning";
        case "CANCELED": return "soldout";
        default: return "";
    }
}

// 로그아웃
async function logout() {
    if (!confirm("로그아웃 하시겠습니까?")) return;

    try {
        const res = await fetch("/admin/auth/logout", { method: "POST" });
        if (res.ok) {
            location.href = "/admin/login";
        } else {
            alert("로그아웃 처리 중 오류가 발생했습니다.");
        }
    } catch (error) {
        console.error("Logout Error:", error);
        alert("네트워크 통신 오류가 발생했습니다.");
    }
}