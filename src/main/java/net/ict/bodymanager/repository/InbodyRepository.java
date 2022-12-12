package net.ict.bodymanager.repository;

import net.ict.bodymanager.entity.Inbody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDate;

public interface InbodyRepository extends JpaRepository<Inbody, Long> {


    @Modifying
    @Query(value = "UPDATE Inbody i SET i.weight = :weight, i.SMM = :SMM, i.BFM = :BFM, i.BMI = :BMI, i.PBF = :PBF, i.WHR = :WHR, i.BMR = :BMR, i.body_muscle = :body_muscle, i.left_hand_muscle = :left_hand_muscle, i.right_hand_muscle = :right_hand_muscle, i.left_leg_muscle = :left_leg_muscle, i.right_leg_muscle = :right_leg_muscle, i.body_fat = :body_fat, i.left_hand_fat = :left_hand_fat, i.right_hand_fat = :right_hand_fat, i.left_leg_fat = :left_leg_fat, i.right_leg_fat = :right_leg_fat where i.member.member_id = :mem_id and i.created_at = :date" )
    @Transactional
    void inbodyUpdate(@Param("weight") double weight, @Param("SMM") double SMM, @Param("BFM") double BFM, @Param("BMI") double BMI, @Param("PBF") double PBF, @Param("WHR") double WHR, @Param("BMR") double BMR,
                      @Param("body_muscle") double body_muscle, @Param("left_hand_muscle") double left_hand_muscle, @Param("right_hand_muscle") double right_hand_muscle, @Param("left_leg_muscle") double left_leg_muscle, @Param("right_leg_muscle") double right_leg_muscle,
                      @Param("body_fat") double body_fat, @Param("left_hand_fat") double left_hand_fat, @Param("right_hand_fat") double right_hand_fat, @Param("left_leg_fat") double left_leg_fat, @Param("right_leg_fat") double right_leg_fat,
                      @Param("mem_id") Long mem_id, @Param("date")LocalDate localDate);




}
