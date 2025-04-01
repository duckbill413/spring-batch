package wh.duckbill.fastcampus.part3;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/**
 * author        : duckbill413
 * date          : 2023-01-24
 * description   :
 **/
@Getter
@Entity
@NoArgsConstructor
public class Person {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String age;
    private String address;

    public Person(int id, String name, String age, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public Person(String name, String age, String address) {
        this(0, name, age, address);
    }

    public boolean isNotEmptyName(){
        return Objects.nonNull(this.name) && !name.isEmpty();
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public Person unknownName() {
        this.name = "UNKNOWN";
        return this;
    }
}
