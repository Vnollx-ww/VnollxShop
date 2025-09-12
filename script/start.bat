@echo off
chcp 65001 >nul
title Start All Services

echo [1/2] Starting Nacos...
cd /d D:\nacos\nacos\bin
start cmd /k "startup.cmd -m standalone"
timeout /t 2 >nul

echo [2/2] Starting Redis...
cd /d D:\Redis-x64-5.0.14.1(1)
start "Redis Server" redis-server.exe
timeout /t 2 >nul