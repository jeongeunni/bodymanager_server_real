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
public class OrderInfoDTO {
    private Long order_id;
    private Long member_id;

    @NotEmpty
    private LocalDateTime created_at;
}
