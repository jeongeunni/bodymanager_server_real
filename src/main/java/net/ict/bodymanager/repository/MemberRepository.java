package net.ict.bodymanager.repository;

import net.ict.bodymanager.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByPhone(String phone);

  boolean existsByEmail(String email);

  @Query("select m.email from Member m where m.phone =:phone and m.name =:name")
  String findByPhoneAndName(String phone, String name);

  @EntityGraph(attributePaths = "roles")
  Optional<Member> findByEmail(String email);

//  @Modifying
//  @Transactional
//  @Query("update Member m set m.refreshToken =:refreshToken where m.email = :email ")
//  void updateToken(@Param("refreshToken") String refreshToken, @Param("email") String email);
}

