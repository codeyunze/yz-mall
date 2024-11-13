import { defineConfig } from '@vben/vite-config';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      server: {
        proxy: {
          '/api': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/api/, ''),
            // mock代理目标地址
            target: 'http://localhost:5320/api',
            ws: true,
          },
          '/gateway': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/gateway/, ''),
            // mock代理目标地址
            target: 'http://192.168.3.77:30001/gateway',
            ws: true,
          },
        },
      },
    },
  };
});
