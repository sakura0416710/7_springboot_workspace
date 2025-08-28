import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // {key : value} 형식
    proxy:{
      //localhost:5173/api/attm/list 의 형식에서 api를/react라고 쓰겠다 설정
      '/react':{
        target:'http://localhost:8080',
        changeOrigin: true, //cors문제 해결하기
        rewrite:path => path.replace(/^\/react/, '') //맨 앞(^)react api를 지워주기
      }
    }
  }
})
