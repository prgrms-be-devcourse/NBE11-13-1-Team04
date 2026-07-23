// 폼 제출 핸들러
async function searchOrders(event) {
    event.preventDefault();

    const emailInput = document.getElementById("searchEmail");
    const email = emailInput.value.trim();

    if (!email) {
        alert("이메일을 입력해주세요.");
        return;
    }

    try {
        // GET /api/orders/mine?email=user@example.com
        const response = await fetch(`/api/orders/mine?email=${encodeURIComponent(email)}`);

        if (!response.ok) {
            throw new Error("주문 내역을 불러올 수 없습니다.");
        }

        const orders = await response.json();
        renderOrderList(orders);
    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

function renderOrderList(orders) {
    const tbody = document.getElementById("orderListBody");
    tbody.innerHTML = "";

    if (!orders || orders.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">해당 이메일의 주문 내역이 존재하지 않습니다.</td></tr>`;
        return;
    }

    orders.forEach(order => {
        const tr = document.createElement("tr");
        const formattedDate = order.orderedAt ? new Date(order.orderedAt).toLocaleString() : "-";

        tr.innerHTML = `
            <td>${order.orderId}</td>
            <td>${order.email}</td>
            <td>${order.address || "-"}</td>
            <td>${getStatusBadge(order.status)}</td>
            <td>${formattedDate}</td>
            <td>
                <button class="btn-outline btn-sm" onclick="location.href='/orders/${order.orderId}'">상세보기</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function getStatusBadge(status) {
    switch (status) {
        case "ORDERED":
            return `<span class="badge badge-ordered">미확정</span>`;
        case "CONFIRMED":
            return `<span class="badge badge-confirmed">주문 확정</span>`;
        case "CANCEL_REQUESTED":
            return `<span class="badge badge-cancel-requested">취소 요청</span>`;
        case "CANCELED":
            return `<span class="badge badge-canceled">취소 완료</span>`;
        default:
            return `<span class="badge">${status}</span>`;
    }
}