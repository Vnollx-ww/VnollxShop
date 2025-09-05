package com.example.gateway.controller;

import com.example.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    private final DiscoveryClient discoveryClient;

    public HealthController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * 基础健康检查
     */
    @GetMapping("/basic")
    public Result<Map<String, Object>> basicHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("service", applicationName);
        healthInfo.put("port", serverPort);
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        log.info("基础健康检查: {}", healthInfo);
        return Result.success(healthInfo);
    }

    /**
     * 详细健康检查
     */
    @GetMapping("/detailed")
    public Result<Map<String, Object>> detailedHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("service", applicationName);
        healthInfo.put("port", serverPort);
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // 服务发现信息
        Map<String, Object> discoveryInfo = new HashMap<>();
        discoveryInfo.put("services", discoveryClient.getServices());
        discoveryInfo.put("instances", discoveryClient.getInstances(applicationName));
        healthInfo.put("discovery", discoveryInfo);
        
        // 系统信息
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        healthInfo.put("system", systemInfo);
        
        log.info("详细健康检查: {}", healthInfo);
        return Result.success(healthInfo);
    }

    /**
     * 服务状态检查
     */
    @GetMapping("/services")
    public Result<Map<String, Object>> servicesHealth() {
        Map<String, Object> servicesInfo = new HashMap<>();
        
        // 获取所有注册的服务
        servicesInfo.put("registeredServices", discoveryClient.getServices());
        
        // 检查关键服务的状态
        Map<String, Object> serviceStatus = new HashMap<>();
        String[] criticalServices = {"data-service", "user-service", "product-service"};
        
        for (String service : criticalServices) {
            try {
                var instances = discoveryClient.getInstances(service);
                serviceStatus.put(service, instances.isEmpty() ? "DOWN" : "UP");
            } catch (Exception e) {
                serviceStatus.put(service, "ERROR");
                log.warn("检查服务状态失败: {}", service, e);
            }
        }
        
        servicesInfo.put("serviceStatus", serviceStatus);
        servicesInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return Result.success(servicesInfo);
    }
}
