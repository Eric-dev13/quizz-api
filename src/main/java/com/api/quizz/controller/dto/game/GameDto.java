package com.api.quizz.controller.dto.game;

import com.api.quizz.controller.dto.category.CategoryDto;
import com.api.quizz.controller.dto.player.PlayerDto;
import com.api.quizz.controller.dto.question.QuestionDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GameDto {
    private Long id;
    private LocalDateTime createdAt;
    private Long score;
    private Long playerId;
    private PlayerDto player;
    private String title;
    private List<CategoryDto> categories = new ArrayList<>();
    private List<QuestionDto> questions = new ArrayList<>();

    public GameDto (String title, PlayerDto player, List<CategoryDto> categories, List<QuestionDto> questions) {
        this.title =title;
        this.player = player;
        this.categories = categories;
        this.questions = questions;
    }

    public GameDto (String title, long playerId, List<CategoryDto> categories, List<QuestionDto> questions) {
        this.title =title;
        this.playerId = playerId;
        this.categories = categories;
        this.questions = questions;
    }

}
