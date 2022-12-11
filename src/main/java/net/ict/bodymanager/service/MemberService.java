package net.ict.bodymanager.service;


import net.ict.bodymanager.dto.MemberDTO;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.util.LocalUploader;
import net.ict.bodymanager.util.S3Uploader;
import org.springframework.web.multipart.MultipartFile;


import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public interface MemberService {


////<<<<<<< HEAD
//    Long register(MemberDTO memberDTO);
//    MemberDTO readOne(Long member_id);
//    void modify(MemberDTO memberDTO);
//    String findEmail(String phone, String name);
//    void remove(Long member_id);
////=======
    String register(MemberDTO memberDTO);
    String findEmail(String phone, String name);
    void remove(Long member_id);
    void modify(MemberDTO memberDTO);
//
//>>>>>>> 030fe50 ([준영] 로그인 유지 완료)

    default Member dtoToEntity(MemberDTO memberDTO, LocalUploader localUploader, S3Uploader s3Uploader){
        MultipartFile[] files = memberDTO.getProfile();
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(memberDTO.getBirth(), formatter);

           Member member = Member.builder()
                   .member_id(memberDTO.getMember_id())
                   .email(memberDTO.getEmail())
                   .password(memberDTO.getPassword())
                   .name(memberDTO.getName())
                   .address(memberDTO.getAddress())
                   .phone(memberDTO.getPhone())
                   .gender(memberDTO.getGender())
                   .height(Double.parseDouble(memberDTO.getHeight()))
                   .remark(memberDTO.getRemark())
////<<<<<<< HEAD
//                   .profile(memberDTO.getProfile())
//                   .birth(memberDTO.getBirth())
//                   .type("admin")
//                   .roles(Collections.singletonList("ROLE_ADMIN"))
////=======
                   .profile(filename)
                   .birth(date)
                   .type("user")
                   .roles(Collections.singletonList("ROLE_USER"))
//>>>>>>> 030fe50 ([준영] 로그인 유지 완료)
                   .build();
           return member;
    }

    default MemberDTO entityToDTO(Member member) {

        MemberDTO memberDTO = MemberDTO.builder()
                .member_id(member.getMember_id())
                .email(member.getEmail())
                .password(member.getPassword())
                .gender(member.getGender())
                .name(member.getName())
                .created_at(member.getCreated_at())
                .build();
        return memberDTO;
    }
}

