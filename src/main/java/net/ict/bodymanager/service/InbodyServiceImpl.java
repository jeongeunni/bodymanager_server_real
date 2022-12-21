package net.ict.bodymanager.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.controller.TokenHandler;
import net.ict.bodymanager.controller.dto.InbodyDTO;
import net.ict.bodymanager.controller.dto.InbodyRequestDTO;
import net.ict.bodymanager.entity.Inbody;
import net.ict.bodymanager.entity.Member;
import net.ict.bodymanager.entity.QInbody;
import net.ict.bodymanager.filter.JwtTokenProvider;
import net.ict.bodymanager.repository.InbodyRepository;
import net.ict.bodymanager.repository.MemberRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
//@RequiredArgsConstructor
@Transactional
@Log4j2
public class InbodyServiceImpl extends QuerydslRepositorySupport implements InbodyService {

  public InbodyServiceImpl(InbodyRepository inbodyRepository, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, ModelMapper modelMapper, TokenHandler tokenHandler) {
    super(Inbody.class);
    this.inbodyRepository = inbodyRepository;
    this.memberRepository = memberRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.tokenHandler = tokenHandler;
  }

  private final InbodyRepository inbodyRepository;
  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final TokenHandler tokenHandler;

  @Autowired
  EntityManager entityManager;

  @Override
  public String musclePart(InbodyRequestDTO inbodyRequestDTO) {

/*    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();
    log.info("request.getCookies()" + request.getCookies());
    log.info("request.getHeaderNames()" + request.getHeaderNames());
    String token = jwtTokenProvider.resolveToken(request);
    String email = jwtTokenProvider.getUserPk(token);
    Member member_find = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("정보 없음"));
    Long member_id = member_find.getMember_id();
    System.out.println("+++++++++++++++++++++++++++++++++++memeber : " + member_id);  //20 이런식으러

    Member member = memberRepository.getById(member_id);*/

    Long member_id = tokenHandler.getIdFromToken();
    Member member = memberRepository.getById(member_id);

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
    QInbody inbody = QInbody.inbody;

    List<Tuple> muscleList = null;

    if (inbodyRequestDTO.getDate().size() == 2) {
      muscleList = jpaQueryFactory.select(inbody.created_at, inbody.body_muscle, inbody.right_hand_muscle, inbody.left_hand_muscle, inbody.right_leg_muscle, inbody.left_leg_muscle)
              .from(inbody).where(inbody.member.member_id.eq(member.getMember_id()).and(inbody.created_at.eq(LocalDate.parse(inbodyRequestDTO.getDate().get(0)))).or(inbody.created_at.eq(LocalDate.parse(inbodyRequestDTO.getDate().get(1))))).fetch();
    } else {
      muscleList = jpaQueryFactory.select(inbody.created_at, inbody.body_muscle, inbody.right_hand_muscle, inbody.left_hand_muscle, inbody.right_leg_muscle, inbody.left_leg_muscle)
              .from(inbody).where(inbody.member.member_id.eq(member.getMember_id()).and(inbody.created_at.eq(LocalDate.parse(inbodyRequestDTO.getDate().get(0))))).fetch();
    }

    String[] subjectName = {"body", "right_hand", "left_hand", "right_leg", "left_leg"};
    JSONArray jsonArray = new JSONArray();

    for (int i = 0; i < subjectName.length; i++) {
      JSONObject value = new JSONObject();
      value.put("subject", subjectName[i]);

      for (int j = 0; j < muscleList.size(); j++) {
        value.put(muscleList.get(j).toArray()[0].toString(), muscleList.get(j).toArray()[i + 1]);
      }
      jsonArray.put(value);
    }

    JSONObject valueArr = new JSONObject();
    valueArr.put("value", jsonArray);

    JSONObject dataMuscle = new JSONObject();
    dataMuscle.put("message", "ok");
    dataMuscle.put("data", valueArr);

    return dataMuscle.toString();
  }

