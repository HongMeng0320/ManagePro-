import { defineStore } from 'pinia';
import { ref } from 'vue';
import { login as userLogin, getUserInfo } from '@/api/user';
import { User, LoginParams } from '@/types';
import router from '@/router';
import { ElMessage } from 'element-plus';

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '');
  const userInfo = ref<User | null>(null);
  const loading = ref<boolean>(false);
  const loginInProgress = ref<boolean>(false);

  // 登录
  async function login(loginForm: LoginParams) {
    // 防止重复提交登录请求
    if (loginInProgress.value) {
      return;
    }
    
    try {
      loginInProgress.value = true;
      loading.value = true;
      
      // 清除过期标记，确保登录请求能够正常进行
      window.hasShownLoginExpired = false;
      
      console.log('开始登录请求:', loginForm);
      const res = await userLogin(loginForm);
      console.log('登录成功，返回数据:', res);
      
      token.value = res.token;
      userInfo.value = res.user;
      
      // 存储用户信息和token
      localStorage.setItem('token', res.token);
      localStorage.setItem('userInfo', JSON.stringify(res.user));
      
      ElMessage.success('登录成功');
      
      // 延迟跳转，确保数据都已更新
      setTimeout(() => {
        console.log('准备跳转到首页');
        router.push('/');
      }, 300);
    } catch (error: any) {
      console.error('登录失败:', error);
      
      // 显示更详细的错误信息
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
        ElMessage.error(error.response.data?.message || '登录失败，请检查用户名和密码');
      } else if (error.request) {
        console.error('请求未收到响应:', error.request);
        ElMessage.error('服务器未响应，请检查网络连接');
      } else {
        console.error('请求配置错误:', error.message);
        ElMessage.error('登录请求发送失败');
      }
      
      // 登录失败，清除可能已存储的令牌和用户信息
      token.value = '';
      userInfo.value = null;
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    } finally {
      loading.value = false;
      // 延迟重置登录状态，防止快速重复点击
      setTimeout(() => {
        loginInProgress.value = false;
      }, 1000);
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return;
    
    try {
      loading.value = true;
      const res = await getUserInfo();
      userInfo.value = res;
      
      // 更新localStorage中的用户信息
      localStorage.setItem('userInfo', JSON.stringify(res));
      return res; // 返回结果方便调用者使用
    } catch (error) {
      console.error('获取用户信息失败:', error);
      throw error; // 抛出错误方便调用者捕获
    } finally {
      loading.value = false;
    }
  }

  // 登出
  function logout() {
    token.value = '';
    userInfo.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    
    // 清除可能存在的过期提示标记
    window.hasShownLoginExpired = false;
    
    router.push('/login');
    ElMessage.success('已退出登录');
  }

  // 是否是管理员
  function isAdmin() {
    return userInfo.value?.role === 'admin';
  }

  // 初始化 - 从localStorage加载用户信息
  function initUserInfo() {
    const storedUserInfo = localStorage.getItem('userInfo');
    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo);
      } catch (error) {
        console.error('解析用户信息失败:', error);
        localStorage.removeItem('userInfo');
      }
    }
  }
  
  // 初始化用户信息
  initUserInfo();

  return {
    token,
    userInfo,
    loading,
    login,
    fetchUserInfo,
    logout,
    isAdmin
  };
}); 