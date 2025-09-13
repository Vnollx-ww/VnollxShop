import Vue from "vue";
import VueRouter from "vue-router";

import Layout from "@/components/Layout";

Vue.use(VueRouter);

const router = new VueRouter({
    mode: 'hash', // 使用 hash 模式
    routes: [
        {
            path: '/',
            name: '主页',
            component: () => import('@/views/ProductList'),
        },
        {
            path: '/homepage',
            name: '主页',
            component: () => import('@/views/ProductList'),
        },
        {
            path: '/login',
            name: '系统登录',
            component: () => import('@/views/Login'),
        },
        {
            path: '/product/:id',
            name: '商品详情',
            component: () => import('@/views/ProductDetail'),
        },
        {
            path: '/cart',
            name: '购物车',
            component: () => import('@/views/Card.vue'),
        },
        {
            path: '/orders',
            name: '订单列表',
            component: () => import('@/views/Orders'),
        },
        {
            path: '/profile',
            name: '个人中心',
            component: () => import('@/views/Profile'),
        },
        {
            path: '/flash-sale',
            name: '秒杀专区',
            component: () => import('@/views/FlashSale'),
        },
        //嵌套路由
        {
            path: '/layout',
            name: '系统管理',
            component: Layout,
            redirect:'/system/user',
            children:[

            ]
        },
    ]
});
export default router;