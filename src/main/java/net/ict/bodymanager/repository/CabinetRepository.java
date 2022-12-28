package net.ict.bodymanager.repository;

import net.ict.bodymanager.entity.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDate;

public interface CabinetRepository extends JpaRepository<Cabinet, Long> {

    // 기존 cabinet이 없는 경우
    @Modifying
    @Query(value = "UPDATE Cabinet c SET c.end_date = :cab_end , c.start_date = :cab_start where c.member.member_id = :mem_id" )
    @Transactional
    void updateCab_before(@Param("cab_end") LocalDate cab_end, @Param("cab_start") LocalDate cab_start, @Param("mem_id") Long mem_id);

    @Modifying
    @Query(value = "UPDATE Cabinet c SET c.end_date = :cab_end where c.member.member_id = :mem_id" )
    @Transactional
    void updateCab_after(@Param("cab_end") LocalDate cab_end, @Param("mem_id") Long mem_id);


}
