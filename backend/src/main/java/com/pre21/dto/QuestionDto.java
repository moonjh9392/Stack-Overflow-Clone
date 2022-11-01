package com.pre21.dto;

import com.pre21.entity.Answers;
import com.pre21.entity.QuestionsTags;
import com.pre21.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionDto {


    @Getter
    @AllArgsConstructor
    public static class Like {
        private Long userId;
        private boolean likeYn;
        private boolean unlikeYn;
        private int count;
    }


    // 질문 조회 ResponseDto
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetResponseDto {
        private Long questionId;    // 질문 Id
        private String title;   // 질문 제목
        private String contents;    // 질문 내용
        private List<QuestionsTagsResponseDto> questionsTags;   // 질문에 사용한 태그 정보
        private int vote;   // 질문 추천수
        private List<AnswersDto.ResponseDto> answers;   // 질문에 달린 답글 정보
        private int views;  // 질문 조회수
        private LocalDateTime createdAt;    // 질문 생성 일자
        private String nickName;    // 질문을 생성한 유저 닉네임
    }

    // 질문 전체 조회
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetResponseDtos {
        private Long questionId;
        private String title;
        private String contents;
        //private List<String> strTags;
        //private List<QuestionsTags> questionsTags;
        private int vote;
        private boolean chooseYn;
        private int views;
        private LocalDateTime createdAt;
        private int AnswersCount;   // 질문에 달린 답변 개수
    }
}
