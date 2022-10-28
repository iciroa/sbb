package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    //@Test
    void testJpa() {
        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해서 알고 싶습니다.");
        q1.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q1);

        Question q2 = new Question();
        q2.setSubject("스프링부트 모델 질문입니다.");
        q2.setContent("id는 자동으로 생성되나요?");
        q2.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q2);


    }

    @Test
    void testJpaRead() {
        List<Question> all = this.questionRepository.findAll();
        Assertions.assertThat(2).isEqualTo(all.size());

        Question q = all.get(0);
        Assertions.assertThat("sbb가 무엇인가요?").isEqualTo(q.getSubject());
    }

    @Test
    void findBySubjectTest() {
        Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
        Assertions.assertThat(1).isEqualTo(q.getId());
    }

    @Test
    void findBySubjectAndContentTest() {
        Question q = this.questionRepository.findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
        Assertions.assertThat(1).isEqualTo(q.getId());
    }

    @Test
    void findBySubjectLikeTest() {
        List<Question> qList = this.questionRepository.findBySubjectLike("sbb가 무엇인가요%");
        Assertions.assertThat(1).isEqualTo(qList.size());
    }

    @Test
    @DisplayName("제목 바꾸기")
    void changeSubjectTest() {
        Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
        q.setSubject("sbb가 진짜 무엇인가요?");
        this.questionRepository.save(q);

        Question q2 = this.questionRepository.findBySubject("sbb가 진짜 무엇인가요?");
        Assertions.assertThat(1).isEqualTo(q2.getId());

    }

    @Test
    @DisplayName("삭제")
    void deleteTest() {
        //Assertions.assertThat(2).isEqualTo(this.questionRepository.count());
        Optional<Question> oq = this.questionRepository.findById(1);
        Assertions.assertThat(oq.isPresent()).isTrue();

        Question q = oq.get();
        this.questionRepository.delete(q);
        Assertions.assertThat(1).isEqualTo(this.questionRepository.count());
    }

    @Test
    @DisplayName("답변생성")
    void insertAnswer() {
        Optional<Question> oq = this.questionRepository.findById(2);
        Assertions.assertThat(oq.isPresent()).isTrue();
        Question q = oq.get();

        Answer a = new Answer();
        a.setContent("네, 자동으로 생성됩니다.");
        a.setQuestion(q);
        a.setCreateDate(LocalDateTime.now());

        this.answerRepository.save(a);

        Optional<Answer> oa = this.answerRepository.findById(1);
        Assertions.assertThat(oa.isPresent()).isTrue();
        Answer a1 = oa.get();
        Assertions.assertThat(2).isEqualTo(a1.getQuestion().getId());

    }

    @Test
    @DisplayName("연계")
    @Transactional
    void searchCascade() {
        Optional<Question> oq = this.questionRepository.findById(2);
        Assertions.assertThat(oq.isPresent()).isTrue();
        Question q = oq.get();

        List<Answer> answerList = q.getAnswerList();

        Assertions.assertThat(1).isEqualTo(answerList.size());
        Assertions.assertThat("네, 자동으로 생성됩니다.").isEqualTo(answerList.get(0).getContent());

    }
}
