document.addEventListener("DOMContentLoaded", () => {
    fetchProducts();
});

const API_BASE_URL = "/api/products";
const ADMIN_API_BASE_URL = "/api/admin/products";
let productList = [];

// ==========================================================================
// 1. 상품 목록 조회 (GET)
// ==========================================================================
async function fetchProducts(endpoint = API_BASE_URL) {
    try {
        const response = await fetch(endpoint, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            },
            credentials: "include"
        });
        if (!response.ok) {
            throw new Error("상품 목록을 불러오는 데 실패했습니다.");
        }
        productList = await response.json();
        renderProductTable(productList);
    } catch (error) {
        console.error("Fetch Products Error:", error);
        alert(error.message);
    }
}

function renderProductTable(products) {
    const tbody = document.getElementById("productTableBody");
    tbody.innerHTML = "";

    if (!products || products.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">조회된 상품이 없습니다.</td></tr>`;
        return;
    }

    products.forEach(product => {
        const tr = document.createElement("tr");
        const formattedPrice = product.price ? product.price.toLocaleString() + "원" : "0원";
        const stockCount = product.stock !== undefined && product.stock !== null ? product.stock.toLocaleString() + "개" : "0개";

        tr.innerHTML = `
            <td>${product.id}</td>
            <td>
                <img src="${product.filePath || 'https://placehold.co/50x50?text=No+Img'}" 
                     alt="${product.name}" 
                     class="product-img-thumb" 
                     onerror="this.onerror=null; this.src='https://placehold.co/50x50?text=No+Img';">
            </td>
            <td><strong>${product.name}</strong></td>
            <td><span class="badge">${product.category}</span></td>
            <td>${formattedPrice}</td>
            <td>${stockCount}</td> <!-- 👈 수량 표시 추가 -->
            <td>
                <button class="btn-sm btn-outline" onclick="openEditModal(${product.id})">수정</button>
                <button class="btn-sm btn-danger" onclick="deleteProduct(${product.id})">삭제</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// ==========================================================================
// 2. 카테고리 검색 (GET by Category)
// ==========================================================================
function searchProducts(event) {
    event.preventDefault();
    const category = document.getElementById("categorySelect").value.trim();

    if (!category) {
        fetchProducts();
        return;
    }

    const endpoint = `${API_BASE_URL}/category?category=${encodeURIComponent(category)}`;
    fetchProducts(endpoint);
}

function resetFilters() {
    document.getElementById("searchForm").reset();
    fetchProducts();
}

// ==========================================================================
// 3. 모달 제어 및 이미지 미리보기
// ==========================================================================
function openCreateModal() {
    document.getElementById("productForm").reset();
    document.getElementById("productId").value = "";
    document.getElementById("productStock").value = ""; // 👈 수량 리셋
    document.getElementById("modalTitle").innerText = "☕ 새 상품 등록";
    document.getElementById("btnSave").innerText = "등록하기";

    // 신규 등록 시 파일 선택 필수
    document.getElementById("imageRequiredMark").style.display = "inline";
    document.getElementById("productImage").required = true;

    document.getElementById("imagePreviewContainer").style.display = "none";
    document.getElementById("imagePreview").src = "";
    document.getElementById("productModal").style.display = "flex";
}

function openEditModal(productId) {
    const product = productList.find(p => p.id === productId);
    if (!product) return;

    document.getElementById("productId").value = product.id;
    document.getElementById("productName").value = product.name;
    document.getElementById("productCategory").value = product.category;
    document.getElementById("productPrice").value = product.price;
    document.getElementById("productStock").value = product.stock ?? 0; // 👈 기존 수량 입력값에 바인딩

    // 수정 시 파일 선택 선택사항 처리
    document.getElementById("imageRequiredMark").style.display = "none";
    document.getElementById("productImage").required = false;

    // 기존 이미지 미리보기
    if (product.filePath) {
        document.getElementById("imagePreview").src = product.filePath;
        document.getElementById("imagePreviewContainer").style.display = "flex";
    } else {
        document.getElementById("imagePreviewContainer").style.display = "none";
    }

    document.getElementById("modalTitle").innerText = "☕ 상품 수정";
    document.getElementById("btnSave").innerText = "수정하기";
    document.getElementById("productModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("productModal").style.display = "none";
}

function previewImage(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById("imagePreview").src = e.target.result;
            document.getElementById("imagePreviewContainer").style.display = "flex";
        };
        reader.readAsDataURL(file);
    }
}

// ==========================================================================
// 4. 상품 등록 및 수정 (POST / PUT via Multipart FormData)
// ==========================================================================
async function handleFormSubmit(event) {
    event.preventDefault();

    const productId = document.getElementById("productId").value;
    const isEdit = Boolean(productId);

    // Multipart/form-data 전송용 FormData 객체
    const formData = new FormData();
    formData.append("name", document.getElementById("productName").value.trim());
    formData.append("category", document.getElementById("productCategory").value.trim());
    formData.append("price", document.getElementById("productPrice").value);
    formData.append("stock", document.getElementById("productStock").value); // 👈 stock 데이터 추가

    const imageInput = document.getElementById("productImage");
    const imageFile = imageInput.files[0];

    if (imageFile) {
        formData.append("image", imageFile);
    } else if (isEdit) {
        const existingFilePathInput = document.getElementById("existingFilePath");
        const existingFilePath = existingFilePathInput ? existingFilePathInput.value : null;

        if (existingFilePath) {
            formData.append("filePath", existingFilePath);
        }
    }

    const url = isEdit ? `${ADMIN_API_BASE_URL}/${productId}` : ADMIN_API_BASE_URL;
    const method = isEdit ? "PUT" : "POST";

    try {
        const response = await fetch(url, {
            method: method,
            body: formData
        });

        if (!response.ok) {
            const errorMsg = isEdit ? "상품 수정에 실패했습니다." : "상품 등록에 실패했습니다.";
            throw new Error(errorMsg);
        }

        alert(isEdit ? "상품 정보가 수정되었습니다." : "새 상품이 등록되었습니다.");
        closeModal();
        fetchProducts();
    } catch (error) {
        console.error("Save Product Error:", error);
        alert(error.message);
    }
}

// ==========================================================================
// 5. 상품 논리 삭제 (DELETE)
// ==========================================================================
async function deleteProduct(productId) {
    if (!confirm("정말 이 상품을 삭제하시겠습니까?")) return;

    try {
        const response = await fetch(`${ADMIN_API_BASE_URL}/${productId}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("상품 삭제 처리에 실패했습니다.");
        }

        alert("상품이 삭제 처리되었습니다.");
        fetchProducts();
    } catch (error) {
        console.error("Delete Product Error:", error);
        alert(error.message);
    }
}

// ==========================================================================
// 6. 로그아웃
// ==========================================================================
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