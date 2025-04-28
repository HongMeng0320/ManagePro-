<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { useRouter, useRoute } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const isInitializing = ref(true);

onMounted(async () => {
  // 清除可能存在的过期提示标记
  window.hasShownLoginExpired = false;
  
  try {
    isInitializing.value = true;
    
    // 如果已登录，获取用户信息
    if (userStore.token) {
      try {
        await userStore.fetchUserInfo();
      } catch (error) {
        console.error('初始化用户信息失败:', error);
        
        // 只有在非登录页面时才显示错误并跳转
        if (route.path !== '/login') {
          // 如果获取用户信息失败，可能是token过期
          // 清除token并重定向到登录页
          userStore.logout();
        }
      }
    }
  } finally {
    isInitializing.value = false;
  }
});
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

#app {
  height: 100%;
}
</style> 