// API响应类型
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 用户类型
export interface User {
  userId: number;
  username: string;
  email?: string;
  phone?: string;
  role: string;
  status: number;
  createdAt: string;
  updatedAt: string;
}

// 登录请求参数
export interface LoginParams {
  username: string;
  password: string;
}

// 登录响应数据
export interface LoginResult {
  token: string;
  user: User;
}

// 积分记录类型
export interface Point {
  pointId: number;
  userId: number;
  pointType: string;
  points: number;
  recordedAt: string;
}

// 分页参数
export interface PageParams {
  pageNum: number;
  pageSize: number;
}

// 分页结果
export interface PageResult<T> {
  total: number;
  list: T[];
}

// 用户详情带积分
export interface UserDetail {
  user: User;
  totalPoints: number;
} 