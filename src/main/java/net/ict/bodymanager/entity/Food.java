package net.ict.bodymanager.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Food {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long food_id;

  @ManyToOne
  @JoinColumn(name="member_id")
  private Member member;

  @Column(nullable = false)
  private String time;

  @Column(length = 2000, nullable = false)
  private String content;

  @Column(nullable = false)
  private int grade;

  @Column(nullable = false)
  private LocalDateTime created_at;

  @Column(nullable = false)
  private String food_img;

  public void change(String content, String food_img){
    this.content=content;
    this.food_img = food_img;
  }

}
