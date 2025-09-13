import request from "@/utils/request.js"

// 创建订单（需要token），通过表单体提交。
// 后端期望 CreateOrderDTO.items 为 List<OrderItemDTO>，需用 items[0].pid 这种索引字段提交。
export function createOrder(data){
    const form = new URLSearchParams()
    const payload = data || {}
    Object.keys(payload).forEach(key => {
        const value = payload[key]
        if (value === undefined || value === null) return
        if (key === 'items' && Array.isArray(value)){
            value.forEach((item, idx) => {
                if (!item) return
                if (item.pid !== undefined) form.append(`items[${idx}].pid`, String(item.pid))
                if (item.number !== undefined) form.append(`items[${idx}].number`, item.number)
                if (item.unitPrice !== undefined) form.append(`items[${idx}].unitPrice`, item.unitPrice)
                if (item.productName !== undefined) form.append(`items[${idx}].productName`, String(item.productName))
            })
        } else {
            form.append(key, String(value))
        }
    })
    return request({
        url: "/api/order/create",
        method: "post",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        data: form
    })
}

// 订单列表（需要token）
export function getOrderList(){
    return request({
        url: "/api/order/list",
        method: "get"
    })
}

// 删除订单（需要token）
export function deleteOrder(data){
    return request({
        url: "/api/order/delete",
        method: "delete",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}




