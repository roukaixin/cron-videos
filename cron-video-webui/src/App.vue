<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <el-card shadow="always">
          <el-row>
            <el-col :span="6">
              <router-link to="/media" class="nav-item" active-class="active">
                <i class="el-icon-video-camera"></i>
                <span>影视管理</span>
              </router-link>
            </el-col>
            <el-col :span="6">
              <router-link to="/cloud-storage-auth" class="nav-item" active-class="active">
                <i class="el-icon-cloudy"></i>
                <span>网盘认证</span>
              </router-link>
            </el-col>
            <el-col :span="6">
              <router-link to="/downloader" class="nav-item" active-class="active">
                <i class="el-icon-download"></i>
                <span>下载器管理</span>
              </router-link>
            </el-col>
            <el-col :span="6">
              <router-link to="/download-task" class="nav-item" active-class="active">
                <i class="el-icon-folder-opened"></i>
                <span>下载任务管理</span>
              </router-link>
            </el-col>
          </el-row>
        </el-card>
      </el-header>
      <el-main>
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component"/>
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts" setup>

defineOptions({
  name: "App"
})

</script>

<style scoped>

.el-card {
  background-color: #f56c6c;
  border-radius: 8px;
}
.header {
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

.custom-nav {
  display: flex;
  width: 100%;
  height: 70px;
  background-color: #fff;
  border-bottom: none;
  justify-content: space-between;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #606266;
  text-decoration: none;
  font-size: 18px;
  transition: all 0.3s ease;
  position: relative;
  padding: 10px 0;
  overflow: hidden;
  text-align: center;
}

.nav-item i {
  font-size: 28px;
  margin-bottom: 8px;
  transition: transform 0.3s ease;
}

.nav-item span {
  transition: transform 0.3s ease, opacity 0.2s ease;
  font-weight: 500;
  width: 100%;
  text-align: center;
  display: block;
}

.nav-item:hover {
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.05);
}

.nav-item:hover i {
  transform: translateY(-2px) scale(1.1);
}

.nav-item:hover span {
  transform: translateY(2px);
}

.nav-item.active i {
  transform: scale(1.1);
}

.nav-item:hover::after {
  left: 15%;
  width: 70%;
}

/* 添加波纹效果 */
.nav-item::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background-color: rgba(64, 158, 255, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.6s ease, height 0.6s ease;
  z-index: -1;
}

.nav-item:active::before {
  width: 200px;
  height: 200px;
}

@media (max-width: 768px) {
  .nav-item i {
    font-size: 22px;
  }

  .nav-item span {
    font-size: 16px;
  }
}
</style>