package com.qredo.device.android.vault.callback;

import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;

import rx.Subscriber;

public class VaultCallbackSubscriber<T> implements VaultCallback<T>
{
    private final Subscriber<? super T> subscriber;

    public VaultCallbackSubscriber(Subscriber<? super T> subscriber)
    {
        this.subscriber = subscriber;
    }

    @Override public void onSuccess(@NonNull T item)
    {
        subscriber.onNext(item);
        subscriber.onCompleted();
    }

    @Override public void onFailure(@NonNull String reason)
    {
        subscriber.onError(new QredoError(reason));
    }
}
