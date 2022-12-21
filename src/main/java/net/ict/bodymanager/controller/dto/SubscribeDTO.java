package net.ict.bodymanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeDTO {

    private Long sub_id;

    private Long member_id;

    private LocalDate membership_start;

    private LocalDate membership_end;

    private LocalDate suit_start;

    private LocalDate suit_end;
}
