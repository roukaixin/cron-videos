import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/media'
  },
  {
    path: '/media',
    name: 'Media',
    component: () => import('@/views/Media.vue'),
    meta: { keepAlive: true }
  },
  {
    path: '/aria2-server',
    name: 'Aria2Server.vue',
    component: () => import('@/views/Aria2Server.vue'),
    meta: { keepAlive: true }
  },
  {
    path: '/aria2-download-task',
    name: 'Aria2DownloadTask',
    component: () => import('@/views/Aria2DownloadTask.vue'),
    meta: { keepAlive: true }
  },
  {
    path: '/cloud-storage-auth',
    name: 'CloudStorageAuthManager',
    component: () => import('@/views/CloudStorageAuth.vue'),
    meta: { keepAlive: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 