@echo off
echo Testing Redis connection...
echo.

echo Checking if Redis container exists...
docker ps -a | findstr urlify-redis
if %ERRORLEVEL% NEQ 0 (
    echo Redis container not found. Starting with docker-compose...
    docker-compose up -d redis
    timeout /t 3
)

echo.
echo Checking if Redis is running...
docker ps | findstr urlify-redis
if %ERRORLEVEL% NEQ 0 (
    echo Redis container exists but is not running. Starting it...
    docker start urlify-redis
    timeout /t 3
)

echo.
echo Testing Redis connection...
docker exec urlify-redis redis-cli ping

echo.
echo Testing from host machine...
docker exec urlify-redis redis-cli -h localhost -p 6379 ping

echo.
echo Done!
