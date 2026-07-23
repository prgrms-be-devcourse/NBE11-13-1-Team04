let dailyChartInstance = null;
let monthlyChartInstance = null;
let topProductChartInstance = null;

document.addEventListener("DOMContentLoaded", () => {
    initDateFilter();
    fetchSalesStats();
});

// 1. 연도 및 월 선택 셀렉트박스 옵션 생성
function initDateFilter() {
    const yearSelect = document.getElementById("yearSelect");
    const monthSelect = document.getElementById("monthSelect");

    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth() + 1;

    // 최근 4개년 세팅
    for (let y = currentYear; y >= currentYear - 3; y--) {
        const option = document.createElement("option");
        option.value = y;
        option.textContent = `${y}년`;
        yearSelect.appendChild(option);
    }

    // 1~12월 세팅
    for (let m = 1; m <= 12; m++) {
        const option = document.createElement("option");
        option.value = m;
        option.textContent = `${m}월`;
        if (m === currentMonth) option.selected = true;
        monthSelect.appendChild(option);
    }
}

// 2. 조회 버튼 클릭
function handleSalesSearch(event) {
    event.preventDefault();
    fetchSalesStats();
}

// 3. 통계 데이터 Fetch 및 렌더링
async function fetchSalesStats() {
    const year = document.getElementById("yearSelect").value;
    const month = document.getElementById("monthSelect").value;

    try {
        const response = await fetch(`/api/admin/sales/stats?year=${year}&month=${month}`);
        if (!response.ok) {
            throw new Error("통계 데이터를 불러오는 데 실패했습니다.");
        }
        const data = await response.json();

        renderSummary(data.summary);
        renderDailyChart(data.dailyStats);
        renderMonthlyChart(data.monthlyStats);
        renderTopProductChart(data.topProducts);
    } catch (error) {
        console.error("Fetch Sales Stats Error:", error);
        alert(error.message);
    }
}

// 4. 요약 카드 렌더링
function renderSummary(summary) {
    if (!summary) return;
    document.getElementById("statMonthPrice").textContent = (summary.currentMonthTotalPrice || 0).toLocaleString() + " 원";
    document.getElementById("statMonthCount").textContent = (summary.currentMonthTotalCount || 0).toLocaleString() + " 개";
    document.getElementById("statTodayPrice").textContent = (summary.todayTotalPrice || 0).toLocaleString() + " 원";
    document.getElementById("statTodayCount").textContent = (summary.todayTotalCount || 0).toLocaleString() + " 개";
}

// 5. 일별 매출/수량 이중축 선 그래프 (Line Chart)
function renderDailyChart(dailyStats) {
    const ctx = document.getElementById("dailyChart").getContext("2d");

    const labels = dailyStats.map(item => item.period);
    const prices = dailyStats.map(item => item.totalPrice);
    const amounts = dailyStats.map(item => item.totalAmount);

    if (dailyChartInstance) dailyChartInstance.destroy();

    dailyChartInstance = new Chart(ctx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [
                {
                    label: "매출액 (원)",
                    data: prices,
                    borderColor: "#6f4e37",
                    backgroundColor: "rgba(111, 78, 55, 0.1)",
                    yAxisID: "yPrice",
                    tension: 0.2,
                    fill: true
                },
                {
                    label: "판매 수량 (개)",
                    data: amounts,
                    borderColor: "#2e7d32",
                    backgroundColor: "rgba(46, 125, 50, 0.1)",
                    yAxisID: "yAmount",
                    tension: 0.2,
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                yPrice: {
                    type: "linear",
                    position: "left",
                    title: { display: true, text: "매출액 (원)" },
                    ticks: { callback: value => value.toLocaleString() }
                },
                yAmount: {
                    type: "linear",
                    position: "right",
                    title: { display: true, text: "수량 (개)" },
                    grid: { drawOnChartArea: false }
                }
            }
        }
    });
}

// 6. 월별 매출 막대 그래프 (Bar Chart)
function renderMonthlyChart(monthlyStats) {
    const ctx = document.getElementById("monthlyChart").getContext("2d");

    const labels = monthlyStats.map(item => item.period);
    const prices = monthlyStats.map(item => item.totalPrice);

    if (monthlyChartInstance) monthlyChartInstance.destroy();

    monthlyChartInstance = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "월 매출액 (원)",
                data: prices,
                backgroundColor: "#5d4037",
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    ticks: { callback: value => value.toLocaleString() }
                }
            }
        }
    });
}

// 7. 인기 상품 TOP 5 도넛 차트 (Doughnut Chart)
function renderTopProductChart(topProducts) {
    const ctx = document.getElementById("topProductChart").getContext("2d");

    const labels = topProducts.map(item => item.productName);
    const amounts = topProducts.map(item => item.totalAmount);

    if (topProductChartInstance) topProductChartInstance.destroy();

    topProductChartInstance = new Chart(ctx, {
        type: "doughnut",
        data: {
            labels: labels,
            datasets: [{
                data: amounts,
                backgroundColor: [
                    "#6f4e37",
                    "#8d6e63",
                    "#a1887f",
                    "#d7ccc8",
                    "#efebe9"
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: "bottom" }
            }
        }
    });
}

// 8. 공통 로그아웃 함수
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