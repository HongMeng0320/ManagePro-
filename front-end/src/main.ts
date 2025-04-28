import { createApp } from 'vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';

// 创建Vue应用实例
const app = createApp(App);

// 使用状态管理
app.use(createPinia());

// 使用路由
app.use(router);

// 使用Element Plus
app.use(ElementPlus);

// 挂载应用
app.mount('#app'); 