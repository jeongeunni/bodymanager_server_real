package net.ict.bodymanager.service;

import net.ict.bodymanager.controller.dto.AttendDTO;
import net.ict.bodymanager.entity.Attend;
import net.ict.bodymanager.entity.Member;

public interface AttendService {
    void register(AttendDTO attendDTO);
//    void modify();

    String readDay();
    String readMonth(String year,String month);

    default Attend dtoToEntity(AttendDTO attendDTO, Member member){
        Long member_id = member.getMember_id();

        Attend attend = Attend.builder()
                .member(member)
                .start_date(attendDTO.getStart_date())
                .build();
        return attend;
    }
}
