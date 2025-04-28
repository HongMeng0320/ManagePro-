<template>
  <div class="error-container">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <div class="error-message">页面不存在</div>
      <div class="error-description">抱歉，您访问的页面不存在或已被删除</div>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
// 没有特定的逻辑
</script>

<style scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #f5f7fa;
}

.error-content {
  text-align: center;
  padding: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #409EFF;
  margin: 0;
  line-height: 1.2;
}

.error-message {
  font-size: 32px;
  color: #303133;
  margin: 20px 0;
}

.error-description {
  font-size: 16px;
  color: #909399;
  margin-bottom: 30px;
}
</style> 