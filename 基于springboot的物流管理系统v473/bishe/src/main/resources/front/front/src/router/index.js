import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/cangku',
    name: 'Warehouse',
    component: () => import('@/views/Warehouse.vue'),
    meta: { title: '仓库' }
  },
  {
    path: '/cangku/:id',
    name: 'WarehouseDetail',
    component: () => import('@/views/WarehouseDetail.vue'),
    meta: { title: '仓库详情' }
  },
  {
    path: '/gonggao',
    name: 'Notice',
    component: () => import('@/views/Notice.vue'),
    meta: { title: '公告' }
  },
  {
    path: '/gonggao/:id',
    name: 'NoticeDetail',
    component: () => import('@/views/NoticeDetail.vue'),
    meta: { title: '公告详情' }
  },
  {
    path: '/shangpin',
    name: 'Product',
    component: () => import('@/views/Product.vue'),
    meta: { title: '商品' }
  },
  {
    path: '/shangpin/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/User.vue'),
    meta: { title: '个人中心', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'UserProfile',
        component: () => import('../components/user/UserProfile.vue'),
        meta: { title: '基本资料', requiresAuth: true }
      },
      {
        path: 'address',
        name: 'AddressManage',
        component: () => import('../components/user/AddressManage.vue'),
        meta: { title: '收货地址', requiresAuth: true }
      },
      {
        path: 'huowu',
        name: 'GoodsManage',
        component: () => import('../components/user/GoodsManage.vue'),
        meta: { title: '货物管理', requiresAuth: true }
      },
      {
        path: 'collection',
        name: 'FavoriteManage',
        component: () => import('../components/user/FavoriteManage.vue'),
        meta: { title: '商品收藏', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'OrderManage',
        component: () => import('../components/user/OrderManage.vue'),
        meta: { title: '商品订单', requiresAuth: true }
      },
      {
        path: 'alerts',
        name: 'AlertManage',
        component: () => import('../components/user/AlertManage.vue'),
        meta: { title: '预警消息', requiresAuth: true }
      }
    ]
  },
  {
    path: '/order/confirm',
    name: 'OrderConfirm',
    component: () => import('@/views/OrderConfirm.vue'),
    meta: { title: '确认订单', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('Token')

  if (to.meta.requiresAuth && !token) {
    window.dispatchEvent(new CustomEvent('showLoginModal', {
      detail: { message: '请先登录' }
    }))
    next(false)
  } else {
    document.title = to.meta.title ? `${to.meta.title} - 生鲜冷链物流监控与预警系统` : '生鲜冷链物流监控与预警系统'
    next()
  }
})

export default router
