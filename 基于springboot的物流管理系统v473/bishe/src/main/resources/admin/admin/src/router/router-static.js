import { createRouter, createWebHashHistory } from 'vue-router'
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import register from '@/views/register'
import center from '@/views/center'
import pay from '@/views/pay'

    import users from '@/views/modules/users/list'
    import cangku from '@/views/modules/cangku/list'
    import chat from '@/views/modules/chat/list'
    import cheliang from '@/views/modules/cheliang/list'
    import gonggao from '@/views/modules/gonggao/list'
    import huowu from '@/views/modules/huowu/list'
    import jiankongyujing from '@/views/modules/jiankongyujing/list'
    import shangpin from '@/views/modules/shangpin/list'
    import shangpinChuruInout from '@/views/modules/shangpinChuruInout/list'
    import shangpinChuruInoutList from '@/views/modules/shangpinChuruInoutList/list'
    import shangpinCollection from '@/views/modules/shangpinCollection/list'
    import shangpinOrder from '@/views/modules/shangpinOrder/list'
    import siji from '@/views/modules/siji/list'
    import yonghu from '@/views/modules/yonghu/list'
    import yuangong from '@/views/modules/yuangong/list'
    import config from '@/views/modules/config/list'
    import dictionaryCangku from '@/views/modules/dictionaryCangku/list'
    import dictionaryCheliang from '@/views/modules/dictionaryCheliang/list'
    import dictionaryGonggao from '@/views/modules/dictionaryGonggao/list'
    import dictionaryHuowu from '@/views/modules/dictionaryHuowu/list'
    import dictionaryShangpin from '@/views/modules/dictionaryShangpin/list'
    import dictionaryShangpinChuruInout from '@/views/modules/dictionaryShangpinChuruInout/list'



const router = createRouter({
  // history: createWebHistory(import.meta.env.BASE_URL),
  history:createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '系统首页',
      redirect: '/index'
    },
    {
      path: '/index',
      name: '系统首页',
      component: Index,
      children: [
        {
          path:'/',
          name:'首页',
          component:Home
        },
        {
          path: '/updatePassword',
          name: '修改密码',
          component: UpdatePassword,
        },
        {
          path: '/center',
          name: '个人信息',
          component: center,
          meta: {icon:'', title:'个人信息'}
        },
        {
          path: '/pay',
          name: '支付',
          component: pay,
          meta: {icon:'', title:'支付'}
        },
        {
          path: '/users',
          name: '管理信息',
          component: users
        }
        ,{
          path: '/dictionaryCangku',
          name: '仓库类型',
          component: dictionaryCangku
        }
        ,{
          path: '/dictionaryCheliang',
          name: '车辆类型',
          component: dictionaryCheliang
        }
        ,{
          path: '/dictionaryGonggao',
          name: '公告类型',
          component: dictionaryGonggao
        }
        ,{
          path: '/dictionaryHuowu',
          name: '货物类型',
          component: dictionaryHuowu
        }

        ,{
          path: '/dictionaryShangpin',
          name: '商品类型',
          component: dictionaryShangpin
        }
        ,{
          path: '/dictionaryShangpinChuruInout',
          name: '出入库类型',
          component: dictionaryShangpinChuruInout
        }
        ,{
          path: '/config',
          name: '轮播图',
          component: config
        }
        ,{
          path: '/cangku',
          name: '仓库',
          component: cangku
        }
        ,{
          path: '/chat',
          name: '反馈信息',
          component: chat
        }
        ,{
          path: '/cheliang',
          name: '车辆',
          component: cheliang
        }
        ,{
          path: '/gonggao',
          name: '公告',
          component: gonggao
        }
        ,{
          path: '/huowu',
          name: '货物',
          component: huowu
        }
        ,{
          path: '/jiankongyujing',
          name: '监控预警',
          component: jiankongyujing
        }
        ,{
          path: '/shangpin',
          name: '商品',
          component: shangpin
        }
        ,{
          path: '/shangpinChuruInout',
          name: '出入库',
          component: shangpinChuruInout
        }
        ,{
          path: '/shangpinChuruInoutList',
          name: '出入库详情',
          component: shangpinChuruInoutList
        }
        ,{
          path: '/shangpinCollection',
          name: '商品收藏',
          component: shangpinCollection
        }
        ,{
          path: '/shangpinOrder',
          name: '商品订单',
          component: shangpinOrder
        }
        ,{
          path: '/siji',
          name: '司机',
          component: siji
        }
        ,{
          path: '/yonghu',
          name: '用户',
          component: yonghu
        }
        ,{
          path: '/yuangong',
          name: '员工',
          component: yuangong
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/register',
      name: 'register',
      component: register,
      meta: {icon:'', title:'注册'}
    },
    /*默认跳转路由*/
    {
      path: '/:pathMatch(.*)*',
      component: NotFound
    }
  ],

})

export default router
