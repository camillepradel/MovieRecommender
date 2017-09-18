package com.camillepradel.movierecommender.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMethod;

import com.camillepradel.movierecommender.model.Movie;
import com.camillepradel.movierecommender.model.Rating;
import com.camillepradel.movierecommender.model.db.AbstractDatabase;
import com.camillepradel.movierecommender.model.db.Neo4jDatabase;
import javax.annotation.PostConstruct;

@Controller
public class MainController {

    AbstractDatabase db;

    @PostConstruct
    public void init() {
        this.db = new Neo4jDatabase();
    }
    
    @RequestMapping("/hello")
    public ModelAndView showMessage(
            @RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        ModelAndView mv = new ModelAndView("helloworld");
        String message = "Looks like it's working!";
        mv.addObject("message", message);
        mv.addObject("name", name);
        return mv;
    }

    @RequestMapping("/movies")
    public ModelAndView showMovies(
            @RequestParam(value = "user_id", required = false) Integer userId) {
        System.out.println("show Movies of user " + userId);

        List<Movie> movies = userId != null ? db.getMoviesRatedByUser(userId) : db.getAllMovies();

        ModelAndView mv = new ModelAndView("movies");
        mv.addObject("userId", userId);
        mv.addObject("movies", movies);
        return mv;
    }

    @RequestMapping(value = "/movieratings", method = RequestMethod.GET)
    public ModelAndView showMoviesRattings(
            @RequestParam(value = "user_id", required = true) Integer userId) {
        System.out.println("GET /movieratings for user " + userId);

        List<Movie> allMovies = db.getAllMovies();
        List<Rating> ratings = db.getRatingsFromUser(userId);

        ModelAndView mv = new ModelAndView("movieratings");
        mv.addObject("userId", userId);
        mv.addObject("allMovies", allMovies);
        mv.addObject("ratings", ratings);

        return mv;
    }

    @RequestMapping(value = "/movieratings", method = RequestMethod.POST)
    public String saveOrUpdateRating(@ModelAttribute("rating") Rating rating) {
        System.out.println("POST /movieratings for user " + rating.getUserId()
                + ", movie " + rating.getMovie().getId()
                + ", score " + rating.getScore());

        db.addOrUpdateRating(rating);

        return "redirect:/movieratings?user_id=" + rating.getUserId();
    }

    @RequestMapping(value = "/recommendations", method = RequestMethod.GET)
    public ModelAndView ProcessRecommendations(
            @RequestParam(value = "user_id", required = true) Integer userId,
            @RequestParam(value = "processing_mode", required = false, defaultValue = "0") Integer processingMode) {
        System.out.println("GET /recommendations for user " + userId + " with processingMode " + processingMode);

        List<Rating> recommendations = db.processRecommendationsForUser(userId, processingMode);

        ModelAndView mv = new ModelAndView("recommendations");
        mv.addObject("recommendations", recommendations);

        return mv;
    }
}
