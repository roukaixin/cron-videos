<template>
  <el-container class="app-shell">
    <el-card class="layout-card" shadow="never">
      <template #header>
        <div class="nav-shell">
          <el-header class="nav-header">
            <el-row class="nav-row" :gutter="12">
              <el-col :span="6" class="nav-col">
                <router-link
                  :to="{ name: 'Media' }"
                  class="nav-item"
                  active-class="active"
                  exact-active-class="active"
                >
                  <span>影视管理</span>
                </router-link>
              </el-col>
              <el-col :span="6" class="nav-col">
                <router-link
                  :to="{ name: 'CloudStorageAuthManager' }"
                  class="nav-item"
                  active-class="active"
                  exact-active-class="active"
                >
                  <span>网盘认证</span>
                </router-link>
              </el-col>
              <el-col :span="6" class="nav-col">
                <router-link
                  :to="{ name: 'Downloader' }"
                  class="nav-item"
                  active-class="active"
                  exact-active-class="active"
                >
                  <span>下载器管理</span>
                </router-link>
              </el-col>
              <el-col :span="6" class="nav-col">
                <router-link
                  :to="{ name: 'DownloadTask' }"
                  class="nav-item"
                  active-class="active"
                  exact-active-class="active"
                >
                  <span>下载任务管理</span>
                </router-link>
              </el-col>
            </el-row>
          </el-header>
        </div>
      </template>
      <el-main class="app-main">
        <div class="content-scroll">
          <router-view />
        </div>
      </el-main>
    </el-card>
  </el-container>
</template>

<script lang="ts" setup>

defineOptions({
  name: "App"
})

</script>

<style scoped>
.app-shell {
  height: 100%;
  padding: 12px;
  box-sizing: border-box;
}

.layout-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
}

.layout-card :deep(.el-card__header) {
  padding: 12px;
  border-bottom: none;
}

.layout-card :deep(.el-card__body) {
  padding: 0;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.app-main {
  flex: 1;
  min-height: 0;
  padding: 0 12px 12px;
}

.content-scroll {
  height: 100%;
  overflow: hidden;
}

.nav-shell {
  padding: 6px;
  border-radius: 6px;
  background: #f7f9fc;
  border: 1px solid #dfe6f3;
}

.nav-header {
  height: auto;
  padding: 0;
}

.nav-row {
  margin: 0 !important;
}

.nav-col {
  min-width: 0;
}

.nav-item {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #4e5969;
  text-decoration: none;
  font-size: 16px;
  transition: all 0.25s ease;
  position: relative;
  padding: 12px 14px;
  border-radius: 6px;
  min-height: 46px;
  text-align: center;
  border: 1px solid #dbe5f1;
  background-color: #ffffff;
}

.nav-item span {
  font-weight: 500;
  width: 100%;
  text-align: center;
  display: block;
  letter-spacing: 0.3px;
}

.nav-item:hover {
  color: #3b82f6;
  background-color: #f2f7ff;
  border-color: #cfe0f7;
}

.nav-item.active {
  color: #2f72e8;
  background-color: #edf3fc;
  border-color: #c8d7eb;
  box-shadow: none;
}

.nav-item.active span {
  font-weight: 600;
}

.nav-item::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 0;
  height: 3px;
  background-color: #409EFF;
  border-radius: 999px;
  transition: width 0.25s ease;
}

.nav-item:hover::after,
.nav-item.active::after {
  width: 100%;
}

@media (max-width: 768px) {
  .app-shell {
    padding: 8px;
  }

  .layout-card :deep(.el-card__header) {
    padding: 8px;
  }

  .app-main {
    padding: 0 8px 8px;
  }

  .nav-shell {
    padding: 0;
    border-radius: 6px;
  }

  .nav-row {
    row-gap: 8px;
  }

  .nav-item {
    min-height: 40px;
    padding: 8px 6px;
    border-radius: 6px;
  }

  .nav-item span {
    font-size: 14px;
    letter-spacing: 0;
  }
}
</style>

<style>
html,
body,
#app {
  height: 100%;
}

body {
  margin: 0;
  overflow: hidden;
}
</style>
