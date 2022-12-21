package net.ict.bodymanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//회원가입시 값 넣을 DTO
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

  private Long member_id;

  @NotEmpty
  private String email;
  @NotEmpty(message = "")
  private String password;
  @NotEmpty
  private String name;
  @NotEmpty
  private String address;
  @NotEmpty
  private String phone;
  @NotEmpty
  private String gender;
  @NotEmpty
  private String height;
  @NotEmpty
  private String remark;
  @NotNull
  private String birth;

  private MultipartFile[] profile;

  @NotEmpty
  private String type; //user, trainer , admin, 미인증, dormant(회원 삭제 시)
//(이메일) 미인증인 사람들은 어떤 권한을 가지고 있냐 : 다 안됨?

  private LocalDateTime created_at;


}
