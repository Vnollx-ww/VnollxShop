package com.example.user.feign.middleware;

import com.example.common.model.middleware.oss.UploadFileDTO;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "middleware-service",path = "/api/middleware") // 这里只需要服务名
public interface OssFeignClient {
    @PostMapping("/oss/upload")
    Result<String> uploadFile(@RequestBody UploadFileDTO dto);
}
