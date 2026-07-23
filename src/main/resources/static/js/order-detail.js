// 현재 페이지의 orderId를 저장할 전역 변수
let currentOrderId = null;

document.addEventListener("DOMContentLoaded", () => {
    // URL 경로에서 orderId 추출 (예: /orders/detail/12 -> 12)
    const pathSegments = window.location.pathname.split('/');
    const orderIdFromUrl = pathSegments[pathSegments.length - 1];

    if (orderIdFromUrl && !isNaN(orderIdFromUrl)) {
        currentOrderId = Number(orderIdFromUrl);
        fetchOrderDetail(currentOrderId);
    } else {
        alert("올바르지 않은 접근입니다.");
        location.href = "/orders";
    }
});

// API: 주문 상세 정보 조회
async function fetchOrderDetail(orderId) {
    try {
        const response = await fetch(`/api/orders/${orderId}`);
        if (!response.ok) throw new Error("주문 상세 정보를 불러올 수 없습니다.");

        const order = await response.json();
        renderOrderDetail(order);
    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// 상세 정보 렌더링
function renderOrderDetail(order) {
    currentOrderId = order.orderId; // ★ 핵심: 현재 조회 중인 orderId 바인딩

    document.getElementById("detailOrderId").innerText = order.orderId;
    document.getElementById("detailOrderCode").innerText = order.orderCode;
    document.getElementById("detailEmail").innerText = order.email;
    document.getElementById("detailStatus").innerHTML = getStatusBadge(order.status);
    document.getElementById("detailPostalCode").innerText = order.postalCode || "-";
    document.getElementById("detailAddress").innerText = order.address || "-";
    document.getElementById("detailOrderedAt").innerText = order.orderedAt ? new Date(order.orderedAt).toLocaleString() : "-";

    renderOrderItems(order.items, order.totalPrice);

    // 취소 버튼 제어
    const btnCancel = document.getElementById("btnCancel");

    // onclick 이벤트에 매개변수로 order.orderId 직접 바인딩도 가능
    btnCancel.setAttribute("onclick", `requestCancelOrder(${order.orderId})`);

    if (order.status === "CANCELED" || order.status === "CONFIRMED") {
        btnCancel.style.display = "none"; // 이미 확정/취소된 경우 숨김
    } else if (order.status === "CANCEL_REQUESTED") {
        btnCancel.style.display = "inline-block";
        btnCancel.disabled = true; // 이미 취소 요청한 상태면 버튼 비활성화
        btnCancel.innerText = "취소 요청 처리 중";
    } else {
        btnCancel.style.display = "inline-block";
        btnCancel.disabled = false;
        btnCancel.innerText = "주문 취소 요청";
    }
}

// 주문 상품 목록 렌더링
function renderOrderItems(items, totalPrice) {
    const tbody = document.getElementById("orderItemsBody");
    tbody.innerHTML = "";

    if (!items || items.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;">주문한 상품이 없습니다.</td></tr>`;
    } else {
        items.forEach(item => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${item.productName}</td>
                <td>${item.price.toLocaleString()}원</td>
                <td>${item.amount}개</td>
                <td>${item.subtotal.toLocaleString()}원</td>
            `;
            tbody.appendChild(tr);
        });
    }

    const total = totalPrice ?? (items || []).reduce((sum, item) => sum + item.subtotal, 0);
    document.getElementById("detailTotalPrice").innerText = `${total.toLocaleString()}원`;
}

// API: 주문 취소 요청
async function requestCancelOrder(orderId) {
    // 넘어온 orderId가 없으면 전역변수 참조
    const targetOrderId = orderId || currentOrderId;

    // ★ 유효성 검사: targetOrderId가 null/undefined/nan이면 요청 차단
    if (!targetOrderId) {
        alert("주문 번호를 찾을 수 없습니다.");
        return;
    }

    if (!confirm("정말로 주문 취소를 요청하시겠습니까?")) return;

    try {
        const response = await fetch(`/api/orders/${targetOrderId}/cancel`, {
            method: "PATCH"
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.message || "주문 취소 요청에 실패했습니다.");
        }

        alert("주문 취소 요청이 접수되었습니다.");
        fetchOrderDetail(targetOrderId); // 상태 업데이트를 위해 다시 조회
    } catch (error) {
        alert(error.message);
    }
}

// 상태 뱃지 생성 함수
function getStatusBadge(status) {
    switch (status) {
        case "ORDERED":
            return `<span class="badge badge-ordered">미확정</span>`;
        case "CONFIRMED":
            return `<span class="badge badge-confirmed">주문 확정</span>`;
        case "CANCEL_REQUESTED":
            return `<span class="badge badge-cancel-requested">취소 요청됨</span>`;
        case "CANCELED":
            return `<span class="badge badge-canceled">취소 완료</span>`;
        default:
            return `<span class="badge">${status}</span>`;
    }
}