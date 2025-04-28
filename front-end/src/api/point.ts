import { get, post } from '@/utils/request';
import { Point, PageResult } from '@/types';

/**
 * 查询积分明细
 */
export function getPointList(params: {
  userId?: number;
  pointType?: string;
  startTime?: string;
  endTime?: string;
  pageNum: number;
  pageSize: number;
}): Promise<PageResult<Point>> {
  return get<PageResult<Point>>('/api/points', params);
}

/**
 * 获取用户总积分
 */
export function getUserTotalPoints(userId?: number): Promise<number> {
  return get<number>('/api/points/total', { userId });
}

/**
 * 添加积分记录 (管理员)
 */
export function addPoint(point: Partial<Point>): Promise<void> {
  return post<void>('/api/points', point);
} 