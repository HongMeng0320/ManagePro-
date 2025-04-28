<template>
  <div class="user-management-container">
    <el-card class="user-management-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAddUser">新增用户</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名关键词" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" icon="Search">查询</el-button>
          <el-button @click="resetSearch" icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 用户表格 -->
      <el-table
        :data="userList"
        stripe
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'info'">
              {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button
                size="small"
                icon="View"
                @click="handleViewUser(scope.row)"
              >
                查看
              </el-button>
              <el-button
                size="small"
                type="primary"
                icon="Edit"
                @click="handleEditUser(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                size="small"
                :type="scope.row.status === 1 ? 'danger' : 'success'"
                :icon="scope.row.status === 1 ? 'Lock' : 'Unlock'"
                @click="handleToggleStatus(scope.row)"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </div>
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
          <div class="detail-item">
            <span class="label">更新时间：</span>
            <span class="value">{{ formatDate(currentUser.updatedAt) }}</span>
          </div>
        </div>
      </el-dialog>
      
      <!-- 用户编辑对话框 -->
      <el-dialog
        v-model="userFormDialogVisible"
        :title="isEdit ? '编辑用户' : '新增用户'"
        width="600px"
      >
        <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userFormRules"
          label-width="100px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" :disabled="isEdit" />
          </el-form-item>
          <el-form-item label="密码" prop="password" v-if="!isEdit">
            <el-input v-model="userForm.password" type="password" show-password />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" />
          </el-form-item>
          <el-form-item label="手机" prop="phone">
            <el-input v-model="userForm.phone" />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="userForm.role">
              <el-option label="管理员" value="admin" />
              <el-option label="普通用户" value="user" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="userForm.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="userFormDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitUserForm" :loading="formLoading">
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
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import { getUserList, getUserDetail, addUser, updateUser, updateUserStatus } from '@/api/user';
import type { User, UserDetail } from '@/types';

// 数据状态
const loading = ref(false);
const formLoading = ref(false);
const userList = ref<User[]>([]);
const currentUser = ref<User | null>(null);
const userDetail = reactive<{totalPoints: number}>({
  totalPoints: 0
});

// 对话框状态
const userDetailDialogVisible = ref(false);
const userFormDialogVisible = ref(false);
const isEdit = ref(false);
const userFormRef = ref<FormInstance>();

// 搜索表单
const searchForm = reactive({
  username: '',
  status: 1 as '' | 0 | 1  // 默认选择"启用"状态
});

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// 用户表单
const userForm = reactive({
  userId: 0,
  username: '',
  password: '',
  email: '',
  phone: '',
  role: 'user',
  status: 1
});

// 表单验证规则
const userFormRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
});

// 获取用户列表
const fetchUserList = async () => {
  try {
    loading.value = true;
    const res = await getUserList(pagination.pageNum, pagination.pageSize);
    userList.value = res.list;
    pagination.total = res.total;
  } catch (error) {
    console.error('获取用户列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 查看用户详情
const handleViewUser = async (user: User) => {
  try {
    loading.value = true;
    currentUser.value = user;
    const detail = await getUserDetail(user.userId);
    userDetail.totalPoints = detail.totalPoints;
    userDetailDialogVisible.value = true;
  } catch (error) {
    console.error('获取用户详情失败:', error);
    ElMessage.error('获取用户详情失败');
  } finally {
    loading.value = false;
  }
};

// 新增用户
const handleAddUser = () => {
  isEdit.value = false;
  userForm.userId = 0;
  userForm.username = '';
  userForm.password = '';
  userForm.email = '';
  userForm.phone = '';
  userForm.role = 'user';
  userForm.status = 1;
  userFormDialogVisible.value = true;
};

// 编辑用户
const handleEditUser = (user: User) => {
  isEdit.value = true;
  userForm.userId = user.userId;
  userForm.username = user.username;
  userForm.password = '';
  userForm.email = user.email || '';
  userForm.phone = user.phone || '';
  userForm.role = user.role;
  userForm.status = user.status;
  userFormDialogVisible.value = true;
};

// 切换用户状态
const handleToggleStatus = (user: User) => {
  const newStatus = user.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? '启用' : '禁用';
  
  ElMessageBox.confirm(`确认要${statusText}用户"${user.username}"吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateUserStatus(user.userId, newStatus);
      ElMessage.success(`${statusText}用户成功`);
      fetchUserList();
    } catch (error) {
      console.error(`${statusText}用户失败:`, error);
      ElMessage.error(`${statusText}用户失败`);
    }
  }).catch(() => {});
};

// 提交用户表单
const submitUserForm = async () => {
  if (!userFormRef.value) return;
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        formLoading.value = true;
        
        if (isEdit.value) {
          // 编辑用户
          const updateData: Partial<User> = {
            email: userForm.email,
            phone: userForm.phone,
            role: userForm.role,
            status: userForm.status
          };
          await updateUser(userForm.userId, updateData);
          ElMessage.success('更新用户成功');
        } else {
          // 新增用户
          await addUser({
            username: userForm.username,
            password: userForm.password,
            email: userForm.email,
            phone: userForm.phone,
            role: userForm.role,
            status: userForm.status
          });
          ElMessage.success('添加用户成功');
        }
        
        userFormDialogVisible.value = false;
        fetchUserList();
      } catch (error) {
        console.error(isEdit.value ? '更新用户失败:' : '添加用户失败:', error);
        ElMessage.error(isEdit.value ? '更新用户失败' : '添加用户失败');
      } finally {
        formLoading.value = false;
      }
    }
  });
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  fetchUserList();
};

// 重置搜索
const resetSearch = () => {
  searchForm.username = '';
  searchForm.status = 1;  // 重置为默认状态"启用"
  pagination.pageNum = 1;
  fetchUserList();
};

// 处理每页条数变化
const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  fetchUserList();
};

// 处理页码变化
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val;
  fetchUserList();
};

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

onMounted(() => {
  fetchUserList();
});
</script>

<style scoped>
.user-management-container {
  padding: 15px;
}

.user-management-card {
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

/* 操作按钮样式 */
.operation-buttons {
  display: flex;
  justify-content: space-between;
  gap: 5px;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .operation-buttons {
    flex-direction: column;
    gap: 5px;
  }
}
</style> 