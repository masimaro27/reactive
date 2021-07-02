package main.java.toby.live._2;

import java.util.concurrent.Flow;

public class DelegateSub implements Flow.Subscriber<Integer> {


    Flow.Subscriber subscriber;

    public DelegateSub(Flow.Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(Integer item) {
        subscriber.onNext(item);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}
