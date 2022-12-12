package net.ict.bodymanager.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.controller.TokenHandler;
import net.ict.bodymanager.dto.FoodModifyRequestDTO;
import net.ict.bodymanager.dto.FoodRequestDTO;
import net.ict.bodymanager.entity.*;
import net.ict.bodymanager.repository.FoodRepository;
import net.ict.bodymanager.repository.MemberRepository;
import net.ict.bodymanager.util.LocalUploader;
import net.ict.bodymanager.util.S3Uploader;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class FoodServiceImpl implements FoodService {
    private final ModelMapper modelMapper;
    private final FoodRepository foodRepository;
    private final MemberRepository memberRepository;
    private final LocalUploader localUploader;
    private final S3Uploader s3Uploader;
    private final TokenHandler tokenHandler;
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void register(FoodRequestDTO foodRequestDTO) {
        Long member_id = tokenHandler.getIdFromToken();
        Member member = memberRepository.getById(member_id);

        Food food = dtoToEntity(foodRequestDTO,member,localUploader,s3Uploader);

        QFood qFood = QFood.food;
        StringTemplate dateFormat = Expressions.stringTemplate("DATE_FORMAT( {0}, {1} )", qFood.created_at, ConstantImpl.create("%Y-%m-%d"));
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<String> registerselect = queryFactory
                .select(qFood.time)
                .from(qFood)
                .where(qFood.member.member_id.eq(member.getMember_id()).and(dateFormat.eq(String.valueOf(LocalDate.now()))).and(qFood.time.eq(foodRequestDTO.getTime())))
                .fetch();

        JSONObject object = new JSONObject();

        if(registerselect.isEmpty()){
            foodRepository.save(food);
            object.put("message","ok");
        }
        else{
            object.put("message","not found");
        }


//        return object.toString();
    }

    @Override
    public void modify(FoodModifyRequestDTO foodDTO) {


        Long member_id = tokenHandler.getIdFromToken();
        Member member = memberRepository.getById(member_id);
        String fileName = fileName(foodDTO,localUploader,s3Uploader);

        QFood qFood = QFood.food;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Long> modifyselect = queryFactory
                .select(qFood.food_id)
                .from(qFood)
                .where(qFood.member.member_id.eq(member.getMember_id()).and(qFood.food_id.eq(foodDTO.getFood_id())))
                .fetch();

//        List<String> timeselect = queryFactory
//                .select(qFood.time)
//                .from(qFood)
//                .where(qFood.member.member_id.eq(member.getMember_id()).and(qFood.time.eq(foodDTO.getTime())))
//                .fetch();

        JSONObject object = new JSONObject();
        if(modifyselect.isEmpty()){
            object.put("message","not found");
        }
        else {

            Optional<Food> result = foodRepository.findById(foodDTO.getFood_id());
            Food food = result.orElseThrow();
            food.change(foodDTO.getContent(), fileName);
            foodRepository.save(food);
            object.put("message","ok");

        }


//        return object.toString();

    }

    @Override
    public String remove(Long food_id) {
        Long member_id = tokenHandler.getIdFromToken();
        Member member = memberRepository.getById(member_id);

        QFood food = QFood.food;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Long> deleteselect = queryFactory
                .select(food.food_id)
                .from(food)
                .where(food.member.member_id.eq(member.getMember_id()).and(food.food_id.eq(food_id)))
                .fetch();

        JSONObject object = new JSONObject();

        if (deleteselect.isEmpty()){
            object.put("message","not found");
        }
        else{

            foodRepository.deleteById(food_id);
            object.put("message","ok");
        }


        return object.toString();
    }


    @Override
    public String readOne(String date) {
        Long member_id = tokenHandler.getIdFromToken();

        log.info("--------------------------------------------------------------------"+member_id);

        QFood food = QFood.food;
        QMember member = QMember.member;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        StringTemplate dateFormat = Expressions.stringTemplate("DATE_FORMAT( {0}, {1} )", food.created_at, ConstantImpl.create("%Y-%m-%d"));
        StringTemplate timeFormat = Expressions.stringTemplate("DATE_FORMAT( {0}, {1} )", food.created_at, ConstantImpl.create("%H:%i %p"));

        List<Tuple> readbreakfast = queryFactory
                .select(food.food_id, food.food_img, food.content, timeFormat, food.grade)
                .from(food)
                .leftJoin(member).on(food.member.member_id.eq(member.member_id))
                .where(dateFormat.eq(date).and(food.member.member_id.eq(member_id)).and(food.time.eq("아침")))
                .fetch();


        List<Tuple> readlunch = queryFactory
                .select(food.food_id, food.food_img, food.content, timeFormat, food.grade)
                .from(food)
                .leftJoin(member).on(food.member.member_id.eq(member.member_id))
                .where(dateFormat.eq(date).and(food.member.member_id.eq(member_id)).and(food.time.eq("점심")))
                .fetch();

        List<Tuple> readdinner = queryFactory
                .select(food.food_id, food.food_img, food.content, timeFormat, food.grade)
                .from(food)
                .leftJoin(member).on(food.member.member_id.eq(member.member_id))
                .where(dateFormat.eq(date).and(food.member.member_id.eq(member_id)).and(food.time.eq("저녁")))
                .fetch();

        JSONObject object = null;

        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();

        if(readbreakfast.isEmpty() && readlunch.isEmpty() && readdinner.isEmpty()){
            jsonObject.put("message", "empty");
            jsonObject.put("data",data);

        }
        else {


            for (int i = 0; i < readbreakfast.size(); i++) {
                object = new JSONObject();
                object.put("id", readbreakfast.get(i).toArray()[0]);
                object.put("photo", readbreakfast.get(i).toArray()[1].toString());
                object.put("content", readbreakfast.get(i).toArray()[2].toString());
                object.put("created_at", readbreakfast.get(i).toArray()[3].toString());
                object.put("grade", readbreakfast.get(i).toArray()[4]);

                data.put("breakfast", object);

                log.info(data);
            }
            for (int i = 0; i < readlunch.size(); i++) {
                object = new JSONObject();
                object.put("id", readlunch.get(i).toArray()[0]);
                object.put("photo", readlunch.get(i).toArray()[1].toString());
                object.put("content", readlunch.get(i).toArray()[2].toString());
                object.put("created_at", readlunch.get(i).toArray()[3].toString());
                object.put("grade", readlunch.get(i).toArray()[4]);

                data.put("lunch", object);
            }
            for (int i = 0; i < readdinner.size(); i++) {
                object = new JSONObject();
                object.put("id", readdinner.get(i).toArray()[0]);
                object.put("photo", readdinner.get(i).toArray()[1].toString());
                object.put("content", readdinner.get(i).toArray()[2].toString());
                object.put("created_at", readdinner.get(i).toArray()[3].toString());
                object.put("grade", readdinner.get(i).toArray()[4]);

                data.put("dinner", object);
            }

            log.info(data);

            jsonObject.put("data", data);
            jsonObject.put("message", "ok");
        }

        log.info(jsonObject);

        return jsonObject.toString();
    }

}
