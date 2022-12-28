package net.ict.bodymanager.service;


import net.ict.bodymanager.dto.FoodModifyRequestDTO;
import net.ict.bodymanager.dto.FoodRequestDTO;
import net.ict.bodymanager.entity.Food;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.util.LocalUploader;
import net.ict.bodymanager.util.S3Uploader;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface FoodService {
    String register(FoodRequestDTO foodRequestDTO);

    String readOne(String date);

    String remove(Long food_id);

    String modify(FoodModifyRequestDTO foodDTO);


    default Food dtoToEntity(FoodRequestDTO foodRequestDTO, Member member, LocalUploader localUploader, S3Uploader s3Uploader){
        MultipartFile[] files = foodRequestDTO.getFood_img();
        if (files == null || files.length <= 0) {
            return null;
        }
        List<String> uploadedFilePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadedFilePaths.addAll(localUploader.uploadLocal(file));
        }
        List<String> s3Paths =
                uploadedFilePaths.stream().map(fileName -> s3Uploader.
                        upload(fileName)).collect(Collectors.toList());
        String filename = s3Paths.get(0);
        Food food = Food.builder()
                .member(member)
                .time(foodRequestDTO.getTime())
                .content(foodRequestDTO.getContent())
                .created_at(LocalDateTime.now())
                .food_img(filename)
                .build();
        return food;
    }

    default String fileName(FoodModifyRequestDTO foodDTO,LocalUploader localUploader,S3Uploader s3Uploader){
        MultipartFile[] files = foodDTO.getFood_img();
        if (files == null || files.length <= 0) {
            return null;
        }
        List<String> uploadedFilePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadedFilePaths.addAll(localUploader.uploadLocal(file));
        }
        List<String> s3Paths =
                uploadedFilePaths.stream().map(fileName -> s3Uploader.
                        upload(fileName)).collect(Collectors.toList());
        String filename = s3Paths.get(0);

        return filename;

//
//        Food food = Food.builder()
//                .member(member)
//                .time(foodRequestDTO.getTime())
//                .content(foodRequestDTO.getContent())
//                .created_at(LocalDateTime.now())
//                .food_img(filename)
//                .build();
//        return food;
    }


}

