package net.ict.bodymanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.controller.dto.MailDTO;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.repository.MemberRepository;
import net.ict.bodymanager.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/initial/mail")
public class MailController {
  private final MemberRepository memberRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  //이메일 전송
  @PostMapping("")
  public ResponseEntity<Map<String, String>> sendMail(@RequestBody MailDTO mailDto) {
    String email = mailDto.getEmail();

    if (memberRepository.existsByEmail(email)) {
      emailService.sendSimpleMessage(mailDto);
      Map<String, String> result = Map.of("message", "ok");
      return ResponseEntity.ok(result);
    } else {
      Map<String, String> result = Map.of("message", "not found");
      return ResponseEntity.ok(result);
    }
  }

  //메일 인증코드 확인 후 비밀번호 변경
  @PostMapping("/check")
  public ResponseEntity<Map<String, String>> checkCode(@RequestBody MailDTO mailDto) {
    String email = mailDto.getEmail();
    String password = mailDto.getPassword();
    String check = emailService.checkMessage(mailDto);
    Map<String, String> result;
    if (check.equals("ok")) {
      Optional<Member> opt = memberRepository.findByEmail(email);
      Member member = opt.orElseThrow();
      member.changePassword(passwordEncoder.encode(password));
      memberRepository.save(member);
      result = Map.of("message", "ok");
    } else{
      result = Map.of("message", "not found");
    }
    return ResponseEntity.ok(result);
  }
}