package com.example.middleware.conroller;

import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.result.Result;
import com.example.middleware.service.MqService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/middleware/mq")
@RequiredArgsConstructor
public class MqController {
    private final MqService mqService;
    @PostMapping("/order")
    public Result<Void> createOrderMessage(@RequestBody CreateOrderDTO dto){
        mqService.createOrderMessage(dto);
        return Result.success();
    }
}
