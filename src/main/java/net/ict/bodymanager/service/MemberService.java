package net.ict.bodymanager.service;


import net.ict.bodymanager.dto.MemberDTO;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.util.LocalUploader;
import net.ict.bodymanager.util.S3Uploader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public interface MemberService {

    String join(MemberDTO memberDTO);
    String findEmail(String phone, String name);
    String logout(HttpServletRequest req);
    String loginning(HttpServletRequest req);
    String login(Map<String, String> user, HttpServletResponse response);
    ResponseEntity<Map<String, String>> emailDuple(Member member);
    ResponseEntity<Map<String, String>> phoneDuple(Member member);

    void remove(Long member_id);
    void modify(MemberDTO memberDTO);

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
                uploadedFilePaths.stream().map(s3Uploader::upload).collect(Collectors.toList());

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
                   .profile(filename)
                   .birth(date)
                   .type("user")
                   .roles(Collections.singletonList("ROLE_USER"))
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

