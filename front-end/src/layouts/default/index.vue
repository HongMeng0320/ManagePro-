<template>
  <div class="app-container">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="logo">
        <span>系统管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
        
        <el-menu-item index="/points">
          <el-icon><Wallet /></el-icon>
          <span>积分记录</span>
        </el-menu-item>
        
        <!-- 管理员菜单 -->
        <template v-if="userStore.isAdmin()">
          <el-menu-item index="/user-management">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          
          <el-menu-item index="/point-management">
            <el-icon><Operation /></el-icon>
            <span>积分管理</span>
          </el-menu-item>
        </template>
      </el-menu>
    </div>
    
    <!-- 主体内容 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <div class="navbar">
        <div class="hamburger" @click="toggleSidebar">
          <el-icon><Fold v-if="isCollapse" /><Expand v-else /></el-icon>
        </div>
        
        <div class="right-menu">
          <el-dropdown trigger="click">
            <div class="avatar-container">
              <span class="username">{{ userStore.userInfo?.username }}</span>
              <el-avatar size="small" :icon="User" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 内容区域 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { 
  HomeFilled, 
  User, 
  UserFilled, 
  Wallet, 
  Operation, 
  Fold, 
  Expand 
} from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { ElMessageBox } from 'element-plus';

const route = useRoute();
const userStore = useUserStore();
const isCollapse = ref(false);

// 当前活动菜单
const activeMenu = computed(() => {
  return route.path;
});

// 切换侧边栏折叠状态
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
};

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout();
  }).catch(() => {});
};
</script>

<style scoped>
.app-container {
  display: flex;
  height: 100%;
}

.sidebar {
  width: 210px;
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  overflow-y: auto;
}

.sidebar.is-collapse {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}

.sidebar-menu {
  border-right: none;
  height: calc(100% - 60px);
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.hamburger {
  cursor: pointer;
  font-size: 20px;
}

.right-menu {
  display: flex;
  align-items: center;
}

.avatar-container {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-right: 10px;
  font-size: 14px;
}

.app-main {
  flex: 1;
  padding: 15px;
  background-color: #f0f2f5;
  overflow-y: auto;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 