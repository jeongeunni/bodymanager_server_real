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
public class MessageDTO {

    private Long messageId;

    @NotEmpty
    private int roomId;

    @NotEmpty
    private int senderId;

    @NotEmpty
    private String content;

    private LocalDateTime createdAt;

}
