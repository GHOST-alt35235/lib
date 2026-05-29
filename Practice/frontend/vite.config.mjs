import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/role': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/category': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/book': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/distribute': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/upload': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
