package net.ict.bodymanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodRequestDTO {

    //    private Long food_id;
    private String time;
    private String content;
    private MultipartFile[] food_img;
//    private List<String> food_img;



}

