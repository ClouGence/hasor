<template>
  <el-container>
    <el-header>
      <el-menu class="el-menu-demo" mode="horizontal">
        <el-menu-item index="1">
          <router-link to="/">
            <el-link><i class="el-icon-notebook-1" />Interface</el-link>
          </router-link>
        </el-menu-item>
        <el-menu-item index="2">
          <router-link to="/new">
            <el-link><i class="el-icon-plus" />New</el-link>
          </router-link>
        </el-menu-item>
        <el-menu-item index="3">
          <el-link href="https://www.hasor.net/web/dataql/what_is_dataql.html" target="_blank"><i class="el-icon-warning-outline"></i>What is DataQL?</el-link>
        </el-menu-item>
      </el-menu>
      <div v-if="newVersionIcon" class="newStyle" @click="newVersionDialog=true">
        <span class="iconfont iconnew" style="font-size: 39px" />
      </div>
      <div class="gitStyle">
        <!-- Github -->
        <span><a target="_blank" href="https://github.com/zycgit/hasor/network"><img alt="GitHub forks" src="https://img.shields.io/github/forks/zycgit/hasor?style=social"></a></span>
        <span><a target="_blank" href="https://github.com/zycgit/hasor/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/zycgit/hasor?style=social"></a></span>
        <br />
        <!-- gitee -->
        <span><a target="_blank" href="https://gitee.com/zycgit/hasor/stargazers"><img src="https://gitee.com/zycgit/hasor/badge/star.svg?theme=white" alt="star"></a></span>
        <span><a target="_blank" href="https://gitee.com/zycgit/hasor/members"><img src="https://gitee.com/zycgit/hasor/badge/fork.svg?theme=white" alt="fork"></a></span>
      </div>
    </el-header>
    <el-main>
      <div :style="{height:fullHeight + 'px', overflow: 'hidden'}">
        <router-view />
      </div>
    </el-main>
    <el-dialog :visible.sync="newVersionDialog" width="50%" center :destroy-on-close="true">
      <span slot="title" class="dialog-footer">NewVersion : {{ newVersionInfo.lastVersion }}</span>
      <span>Your version : {{ currentVersion }}</span>
      <div v-if="newVersionInfo.changelog !=='unknown'" style="padding: 10px;">
        <iframe :src="newVersionInfo.changelog + '?currentVersion='+ currentVersion"
                width="100%" height="100%" frameborder="no" border="0" marginwidth="0"
                marginheight="0" scrolling="yes" allowtransparency="yes"
        ></iframe>
      </div>
      <div v-if="newVersionInfo.changelog ==='unknown'" style="padding: 10px;">
        <li>WebSite：https://www.hasor.net/web/dataway/about.html</li>
        <li>Blog：https://www.hasor.net/blog/index.html</li>
        <li>Changelog: https://www.hasor.net/web/changelog/index.html</li>
        <li><a target="_blank" href="https://maven-badges.herokuapp.com/maven-central/net.hasor/hasor-dataway">
          <img src="https://maven-badges.herokuapp.com/maven-central/net.hasor/hasor-dataway/badge.svg" alt="Maven">
        </a></li>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" type="primary" round @click="closeCheckVersion">Close</el-button>
        <br />
        <el-checkbox v-model="newVersionDialogRemember">Remember and don't show again</el-checkbox>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import request from './utils/request';
import {ApiUrl} from './utils/api-const';

export default {
    name: 'App',
    data: function () {
        return {
            fullHeight: document.documentElement.clientHeight - 60,
            newVersionIcon: false,
            newVersionDialog: false,
            newVersionDialogRemember: false,
            currentVersion: window.DATAWAY_VERSION,
            newVersionInfo: {lastVersion: window.DATAWAY_VERSION, changelog: 'unknown'}
        };
    },
    mounted() {
        this.fullHeight = document.documentElement.clientHeight - 60;
        window.addEventListener('resize', () => {
            return (() => {
                this.fullHeight = document.documentElement.clientHeight - 60;
            })();
        });
        // check new version
        if (window.ALL_MAC !== '{ALL_MAC}') {
            const self = this;
            this.$nextTick(function () {
                request(ApiUrl.checkVersion, {
                    'method': 'POST',
                    'loading': false,
                    'data': {
                        version: window.DATAWAY_VERSION,
                        info: window.ALL_MAC
                    }
                }, response => {
                    self.checkVersion(response.data.result);
                }, response => {
                    self.checkVersion({lastVersion: 'unknown', changelog: 'unknown'});
                });
            });
        }
    },
    methods: {
        closeCheckVersion() {
            this.newVersionDialog = false;
            localStorage.setItem('lastCheckVersionDialogRemember', this.newVersionDialogRemember.toString());
            localStorage.setItem('lastCheckVersion', this.newVersionInfo.lastVersion);
        },
        checkVersion(versionInfo) {
            if (Object.prototype.toString.call(versionInfo).toUpperCase() !== '[OBJECT OBJECT]') {
                versionInfo = {lastVersion: versionInfo, changelog: 'unknown'};
            }
            try {
                if (versionInfo.lastVersion === 'unknown') {
                    return;
                }
                this.newVersionInfo = versionInfo;
                this.newVersionIcon = window.DATAWAY_VERSION !== versionInfo.lastVersion;
                this.newVersionDialogRemember = localStorage.getItem('lastCheckVersionDialogRemember') === 'true';
                this.newVersionDialog = localStorage.getItem('lastCheckVersion') !== versionInfo.lastVersion || !this.newVersionDialogRemember; // 对话框是否打开
            } catch (e) {
                console.log(e);
            }
        }
    }
};
</script>
<style scoped>
    .el-header, .el-main {
        padding: 0;
    }

    .gitStyle {
        float: right;
        top: -55px;
        position: relative;
    }

    .gitStyle span {
        display: inline-block;
        width: 100px;
    }

    .newStyle, .newStyle:hover {
        color: #F3A732;
        float: right;
        width: 40px;
        position: relative;
        height: 40px;
        top: -52px;
        padding-right: 15px;
    }

    .newStyle:hover {
        cursor: pointer
    }
</style>
