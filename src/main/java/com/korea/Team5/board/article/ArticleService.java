package com.korea.Team5.board.article;

import com.korea.Team5.DataNotFoundException;
import com.korea.Team5.USER.Member;
import com.korea.Team5.movie.entity.MovieInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;

  public void create(String title, String content, Member member, MovieInfo movieInfo){

    Article article = new Article();
    article.setTitle(title);
    article.setContent(content);
    article.setCreateDate(LocalDateTime.now());
    article.setMember(member);
    article.setMovieInfo(movieInfo);
    this.articleRepository.save(article);
  }
  public List<Article> list(){
    return this.articleRepository.findAll();
  }


  public Page<Article> getListByMovieInfo(Integer id, int page){
    Pageable pageable = PageRequest.of(page, 10);
    return this.articleRepository.findByMovieInfoId(id, pageable);
  }

  public void modify(Article article, String title, String content) {
    article.setTitle(title);
    article.setContent(content);
    this.articleRepository.save(article);
  }

  public void delete(Article article) {
    this.articleRepository.delete(article);

  }


  public Article getArticle(Integer id) {
    Optional<Article> article = this.articleRepository.findById(id);
    if (article.isPresent()) {
      Article article1 = article.get();

      Integer currentViews = article1.getViews();
      if (currentViews == null) {
        currentViews = 0;
      }
      currentViews++;
      article1.setViews(currentViews);
      articleRepository.save(article1); // 조회수가 증가한 업데이트된 공지사항을 저장

      return article1;
    } else {
      throw new DataNotFoundException("Article not found");
    }
  }


}
