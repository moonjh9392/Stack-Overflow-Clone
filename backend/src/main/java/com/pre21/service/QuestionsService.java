package com.pre21.service;

import com.pre21.dto.QuestionPatchDto;
import com.pre21.dto.QuestionsPostDto;
import com.pre21.entity.*;
import com.pre21.exception.BusinessLogicException;
import com.pre21.exception.ExceptionCode;
import com.pre21.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionsService {
    private final QuestionsRepository questionsRepository;
    private final QuestionsTagsRepository questionsTagsRepository;
    private final TagsRepository tagsRepository;
    private final UserRepository userRepository;
    private final AnswersRepository answersRepository;
    private final AdoptionRepository adoptionRepository;
    private final BookmarkRepository bookmarkRepository;


    // 질문 생성
    public void createQuestion(QuestionsPostDto questionsPostDto,
                               Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        Questions questions = new Questions(questionsPostDto.getTitle(), questionsPostDto.getContents());
        List<String> tags = questionsPostDto.getTags();

        tags.forEach(
                e -> {
                    if (tagsRepository.findByTitle(e).isEmpty()) {
                        Tags tags1 = tagsRepository.save(new Tags(e));
                        QuestionsTags questionsTags = new QuestionsTags(questions, tags1);
                        questionsTagsRepository.save(questionsTags);
                        questions.addQuestionsTags(questionsTags);
                        // questionsRepository.save(questions);
                        findUser.addQuestion(questions);
                        questions.addUser(findUser);

                    } else {
                        Tags tags1 = tagsRepository.findByTitle(e).orElseThrow(IllegalArgumentException::new);
                        updateTagCount(tags1);
                        QuestionsTags questionsTags = new QuestionsTags(questions, tags1);
                        questionsTagsRepository.save(questionsTags);
                        questions.addQuestionsTags(questionsTags);
                        findUser.addQuestion(questions);
                        questions.addUser(findUser);
                    }
                }
        );
    }

    // 질문 조회
    public Questions findQuestion(Long questionId) {
        Questions findQuestion = verifiedExistQuestion(questionId);

        return findQuestion;
    }

    // 질문 전체 조회
    public List<Questions> findQuestions() {

        return (List<Questions>) questionsRepository.findAll();
    }

    public Page<Questions> findPageQuestions(int page, int size) {

        return questionsRepository.findAll(PageRequest.of(page, size,
                Sort.by("id").descending()));
    }

    // 질문 전체 개수 출력
    public long findQuestionCount() {

        return questionsRepository.count();
    }

    // 질문 삭제
    public void deleteQuestion(long questionId) throws Exception {
        Questions findQuestion = verifiedExistQuestion(questionId);

        questionsRepository.delete(findQuestion);

    }

    // 태그 수 업데이트
    private void updateTagCount(Tags tags) {

        int earnedTagCount = tags.getCount() + 1;
        tags.setCount(earnedTagCount);
        tags.setLatest(LocalDateTime.now());

        tagsRepository.save(tags);
    }


    /**
     * @param questionId : 질문식별자
     * @param answerId   : 유저식별자
     * @param userId     : 유저식별자
     * @method : 질문 채택 여부 반영
     * @author mozzi327
     */
    public void adoptingQuestion(Long questionId,
                                 Long answerId,
                                 Long userId) {
        Questions findQuestion = verifiedExistQuestion(questionId);
        if (!Objects.equals(findQuestion.getUsers().getId(), userId))
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_USER);
        if (findQuestion.isChooseYn()) throw new BusinessLogicException(ExceptionCode.ALREADY_ADOPTED);
        findQuestion.setChooseYn(true);
        questionsRepository.save(findQuestion);
        Answers findAnswer = verifiedExistAnswer(answerId);
        findAnswer.setChooseYn(true);
        //answersRepository.save(findAnswer);
        User findUser = verifiedExistUser(userId);
        Adoption adoption = new Adoption();
        adoption.setQuestions(findQuestion);
        adoption.setAnswers(findAnswer);
        adoption.setUsers(findUser);
        adoptionRepository.save(adoption);

    }


    /**
     * 질문 patch 요청에 대한 서비스 메서드입니다.
     *
     * @param userId           Long 타입 사용자 Id 값입니다.
     * @param questionId       Long 타입 Question Id 값입니다.
     * @param questionPatchDto QuestionPatchDto 요청입니다.
     * @author dev32user
     */
    public Questions patchQuestion(Long userId, Long questionId, QuestionPatchDto questionPatchDto) {
        if (!Objects.equals(userId, verifiedExistQuestion(questionId).getUsers().getId())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_USER);
        }

        Optional<Questions> optionalQuestion = questionsRepository.findById(questionId);
        User findUser = userRepository
                .findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        Questions updatedQuestion =
                optionalQuestion
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));

        updatedQuestion.setTitle(questionPatchDto.getTitle());
        updatedQuestion.setContents(questionPatchDto.getContents());
        List<String> tags = questionPatchDto.getTags();

        questionsTagsRepository.deleteAllByQuestions(updatedQuestion);

        tags.forEach(
                e -> {
                    if (tagsRepository.findByTitle(e).isEmpty()) {
                        Tags tags1 = tagsRepository.save(new Tags(e));
                        QuestionsTags questionsTags = new QuestionsTags(updatedQuestion, tags1);
                        questionsTagsRepository.save(questionsTags);
                        updatedQuestion.addQuestionsTags(questionsTags);
                        findUser.addQuestion(updatedQuestion);
                        updatedQuestion.addUser(findUser);

                    } else {
                        Tags tags1 = tagsRepository.findByTitle(e).orElseThrow(IllegalArgumentException::new);
                        updateTagCount(tags1);
                        QuestionsTags questionsTags = new QuestionsTags(updatedQuestion, tags1);
                        questionsTagsRepository.save(questionsTags);
                        updatedQuestion.addQuestionsTags(questionsTags);
                        findUser.addQuestion(updatedQuestion);
                        updatedQuestion.addUser(findUser);
                    }
                }
        );

        return questionsRepository.save(updatedQuestion);
    }

    public void addQuestionBookmark(Long questionId, Long userId) {
        User findUser = verifiedExistUser(userId);
        Questions findQuestion = verifiedExistQuestion(questionId);
        Optional<Bookmark> findBookmark = bookmarkRepository.findBookmarksByUsersAndQuestions(findUser, findQuestion);

        if (findBookmark.isPresent()) {
            bookmarkRepository.delete(findBookmark.get());
        } else {
            Bookmark bookmark = new Bookmark(questionId);
            bookmark.setQuestions(findQuestion);
            bookmark.setUsers(findUser);
            bookmarkRepository.save(bookmark);
        }
    }

    public void addAnswerBookmark(Long questionId,  Long answerId, Long userId) {
        User findUser = verifiedExistUser(userId);
        Questions findQuestion = verifiedExistQuestion(questionId);
        Answers findAnswer = verifiedExistAnswer(answerId);
        Optional<Bookmark> findBookmark = bookmarkRepository.findBookmarksByUsersAndQuestionsAndAnswers(findUser, findQuestion, findAnswer);

        if (findBookmark.isPresent()) {
            bookmarkRepository.delete(findBookmark.get());
        } else {
            Bookmark bookmark = new Bookmark(questionId, answerId);
            bookmark.setQuestions(findQuestion);
            bookmark.setUsers(findUser);
            bookmark.setAnswers(findAnswer);
            bookmarkRepository.save(bookmark);
        }
    }



    /**
     * @method 유저 조회
     * @param userId 유저식별자
     * @return User
     * @author mozzi327
     */
    private User verifiedExistUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );
    }


    /**
     * @method 질문 조회
     * @param questionId 질문식별자
     * @return Questions
     * @author mozzi327
     */
    private Questions verifiedExistQuestion(Long questionId) {
        return questionsRepository.findById(questionId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND)
        );
    }


    /**
     * @method 답변 조회
     * @param answerId 답변식별자
     * @return Answers
     * @author mozzi327
     */
    private Answers verifiedExistAnswer(Long answerId) {
        return answersRepository.findById(answerId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND)
        );
    }

    public Page<Questions> findMyQuestions(Long userId, int page, int size) {
        return questionsRepository.findAllByUsersId(
                userId,
                PageRequest.of(page, size, Sort.by("id").descending()));
    }
}
