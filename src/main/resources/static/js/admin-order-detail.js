// 현재 화면의 orderId 저장용 글로벌 변수
let currentOrderId = null;

document.addEventListener("DOMContentLoaded", () => {
    const pathParts = window.location.pathname.split("/");
    const orderId = pathParts[pathParts.length - 1];

    if (orderId && !isNaN(orderId)) {
        currentOrderId = orderId;
        fetchOrderDetail(orderId);
    }
});

async function fetchOrderDetail(orderId) {
    try {
        const response = await fetch(`/api/admin/orders/${orderId}`);
        if (!response.ok) {
            throw new Error("주문 상세 정보를 가져오는 데 실패했습니다.");
        }
        const data = await response.json();
        renderOrderDetail(data);
    } catch (error) {
        console.error("Fetch Order Detail Error:", error);
        alert(error.message);
    }
}

function renderOrderDetail(data) {
    document.getElementById("detailOrderCode").textContent = data.orderCode;
    document.getElementById("detailOrderedAt").textContent = data.orderedAt ? data.orderedAt.replace("T", " ").substring(0, 16) : "-";
    document.getElementById("detailEmail").textContent = data.email;
    document.getElementById("detailAddress").textContent = data.address;
    document.getElementById("detailPostalCode").textContent = data.postalCode;
    document.getElementById("detailTotalPrice").textContent = data.totalPrice ? data.totalPrice.toLocaleString() : "0";

    const statusElem = document.getElementById("detailStatus");
    statusElem.textContent = data.statusDescription;
    statusElem.className = `status-badge ${getStatusBadgeClass(data.status)}`;

    // ----------------------------------------------------
    // 💡 [추가] 주문 상태별 Action 버튼 영역 동적 렌더링
    // ----------------------------------------------------
    renderActionButtons(data.status);

    // 상품 목록 렌더링
    const tbody = document.getElementById("detailItemTableBody");
    tbody.innerHTML = "";

    if (!data.orderItems || data.orderItems.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;">주문 상품이 없습니다.</td></tr>`;
        return;
    }

    data.orderItems.forEach(item => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${item.productId}</td>
            <td><strong>${item.productName}</strong></td>
            <td>${item.productPrice.toLocaleString()}원</td>
            <td>${item.amount}개</td>
            <td>${item.itemTotalPrice.toLocaleString()}원</td>
        `;
        tbody.appendChild(tr);
    });
}

/**
 * 주문 상태에 따른 버튼 생성 함수
 * HTML 상에 <div id="actionButtonContainer"></div> 요소가 정의되어 있어야 합니다.
 */
function renderActionButtons(status) {
    const container = document.getElementById("actionButtonContainer");
    if (!container) return;

    container.innerHTML = "";

    if (status === "CANCEL_REQUESTED") {
        // 취소 요청 상태 -> [취소 승인], [취소 거절] 버튼 노출
        container.innerHTML = `
            <button type="button" class="btn-success" onclick="approveCancel()">취소 승인</button>
            <button type="button" class="btn-warning" onclick="rejectCancel()">취소 거절</button>
        `;
    } else if (status === "ORDERED") {
        // 미확정/주문 확정 상태 -> [관리자 직접 취소] 버튼 노출
        container.innerHTML = `
            <button type="button" class="btn-warning" onclick="cancelOrder()">직접 취소</button>
        `;
    }
    // 이미 CANCELED 인 경우에는 버튼을 노출하지 않음
}

// ==========================================================================
// 💡 [추가] API 연동 처리 함수들
// ==========================================================================

// 1. 취소 승인
async function approveCancel() {
    if (!confirm("해당 주문의 취소 요청을 승인하시겠습니까?")) return;

    try {
        const response = await fetch(`/api/admin/orders/${currentOrderId}/cancel-approve`, {
            method: "PATCH"
        });
        if (!response.ok) throw new Error("취소 승인 처리에 실패했습니다.");

        alert("취소 요청이 승인되었습니다.");
        fetchOrderDetail(currentOrderId); // 상세 정보 재조회
    } catch (error) {
        console.error("Approve Cancel Error:", error);
        alert(error.message);
    }
}

// 2. 취소 거절
async function rejectCancel() {
    if (!confirm("해당 주문의 취소 요청을 거절하시겠습니까?")) return;

    try {
        const response = await fetch(`/api/admin/orders/${currentOrderId}/cancel-reject`, {
            method: "PATCH"
        });
        if (!response.ok) throw new Error("취소 거절 처리에 실패했습니다.");

        alert("취소 요청이 거절되었습니다.");
        fetchOrderDetail(currentOrderId);
    } catch (error) {
        console.error("Reject Cancel Error:", error);
        alert(error.message);
    }
}

// 3. 관리자 직접 취소
async function cancelOrder() {
    if (!confirm("관리자 권한으로 이 주문을 직접 취소하시겠습니까?")) return;

    try {
        const response = await fetch(`/api/admin/orders/${currentOrderId}/cancel`, {
            method: "PATCH"
        });
        if (!response.ok) throw new Error("주문 취소 처리에 실패했습니다.");

        alert("주문이 성공적으로 취소되었습니다.");
        fetchOrderDetail(currentOrderId);
    } catch (error) {
        console.error("Cancel Order Error:", error);
        alert(error.message);
    }
}

function getStatusBadgeClass(status) {
    switch (status) {
        case "CONFIRMED": return "available";
        case "ORDERED": return "ordered";
        case "CANCEL_REQUESTED": return "warning";
        case "CANCELED": return "soldout";
        default: return "";
    }
}

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