package net.ict.bodymanager.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import net.ict.bodymanager.controller.TokenHandler;
import net.ict.bodymanager.entity.*;
import net.ict.bodymanager.filter.JwtTokenProvider;
import net.ict.bodymanager.repository.MemberRepository;
import net.ict.bodymanager.repository.MessageRepository;
import net.ict.bodymanager.repository.MessageRoomRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Log4j2
public class MessageServiceImpl implements MessageService {

  private final MemberRepository memberRepository;
  private final MessageRoomRepository messageRoomRepository;
  private final MessageRepository messageRepository;
  private final TokenHandler tokenHandler;

  public MessageServiceImpl(MemberRepository memberRepository, MessageRoomRepository messageRoomRepository, MessageRepository messageRepository, JwtTokenProvider jwtTokenProvider, TokenHandler tokenHandler) {
    this.memberRepository = memberRepository;
    this.messageRoomRepository = messageRoomRepository;
    this.messageRepository = messageRepository;
    this.tokenHandler = tokenHandler;
  }

  @Autowired
  EntityManager entityManager;


  @Override
  public String room_list() {

    Long member_id = tokenHandler.getIdFromToken();
    log.info("member_id" + member_id);
    if (member_id == null) {
      JSONObject data = new JSONObject();
      data.put("message", "insufficuent request data");
      return data.toString();
    }
    Optional<Member> id = memberRepository.findById(member_id);

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
    QMessageRoom messageRoom = QMessageRoom.messageRoom;
    QMember member = QMember.member;

    // 로그인 - 회원일 경우 - receiver 가 트레이너
    List<Tuple> roomListTrainer = jpaQueryFactory.select(messageRoom.roomId, messageRoom.trainerId.name, member.profile)
            .from(messageRoom)
//                .join(messageRoom.memberId, member)
            .join(messageRoom.trainerId, member)
            .where(messageRoom.memberId.member_id.eq(id.get().getMember_id()))
            .fetch();

    // 로그인 - 트레이너일 경우 - receiver 가 회원
    List<Tuple> roomListMem = jpaQueryFactory.select(messageRoom.roomId, messageRoom.memberId.name, member.profile)
            .from(messageRoom)
            .join(messageRoom.memberId, member)
            .where(messageRoom.trainerId.member_id.eq(id.get().getMember_id()))
            .fetch();

    JSONArray array = new JSONArray();
    JSONObject object = null;
    JSONObject data = null;

    // 타입이 0일 경우 일반 회원
    if (id.get().getType().equals("user")) {
      for (int i = 0; i < roomListTrainer.size(); i++) {
        object = new JSONObject();
        object.put("room_id", roomListTrainer.get(i).toArray()[0]);
        object.put("receiver_name", roomListTrainer.get(i).toArray()[1]);
        object.put("receiver_profile", roomListTrainer.get(i).toArray()[2]);
        array.put(object);
      }
      data = new JSONObject();
      data.put("data", array);
      data.put("message", "ok");
    } else {
      for (int i = 0; i < roomListMem.size(); i++) {
        object = new JSONObject();
        object.put("room_id", roomListMem.get(i).toArray()[0]);
        object.put("receiver_name", roomListMem.get(i).toArray()[1]);
        object.put("receiver_profile", roomListMem.get(i).toArray()[2]);
        array.put(object);
      }
      data = new JSONObject();
      data.put("data", array);
      data.put("message", "ok");
    }


    return data.toString();
  }


