package net.ict.bodymanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendDTO {

  private Long attend_id;
  private Long member_id;
  @NotEmpty
  private LocalDateTime start_date;
  private LocalDateTime end_date;
}
