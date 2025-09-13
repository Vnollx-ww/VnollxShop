import request from "@/utils/request.js"

// 购物车列表（需要token，由网关注入 X-User-Id）
export function getCartList(){
    return request({
        url: "/api/card/list",
        method: "get"
    })
}

// 购物车数量（需要token）：后端路由 /api/card/count
export function getCartCount(){
    return request({
        url: "/api/card/count",
        method: "get"
    })
}

// 加入购物车（需要token）
export function addCartItem(data){
    // data: { pid, productName, cover, unitPrice }
    return request({
        url: "/api/card/add",
        method: "post",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}

// 更新购物车数量（需要token）
export function updateCartNumber(data){
    // data: { ciid, number }
    return request({
        url: "/api/card/update",
        method: "put",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}

// 删除购物车项（需要token）
export function deleteCartItems(data){
    // data: { idList: [string], keyword?: string }
    const form = new URLSearchParams()
    if (data && Array.isArray(data.idList)){
        data.idList.forEach(v=> form.append('idList', String(v)))
    }
    if (data && data.keyword){
        form.append('keyword', data.keyword)
    }
    return request({
        url: "/api/card/delete",
        method: "delete",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        data: form
    })
}


