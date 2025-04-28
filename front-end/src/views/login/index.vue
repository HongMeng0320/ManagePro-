<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">系统登录</div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            :loading="userStore.loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, onBeforeMount } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const loginFormRef = ref<FormInstance>();

// 如果已登录，直接跳转到首页 - 此检查在组件挂载前执行
onBeforeMount(() => {
  checkLoginStatus();
});

// 页面加载完成后再次检查登录状态，处理页面刷新的情况
onMounted(() => {
  // 清除可能存在的过期提示标记
  window.hasShownLoginExpired = false;
  checkLoginStatus();
});

// 检查登录状态
function checkLoginStatus() {
  const token = localStorage.getItem('token');
  if (token) {
    // 如果是从别的页面跳转过来的，不重复跳转
    if (router.currentRoute.value.query.redirect) {
      return;
    }
    // 避免重复导航
    if (router.currentRoute.value.path !== '/') {
      router.push('/');
    }
  }
}

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
});

// 表单验证规则
const loginRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
});

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return;
  
  // 清除登录过期标记，确保能够重新登录
  window.hasShownLoginExpired = false;
  
  console.log('验证表单...');
  await loginFormRef.value.validate((valid) => {
    if (valid) {
      console.log('表单验证通过，开始登录流程...');
      userStore.login(loginForm);
    } else {
      console.log('表单验证失败');
    }
  });
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #f5f7fa;
}

.login-box {
  width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
  margin-bottom: 30px;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
}
</style> 