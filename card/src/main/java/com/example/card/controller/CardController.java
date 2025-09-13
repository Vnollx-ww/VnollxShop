package com.example.card.controller;

import com.example.card.service.CardService;
import com.example.common.model.cart.dto.AddCardItemDTO;
import com.example.common.model.cart.dto.DeleteCardItemByShopDTO;
import com.example.common.model.cart.dto.DeleteCardItemDTO;
import com.example.common.model.cart.dto.UpdateNumberDTO;
import com.example.common.model.cart.vo.CartItemVO;
import com.example.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    @GetMapping("/list")
    public Result<List<CartItemVO>> getCardItemList( HttpServletRequest request){
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        return Result.success(cardService.getCardItemList(uid));
    }
    @GetMapping("/count")
    public Result<Long> getCardItemListCount( HttpServletRequest request){
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        return Result.success(cardService.getCardItemListCount(uid));
    }
    @PutMapping("/update")
    public Result<Void> updateNumber(@ModelAttribute UpdateNumberDTO dto){

        cardService.updateNumber(dto);
        return Result.success();
    }
    @DeleteMapping("/delete")
    public Result<Void> deleteCardItem(@ModelAttribute DeleteCardItemDTO dto){

        cardService.deleteCardItem(dto);
        return Result.success();
    }
    @DeleteMapping("/delete/shop")
    public Result<Void> deleteCardItemByShop(@RequestBody DeleteCardItemByShopDTO dto){

        cardService.deleteCardItemByShop(dto);
        return Result.success();
    }
    @PostMapping("/add")
    public Result<Void> addCardItem(@ModelAttribute AddCardItemDTO dto, HttpServletRequest request){
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        cardService.addCardItem(dto,uid);
        return Result.success();
    }
}
