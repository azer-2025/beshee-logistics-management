import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import {createSvgIconsPlugin} from 'vite-plugin-svg-icons';
import path from 'path'
import { fileURLToPath } from 'node:url'

// https://vite.dev/config/
export default defineConfig({
  base: './',
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
    createSvgIconsPlugin({
      // 指定需要缓存的图标文件夹
      iconDirs: [path.resolve(process.cwd(), 'src/icons/svg')],
      // 指定symbolId格式
      symbolId: 'icon-[dir]-[name]',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
    extensions: [".mjs", ".js", ".ts", ".jsx", ".tsx", ".json", ".vue"],
  },
  server: {
    port: 5174,
    // cors: true,
    proxy: {
      '/wuliuguanlixitong': {
        target: 'http://localhost:8080/wuliuguanlixitong/',
        changeOrigin: true,
        ws: true,
        rewrite: (path) => path.replace(/^\/wuliuguanlixitong/, ''),
      },
    },
  },
})
