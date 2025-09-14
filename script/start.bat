@echo off
chcp 65001 >nul
title Start All Services

echo [1/4] Starting Nacos...
cd /d D:\nacos\nacos\bin
start cmd /k "startup.cmd -m standalone"
timeout /t 2 >nul

echo [2/4] Starting Redis...
cd /d D:\Redis-x64-5.0.14.1(1)
start "Redis Server" redis-server.exe
timeout /t 2 >nul


@echo off
echo [3/4] Starting RocketMQ NameServer...
cd /d D:\rocketmq-all-5.3.3-bin-release\bin
start "RocketMQ NameServer" mqnamesrv.cmd
timeout /t 2 >nul

echo [4/4] Starting RocketMQ Broker...
cd /d D:\rocketmq-all-5.3.3-bin-release\bin
start "RocketMQ Broker" mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true
timeout /t 5 >nul

echo All services started successfully!
pause