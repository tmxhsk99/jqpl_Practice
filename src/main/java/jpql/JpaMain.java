package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
//            Member member = new Member();
//            member.setUsername("member1");
//            member.setAge(20);
//            em.persist(member);

            /*
            //1.결과가 여러개일 경우
            TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);

            List<Member> resultList = query.getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }*/
            //2. 결과가 단일 일 경우
            /*
            TypedQuery<Member> query = em.createQuery("select m from Member m where m.id = 10", Member.class);
            Member result = query.getSingleResult();
            //결과가 단일이 아니고 없거나 2개이상인 경우는 Excecption 이 터진다.
            //Spring Data JPA -> 결과가 없으면 optional 이나 null 로 반환 [스프링이 try catch 처리를 해줌]
            System.out.println("result = " + result);
            */
            //3. 쿼리 바인딩
            /*Member singleResult = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();
            System.out.println("singleResult = " + singleResult.getUsername());*/
            //
            //프로젝션 - 여러값 조회
            // 1. Query 타입으로 조회
            //Query query = em.createQuery("SELECT m.username, m.age FROM Member m");
            // 2. Object[] 타입으로 조회
            /*List resultList = query.getResultList();
            Object object = resultList.get(0);
            Object[] objArr = (Object[]) object;
            for (Object o : objArr) {
                System.out.println("o = " + o);
            }*/
            // 3. new 명령어로 조회
            // 단순값을 DTO로 바로 조히 , 순서와 타입이 일치하는 생성자 필요 , 패키지명을 포함한 전체 클래스명 입력
            /*Query query = em.createQuery("SELECT new jpql.UserDTO(m.username,m.age) FROM Member m");
            List<UserDTO> resultList = query.getResultList();
            for (UserDTO userDTO : resultList) {
                System.out.println("username= " + userDTO.getUsername());
                System.out.println("userAge = " + userDTO.getAge());
            }*/
            //4. 페이징 API
            //JPA는 페이징을 다음 두 API로 추상화
            //setFirstResult(int startPosition) : 조회 시작 위치 0부터 시작
            //setMaxResult(int endPosition) : 조화할 데이터 수

            //멤버를 생성한다.
            for (int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }
            em.flush();
            em.clear();
            //멤버를 조회한다.
            String jpql = "SELECT m from Member m order by m.username desc";
            List<Member> resultList = em.createQuery(jpql, Member.class)
                                        .setFirstResult(10)
                                        .setMaxResults(20)
                                        .getResultList();
            for (Member member : resultList) {
                System.out.println("member = " + member.toString());
            }

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.clear();
        }
        emf.close();
    }
}
