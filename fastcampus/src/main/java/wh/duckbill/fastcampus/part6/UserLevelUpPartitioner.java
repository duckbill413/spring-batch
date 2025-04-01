package wh.duckbill.fastcampus.part6;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import wh.duckbill.fastcampus.part4.model.UsersRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * author        : duckbill413
 * date          : 2023-02-19
 * description   :
 **/

public class UserLevelUpPartitioner implements Partitioner {
    private final UsersRepository usersRepository;

    public UserLevelUpPartitioner(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int minId = usersRepository.findMinId(); // 1
        int maxId = usersRepository.findMaxId(); // 40_000

        int targetSize = (maxId - minId) / gridSize + 1; // 5000

        /**
         * ExecutionContext 생성
         * partition0 : 1, 5_000
         * partition1 : 5_001, 10_000
         * ~~~
         */
        Map<String, ExecutionContext> result = new HashMap<>();
        int number = 0;
        int start = minId;
        int end = start + targetSize -1;

        while (start <= maxId){
            ExecutionContext value = new ExecutionContext();
            result.put("partition"+number, value);

            if (end > maxId)
                end = maxId;

            value.putLong("minId", start);
            value.putLong("maxId", end);

            start += targetSize;
            end += targetSize;
            number += 1;
        }
        return result;
    }
}