  @Override
  public String fatPart(InbodyRequestDTO inbodyRequestDTO) {

/*
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();

    String token = jwtTokenProvider.resolveToken(request);
    String email = jwtTokenProvider.getUserPk(token);
    Member member_find = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("정보 없음"));
    Long member_id = member_find.getMember_id();
    System.out.println("+++++++++++++++++++++++++++++++++++memeber : " + member_id);  //20 이런식으러

    Member member = memberRepository.getById(member_id);
*/

    Long member_id = tokenHandler.getIdFromToken();
    Member member = memberRepository.getById(member_id);

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
    QInbody inbody = QInbody.inbody;

    List<Tuple> fatList = null;

    if (inbodyRequestDTO.getDate().size() == 2) {
      fatList = jpaQueryFactory.select(inbody.created_at, inbody.body_fat, inbody.right_hand_fat, inbody.left_hand_fat, inbody.right_leg_fat, inbody.left_leg_fat)
              .from(inbody).where(inbody.member.member_id.eq(member.getMember_id()).and(inbody.created_at.eq(LocalDate.parse(inbodyRequestDTO.getDate().get(0)))).or(inbody.created_at.eq(LocalDate.parse(inbodyRequestDTO.getDate().get(1))))).fetch();
    } else {
      fatList = jpaQueryFactory.select(inbody.created_at, inbody.body_fat, inbody.right_hand_fat, inbody.left_hand_fat, inbody.right_leg_fat, inbody.left_leg_fat)
              .from(inbody).where(inbody.member.member_id.eq(member.getMember_id()).and(inbody.created_at.eq(LocalDate.parse(inbodyRequestDTO.getDate().get(0))))).fetch();
    }

    String[] subjectName = {"body", "right_hand", "left_hand", "right_leg", "left_leg"};
    JSONArray jsonArray = new JSONArray();

    for (int i = 0; i < subjectName.length; i++) {
      JSONObject value = new JSONObject();
      value.put("subject", subjectName[i]);

      for (int j = 0; j < fatList.size(); j++) {

        value.put(fatList.get(j).toArray()[0].toString(), fatList.get(j).toArray()[i + 1]);
      }
      jsonArray.put(value);
    }

    JSONObject valueArr = new JSONObject();
    valueArr.put("value", jsonArray);

    JSONObject dataFat = new JSONObject();
    dataFat.put("message", "ok");
    dataFat.put("data", valueArr);

    return dataFat.toString();
  }

  @Override
  public String inbodyInfo() {

/*    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();

    String token = jwtTokenProvider.resolveToken(request);
    String email = jwtTokenProvider.getUserPk(token);
    Member member_find = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("정보 없음"));
    Long member_id = member_find.getMember_id();
    System.out.println("+++++++++++++++++++++++++++++++++++memeber : " + member_id);  //20 이런식으러

    Member member = memberRepository.getById(member_id);*/

    Long member_id = tokenHandler.getIdFromToken();
    Member member = memberRepository.getById(member_id);

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
    QInbody inbody = QInbody.inbody;


    List<Tuple> inbodyList = jpaQueryFactory.select(inbody.weight, inbody.SMM, inbody.BFM, inbody.BMI, inbody.PBF, inbody.WHR, inbody.BMR)
            .from(inbody).where(inbody.member.member_id.eq(member.getMember_id())).fetch();

    String[] name = {"weight", "SMM", "BFM", "BMI", "PBF", "WHR", "BMR"};

    JSONObject value = new JSONObject();
    for (int i = 0; i < name.length; i++) {
      value.put(name[i], inbodyList.get(0).toArray()[i]);
    }

    JSONObject data = new JSONObject();
    data.put("data", value);
    data.put("message", "ok");

    return data.toString();
  }

  @Override
  public String bodyChangeFlow() {
          Long member_id = tokenHandler.getIdFromToken();
          if(member_id == null){
            JSONObject data = new JSONObject();
            data.put("message", "insufficuent request data");
            return data.toString();
          }
          Member member = memberRepository.getById(member_id);



          JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
          QInbody inbody = QInbody.inbody;

          List<Tuple> changeList = jpaQueryFactory.select(inbody.created_at, inbody.weight, inbody.SMM, inbody.BFM).from(inbody).where(inbody.member.member_id.eq(member.getMember_id())).fetch();

          String[] name = {"name", "weight", "SMM", "BFM"};

          JSONArray valueArr = new JSONArray();

          for (int i = 0; i < changeList.size(); i++) {
            JSONObject value = new JSONObject();
            for (int j = 0; j < name.length; j++) {
              value.put(name[j], changeList.get(i).toArray()[j]);
            }
            valueArr.put(value);
          }
          JSONObject data = new JSONObject();
          data.put("data", valueArr);
          data.put("message", "ok");
          return data.toString();
  }

  @Override
  public void register(InbodyDTO inbodyDTO) {

    Long member_id = tokenHandler.getIdFromToken();
    Member member = memberRepository.getById(member_id);

    log.info(member_id);
    log.info(member.getMember_id());
/*    // 로그인한 회원 정보 가져오기
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();

    String token = jwtTokenProvider.resolveToken(request);
    String email = jwtTokenProvider.getUserPk(token);
    Member member_find = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("정보 없음"));
    Long member_id = member_find.getMember_id();
    System.out.println("+++++++++++++++++++++++++++++++++++memeber : " + member_id);  //20 이런식으러

    Member member = memberRepository.getById(member_id);*/

    Inbody inbody = dtoTOEntity(inbodyDTO, member);
    inbodyRepository.save(inbody);
  }

