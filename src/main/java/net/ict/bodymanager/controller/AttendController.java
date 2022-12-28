package net.ict.bodymanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.dto.AttendDTO;
import net.ict.bodymanager.service.AttendService;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/attend")
@Log4j2
@RequiredArgsConstructor
public class AttendController {
    private final ModelMapper modelMapper;
    private final AttendService attendService;


    @PostMapping(value = "/register",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>> register(){

        AttendDTO attendDTO = new AttendDTO();
        attendService.register(attendDTO);

        Map<String,Object> result = Map.of("message","ok");
        log.info(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/readDay")
    public String readDay(){
        return attendService.readDay();
    }

    @PostMapping(value = "/readmonth")
    public String readMonth(@RequestBody Map<String, Object > map){
        return attendService.readMonth(String.valueOf(map.get("year")), String.valueOf(map.get("month")));
    }

}


