package wh.duckbill.fastcampus.part4.model;

/**
 * author        : duckbill413
 * date          : 2023-02-07
 * description   :
 **/

import lombok.*;
import wh.duckbill.fastcampus.part5.Orders;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Level level = Level.NORMAL;
    private LocalDate updatedDate;
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    private List<Orders> orders = new ArrayList<>();

    @Builder
    public Users(String name, List<Orders> orders) {
        this.name = name;
        this.orders = orders;
    }
    private int getTotalAmount(){
        return this.orders.stream().mapToInt(Orders::getPrice).sum();
    }

    public boolean availableLevelUp() {
        return Level.availableLevelUp(this.getLevel(), this.getTotalAmount());
    }

    public Level levelUp() {
        Level nextLevel = Level.getNextLevel(this.getTotalAmount());

        this.level = nextLevel;
        this.updatedDate = LocalDate.now();

        return nextLevel;
    }
}
