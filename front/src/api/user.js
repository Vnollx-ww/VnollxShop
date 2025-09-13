import request from "@/utils/request.js"

// 用户登录（白名单，无需token）
export function login(data){
    return request({
        url: "/api/user/login",
        method: "post",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}

// 用户注册（白名单，无需token）
export function register(data){
    return request({
        url: "/api/user/register",
        method: "post",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}

// 获取用户信息（按ID）
export function getUserInfoById(uid){
    return request({
        url: "/api/user/info/id",
        method: "get",
        params: { uid }
    })
}

// 通过邮箱获取用户信息
export function getUserInfoByEmail(email){
    return request({
        url: "/api/user/info/email",
        method: "get",
        params: { email }
    })
}

// 基于网关注入的 Token 获取当前用户信息（不传 uid）
export function getMyInfo(){
    return request({
        url: "/api/user/info/id",
        method: "get"
    })
}

// 更新用户资料（需要token）
export function updateUserInfo(data){
    return request({
        url: "/api/user/update/info",
        method: "put",
        data: data
    })
}

// 修改密码（需要token）
export function updatePassword(data){
    return request({
        url: "/api/user/update/password",
        method: "put",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}

// 获取余额（需要token）
export function getBalance(uid){
    return request({
        url: "/api/user/get-balance",
        method: "get",
        params: { uid }
    })
}

// 更新余额（需要token）
export function updateBalance(data){
    return request({
        url: "/api/user/update/balance",
        method: "put",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}
export function recharge(data){
    return request({
        url: "/api/user/recharge",
        method: "put",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}
// 退出登录（需要token）
export function logout(data){
    return request({
        url: "/api/user/logout",
        method: "post",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        params: data
    })
}