  // ??
  @Override
  public void modify(InbodyDTO inbodyDTO) {
/*    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();

    String token = jwtTokenProvider.resolveToken(request);
    String email = jwtTokenProvider.getUserPk(token);
    Member member_find = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("정보 없음"));
    Long member_id = member_find.getMember_id();
    System.out.println("+++++++++++++++++++++++++++++++++++memeber : " + member_id);  //20 이런식으러

    Member member = memberRepository.getById(member_id);*/

    Long member_id = tokenHandler.getIdFromToken();
    Member member = memberRepository.getById(member_id);

    inbodyRepository.inbodyUpdate(inbodyDTO.getWeight(), inbodyDTO.getSMM(), inbodyDTO.getBFM(), inbodyDTO.getBMI(), inbodyDTO.getPBF(), inbodyDTO.getWHR(), inbodyDTO.getBMR(),
            inbodyDTO.getBody_muscle(), inbodyDTO.getLeft_hand_muscle(), inbodyDTO.getRight_hand_muscle(), inbodyDTO.getLeft_leg_muscle(), inbodyDTO.getRight_leg_muscle(),
            inbodyDTO.getBody_fat(), inbodyDTO.getLeft_hand_fat(), inbodyDTO.getRight_hand_fat(), inbodyDTO.getLeft_leg_fat(), inbodyDTO.getRight_leg_fat(),
            member.getMember_id(), LocalDate.now());
  }

  @Override
  public String inbodyList() {

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
    QInbody inbodyList = QInbody.inbody;

/*    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();

    String token = jwtTokenProvider.resolveToken(request);
    String email = jwtTokenProvider.getUserPk(token);
    Member member_find = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("정보 없음"));
    Long member_id = member_find.getMember_id();
    System.out.println("+++++++++++++++++++++++++++++++++++memeber : " + member_id);

    Member member = memberRepository.getById(member_id);*/


    Long member_id = tokenHandler.getIdFromToken();
    Member member = memberRepository.getById(member_id);

    // 로그인 아이디
    List<Tuple> inbodySelect = jpaQueryFactory.select(inbodyList.weight, inbodyList.SMM, inbodyList.BFM, inbodyList.BMI, inbodyList.PBF, inbodyList.WHR, inbodyList.BMR, inbodyList.body_muscle, inbodyList.left_hand_muscle, inbodyList.right_hand_muscle, inbodyList.left_leg_muscle, inbodyList.right_leg_muscle, inbodyList.body_fat, inbodyList.left_hand_fat ,inbodyList.right_hand_fat, inbodyList.left_leg_fat, inbodyList.right_leg_fat).from(inbodyList).where(inbodyList.member.member_id.eq(member.getMember_id()).and(inbodyList.created_at.eq(LocalDate.now()))).fetch();

    String [] name = {"weight", "SMM", "BFM", "BMI", "PBF", "WHR", "BMR", "body_muscle", "left_hand_muscle", "right_hand_muscle", "left_leg_muscle", "right_leg_muscle", "body_fat", "left_hand_fat", "right_hand_fat", "left_leg_fat", "right_leg_fat"};

    JSONObject inbodyData = new JSONObject();
    JSONObject data = new JSONObject();
    if (inbodySelect.isEmpty()){
      data.put("message", "not data");
    } else {
      for (int i = 0 ; i < name.length ; i++){
        inbodyData.put(name[i], inbodySelect.get(0).toArray()[i]);
      }
      data.put("data", inbodyData);
      data.put("message", "ok");
    }

    return data.toString();
  }

  @Override
  public List<LocalDate> check() {
/*    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();

    String token = jwtTokenProvider.resolveToken(request);
    String email = jwtTokenProvider.getUserPk(token);
    Member member_find = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("정보 없음"));
    Long member_id = member_find.getMember_id();
    System.out.println("+++++++++++++++++++++++++++++++++++memeber : " + member_id);  //20 이런식으러

    Member member = memberRepository.getById(member_id);*/

    Long member_id = tokenHandler.getIdFromToken();
    Member member = memberRepository.getById(member_id);

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
    QInbody inbody = QInbody.inbody;
    List<LocalDate> checkList = jpaQueryFactory.select(inbody.created_at).from(inbody).where(inbody.member.member_id.eq(member.getMember_id()).and(inbody.created_at.eq(LocalDate.now()))).fetch();

    log.info(checkList);
    return checkList;
  }


}
