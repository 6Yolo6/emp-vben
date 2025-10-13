@echo off
chcp 65001 >nul
REM EMP Platform 开发环境初始化脚本 (Windows)

echo ==========================================
echo EMP Platform 开发环境初始化
echo ==========================================
echo.

REM 检查Docker是否安装
docker --version >nul 2>&1
if errorlevel 1 (
    echo 错误: Docker未安装，请先安装Docker Desktop
    pause
    exit /b 1
)

REM 检查Docker Compose是否安装
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo 错误: Docker Compose未安装
    pause
    exit /b 1
)

REM 创建必要的目录
echo 创建数据目录...
if not exist "docker\postgres\data" mkdir docker\postgres\data
if not exist "docker\redis\data" mkdir docker\redis\data
if not exist "docker\nacos\logs" mkdir docker\nacos\logs
if not exist "docker\nacos\data" mkdir docker\nacos\data

REM 停止并删除旧容器
echo 清理旧容器...
docker-compose down

REM 启动所有服务
echo 启动服务...
docker-compose up -d

REM 等待服务启动
echo 等待服务启动...
timeout /t 10 /nobreak >nul

echo.
echo ==========================================
echo 服务状态检查
echo ==========================================

REM 检查PostgreSQL
docker exec emp-postgres pg_isready -U emp >nul 2>&1
if errorlevel 1 (
    echo × PostgreSQL: 启动失败
) else (
    echo √ PostgreSQL: 运行正常 (localhost:5432)
)

REM 检查Redis
docker exec emp-redis redis-cli -a emp123456 ping >nul 2>&1
if errorlevel 1 (
    echo × Redis: 启动失败
) else (
    echo √ Redis: 运行正常 (localhost:6379)
)

REM 检查Nacos
curl -s http://localhost:8848/nacos/ >nul 2>&1
if errorlevel 1 (
    echo × Nacos: 启动失败或仍在启动中
    echo   请稍后访问: http://localhost:8848/nacos
) else (
    echo √ Nacos: 运行正常 (http://localhost:8848/nacos)
    echo   默认账号: nacos / nacos
)

echo.
echo ==========================================
echo 开发环境初始化完成！
echo ==========================================
echo.
echo 服务访问信息:
echo   PostgreSQL: localhost:5432
echo     - 数据库: emp_dev
echo     - 用户名: emp
echo     - 密码: emp123456
echo.
echo   Redis: localhost:6379
echo     - 密码: emp123456
echo.
echo   Nacos: http://localhost:8848/nacos
echo     - 用户名: nacos
echo     - 密码: nacos
echo.
echo 查看日志: docker-compose logs -f [服务名]
echo 停止服务: docker-compose down
echo ==========================================
echo.
pause
