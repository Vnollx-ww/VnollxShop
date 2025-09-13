package com.example.middleware.conroller;

import com.example.common.model.middleware.oss.UploadFileDTO;
import com.example.common.result.Result;
import com.example.middleware.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/middleware/oss")
@RequiredArgsConstructor
public class OssController {
    private final OssService ossService;
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestBody UploadFileDTO dto){
        return Result.success(ossService.uploadFile(dto));
    }
}
