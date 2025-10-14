@echo off
echo ========================================
echo  启动 EMP Platform 所有服务
echo ========================================

echo.
echo [1/3] 启动 emp-system 服务...
cd emp-system
start "emp-system" cmd /k "mvn spring-boot:run"
cd ..

timeout /t 10

echo.
echo [2/3] 启动 emp-auth 服务...
cd emp-auth
start "emp-auth" cmd /k "mvn spring-boot:run"
cd ..

timeout /t 10

echo.
echo [3/3] 启动 emp-gateway 网关服务...
cd emp-gateway
start "emp-gateway" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo  所有服务启动完成！
echo ========================================
echo.
echo 服务地址：
echo  - emp-system:  http://localhost:8080
echo  - emp-auth:    http://localhost:8081
echo  - emp-gateway: http://localhost:8888
echo.
echo API文档：
echo  - emp-system:  http://localhost:8080/doc.html
echo  - emp-auth:    http://localhost:8081/doc.html
echo  - emp-gateway: http://localhost:8888/doc.html
echo.
pause
