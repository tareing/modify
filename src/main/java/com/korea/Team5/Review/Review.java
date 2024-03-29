package com.korea.Team5.Review;
import com.korea.Team5.USER.Member;
import com.korea.Team5.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;
@Entity
@Getter
@Setter
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer Id;

  private String content;//내용


  private int starRating;

  private LocalDateTime createDate;//등록시간

  @ManyToOne
  private Member member;//사용자와연결

  @ManyToMany
  Set<Member> voter; // 추천 ,좋아요


  @ManyToOne
  private Movie movie;//무비와연결

  private LocalDateTime modifyDate;//수정일시
}