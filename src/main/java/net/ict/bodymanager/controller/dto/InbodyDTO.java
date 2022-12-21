package net.ict.bodymanager.controller.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InbodyDTO {

//    private Long inbody_id;
//    private Member member_id;
    @NotNull
    private double weight;
    @NotNull
    private double SMM;
    @NotNull
    private double BFM;
    @NotNull
    private double BMI;
    @NotNull
    private double PBF;
    @NotNull
    private double WHR;
    @NotNull
    private double BMR;
    @NotNull
    private double body_muscle;
    @NotNull
    private double left_hand_muscle;
    @NotNull
    private double right_hand_muscle;
    @NotNull
    private double left_leg_muscle;
    @NotNull
    private double right_leg_muscle;
    @NotNull
    private double body_fat;
    @NotNull
    private double left_hand_fat;
    @NotNull
    private double right_hand_fat;
    @NotNull
    private double left_leg_fat;
    @NotNull
    private double right_leg_fat;

//    private LocalDate created_at;


}


