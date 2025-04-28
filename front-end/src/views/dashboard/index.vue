<template>
  <div class="dashboard-container">
    <!-- 欢迎信息 -->
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <span>欢迎使用系统管理平台</span>
        </div>
      </template>
      <div class="welcome-content">
        <h2>您好，{{ userStore.userInfo?.username }}</h2>
        <p>上次登录时间：{{ formatDate(userStore.userInfo?.updatedAt) }}</p>
        <p>您当前的积分：<span class="point-value">{{ totalPoints }}</span></p>
      </div>
    </el-card>

    <!-- 数据统计 -->
    <div class="statistics">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="statistic-card">
            <div class="statistic-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="statistic-info">
              <div class="statistic-title">用户角色</div>
              <div class="statistic-value">{{ userStore.userInfo?.role === 'admin' ? '管理员' : '普通用户' }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="statistic-card">
            <div class="statistic-icon">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="statistic-info">
              <div class="statistic-title">积分总数</div>
              <div class="statistic-value">{{ totalPoints }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="statistic-card">
            <div class="statistic-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="statistic-info">
              <div class="statistic-title">账号创建时间</div>
              <div class="statistic-value">{{ formatDate(userStore.userInfo?.createdAt) }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近积分记录 -->
    <el-card class="recent-points">
      <template #header>
        <div class="card-header">
          <span>最近积分记录</span>
          <el-button type="primary" size="small" @click="$router.push('/points')">查看更多</el-button>
        </div>
      </template>
      <el-table :data="recentPoints" stripe style="width: 100%">
        <el-table-column prop="pointType" label="积分类型">
          <template #default="scope">
            <el-tag :type="scope.row.pointType === 'reward' ? 'success' : 'danger'">
              {{ scope.row.pointType === 'reward' ? '奖励' : '消费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分值">
          <template #default="scope">
            <span :class="scope.row.points > 0 ? 'text-success' : 'text-danger'">
              {{ scope.row.points > 0 ? '+' + scope.row.points : scope.row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="recordedAt" label="记录时间">
          <template #default="scope">
            {{ formatDate(scope.row.recordedAt) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { User, Wallet, Timer } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { getPointList, getUserTotalPoints } from '@/api/point';
import type { Point } from '@/types';

const userStore = useUserStore();
const totalPoints = ref(0);
const recentPoints = ref<Point[]>([]);

// 获取用户总积分
const fetchTotalPoints = async () => {
  try {
    totalPoints.value = await getUserTotalPoints();
  } catch (error) {
    console.error('获取总积分失败:', error);
  }
};

// 获取最近积分记录
const fetchRecentPoints = async () => {
  try {
    const res = await getPointList({
      pageNum: 1,
      pageSize: 5
    });
    recentPoints.value = res.list;
  } catch (error) {
    console.error('获取积分记录失败:', error);
  }
};

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '暂无数据';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

onMounted(() => {
  fetchTotalPoints();
  fetchRecentPoints();
});
</script>

<style scoped>
.dashboard-container {
  padding: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  padding: 10px 0;
}

.welcome-content h2 {
  margin-bottom: 10px;
  color: #303133;
}

.point-value {
  font-weight: bold;
  color: #409EFF;
}

.statistics {
  margin-bottom: 20px;
}

.statistic-card {
  height: 120px;
  display: flex;
  align-items: center;
  padding: 20px;
}

.statistic-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
}

.statistic-icon .el-icon {
  font-size: 28px;
  color: #409EFF;
}

.statistic-info {
  flex: 1;
}

.statistic-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.statistic-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.recent-points {
  margin-top: 20px;
}

.text-success {
  color: #67C23A;
}

.text-danger {
  color: #F56C6C;
}
</style> 