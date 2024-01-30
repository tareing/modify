package com.korea.Team5.board;

import com.korea.Team5.Comment.CommentService;
import com.korea.Team5.USER.Member;
import com.korea.Team5.USER.MemberService;
import com.korea.Team5.board.article.Article;
import com.korea.Team5.board.article.ArticleForm;
import com.korea.Team5.board.article.ArticleService;
import com.korea.Team5.kmapi.dto.MovieInfoDto;
import com.korea.Team5.movie.MovieService;
import com.korea.Team5.movie.entity.Genre;
import com.korea.Team5.movie.entity.MovieInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {


    private final MovieService movieService;
    private final BoardService boardService;
    private final MemberService memberService;
    private final ArticleService articleService;


    @GetMapping("/movie")
    public String list(Model model) {
        List<MovieInfo> movieInfoList = this.movieService.infoList();
        model.addAttribute("movieInfoList", movieInfoList);
        List<Genre> genreList = this.movieService.genreList();
        model.addAttribute("genreList", genreList);
        return "boardList";
    }


    @GetMapping("/article/list/{id}")
    public String articleList(Model model, @PathVariable("id") Integer id, int page) {
        Page<Article> articleList = this.articleService.getListByMovieInfo(id, page);
        MovieInfo movieInfo = this.movieService.getMovieInfo(id);
        model.addAttribute("movieInfo", movieInfo);
        model.addAttribute("articles", articleList);
        return "articleList";
    }

    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasRole('USER'))")
    public String articlecreate(ArticleForm articleForm, @RequestParam Integer id, Model model) {
        MovieInfo movieInfo = this.movieService.getMovieInfo(id);


        return "articleCreate";
    }

    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasRole('USER'))")
    public String articleCreate(@Valid ArticleForm articleForm, BindingResult bindingResult, Principal principal, @RequestParam Integer id) {

        if (bindingResult.hasErrors()) {
            return "articleCreate";
        }
        MovieInfo movieInfo = movieService.getMovieInfo(id);
        Member member = memberService.getMember(principal.getName());
        this.articleService.create(articleForm.getTitle(), articleForm.getContent(), member, movieInfo);
        return "redirect:/board/article/list/" + id;
    }

    @GetMapping("/article/detail/{id}")
    public String articledetail(@PathVariable("id") Integer id, Model model) {
        Article article = this.articleService.getArticle(id);
        model.addAttribute("article", article);
        return "articleDetail";
    }



  @GetMapping("/article/modify/{id}")
  @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasRole('USER'))")
  public String articleModify(ArticleForm articleForm, @PathVariable("id") Integer id, Principal principal,Model model,@RequestParam Integer boardId) {
    Article article = this.articleService.getArticle(id);
    if (!article.getMember().getLoginId().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이없습니다.");
    }
    Board board = this.boardService.getBoard(boardId);
    model.addAttribute("board",board);
    articleForm.setTitle(article.getTitle());
    articleForm.setContent(article.getContent());
    return "articleCreate";
  }

  @PostMapping("/article/modify/{id}")
  @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasRole('USER'))")
  public String articleModify(@Valid ArticleForm articleForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id,@RequestParam Integer boardId) {
    if (bindingResult.hasErrors()) {
      return "articleCreate";
    }
    Article article = this.articleService.getArticle(id);
    if (!article.getMember().getLoginId().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이없습니다.");
    }
    this.articleService.modify(article, articleForm.getTitle(), articleForm.getContent());
    return String.format("redirect:/board/article/list/" + boardId);

  }

  @GetMapping("/article/delete/{id}")
  @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasRole('USER'))")
  public String articleDelete(Principal principal,@PathVariable("id") Integer id ,@RequestParam String boardId){

    Article article = this.articleService.getArticle(id);
    if(!article.getMember().getLoginId().equals(principal.getName())){

      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권환이 없습니다.");
    }

    this.articleService.delete(article);

    return "redirect:/board/article/list/" + boardId;
  }


}

