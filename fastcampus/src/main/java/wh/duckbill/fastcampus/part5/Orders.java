package wh.duckbill.fastcampus.part5;

/**
 * author        : duckbill413
 * date          : 2023-02-12
 * description   :
 **/

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Orders {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String itemName;
    private int price;
    private LocalDate createdDate;
}
