package cn.ypz.com.retrofitclient.client;

import rx.Subscription;

interface RxClientActionManger<T> {

    void add(T tag, Subscription subscription);

    void remove(T tag);

    void cancel(T tag);

    void cancelAll();

    void removeAll();
}
