package net.ict.bodymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.dto.MemberDTO;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.filter.JwtTokenProvider;
import net.ict.bodymanager.repository.MemberRepository;
import net.ict.bodymanager.util.LocalUploader;
import net.ict.bodymanager.util.S3Uploader;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final LocalUploader localUploader;
  private final S3Uploader s3Uploader;


  @Override
  public String register(MemberDTO memberDTO) {
    Member member = dtoToEntity(memberDTO ,localUploader,s3Uploader);
    member.changePassword(passwordEncoder.encode(memberDTO.getPassword()));
    Long member_id = memberRepository.save(member).getMember_id();

    JSONObject join = new JSONObject();
    join.put("message", "ok");
    return join.toString();
  }

  @Override
  public void modify(MemberDTO memberDTO) {

    Optional<Member> result = memberRepository.findById(memberDTO.getMember_id());

    Member member = result.orElseThrow();

    member.change(memberDTO.getEmail(), memberDTO.getGender());
    memberRepository.save(member);
  }

  //이메일 찾기
  @Override
  public String findEmail(String phone, String name) {
    String email = memberRepository.findByPhoneAndName(phone,name);
    JSONObject object = new JSONObject();
    JSONObject data = new JSONObject();

    object.put("email",email);

    if(email == null || email.equals("")){
      data.put("message","not found");

    }else {
      data.put("message", "ok");
      data.put("data", object);
    }
    return data.toString();
  }

  @Override
  public void remove(Long member_id) {
    memberRepository.deleteById(member_id);
  }

}
