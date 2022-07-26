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
            Member member = new Member();
            member.setUsername("member1");
            em.persist(member);

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

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.clear();
        }
        emf.close();
    }
}
