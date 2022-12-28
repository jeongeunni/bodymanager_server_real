package net.ict.bodymanager.service;

import net.ict.bodymanager.controller.dto.OrderListDTO;
import net.ict.bodymanager.controller.dto.PTInfoDTO;
import net.ict.bodymanager.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AccountService {

    String MemberInfo();

    String infoList();

    String orderList(String page, String limit);

    void orderRegister(OrderListDTO orderListDTO);


    default PTInfo dtoToEntityPTInfo(PTInfoDTO ptInfoDTO, Member member){

        PTInfo ptInfo = PTInfo.builder()
                .pt_price(ptInfoDTO.getPt_price())
                .trainer(member)
                .build();
        return ptInfo;

    }

    default Purchase dtoToEntityPurchase(OrderListDTO.OrderRequestDTO o, PTInfo ptInfo, OrderInfo orderInfo, Price price){

        if (o.getType().equals("price")) {
            Purchase purchase = Purchase.builder()
                    .orderInfo(orderInfo)
                    .period(Integer.parseInt(o.getCount()))
                    .price(price)
                    .build();
            return purchase;
        } else {
            Purchase purchase = Purchase.builder()
                    .orderInfo(orderInfo)
                    .period(Integer.parseInt(o.getCount()))
                    .ptInfo(ptInfo)
                    .build();
            return purchase;
        }
    }

    default OrderInfo dtoToEntityOrderInfo(Member member){

        OrderInfo orderInfo = OrderInfo.builder()
                .created_at(LocalDateTime.now())
                .member(member)
                .build();
        return orderInfo;
    }

    default PTMember dtoToEntityPTMember(OrderListDTO.OrderRequestDTO o, Member member, PTInfo ptInfo){

        PTMember ptMember = PTMember.builder()
                .pt_present_count(0)
                .pt_remain_count(Integer.parseInt(o.getCount()))
                .pt_total_count(Integer.parseInt(o.getCount()))
                .start_date(LocalDate.parse(o.getStart()))
                .member(member)
                .ptInfo(ptInfo)
                .build();
        return ptMember;
    }

    default Subscribe dtoToEntitySub(OrderListDTO.OrderRequestDTO o, Member member){

        if (o.getId().equals("2")) {
            Subscribe subscribe = Subscribe.builder()
                    .suit_end(LocalDate.parse(o.getStart()).plusMonths(Long.parseLong(o.getCount())))
                    .suit_start(LocalDate.parse(o.getStart()))
                    .member(member)
                    .build();
            return subscribe;
        } else {
            Subscribe subscribe = Subscribe.builder()
                    .membership_end(LocalDate.parse(o.getStart()).plusMonths(Long.parseLong(o.getCount())))
                    .membership_start(LocalDate.parse(o.getStart()))
                    .member(member)
                    .build();
            return subscribe;
        }

     }

    default Cabinet dtoToEntityCab(OrderListDTO.OrderRequestDTO o, Member member) {

        Cabinet cabinet = Cabinet.builder()
                .cab_pwd(0000)
                .cab_num(0)
                .end_date(LocalDate.parse(o.getStart()).plusMonths(Long.parseLong(o.getCount())))
                .start_date(LocalDate.parse(o.getStart()))
                .member(member)
                .build();
        return cabinet;
    }



}


