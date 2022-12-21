package net.ict.bodymanager.service;

import net.ict.bodymanager.controller.dto.InbodyDTO;
import net.ict.bodymanager.controller.dto.InbodyRequestDTO;
import net.ict.bodymanager.entity.Inbody;
import net.ict.bodymanager.entity.Member;

import java.time.LocalDate;
import java.util.List;

public interface InbodyService {

    String musclePart(InbodyRequestDTO inbodyRequestDTO);

    String fatPart(InbodyRequestDTO inbodyRequestDTO);

    String inbodyInfo();

    String bodyChangeFlow();

    void register(InbodyDTO inbodyDTO);

    void modify(InbodyDTO inbodyDTO);

    String inbodyList();

    List<LocalDate> check();

    default Inbody dtoTOEntity(InbodyDTO inbodyDTO, Member member){
        Inbody inbody = Inbody.builder()
                .member(member)
                .created_at(LocalDate.now())
                .weight(inbodyDTO.getWeight())
                .SMM(inbodyDTO.getSMM())
                .BFM(inbodyDTO.getBFM())
                .BMI(inbodyDTO.getBMI())
                .PBF(inbodyDTO.getPBF())
                .WHR(inbodyDTO.getWHR())
                .BMR(inbodyDTO.getBMR())
                .body_muscle(inbodyDTO.getBody_muscle())
                .left_hand_muscle(inbodyDTO.getLeft_hand_muscle())
                .right_hand_muscle(inbodyDTO.getRight_hand_muscle())
                .left_leg_muscle(inbodyDTO.getLeft_leg_muscle())
                .right_leg_muscle(inbodyDTO.getRight_leg_muscle())
                .body_fat(inbodyDTO.getBody_fat())
                .left_hand_fat(inbodyDTO.getLeft_hand_fat())
                .right_hand_fat(inbodyDTO.getRight_hand_fat())
                .left_leg_fat(inbodyDTO.getLeft_leg_fat())
                .right_leg_fat(inbodyDTO.getRight_leg_fat())
                .build();
        return inbody;
    }

    default InbodyDTO entityToDto(Inbody inbody, Member member){
        InbodyDTO inbodyDTO = InbodyDTO.builder()
//                .member_id(member)
                .weight(inbody.getWeight())
                .SMM(inbody.getSMM())
                .BFM(inbody.getBFM())
                .BMI(inbody.getBMI())
                .PBF(inbody.getPBF())
                .WHR(inbody.getWHR())
                .BMR(inbody.getBMR())
                .body_muscle(inbody.getBody_muscle())
                .left_hand_muscle(inbody.getLeft_hand_muscle())
                .right_hand_muscle(inbody.getRight_hand_muscle())
                .left_leg_muscle(inbody.getLeft_leg_muscle())
                .right_leg_muscle(inbody.getRight_leg_muscle())
                .body_fat(inbody.getBody_fat())
                .left_hand_fat(inbody.getLeft_hand_fat())
                .right_hand_fat(inbody.getRight_hand_fat())
                .left_leg_fat(inbody.getLeft_leg_fat())
                .right_leg_fat(inbody.getRight_leg_fat())
                .build();
        return inbodyDTO;
    }

}
