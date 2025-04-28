<template>
  <div class="points-container">
    <el-card class="points-card">
      <template #header>
        <div class="card-header">
          <span>积分明细</span>
          <div class="header-right">
            <el-tag type="success">当前总积分: {{ totalPoints }}</el-tag>
          </div>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="积分类型">
          <el-select v-model="searchForm.pointType" placeholder="全部" clearable>
            <el-option label="奖励积分" value="reward" />
            <el-option label="消费积分" value="consume" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
            :default-time="[
              new Date(2000, 1, 1, 0, 0, 0),
              new Date(2000, 1, 1, 23, 59, 59)
            ]"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 积分表格 -->
      <el-table
        :data="pointList"
        stripe
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="pointId" label="ID" width="80" />
        <el-table-column prop="pointType" label="积分类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.pointType === 'reward' ? 'success' : 'danger'">
              {{ scope.row.pointType === 'reward' ? '奖励' : '消费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分值" width="120">
          <template #default="scope">
            <span :class="scope.row.points > 0 ? 'text-success' : 'text-danger'">
              {{ scope.row.points > 0 ? '+' + scope.row.points : scope.row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="recordedAt" label="记录时间" min-width="180">
          <template #default="scope">
            {{ formatDate(scope.row.recordedAt) }}
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { getPointList, getUserTotalPoints } from '@/api/point';
import type { Point } from '@/types';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const loading = ref(false);
const totalPoints = ref(0);
const pointList = ref<Point[]>([]);
const dateRange = ref<[string, string] | null>(null);

// 搜索表单
const searchForm = reactive({
  pointType: '',
  startTime: '',
  endTime: ''
});

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// 从日期范围更新开始和结束时间
const updateTimeRange = () => {
  if (dateRange.value) {
    searchForm.startTime = dateRange.value[0];
    searchForm.endTime = dateRange.value[1];
  } else {
    searchForm.startTime = '';
    searchForm.endTime = '';
  }
};

// 查询积分列表
const fetchPointList = async () => {
  try {
    loading.value = true;
    updateTimeRange();
    
    const res = await getPointList({
      pointType: searchForm.pointType,
      startTime: searchForm.startTime,
      endTime: searchForm.endTime,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    });
    
    pointList.value = res.list;
    pagination.total = res.total;
  } catch (error) {
    console.error('获取积分列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 查询用户总积分
const fetchTotalPoints = async () => {
  try {
    totalPoints.value = await getUserTotalPoints();
  } catch (error) {
    console.error('获取总积分失败:', error);
  }
};

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  fetchPointList();
};

// 重置搜索
const resetSearch = () => {
  searchForm.pointType = '';
  dateRange.value = null;
  searchForm.startTime = '';
  searchForm.endTime = '';
  pagination.pageNum = 1;
  fetchPointList();
};

// 处理每页条数变化
const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  fetchPointList();
};

// 处理页码变化
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val;
  fetchPointList();
};

onMounted(() => {
  fetchPointList();
  fetchTotalPoints();
});
</script>

<style scoped>
.points-container {
  padding: 15px;
}

.points-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.text-success {
  color: #67C23A;
  font-weight: bold;
}

.text-danger {
  color: #F56C6C;
  font-weight: bold;
}
</style> 