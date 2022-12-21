package net.ict.bodymanager.controller.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderListDTO {

    private List<OrderRequestDTO> order_list;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class OrderRequestDTO {

        private String id;

        private String type;

        private String count;

        private String start;

    }


}
