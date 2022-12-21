package net.ict.bodymanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PTInfoDTO {
    private Long pt_id;
    private Long trainer_id;
    @NotEmpty
    private int pt_price;
}