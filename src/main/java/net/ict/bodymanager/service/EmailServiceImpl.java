package net.ict.bodymanager.service;


import lombok.AllArgsConstructor;
import net.ict.bodymanager.controller.dto.MailDTO;
import net.ict.bodymanager.repository.MemberRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService{

  private final MemberRepository memberRepository;
  private JavaMailSender emailSender;
  public static final String ePw = createKey();

  //인증번호 생성
  public static String createKey() {
    StringBuilder key = new StringBuilder();
    Random rnd = new Random();

    for (int i = 0; i < 8; i++) { // 인증코드 8자리
      int index = rnd.nextInt(3); // 0~2 까지 랜덤

      switch (index) {
        case 0:
          key.append((char) ((int) (rnd.nextInt(26)) + 97));
          //  a~z  (ex. 1+97=98 => (char)98 = 'b')
          break;
        case 1:
          key.append((char) ((int) (rnd.nextInt(26)) + 65));
          //  A~Z
          break;
        case 2:
          key.append((rnd.nextInt(10)));
          // 0~9
          break;
      }
    }
    return key.toString();
  }
  //이메일 날리기
  public void sendSimpleMessage(MailDTO mailDto) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setFrom("1090jun@gmail.com");
    message.setTo(mailDto.getEmail());
    message.setSubject("BodyManager 이메일 인증");
    message.setText("인증번호는 [" + ePw + "]입니다. " +
            "인증 창으로 돌아가 입력해주세요");
    emailSender.send(message);
  }
  //메일 인증 코드 , 이메일 확인
  public String checkMessage(MailDTO mailDto) {
    String email =  mailDto.getEmail();
    String password =  mailDto.getPassword();
    String code = mailDto.getCode();
//    System.out.println("email :  " + email );
//    System.out.println("password :  " + password );
//    System.out.println("ePw :  " + ePw );
//    System.out.println("code : " + code );

    //인증코드와 이메일이 일치한다면
    if (ePw.equals(code) && memberRepository.existsByEmail(email)) {
      return "ok";
    } else {
      return "false";
    }
  }
}