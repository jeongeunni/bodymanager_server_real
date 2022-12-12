package net.ict.bodymanager.repository;

import net.ict.bodymanager.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByPhone(String phone);

  boolean existsByEmail(String email);

  @Query("select m.email from Member m where m.phone =:phone and m.name =:name")
  String findByPhoneAndName(String phone, String name);

  @EntityGraph(attributePaths = "roles")
  Optional<Member> findByEmail(String email);

//<<<<<<< HEAD
//  @EntityGraph(attributePaths = {"imageSet"})
//  @Query("select m from Member m where m.member_id =:member_id")
//  Optional<Member> findByIdWithImages(Long member_id);
//
//=======
//>>>>>>> 030fe50 ([준영] 로그인 유지 완료)

//  @Modifying
//  @Transactional
//  @Query("update Member m set m.mpw =:mpw where m.mid = :mid ")
//  void updatePassword(@Param("mpw") String password, @Param("mid") String mid);
}

