<template>
  <div class="point-management-container">
    <el-card class="point-management-card">
      <template #header>
        <div class="card-header">
          <span>积分管理</span>
          <el-button type="primary" @click="handleAddPoint">新增积分记录</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="用户ID" clearable />
        </el-form-item>
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
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column label="用户名" width="120">
          <template #default="scope">
            <el-button link type="primary" @click="handleViewUser(scope.row.userId)">
              {{ getUsernameById(scope.row.userId) || '未知用户' }}
            </el-button>
          </template>
        </el-table-column>
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
      
      <!-- 用户详情对话框 -->
      <el-dialog
        v-model="userDetailDialogVisible"
        title="用户详情"
        width="600px"
      >
        <div v-if="currentUser" class="user-detail">
          <div class="detail-item">
            <span class="label">用户ID：</span>
            <span class="value">{{ currentUser.userId }}</span>
          </div>
          <div class="detail-item">
            <span class="label">用户名：</span>
            <span class="value">{{ currentUser.username }}</span>
          </div>
          <div class="detail-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ currentUser.email || '未设置' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">手机：</span>
            <span class="value">{{ currentUser.phone || '未设置' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">角色：</span>
            <span class="value">{{ currentUser.role === 'admin' ? '管理员' : '普通用户' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <span class="value">
              <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
                {{ currentUser.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </span>
          </div>
          <div class="detail-item">
            <span class="label">总积分：</span>
            <span class="value">{{ userDetail.totalPoints }}</span>
          </div>
          <div class="detail-item">
            <span class="label">创建时间：</span>
            <span class="value">{{ formatDate(currentUser.createdAt) }}</span>
          </div>
        </div>
      </el-dialog>
      
      <!-- 添加积分对话框 -->
      <el-dialog
        v-model="pointFormDialogVisible"
        title="添加积分记录"
        width="500px"
      >
        <el-form
          ref="pointFormRef"
          :model="pointForm"
          :rules="pointFormRules"
          label-width="100px"
        >
          <el-form-item label="用户ID" prop="userId">
            <el-input v-model.number="pointForm.userId" placeholder="请输入用户ID" />
          </el-form-item>
          <el-form-item label="积分类型" prop="pointType">
            <el-select v-model="pointForm.pointType" placeholder="请选择积分类型">
              <el-option label="奖励积分" value="reward" />
              <el-option label="消费积分" value="consume" />
            </el-select>
          </el-form-item>
          <el-form-item label="积分值" prop="points">
            <el-input-number
              v-model="pointForm.points"
              :min="-1000"
              :max="1000"
              :precision="0"
              :step="10"
              :placeholder="pointForm.pointType === 'reward' ? '正数' : '负数'"
            />
            <div class="form-helper">
              * 奖励积分为正数，消费积分为负数
            </div>
          </el-form-item>
          <el-form-item label="记录时间">
            <el-date-picker
              v-model="pointForm.recordedAt"
              type="datetime"
              placeholder="请选择时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
            <div class="form-helper">
              * 不选择则使用当前时间
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="pointFormDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitPointForm" :loading="formLoading">
              确定
            </el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { getPointList, addPoint } from '@/api/point';
import { getUserDetail, getUserList } from '@/api/user';
import type { Point, User } from '@/types';

// 数据状态
const loading = ref(false);
const formLoading = ref(false);
const pointList = ref<Point[]>([]);
const currentUser = ref<User | null>(null);
const userDetail = reactive<{totalPoints: number}>({
  totalPoints: 0
});
const userMap = ref<Map<number, string>>(new Map());

// 对话框状态
const userDetailDialogVisible = ref(false);
const pointFormDialogVisible = ref(false);
const pointFormRef = ref<FormInstance>();
const dateRange = ref<[string, string] | null>(null);

// 搜索表单
const searchForm = reactive({
  userId: '',
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

// 积分表单
const pointForm = reactive({
  userId: undefined as number | undefined,
  pointType: 'reward',
  points: 0,
  recordedAt: ''
});

// 表单验证规则
const pointFormRules = reactive<FormRules>({
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { type: 'number', message: '用户ID必须为数字', trigger: 'blur' }
  ],
  pointType: [
    { required: true, message: '请选择积分类型', trigger: 'change' }
  ],
  points: [
    { required: true, message: '请输入积分值', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value === 0) {
          callback(new Error('积分值不能为0'));
        } else if (pointForm.pointType === 'reward' && value < 0) {
          callback(new Error('奖励积分必须为正数'));
        } else if (pointForm.pointType === 'consume' && value > 0) {
          callback(new Error('消费积分必须为负数'));
        } else {
          callback();
        }
      }, 
      trigger: 'change' 
    }
  ]
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
      userId: searchForm.userId ? parseInt(searchForm.userId) : undefined,
      pointType: searchForm.pointType,
      startTime: searchForm.startTime,
      endTime: searchForm.endTime,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    });
    
    pointList.value = res.list;
    pagination.total = res.total;
    
    // 加载用户名
    await fetchUsernames();
  } catch (error) {
    console.error('获取积分列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 加载用户名
const fetchUsernames = async () => {
  try {
    // 获取表中显示的所有用户ID
    const userIds = new Set(pointList.value.map(point => point.userId));
    
    // 过滤已经加载过的用户ID
    const newUserIds = Array.from(userIds).filter(id => !userMap.value.has(id));
    
    if (newUserIds.length === 0) return;
    
    // 理想情况下应该有批量查询接口，这里简化处理，分别查询
    for (const userId of newUserIds) {
      try {
        const detail = await getUserDetail(userId);
        userMap.value.set(userId, detail.user.username);
      } catch (error) {
        console.error(`获取用户 ${userId} 信息失败:`, error);
      }
    }
  } catch (error) {
    console.error('获取用户名失败:', error);
  }
};

// 通过ID获取用户名
const getUsernameById = (userId: number): string => {
  return userMap.value.get(userId) || '';
};

// 查看用户详情
const handleViewUser = async (userId: number) => {
  try {
    loading.value = true;
    const detail = await getUserDetail(userId);
    currentUser.value = detail.user;
    userDetail.totalPoints = detail.totalPoints;
    userDetailDialogVisible.value = true;
  } catch (error) {
    console.error('获取用户详情失败:', error);
    ElMessage.error('获取用户详情失败');
  } finally {
    loading.value = false;
  }
};

// 新增积分记录
const handleAddPoint = () => {
  pointForm.userId = undefined;
  pointForm.pointType = 'reward';
  pointForm.points = 0;
  pointForm.recordedAt = '';
  pointFormDialogVisible.value = true;
};

// 提交积分表单
const submitPointForm = async () => {
  if (!pointFormRef.value) return;
  
  await pointFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        formLoading.value = true;
        
        await addPoint({
          userId: pointForm.userId,
          pointType: pointForm.pointType,
          points: pointForm.points,
          recordedAt: pointForm.recordedAt || undefined
        });
        
        ElMessage.success('添加积分记录成功');
        pointFormDialogVisible.value = false;
        fetchPointList();
      } catch (error) {
        console.error('添加积分记录失败:', error);
        ElMessage.error('添加积分记录失败');
      } finally {
        formLoading.value = false;
      }
    }
  });
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  fetchPointList();
};

// 重置搜索
const resetSearch = () => {
  searchForm.userId = '';
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

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

onMounted(() => {
  fetchPointList();
});
</script>

<style scoped>
.point-management-container {
  padding: 15px;
}

.point-management-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
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

.user-detail {
  padding: 10px;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
}

.label {
  width: 100px;
  color: #606266;
}

.value {
  flex: 1;
  color: #303133;
}

.form-helper {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
  margin-top: 4px;
}
</style> 