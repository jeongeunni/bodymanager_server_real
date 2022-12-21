package net.ict.bodymanager.controller.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageLogDTO {
  private Long message_id;

  private Long room_id;

  private Long sender_id;
  @NotEmpty
  private String message_content;

  private LocalDate created_at;
}
