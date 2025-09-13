import request from "@/utils/request.js"

// 创建支付订单（占位，后端对接后可直接使用）
export function createPayment(data){
    // data: { channel: 'alipay' | 'wechat', amount: number }
    return request({
        url: "/api/pay/create",
        method: "post",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}


