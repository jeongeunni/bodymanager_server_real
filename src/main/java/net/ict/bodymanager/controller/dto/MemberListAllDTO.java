package net.ict.bodymanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberListAllDTO {

    private Long member_id;

    private String title;

    private String writer;

    private LocalDateTime regDate;

    private List<MemberImageDTO> memberImages;

}
