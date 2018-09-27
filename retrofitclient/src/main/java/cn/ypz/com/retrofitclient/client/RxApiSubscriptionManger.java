package cn.ypz.com.retrofitclient.client;

import android.util.ArrayMap;

import rx.Subscription;

public class RxApiSubscriptionManger implements RxClientActionManger<Object> {

    private static RxApiSubscriptionManger rxApiSubscriptionManger;
    private ArrayMap<Object, Subscription> apiMap;

    public static RxApiSubscriptionManger getRxApiSubscriptionManger() {
        if (rxApiSubscriptionManger == null) {
            synchronized (RxApiSubscriptionManger.class) {
                rxApiSubscriptionManger = new RxApiSubscriptionManger();
            }
        }
        return rxApiSubscriptionManger;
    }

    private RxApiSubscriptionManger() {
        apiMap = new ArrayMap<>();
    }

    @Override
    public void add(Object tag, Subscription subscription) {
        apiMap.put(tag, subscription);
    }

    @Override
    public void remove(Object tag) {
        if (!apiMapIsEmpty()) apiMap.remove(tag);
    }

    @Override
    public void cancel(Object tag) {
        if (!apiMapIsEmpty()) {
            if (apiMap.get(tag) != null) {
                if (!apiMap.get(tag).isUnsubscribed()) {
                    apiMap.get(tag).unsubscribe();
                }
                remove(tag);
            }
        }
    }

    @Override
    public void cancelDownLoad(Object tag) {
        if (!apiMapIsEmpty()) {
            if (apiMap.get(tag) != null) {
                if (!apiMap.get(tag).isUnsubscribed()) {
                    apiMap.get(tag).unsubscribe();
                }
                remove(tag);
            }
        }
    }


    @Override
    public void cancelAll() {
        if (!apiMapIsEmpty())
            for (Object o : apiMap.keySet()) {
                cancel(o);
            }
    }

    @Override
    public void removeAll() {
        if (!apiMapIsEmpty()) apiMap.clear();
    }

    private boolean apiMapIsEmpty() {
        return apiMap.isEmpty();
    }
}
