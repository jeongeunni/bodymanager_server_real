package net.ict.bodymanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PTProgramDTO {
  private Long program_id;
  private Long pt_member_id;
  @NotNull
  private int pt_count;
  @NotEmpty
  private String title;
  @NotNull
  private double weight;
  @NotNull
  private int count;
  @NotEmpty
  private LocalDateTime pt_date;
}
