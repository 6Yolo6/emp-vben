#!/bin/bash

# EMP Platform 开发环境初始化脚本
# 用途：一键启动开发环境所需的所有服务

echo "=========================================="
echo "EMP Platform 开发环境初始化"
echo "=========================================="

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "错误: Docker未安装，请先安装Docker Desktop"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "错误: Docker Compose未安装"
    exit 1
fi

# 创建必要的目录
echo "创建数据目录..."
mkdir -p docker/postgres/data
mkdir -p docker/redis/data
mkdir -p docker/nacos/logs
mkdir -p docker/nacos/data

# 停止并删除旧容器
echo "清理旧容器..."
docker-compose down

# 启动所有服务
echo "启动服务..."
docker-compose up -d

# 等待服务启动
echo "等待服务启动..."
sleep 10

# 检查服务状态
echo ""
echo "=========================================="
echo "服务状态检查"
echo "=========================================="

# 检查PostgreSQL
if docker exec emp-postgres pg_isready -U emp > /dev/null 2>&1; then
    echo "✓ PostgreSQL: 运行正常 (localhost:5432)"
else
    echo "✗ PostgreSQL: 启动失败"
fi

# 检查Redis
if docker exec emp-redis redis-cli -a emp123456 ping > /dev/null 2>&1; then
    echo "✓ Redis: 运行正常 (localhost:6379)"
else
    echo "✗ Redis: 启动失败"
fi

# 检查Nacos
if curl -s http://localhost:8848/nacos/ > /dev/null 2>&1; then
    echo "✓ Nacos: 运行正常 (http://localhost:8848/nacos)"
    echo "  默认账号: nacos / nacos"
else
    echo "✗ Nacos: 启动失败或仍在启动中"
    echo "  请稍后访问: http://localhost:8848/nacos"
fi

echo ""
echo "=========================================="
echo "开发环境初始化完成！"
echo "=========================================="
echo ""
echo "服务访问信息:"
echo "  PostgreSQL: localhost:5432"
echo "    - 数据库: emp_dev"
echo "    - 用户名: emp"
echo "    - 密码: emp123456"
echo ""
echo "  Redis: localhost:6379"
echo "    - 密码: emp123456"
echo ""
echo "  Nacos: http://localhost:8848/nacos"
echo "    - 用户名: nacos"
echo "    - 密码: nacos"
echo ""
echo "查看日志: docker-compose logs -f [服务名]"
echo "停止服务: docker-compose down"
echo "=========================================="
