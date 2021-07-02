package main.java.toby.live._1;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Flow.*;

public class PubSub {

    public static void main(String[] args) throws InterruptedException {
        // Publisher <-- Observable
        // Subscriber <-- Observer

        ExecutorService es = Executors.newCachedThreadPool();


        Publisher p = new Publisher<>() {
            @Override
            public void subscribe(Subscriber<? super Object> subscriber) {
                Iterable<Integer> iter = Arrays.asList(1,2,3,4,5);
                subscriber.onSubscribe(new Subscription() {
                    Iterator<Integer> it = iter.iterator();
                    /**
                     * Subscription은 Subscriber와 Publisher의 중간연결점
                     * Publisher와 Subscription의 처리속도가 차이가 날 경우 조절해주는 것 : BackPressure
                     * @param n
                     */
                    @Override
                    public void request(long n) {
                        es.execute(() -> {
                            int i = 0;
//                            try{
                                while(i++ < n) {
                                    if (it.hasNext()) {
                                        subscriber.onNext(it.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
//                            } catch (RuntimeException e) {
//                                subscriber.onError(e);
//                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription;
            /**
             * 필수
             * @param subscription
             */
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }
//            int bufferSize = 2;
            /**
             * 옵션
             * @param item
             */
            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + "-onNext " + item);
                /**
                 * if buffer size = 2;
                 * buffer에 얼마나 여유가 있는지,
                 * 다음걸 미리 받아놔야하는지,
                 * 꽉 찬다음 작업한 후에 끝나면 다음걸 받을것인지,
                 * 현재 버퍼사이즈의 남은 수를 고려해서 간단한 로직을 작성 가능하다.
                 *
                 */
//                if (--bufferSize <= 0) {
                    this.subscription.request(1);
//                    bufferSize = 2;
//                }
            }

            /**
             * 옵션
             */
            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            /**
             * 옵션
             */
            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);

        es.awaitTermination(10, TimeUnit.HOURS);
        es.shutdown();
    }

}
