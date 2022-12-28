package net.ict.bodymanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.dto.FoodModifyRequestDTO;
import net.ict.bodymanager.dto.FoodRequestDTO;
import net.ict.bodymanager.repository.FoodRepository;
import net.ict.bodymanager.service.FoodService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/food")
@Log4j2
@RequiredArgsConstructor
public class FoodController {

    @Value("${net.ict.upload.path}")// import 시에 springframework으로 시작하는 Value
    private String uploadPath;
    private final FoodRepository foodRepository;
    private final FoodService foodService;


    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public String registerPost(@ModelAttribute FoodRequestDTO foodRequestDTO){
        return foodService.register(foodRequestDTO);
    }

    @PostMapping(value = "/list", produces = "application/text;charset=UTF-8")
    public String foodlist(@RequestBody Map<String,Object> map){
        return foodService.readOne(String.valueOf(map.get("date")));
    }

    @PostMapping(value = "/modify", consumes = {"multipart/form-data"})
    public String modify(@ModelAttribute FoodModifyRequestDTO foodDTO){
        return foodService.modify(foodDTO);

    }

    @DeleteMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String delete(@RequestBody Map<String,Object> map){
        return foodService.remove(Long.valueOf(map.get("food_id").toString()));

    }}