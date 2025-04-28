import { get, post, put, del, patch } from '@/utils/request';
import { User, PageResult } from '@/types';

/**
 * 用户登录
 */
export function login(data: { username: string; password: string }): Promise<{ user: User; token: string }> {
  return post<{ user: User; token: string }>('/api/login', data);
}

/**
 * 获取当前用户信息
 */
export function getUserInfo(): Promise<User> {
  return get<User>('/api/user/info');
}

/**
 * 获取用户列表
 */
export function getUserList(params: {
  pageNum: number;
  pageSize: number;
  username?: string;
  role?: string;
}): Promise<PageResult<User>> {
  return get<PageResult<User>>('/api/admin/users', { params });
}

/**
 * 获取用户详情
 */
export function getUserDetail(userId: number): Promise<User> {
  return get<User>(`/api/admin/users/${userId}`);
}

/**
 * 更新用户信息
 */
export function updateUser(userId: number, data: Partial<User>): Promise<void> {
  return put<void>(`/api/admin/users/${userId}`, data);
}

/**
 * 更新用户状态
 */
export function updateUserStatus(userId: number, status: number): Promise<void> {
  return patch<void>(`/api/admin/users/${userId}/status`, { status });
}

/**
 * 删除用户
 */
export function deleteUser(userId: number): Promise<void> {
  return del<void>(`/api/admin/users/${userId}`);
}

/**
 * 添加用户
 */
export function addUser(data: Partial<User>): Promise<void> {
  return post<void>('/api/admin/users', data);
} 