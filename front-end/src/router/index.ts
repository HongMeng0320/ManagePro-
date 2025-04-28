import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

// 定义路由
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/',
    component: () => import('@/layouts/default/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '首页',
          requiresAuth: true
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true
        }
      },
      {
        path: 'points',
        name: 'Points',
        component: () => import('@/views/points/index.vue'),
        meta: {
          title: '积分记录',
          requiresAuth: true
        }
      },
      // 管理员路由
      {
        path: 'user-management',
        name: 'UserManagement',
        component: () => import('@/views/admin/user-management/index.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      {
        path: 'point-management',
        name: 'PointManagement',
        component: () => import('@/views/admin/point-management/index.vue'),
        meta: {
          title: '积分管理',
          requiresAuth: true,
          requiresAdmin: true
        }
      }
    ]
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      requiresAuth: false
    }
  }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 系统管理` : '系统管理';
  
  console.log('路由跳转:', from.path, '->', to.path);
  
  // 获取token
  const token = localStorage.getItem('token');
  console.log('当前token状态:', token ? '已登录' : '未登录');
  
  // 判断页面是否需要登录权限
  if (to.meta.requiresAuth) {
    if (!token) {
      // 未登录，跳转到登录页
      console.log('需要认证但未登录，重定向到登录页');
      next('/login');
    } else {
      // 判断是否需要管理员权限
      if (to.meta.requiresAdmin) {
        // 获取用户信息，判断是否是管理员
        const userInfoStr = localStorage.getItem('userInfo');
        
        if (userInfoStr) {
          try {
            const userInfo = JSON.parse(userInfoStr);
            if (userInfo && userInfo.role === 'admin') {
              console.log('用户是管理员，允许访问');
              next();
            } else {
              // 非管理员，跳转到首页
              console.log('用户不是管理员，重定向到首页');
              next('/dashboard');
            }
          } catch (error) {
            console.error('解析用户信息失败:', error);
            next('/login');
          }
        } else {
          // 没有用户信息，可能是token过期，跳转到登录页
          console.log('无法获取用户信息，重定向到登录页');
          next('/login');
        }
      } else {
        console.log('已登录且不需要管理员权限，允许访问');
        next();
      }
    }
  } else {
    // 如果已登录且访问登录页，跳转到首页
    if (token && to.path === '/login') {
      console.log('已登录状态访问登录页，重定向到首页');
      next('/');
    } else {
      console.log('不需要认证，允许访问');
      next();
    }
  }
});

export default router; 