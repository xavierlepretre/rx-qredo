package com.qredo.device.android.rendezvous.callback;

import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;

import rx.Subscriber;

public class RendezvousCallbackSubscriber<T> implements RendezvousCallback<T>
{
    @NonNull private final Subscriber<? super T> subscriber;

    public RendezvousCallbackSubscriber(@NonNull Subscriber<? super T> subscriber)
    {
        this.subscriber = subscriber;
    }

    @Override public void onSuccess(T item)
    {
        subscriber.onNext(item);
        subscriber.onCompleted();
    }

    @Override public void onFailure(@NonNull String reason)
    {
        subscriber.onError(new QredoError(reason));
    }
}
