import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from '@vant/auto-import-resolver'
import path from 'path'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    base: './',
    plugins: [
      vue(),
      Components({ resolvers: [VantResolver()] })
    ],
    resolve: {
      alias: { '@': path.resolve(__dirname, 'src') }
    },
    server: {
      host: '0.0.0.0',
      port: 5173,
      proxy: {
        // 代理到 RuoYi 后端
        '/h5-api': {
          target: env.VITE_API_BASE || 'http://localhost:8080',
          changeOrigin: true
        },
        // 验证码接口
        '/captchaImage': {
          target: env.VITE_API_BASE || 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    build: {
      outDir: 'dist',
      assetsDir: 'static',
      sourcemap: false,
      chunkSizeWarningLimit: 1500
    }
  }
})
