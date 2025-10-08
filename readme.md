# 后端

启动项目之前自动创建数据库

-- 创建数据库
create schema cron_videos collate utf8mb4_general_ci;

active -> 下载中

waiting -> 等待

paused -> 暂停

error -> 错误而停止的下载

complete -> 已停止和已完成

removed -> 用户删除


ffmpeg -v error -i output.mp4 -c copy -copyts -f null -


1280 * 720 high 720p

1920 * 1080 1920 * 1072 super 1080p

3840 * 1596 4k  2160p

3840 * 1608

1920 * 798


## 夸克下载过程


## 阿里云下载过程



# 前端

基于 Vue 3 + TypeScript + Element Plus 开发的影视自动下载管理系统的前端部分。

## 功能模块

- 📺 影视资源管理
- ⚙️ Aria2 下载配置
- 📥 下载任务管理
- 🔗 云盘分享链接管理

## 开发环境要求

- Node.js >= 18.0.0
- npm 或 yarn

## 快速开始

1. 安装依赖

    ```bash
    npm install
    ```

2. 启动开发服务器

    ```bash
    npm run dev
    ```

3. 打开浏览器访问 http://localhost:18080



## 可用命令

```bash
# 开发环境运行
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview

# 类型检查
npm run type-check
```

## 环境变量配置

开发环境配置文件 `.env.development`:

```
VITE_BASE_API=http://localhost:8080/api
VITE_ARIA2_WS_URL=ws://localhost:6800/jsonrpc
```

## 项目结构

```
src/
├── api/        # API 接口
├── components/ # 组件
├── router/     # 路由
├── types/      # 类型定义
├── views/      # 页面
├── App.vue     # 根组件
└── main.ts     # 入口文件
```

## 主要依赖

- Vue 3
- TypeScript
- Element Plus
- Vue Router
- Pinia
- Axios

## 路由说明

- `/media` - 影视管理页面
- `/aria2` - Aria2配置页面
- `/downloads` - 下载管理页面

## 开发注意事项

1. 确保后端API服务运行在 8080 端口
2. Aria2 服务需运行在 6800 端口
3. 代码提交前请运行类型检查