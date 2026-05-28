import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 引入 Vant 全局样式
import 'vant/lib/index.css'
import './assets/style/global.scss'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
