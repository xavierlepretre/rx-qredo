package com.qredo.device.android;

import android.content.Context;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;


public class QredoClientObservable
{
    @NonNull private final QredoClientAdapter clientAdapter;

    public QredoClientObservable(@NonNull QredoClientAdapter clientAdapter)
    {
        this.clientAdapter = clientAdapter;
    }

    @NonNull public Observable<QredoClient> bind(
            @NonNull final String appSecret,
            @NonNull final String userId,
            @NonNull final String userSecret,
            @NonNull final Context applicationContext)
    {
        return Observable.create(new OnSubscribe<QredoClient>()
        {
            @Override public void call(final Subscriber<? super QredoClient> subscriber)
            {
                final QredoConnection connection = new QredoConnection()
                {
                    @Override public void onSuccess(@NonNull QredoClient client)
                    {
                        subscriber.onNext(client);
                    }

                    @Override public void onFailure(@NonNull String reason)
                    {
                        subscriber.onError(new QredoError(reason));
                    }

                    @Override public void onDisconnected()
                    {
                        subscriber.onCompleted();
                    }
                };
                clientAdapter.bind(
                        appSecret,
                        userId,
                        userSecret,
                        applicationContext,
                        connection);
                subscriber.add(Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        clientAdapter.unbind(connection);
                    }
                }));
            }
        });
    }
}
