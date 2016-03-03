package com.qredo.device.android.rendezvous;

import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;
import com.qredo.device.android.conversation.ConversationRef;
import com.qredo.device.android.rendezvous.callback.RendezvousCallbackSubscriber;
import com.qredo.device.android.rendezvous.callback.RendezvousCreatedListener;

import java.util.Set;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class RendezvousManagerObservable
{
    @NonNull private final RendezvousManager rendezvousManager;

    public RendezvousManagerObservable(
            @NonNull RendezvousManager rendezvousManager)
    {
        this.rendezvousManager = rendezvousManager;
    }

    @NonNull public Observable<Rendezvous> create(@NonNull final RendezvousCreationParams params)
    {
        return Observable.create(new OnSubscribe<Rendezvous>()
        {
            @Override public void call(final Subscriber<? super Rendezvous> subscriber)
            {
                rendezvousManager.create(
                        params,
                        new RendezvousCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<ConversationRef> respond(@NonNull final String tag)
    {
        return Observable.create(new OnSubscribe<ConversationRef>()
        {
            @Override public void call(final Subscriber<? super ConversationRef> subscriber)
            {
                rendezvousManager.respond(
                        tag,
                        new RendezvousCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Rendezvous> get(@NonNull final RendezvousRef ref)
    {
        return Observable.create(new OnSubscribe<Rendezvous>()
        {
            @Override public void call(final Subscriber<? super Rendezvous> subscriber)
            {
                rendezvousManager.get(
                        ref,
                        new RendezvousCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Rendezvous> activate(
            @NonNull final RendezvousRef ref,
            final int durationInSecondsBeforeExpiry)
    {
        return Observable.create(new OnSubscribe<Rendezvous>()
        {
            @Override public void call(final Subscriber<? super Rendezvous> subscriber)
            {
                rendezvousManager.activate(
                        ref,
                        durationInSecondsBeforeExpiry,
                        new RendezvousCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Boolean> deactivate(@NonNull final RendezvousRef ref)
    {
        return Observable.create(new OnSubscribe<Boolean>()
        {
            @Override public void call(final Subscriber<? super Boolean> subscriber)
            {
                rendezvousManager.deactivate(
                        ref,
                        new RendezvousCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Set<Rendezvous>> list()
    {
        return Observable.create(new OnSubscribe<Set<Rendezvous>>()
        {
            @Override public void call(final Subscriber<? super Set<Rendezvous>> subscriber)
            {
                rendezvousManager.list(
                        new RendezvousCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Rendezvous> listen()
    {
        return Observable.create(new OnSubscribe<Rendezvous>()
        {
            @Override public void call(final Subscriber<? super Rendezvous> subscriber)
            {
                final RendezvousCreatedListener listener = new RendezvousCreatedListener()
                {
                    @Override public void onReceived(Rendezvous rendezvous)
                    {
                        subscriber.onNext(rendezvous);
                    }

                    @Override public void onFailure(String s)
                    {
                        subscriber.onError(new QredoError(s));
                    }
                };
                rendezvousManager.addListener(listener);
                subscriber.add(Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        rendezvousManager.removeListener(listener);
                    }
                }));
            }
        });
    }
}
