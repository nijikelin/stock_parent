#!/bin/sh
# 记事本打开，修改编码格式为utf8，可解决上传centos后中文乱码问题

# 启用错误检查：命令执行失败时立即退出脚本
set -e

# 定义错误提示函数
error_exit() {
    echo "错误：$1 执行失败！" 1>&2
    exit 1
}

echo =================================
echo  镜像容器更新
echo =================================

echo "准备从Git仓库拉取最新代码"
cd /usr/local/codes/stock_parent || error_exit "切换到代码目录 /usr/local/codes/stock_parent"

echo "开始从Git仓库拉取最新代码"
git pull || error_exit "git pull（拉取代码）"

echo "代码拉取完成"
echo "开始打包"
mvn clean install -Dmaven.test.skip=true || error_exit "mvn clean install（项目打包）"

# 切换到docker目录
cd ./docker || error_exit "切换到docker目录"

echo "停止并删除相关容器"
docker-compose down || error_exit "docker-compose down（停止容器）"

echo "更新镜像，并启动容器"
docker-compose up --build -d || error_exit "docker-compose up --build（构建并启动容器）"

echo "删除未被使用的镜像"
docker image prune -f || error_exit "docker image prune（清理镜像）"

echo "项目启动完成"



































