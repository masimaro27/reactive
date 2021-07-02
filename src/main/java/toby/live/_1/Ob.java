package main.java.toby.live._1;

/**
 * 2021_07_01
 */

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 이야기 순서
 * Duality -> Observer Pattern -> Reactive Streams (표준 - java9 API)
 *
 * Iterable <----> Observable (Duality)
 * pull             push
 *
 * -------------------------------------
 * Observable issue.
 * 1. Complete 가 없다. (끝을 알 수 없다.)
 * 2. Error 처리가 없다.
 *
 * -------------------------------------
 *
 * 세션 2
 * 복습
 * Reactive Streams - JAVA, JVM 업체들이 만나서 만든 표준 API를 살펴봄.
 * 가장 기본적인 형태의 예제를 연습함. Observable 패턴의 Publisher, Subscriber를 활용한 예제
 * 이번 세션에서는 Operator에 대해 진행할 예정.
 *
 *
 */

@SuppressWarnings("deprecation")
public class Ob {


    /*public static void main(String[] args) {

        Iterable<Integer> iter =  () ->
            new Iterator<Integer>() {
                int i = 0;
                final static int MAX = 10;


                public boolean hasNext() {
                    return i < MAX;
                }

                public Integer next() {
                    return ++i;
                }
        };

        for (Integer i : iter) {
            System.out.println(i);
        }

    }*/

    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                setChanged(); // 새로운 변화가 생겼다는 호출
                notifyObservers(i); // 이걸 호출한다
            }
        }
    }

    public static void main(String[] args) {
            // 데이터 받는 쪽
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        // Observable이 던지는 이벤트를 Observer가 받겠다.
        IntObservable io = new IntObservable();
        io.addObserver(ob);

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);

        System.out.println(Thread.currentThread().getName() + " " + "EXIT");

        es.shutdown();

    } // event source - event를 던짐, 어디에? -> target: Observer
}
