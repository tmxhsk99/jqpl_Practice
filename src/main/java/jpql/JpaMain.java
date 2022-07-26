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
/*
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
*/
            //조인

            //데이터 생성
            // Team1 : member1 , member3 / Team2 : Team2(Member.class) / 팀 없음 : member2 / 멤버없는 팀 : noMemberTeam
            Team team = new Team();
            team.setName("Team1");
            for (int i = 0; i < 4; i++) {
                if (i != 3) {
                    Member member = new Member();
                    member.setUsername("member" + i);
                    member.setAge(10 + i);

                    Order order = new Order();
                    order.setMember(member);
                    order.setOrderAmount(3);

                    if (isEven(i)) {
                        member.setTeam(team);
                        team.changeTeam(member);
                        em.persist(team);
                    }
                    em.persist(order);
                    em.persist(member);
                }

                if (i == 3) {
                    Member member = new Member();
                    member.setUsername("Team2");
                    member.setAge(10 + i);
                    Team team2 = new Team();
                    team2.setName("Team2");
                    team2.changeTeam(member);
                    member.setTeam(team2);
                    em.persist(team2);
                    em.persist(member);
                }
            }
            Team team3 = new Team();
            team3.setName("noMemberTeam");
            em.persist(team3);
            em.flush();
            em.clear();

            //1. 내부 조인 : 해당 조건에 해당하는 컬럼만 표시됨 , 없으면 표시되지 않음
           /* String innerJoinjpql = "SELECT m FROM Member m INNER JOIN m.team t";
            List<Member> resultList = em.createQuery(innerJoinjpql, Member.class).getResultList();
            for (Member member : resultList) {
                System.out.println(member.toString() + "TeamName : " + member.getTeam().getName());
            }
            tx.commit();*/

            //2. 외부 조인 (LEFT 조인) : 왼쪽테이블을 모두 select 하고 오른쪽 테이블의 값이 없는 경우는 NULL로 둔다 RIGHT인 경우 반대로 조회된다.
            /*
            String outerJoinJpql = "SELECT m FROM Member m LEFT OUTER JOIN m.team t";
            List<Member> resultList = em.createQuery(outerJoinJpql, Member.class).getResultList();
            for (Member member : resultList) {
                if (member != null && member.getTeam() != null) {
                    System.out.println(member.toString() + "/ TeamName : " + member.getTeam().getName());
                }
                if(member == null && member.getTeam() != null){
                    System.out.println("Member is NULL / TeamName : " + member.getTeam().getName());
                }
                if(member.getTeam() == null && member != null){
                    System.out.println(member.toString() + "/ TeamName is NULL");
                }
            }
            tx.commit();
            */

            //3. 세타 조인 : 통칭 막조인 , 새로운 조건값에 해당하는 값을 조인한다.
            /*String setaJoinJpql = "SELECT m FROM Member m , Team t where m.username = t.name";
            List<Member> resultList = em.createQuery(setaJoinJpql, Member.class).getResultList();
            for (Member member : resultList) {
                if (member != null && member.getTeam() != null) {
                    System.out.println(member.toString() + "/ TeamName : " + member.getTeam().getName());
                }
                if (member == null && member.getTeam() != null) {
                    System.out.println("Member is NULL / TeamName : " + member.getTeam().getName());
                }
                if (member.getTeam() == null && member != null) {
                    System.out.println(member.toString() + "/ TeamName is NULL");
                }
            }
            tx.commit();*/
            //조인 - ON 절
            // ON 절을 활용한 조인 (JPA 2.1 부터 지원)
            // 1. 조인 대상 필터링
            /*
            String filteringJPQL = "SELECT m  FROM Member m INNER JOIN m.team t ON t.name = 'Team1'";
            List<Member> resultList = em.createQuery(filteringJPQL, Member.class).getResultList();
            for (Member member : resultList) {
                printMemberAndTeam(member);
            }
            tx.commit();
            */
            // 2. 연관관계 없는 엔티티 외부 조인
           /* String setaOuterJPQL = "SELECT m,t FROM Member m JOIN m.team t ON m.username = 'Team2'";
            List resultList = em.createQuery(setaOuterJPQL).getResultList();
            for (Object o : resultList) {
                Object[] objectArr = (Object[]) o;
                System.out.println("ObjectArr [0] / Member = " + objectArr[0].toString());
                Team tmpTeam =  (Team) objectArr[1];
                System.out.println("ObjectArr [1] / Team = " + tmpTeam.getName());
            }
            tx.commit();*/
            // 서브 쿼리
            // 나이가 평균보다 많은 회원
            // Alias 를 사용할때 서브쿼리과 메인 쿼리의 는 다르게 사용해야 성능적으로 이슈가 적다
            /*String subQueryJPQL1 = "SELECT m FROM Member m WHERE m.age > (SELECT avg(m2.age) from Member m2)";
            List<Member> resultList = em.createQuery(subQueryJPQL1, Member.class).getResultList();
            for (Member member : resultList) {
                System.out.println(member.toString());
            }
            tx.commit();*/

            // 한건이라도 주문을 한 고객
           /* String subQueryJPQL2 = "SELECT m FROM Member m WHERE (select count(o) from Order o where m = o.member) > 0";
            List<Member> resultList = em.createQuery(subQueryJPQL2, Member.class).getResultList();

            if(resultList.size() == 0){
                System.out.println("order is null");
            }
            for (Member member : resultList) {
                System.out.println("member = " + member.toString());
            }
            tx.commit();*/

            //기본 CASE식
            /*
            String basicCaseJPQL = "select " +
                    "case when m.age <= 10 then '학생요금' " +
                    "when m.age >= 20 then '경로요금'" +
                    "else '일반요금' end  " +
                    "FROM Member m";
            List<String> resultList = em.createQuery(basicCaseJPQL, String.class).getResultList();
            for (String s : resultList) {
                System.out.println("s = " + s);
            }
            tx.commit();*/

            //단순 CASE 식
