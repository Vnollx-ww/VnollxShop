import axios from 'axios'
import { Message } from 'element-ui'

// create an axios instance
const service = axios.create({
    baseURL: "http://localhost:8080", // url = base url + request url
    timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
    config => {
        // 自动附加本地存储中的 Bearer Token
        try {
            const token = localStorage.getItem('token')
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`
            }
        } catch (e) {
            // ignore storage errors
        }
        return config
    },
    error => {
        console.log(error)
        return Promise.reject(error)
    }
)

// response interceptor
service.interceptors.response.use(
    /**
     * If you want to get http information such as headers or status
     * Please return  response => response
     */

    /**
     * Determine the request status by custom code
     * Here is just an example
     * You can also judge the status by HTTP Status Code
     */
    response => {
        const res = response.data
        // 统一Result格式：{ code, message/msg, data }
        const code = res && typeof res === 'object' && 'code' in res ? res.code : 200
        if (code !== 200) {
            // 直接返回后端的错误信息，不在拦截器中显示
            const errorMsg = res.message || res.msg || '请求错误'
            
            // 只处理401未授权情况
            if (code === 401) {
                // 未授权，清除本地token并跳转登录
                localStorage.removeItem('token');
                // 避免循环依赖，直接使用 location
                if (location.hash !== '#/login') {
                    location.hash = '#/login'
                }
                // 显示未授权消息
                Message({
                    message: '请先登录',
                    type: 'error',
                    duration: 5 * 1000
                })
            }
            
            // 创建包含原始错误信息的Error对象
            return Promise.reject(new Error(errorMsg))
        }
        return res
    },
    error => {
        console.log('err' + error) // for debug
        // 网络错误等情况下，在这里显示错误信息
        // 业务错误（后端返回的错误）由调用方处理
        if (error.response && error.response.status) {
            // 网络请求错误
            Message({
                message: '网络请求失败，请检查网络连接',
                type: 'error',
                duration: 5 * 1000
            })
        }
        return Promise.reject(error)
    }
)

export default service