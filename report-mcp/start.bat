@echo off
echo ================================================
echo Spring AI MCP Database Demo 启动脚本
echo ================================================
echo.

echo 正在检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误：未找到Java环境，请确保已安装Java 21或更高版本
    pause
    exit /b 1
)

echo.
echo 正在检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误：未找到Maven环境，请确保已安装Maven
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo 错误：项目编译失败
    pause
    exit /b 1
)

echo.
echo 正在启动应用...
echo 访问地址：http://localhost:8081
echo MCP测试页面：http://localhost:8081/mcp-test
echo 健康检查：http://localhost:8081/mcp/health
echo.

mvn spring-boot:run

pause
