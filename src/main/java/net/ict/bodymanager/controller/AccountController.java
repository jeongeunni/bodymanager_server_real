package net.ict.bodymanager.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.dto.OrderListDTO;
import net.ict.bodymanager.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/account")
@Log4j2
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Valid
    @GetMapping("/member")
    public String memInfo(){
        return accountService.MemberInfo();
    }

    @GetMapping(value = "/price", produces = "application/text;charset=UTF-8")
    public String priceInfo(){
        return accountService.infoList();
    }

    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String orderList(@RequestBody Map<String, String> page){
        return accountService.orderList(page.get("page"), page.get("limit"));
    }

    @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String >> order(@RequestBody OrderListDTO orderListDTO){

        accountService.orderRegister(orderListDTO);
        Map<String , String> resultMap = Map.of("message", "ok");
        return ResponseEntity.ok(resultMap);
    }
//OrderRequestDTO [] orderRequestDTO

}
