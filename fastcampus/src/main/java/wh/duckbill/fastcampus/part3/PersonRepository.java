package wh.duckbill.fastcampus.part3;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author        : duckbill413
 * date          : 2023-02-04
 * description   :
 **/
public interface PersonRepository extends JpaRepository<Person, Integer> {
}
