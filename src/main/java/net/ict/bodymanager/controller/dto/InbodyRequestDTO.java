package net.ict.bodymanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InbodyRequestDTO {

    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private List<String> date;
}