/*            String caseJPQL = "select case t.name " +
                    "when 'Team1' then '인센티브 110%'" +
                    "when 'Team2' then '인센티브 120%'" +
                    "else '인센티브 105%'" +
                    "end " +
                    "from Team t";
            List<String> resultList = em.createQuery(caseJPQL, String.class).getResultList();
            for (String s : resultList) {
                System.out.println("s = " + s);
            }
            tx.commit();*/
            //fetch Join
            //엔티티 페치 조인 회원 조회를 하면서 연관된 팀도 함께 조회(SQL 한번에)
            /*String fetchJoin = "select m from Member m join fetch m.team";
            List<Member> resultList = em.createQuery(fetchJoin, Member.class).getResultList();
            for (Member member : resultList) {
                //팀정보 까지 들어갔는지 확인한다.
                System.out.println("member = " + member.toString());
                System.out.println("member = " + member.getTeam().getName());
            }
            tx.commit();*/

            //컬렉션 페치 조인
            //String collectionFetchJoin = "select t from Team t join fetch t.members where t.name = 'Team1'";
            //String collectionFetchJoin = "select t from Team t join fetch t.members";
            //distinct 사용
            /*String collectionFetchJoin = "select distinct t from Team t join fetch t.members";
            List<Team> resultList = em.createQuery(collectionFetchJoin, Team.class).getResultList();
            for (Team team1 : resultList) {
                System.out.println(team1.getName() + " | " + team1.getMembers().size());
            }
            tx.commit();*/
            //Named Query 사용 어노테이션
            /* List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class).setParameter("username","member0").getResultList();
            for (Member member : resultList) {
                System.out.println("member = " + member.toString());
            }
            tx.commit();*/


            //벌크 연산 예제
            System.out.println("벌크 연산 시작");
            int updateCount = em.createQuery("update Member m set m.age = 20").executeUpdate();
            System.out.println("updateCount = " + updateCount);
            tx.commit();


        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.clear();
            em.close();
        }
        emf.close();
    }

    private static void printMemberAndTeam(Member member) {
        if (member != null && member.getTeam() != null) {
            System.out.println(member.toString() + "/ TeamName : " + member.getTeam().getName());
        }
        if (member == null && member.getTeam() != null) {
            System.out.println("Member is NULL / TeamName : " + member.getTeam().getName());
        }
        if (member.getTeam() == null && member != null) {
            System.out.println(member.toString() + "/ TeamName is NULL");
        }
    }


    /**
     * 짝수인 경우 true 반환 짝수인경우 false 반환
     *
     * @param i
     * @return
     */
    private static boolean isEven(int i) {
        return i % 2 == 0;
    }
}
