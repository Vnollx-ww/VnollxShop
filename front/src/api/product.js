import request from "@/utils/request.js"

// 商品列表（可选idList）
export function getProductList(idList,selectedCategory,searchKeyword){
    // 初始化参数对象
    const params = {};
    // 添加idList（如果存在且是有效数组）
    if (Array.isArray(idList) && idList.length) {
        params.idList = idList;
    }
    // 添加分类筛选参数（如果有值）
    if (selectedCategory) {
        params.category = selectedCategory;
    }
    // 添加搜索关键词参数（如果有值）
    if (searchKeyword) {
        params.keyword = searchKeyword;
    }
    return request({
        url: "/api/product/list",
        method: "get",
        params
    })
}
export function getCategoryList(){
    return request({
        url: "/api/product/list/category",
        method: "get",
    })
}
// 商品详情（需要token，因为服务端读取X-User-Id判断点赞状态）
export function getProductInfo(pid){
    return request({
        url: "/api/product/info",
        method: "get",
        params: { pid }
    })
}

// 点赞
export function addLike(pid){
    return request({
        url: "/api/product/like/add",
        method: "post",
        params: { pid }
    })
}

// 取消点赞
export function cancelLike(pid){
    return request({
        url: "/api/product/like/cancel",
        method: "post",
        params: { pid }
    })
}




