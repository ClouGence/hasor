<template>
    <el-container>
        <el-header>
            <el-menu class="el-menu-demo" mode="horizontal">
                <el-menu-item index="1">
                    <router-link to="/">
                        <el-link><i class="el-icon-notebook-1"/>Interface</el-link>
                    </router-link>
                </el-menu-item>
                <el-menu-item index="2">
                    <router-link to="/new">
                        <el-link><i class="el-icon-plus"/>New</el-link>
                    </router-link>
                </el-menu-item>
                <el-menu-item index="3">
                    <el-link href="https://www.hasor.net/web/dataql/what_is_dataql.html" target="_blank"><i class="el-icon-warning-outline"></i>What is DataQL?</el-link>
                </el-menu-item>
            </el-menu>
            <div class="gitStyle">
                <!-- Github -->
                <span><a target="_blank" class="github-button" href="https://github.com/zycgit/hasor" data-show-count="true" aria-label="Star zycgit/hasor on GitHub">Star</a></span>
                <span><a href='https://gitee.com/zycgit/hasor/stargazers'><img src='https://gitee.com/zycgit/hasor/badge/star.svg?theme=white' alt='star'/></a></span>
                <br/>
                <!-- gitee -->
                <span><a target="_blank" class="github-button" href="https://github.com/zycgit/hasor/fork" data-show-count="true" aria-label="Fork zycgit/hasor on GitHub">Fork</a></span>
                <span><a href='https://gitee.com/zycgit/hasor/members'><img src='https://gitee.com/zycgit/hasor/badge/fork.svg?theme=white' alt='fork'/></a></span>
            </div>
        </el-header>
        <el-main>
            <div :style="{height:fullHeight + 'px', overflow: 'hidden'}">
                <router-view/>
            </div>
        </el-main>
    </el-container>
</template>

<script>
    import request from "./utils/request";
    import {ApiUrl} from "./utils/api-const";

    export default {
        name: 'App',
        data: function () {
            return {
                fullHeight: document.documentElement.clientHeight - 60
            }
        },
        mounted() {
            this.fullHeight = document.documentElement.clientHeight - 60;
            window.addEventListener('resize', () => {
                return (() => {
                    this.fullHeight = document.documentElement.clientHeight - 60;
                })();
            });
            //
            if (window.ALL_MAC !== '{ALL_MAC}') {
                const self = this;
                request(ApiUrl.report, {
                    "method": "POST",
                    "loading": false,
                    "data": window.ALL_MAC
                }, response => {
                }, response => {
                },);
            }
        }
    }
</script>
<style scoped>
    .el-header, .el-main {
        padding: 0;
    }

    .gitStyle {
        float: right;
        top: -55px;
        position: relative;
        padding-right: 20px;
    }

    .gitStyle span {
        display: inline-block;
        width: 90px;
    }
</style>