  @Override
  public String people_list() {

    Long member_id = tokenHandler.getIdFromToken();
    log.info("member_id" + member_id);
    if (member_id == null) {
      JSONObject data = new JSONObject();
      data.put("message", "insufficuent request data");
      return data.toString();
    }
    Optional<Member> id = memberRepository.findById(member_id);

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
    QPTMember ptMember = QPTMember.pTMember;
    QMessageRoom messageRoom = QMessageRoom.messageRoom;
    QMember member = QMember.member;
    QPTInfo ptInfo = QPTInfo.pTInfo;

    // 로그인 - 회원일 경우 - 상대가 트레이너
    List<Tuple> trainerList = jpaQueryFactory.select(member.member_id, member.name, member.profile)
            .from(member)
            .where(member.type.eq("user"))
            .fetch();

    List<Tuple> memberList = jpaQueryFactory.select(member.member_id, member.name, member.profile)
            .from(member)
            .where(member.member_id
                    .in(jpaQueryFactory.select(ptMember.member.member_id).from(ptMember).where(ptMember.ptInfo.pt_id
                            .in(jpaQueryFactory.select(ptInfo.pt_id).from(ptInfo).where(ptInfo.trainer.member_id.eq(id.get().getMember_id())))))).fetch();


    JSONArray array = new JSONArray();
    JSONObject object = null;
    JSONObject data = null;

    // 타입이 0일 경우 일반 회원
    if (id.get().getType().equals("user")) {
      for (int i = 0; i < trainerList.size(); i++) {
        object = new JSONObject();
        object.put("member_id", trainerList.get(i).toArray()[0]);
        object.put("receiver_name", trainerList.get(i).toArray()[1]);
        object.put("receiver_profile", trainerList.get(i).toArray()[2]);
        array.put(object);
      }
      data = new JSONObject();
      data.put("data", array);
      data.put("message", "ok");
    } else {
      for (int i = 0; i < memberList.size(); i++) {
        object = new JSONObject();
        object.put("member_id", memberList.get(i).toArray()[0]);
        object.put("receiver_name", memberList.get(i).toArray()[1]);
        object.put("receiver_profile", memberList.get(i).toArray()[2]);
        array.put(object);
      }
      data = new JSONObject();
      data.put("data", array);
      data.put("message", "ok");
    }

    return data.toString();
  }

  @Override
  public void roomCreate(String receiverId) {

    Long member_id = tokenHandler.getIdFromToken();
    Optional<Member> id = memberRepository.findById(member_id);

    Member member = memberRepository.getById(member_id);
    Member receiver = memberRepository.getById(Long.valueOf(receiverId));

    // 로그인한 회원이 일반회원일 경우 - 메시지를 받는 사람은 트레이너
    if (id.get().getType().equals("user")) {
      MessageRoom messageRoom = MessageRoom.builder()
              .createdAt(LocalDateTime.now())
              .memberId(member)   // member 타입으로 넣어줘야함
              .trainerId(receiver)
              .build();
      messageRoomRepository.save(messageRoom);

    } else {
      MessageRoom messageRoom = MessageRoom.builder()
              .createdAt(LocalDateTime.now())
              .memberId(receiver)   // member 타입으로 넣어줘야함
              .trainerId(member)
              .build();
      messageRoomRepository.save(messageRoom);
    }

  }

  @Override
  public String messageList(String room_id, String offset, String limit) {

    QMessage message = QMessage.message;
    QMember member = QMember.member;

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

    List<Tuple> chatList = jpaQueryFactory.select(member.name, member.profile, message.content, message.createdAt)
            .from(message)
            .join(message.member, member)
            .where(message.messageRoom.roomId.eq(Long.valueOf((room_id)))).offset(Long.parseLong(offset)).limit(Long.parseLong(limit))
            .orderBy(message.createdAt.desc())
            .fetch();

    JSONArray array = new JSONArray();
    JSONObject object = null;

    for (int i = 0; i < chatList.size(); i++) {
      object = new JSONObject();
      object.put("member_name", chatList.get(i).toArray()[0]);
      object.put("profile", chatList.get(i).toArray()[1]);
      object.put("contents", chatList.get(i).toArray()[2]);
      object.put("created_at", chatList.get(i).toArray()[3]);
      array.put(object);
    }

    JSONObject data = new JSONObject();
    data.put("data", array);
    data.put("message", "ok");

    log.info(data);
    return data.toString();
  }

  @Override

  public void chatRegister(Long room_id, String content, String type) {
    log.info("챗 리제스터 시작@@@@@@@2");
//    Long member_id = tokenHandler.getIdFromToken();
//    log.info("member_id" + member_id);

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
    QMessageRoom messageRoom = QMessageRoom.messageRoom;
    QMember mem = QMember.member;

    List<Tuple> list = jpaQueryFactory
            .select(messageRoom.memberId.member_id, messageRoom.trainerId.member_id)
            .from(messageRoom)
            .where(messageRoom.roomId.eq(room_id))
            .fetch();
    log.info(list);
    log.info(list.get(0).toArray()[1]);

    if (type.equals("trainer")) {
      Member member = memberRepository.getById(Long.valueOf(list.get(0).toArray()[1].toString()));
      MessageRoom room = messageRoomRepository.getById(room_id);
      Message message = dtoTOEntity(room, content, member);
      messageRepository.save(message);
    } else {
      Member member = memberRepository.getById(Long.valueOf(list.get(0).toArray()[0].toString()));
      MessageRoom room = messageRoomRepository.getById(room_id);
      Message message = dtoTOEntity(room, content, member);
      messageRepository.save(message);

    }

  }

}
