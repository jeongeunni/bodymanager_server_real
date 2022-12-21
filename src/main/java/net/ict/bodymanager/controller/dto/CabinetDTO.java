package net.ict.bodymanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CabinetDTO {
  private Long cab_id;
  @NotNull
  private int cab_num;
  @NotEmpty
  private LocalDate start_date;

  private LocalDate end_date;
  @NotNull
  private int cab_pwd;

  private Long member_id;

}
