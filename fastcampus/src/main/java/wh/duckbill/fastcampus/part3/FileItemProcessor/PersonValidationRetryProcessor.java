package wh.duckbill.fastcampus.part3.FileItemProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import wh.duckbill.fastcampus.part3.Person;

/**
 * author        : duckbill413
 * date          : 2023-02-06
 * description   :
 **/
@Slf4j
public class PersonValidationRetryProcessor implements ItemProcessor<Person, Person> {
    private final RetryTemplate retryTemplate;

    public PersonValidationRetryProcessor() {
        this.retryTemplate = new RetryTemplateBuilder()
                .maxAttempts(3) // RetryLimit 과 유사
                .retryOn(NotFoundNameException.class)
                .withListener(new SavePersonRetryListener())
                .build();
    }

    @Override
    public Person process(Person item) throws Exception {
        return this.retryTemplate.execute(context -> {
            // RetryCallback
            if (item.isNotEmptyName())
                return item;
            throw new NotFoundNameException();
        }, context -> {
            // RecoveryCallback
            return item.unknownName();
        });
    }

    public static class SavePersonRetryListener implements RetryListener{

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            return true; // retry 를 시작하는 설정 true일때 시작
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("close"); // retry 종료 후 호출
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("onError");
        }

    }
}
