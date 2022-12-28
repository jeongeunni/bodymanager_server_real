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
import org.json.JSONArray;
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
    public String register(FoodRequestDTO foodRequestDTO) {
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


        return object.toString();
    }

    @Override
    public String modify(FoodModifyRequestDTO foodDTO) {

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


        return object.toString();

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
        String[] a = {"breakfast", "lunch", "dinner"};

        QFood food = QFood.food;
        QMember member = QMember.member;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        StringTemplate dateFormat = Expressions.stringTemplate("DATE_FORMAT( {0}, {1} )", food.created_at, ConstantImpl.create("%Y-%m-%d"));
        StringTemplate timeFormat = Expressions.stringTemplate("DATE_FORMAT( {0}, {1} )", food.created_at, ConstantImpl.create("%H:%i %p"));

        JSONObject object = null;

        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject obj = new JSONObject();

        for(int i=0;i<a.length;i++) {
            List<Tuple> readfood = queryFactory
                    .select(food.food_id, food.food_img, food.content, timeFormat, food.grade)
                    .from(food)
                    .leftJoin(member).on(food.member.member_id.eq(member.member_id))
                    .where(dateFormat.eq(date).and(food.member.member_id.eq(member_id)).and(food.time.eq(a[i])))
                    .fetch();


            if (readfood.isEmpty()) {
                object = new JSONObject();
                data.put(a[i], obj);
            }
            else {
                for (int j = 0; j < readfood.size(); j++) {

                    object = new JSONObject();
                    object.put("id", readfood.get(j).toArray()[0]);
                    object.put("photo", readfood.get(j).toArray()[1].toString());
                    object.put("content", readfood.get(j).toArray()[2].toString());
                    object.put("created_at", readfood.get(j).toArray()[3].toString());
                    object.put("grade", readfood.get(j).toArray()[4]);

                    data.put(a[i], object);
                }
            }

            jsonObject.put("message", "ok");
            jsonObject.put("data", data);
        }

        return jsonObject.toString();


    }

}
