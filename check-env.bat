@echo off
chcp 65001 >nul
REM EMP Platform 环境检查脚本

echo ==========================================
echo EMP Platform 环境检查
echo ==========================================
echo.

REM 检查Docker是否运行
docker ps >nul 2>&1
if errorlevel 1 (
    echo × Docker未运行，请先启动Docker Desktop
    echo.
    pause
    exit /b 1
)
echo √ Docker运行正常
echo.

REM 检查PostgreSQL
echo 检查PostgreSQL...
docker exec emp-postgres pg_isready -U emp >nul 2>&1
if errorlevel 1 (
    echo × PostgreSQL未运行或未就绪
    echo   请运行: init-dev.bat
) else (
    echo √ PostgreSQL运行正常
    docker exec emp-postgres psql -U emp -d emp_dev -c "SELECT version();" 2>nul | findstr "PostgreSQL" >nul
    if not errorlevel 1 (
        echo   数据库: emp_dev 可访问
    )
)
echo.

REM 检查Redis
echo 检查Redis...
docker exec emp-redis redis-cli -a emp123456 ping 2>nul | findstr "PONG" >nul
if errorlevel 1 (
    echo × Redis未运行或密码错误
    echo   请运行: init-dev.bat
) else (
    echo √ Redis运行正常
)
echo.

REM 检查Nacos
echo 检查Nacos...
curl -s http://localhost:8848/nacos/ >nul 2>&1
if errorlevel 1 (
    echo × Nacos未运行或仍在启动中
    echo   请访问: http://localhost:8848/nacos
    echo   默认账号: nacos / nacos
) else (
    echo √ Nacos运行正常
    echo   访问地址: http://localhost:8848/nacos
)
echo.

REM 检查Java版本
echo 检查Java版本...
java -version 2>&1 | findstr "version" >nul
if errorlevel 1 (
    echo × Java未安装
) else (
    java -version 2>&1 | findstr "17\|18\|19\|20\|21" >nul
    if errorlevel 1 (
        echo × Java版本不符合要求（需要Java 17+）
        java -version 2>&1
    ) else (
        echo √ Java版本符合要求
        java -version 2>&1 | findstr "version"
    )
)
echo.

REM 检查Maven
echo 检查Maven...
call mvn -version >nul 2>&1
if errorlevel 1 (
    echo × Maven未安装或未配置环境变量
) else (
    echo √ Maven已安装
    call mvn -version 2>&1 | findstr "Apache Maven"
)
echo.

echo ==========================================
echo 环境检查完成
echo ==========================================
echo.
echo 如果所有检查都通过，可以启动应用：
echo   cd emp-system
echo   mvn spring-boot:run
echo.
echo 或者使用IDE启动 EmpSystemApplication
echo.
pause
