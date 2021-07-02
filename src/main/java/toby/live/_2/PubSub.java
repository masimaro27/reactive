package main.java.toby.live._2;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reactive Streams - Operators
 *
 * Publisher -> [Data1] -> Op1 -> [Data2] -> Op2 -> [Data3] -> Subscriber
 *
 *
 * pub -> [data1] -> mapPub -> [data2] -> logSub
 *                   <- subscribe(lobSub)
 *                   -> onSubscribe(s)
 *                   -> onNext
 *                   -> onNext
 *                   -> onComplete
 */
public class PubSub {
    public static void main(String[] args) {
        Flow.Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        /**
         * Function<T,R> ?
         *  T타입을 집어넣으면 R타입을 리턴한다.
         */
        Flow.Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
//        Flow.Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
//        Flow.Publisher<Integer> sumPub = sumPub(pub);
        /**
         * BiFunction<T, U, R> ?
         * 파라미터 2개를 받아서 리턴을 하나한다.
         */
        Flow.Publisher<Integer> sumPub = reducePub(pub, (a, b) -> a + b);
        reducePub.subscribe(logSub());

    }

    private static Flow.Publisher<Integer> reducePub(Flow.Publisher<Integer> pub, BiFunction<Integer, Integer, Integer> f) {
        return
    }

    /**
     * 1,2,3,4,5
     * 0 -> (0,1) -> 0 + 1 = 1;
     * 1 -> (1,2) -> 1 + 2 = 3;
     * 2 -> (3,3) -> 3 + 3 = 6;
     * 3 -> (6,4) -> 6 + 4 = 10;
     * @param pub
     * @return
     */

    private static Flow.Publisher<Integer> sumPub(Flow.Publisher<Integer> pub) {
        return new Flow.Publisher<Integer>() {
            @Override
            public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
                pub.subscribe(new DelegateSub(subscriber) {
                    int sum = 0;
                    @Override
                    public void onNext(Integer item) {
                        sum += item;
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(sum);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    private static Flow.Publisher<Integer> mapPub(Flow.Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Flow.Publisher<Integer>() {
            @Override
            public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
                pub.subscribe(new DelegateSub(subscriber) {
                    @Override
                    public void onNext(Integer item) {
                        subscriber.onNext(f.apply(item));
                    }
                });
            }
        };
    }

    private static Flow.Subscriber<Integer> logSub() {
        /**
         * 퍼블리셔로부터 4가지 형태의 이벤트를 받을거다.
         */
        Flow.Subscriber<Integer> sub = new Flow.Subscriber<Integer>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscriber:");
                // 너가 가지고있는 데이터 다 보내 (받을 수 있는 메시지 제한을 맥스로 세팅)
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext : " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError : " + throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
        return sub;
    }

    private static Flow.Publisher<Integer> iterPub(List<Integer> iter) {
        Flow.Publisher<Integer> pub = new Flow.Publisher<Integer>() {
            // Stream.iterate(seed, function) : Stream.iterate(1, a->a+1).limit(10).collect(Collectors.toList())무한대로 데이터스트림을 만들어내는 재밋는 친구
            @Override
            public void subscribe(Flow.Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Flow.Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(s->sub.onNext(s));
                            sub.onComplete();
                        } catch (Throwable t)  {
                            sub.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
        return pub;
    }
}
