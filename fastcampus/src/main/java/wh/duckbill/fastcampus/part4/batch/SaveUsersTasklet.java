package wh.duckbill.fastcampus.part4.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import wh.duckbill.fastcampus.part4.model.Users;
import wh.duckbill.fastcampus.part4.model.UsersRepository;
import wh.duckbill.fastcampus.part5.Orders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author        : duckbill413
 * date          : 2023-02-07
 * description   :
 **/
@RequiredArgsConstructor
public class SaveUsersTasklet implements Tasklet {
    private final int SIZE = 10_000;
    private final UsersRepository usersRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Users> users = createUsers();

        Collections.shuffle(users);

        usersRepository.saveAll(users);

        return RepeatStatus.FINISHED;
    }

    private List<Users> createUsers(){
        List<Users> users = new ArrayList<>();

        for (int i = 0; i < SIZE; i++) {
            users.add(Users.builder()
                    .orders(Collections.singletonList(Orders.builder()
                            .price(1_000)
                            .createdDate(LocalDate.of(2023, 1, 1))
                            .itemName("item " + i)
                            .build()))
                    .name("test username " + i)
                    .build());
        }
        for (int i = 0; i < SIZE; i++) {
            users.add(Users.builder()
                    .orders(Collections.singletonList(Orders.builder()
                            .price(200_000)
                            .createdDate(LocalDate.of(2023, 1, 2))
                            .itemName("item " + i)
                            .build()))
                    .name("test username " + i)
                    .build());
        }
        for (int i = 0; i < SIZE; i++) {
            users.add(Users.builder()
                    .orders(Collections.singletonList(Orders.builder()
                            .price(300_000)
                            .createdDate(LocalDate.of(2023, 1, 3))
                            .itemName("item " + i)
                            .build()))
                    .name("test username " + i)
                    .build());
        }
        for (int i = 0; i < SIZE; i++) {
            users.add(Users.builder()
                    .orders(Collections.singletonList(Orders.builder()
                            .price(500_000)
                            .createdDate(LocalDate.of(2023, 1, 4))
                            .itemName("item " + i)
                            .build()))
                    .name("test username " + i)
                    .build());
        }
        return users;
    }
}
