<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" size="small" @click="isEditing = true" v-if="!isEditing">
            编辑资料
          </el-button>
          <div v-else>
            <el-button type="success" size="small" @click="saveProfile" :loading="loading">
              保存
            </el-button>
            <el-button size="small" @click="cancelEdit">
              取消
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="profile-info" v-if="!isEditing">
        <div class="info-item">
          <span class="label">用户名：</span>
          <span class="value">{{ userStore.userInfo?.username }}</span>
        </div>
        <div class="info-item">
          <span class="label">邮箱：</span>
          <span class="value">{{ userStore.userInfo?.email || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="label">手机：</span>
          <span class="value">{{ userStore.userInfo?.phone || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="label">角色：</span>
          <span class="value">{{ userStore.userInfo?.role === 'admin' ? '管理员' : '普通用户' }}</span>
        </div>
        <div class="info-item">
          <span class="label">创建时间：</span>
          <span class="value">{{ formatDate(userStore.userInfo?.createdAt) }}</span>
        </div>
        <div class="info-item">
          <span class="label">更新时间：</span>
          <span class="value">{{ formatDate(userStore.userInfo?.updatedAt) }}</span>
        </div>
      </div>
      
      <el-form 
        v-else 
        :model="profileForm" 
        :rules="rules" 
        ref="profileFormRef" 
        label-width="80px"
        class="profile-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input 
            v-model="profileForm.password" 
            type="password" 
            placeholder="不修改请留空" 
            show-password
          />
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>积分汇总</span>
          <el-button type="primary" size="small" @click="$router.push('/points')">
            查看详情
          </el-button>
        </div>
      </template>
      
      <div class="points-summary">
        <div class="point-total">
          <div class="point-icon">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="point-info">
            <div class="point-label">当前总积分</div>
            <div class="point-value">{{ totalPoints }}</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { Wallet } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { getUserTotalPoints } from '@/api/point';
import { updateUser } from '@/api/user';

const userStore = useUserStore();
const totalPoints = ref(0);
const isEditing = ref(false);
const loading = ref(false);
const profileFormRef = ref<FormInstance>();

// 表单数据
const profileForm = reactive({
  username: userStore.userInfo?.username || '',
  email: userStore.userInfo?.email || '',
  phone: userStore.userInfo?.phone || '',
  password: ''
});

// 表单验证规则
const rules = reactive<FormRules>({
  email: [
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  password: [
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
});

// 获取用户总积分
const fetchTotalPoints = async () => {
  try {
    totalPoints.value = await getUserTotalPoints();
  } catch (error) {
    console.error('获取总积分失败:', error);
  }
};

// 保存个人资料
const saveProfile = async () => {
  if (!profileFormRef.value) return;
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;
        
        // 构建要更新的数据
        const updateData: any = {};
        if (profileForm.email) updateData.email = profileForm.email;
        if (profileForm.phone) updateData.phone = profileForm.phone;
        if (profileForm.password) updateData.password = profileForm.password;
        
        // 调用更新接口
        await updateUser(userStore.userInfo!.userId, updateData);
        
        // 重新获取用户信息
        await userStore.fetchUserInfo();
        
        ElMessage.success('个人资料更新成功');
        isEditing.value = false;
      } catch (error) {
        console.error('更新个人资料失败:', error);
        ElMessage.error('更新个人资料失败');
      } finally {
        loading.value = false;
      }
    }
  });
};

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false;
  // 重置表单为当前用户信息
  profileForm.email = userStore.userInfo?.email || '';
  profileForm.phone = userStore.userInfo?.phone || '';
  profileForm.password = '';
};

// 格式化日期
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '暂无数据';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

onMounted(() => {
  fetchTotalPoints();
});
</script>

<style scoped>
.profile-container {
  padding: 15px;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  padding: 10px 0;
}

.info-item {
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

.profile-form {
  padding: 20px 10px 0;
}

.points-summary {
  padding: 20px 0;
}

.point-total {
  display: flex;
  align-items: center;
}

.point-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 20px;
}

.point-icon .el-icon {
  font-size: 28px;
  color: #409EFF;
}

.point-info {
  flex: 1;
}

.point-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.point-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
</style> 