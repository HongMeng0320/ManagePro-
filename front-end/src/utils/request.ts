import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';
import { ApiResponse } from '@/types';

// 创建axios实例
const request = axios.create({
  baseURL: '/', // 修改为根路径
  timeout: 10000  // 请求超时时间
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 获取token
    const token = localStorage.getItem('token');
    // 如果有token则添加到header中
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    const res = response.data;
    
    // 如果返回成功
    if (res.code === 200) {
      return res.data;
    } else {
      // 显示错误信息
      ElMessage.error(res.message || '请求发生错误');
      return Promise.reject(new Error(res.message || '请求发生错误'));
    }
  },
  (error) => {
    // 处理HTTP错误
    if (error.response) {
      const status = error.response.status;
      
      if (status === 401) {
        // 检查是否是登录请求
        const isLoginRequest = error.config.url.includes('/login');
        
        // 如果不是登录请求才显示过期消息
        if (!isLoginRequest && !window.hasShownLoginExpired) {
          ElMessage.error('登录已过期，请重新登录');
          window.hasShownLoginExpired = true;
          
          // 设置一个较短的定时器清除标志，让用户可以快速再次尝试登录
          setTimeout(() => {
            window.hasShownLoginExpired = false;
          }, 1500);
        }
        
        // 清除本地存储中的令牌和用户信息
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        
        // 如果不是在登录页，则跳转到登录页
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login');
        }
      } else if (status === 403) {
        ElMessage.error('权限不足，无法访问');
      } else {
        ElMessage.error(error.response.data?.message || '网络请求失败');
      }
    } else {
      ElMessage.error('请求发生错误，请检查网络连接');
    }
    return Promise.reject(error);
  }
);

// 声明全局变量，用于防止重复显示登录过期提示
declare global {
  interface Window {
    hasShownLoginExpired?: boolean;
  }
}

// 封装GET请求
export function get<T>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.get(url, { params, ...config });
}

// 封装POST请求
export function post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.post(url, data, config);
}

// 封装PUT请求
export function put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.put(url, data, config);
}

// 封装DELETE请求
export function del<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return request.delete(url, config);
}

// 封装PATCH请求
export function patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return request.patch(url, data, config);
}

export default request; 