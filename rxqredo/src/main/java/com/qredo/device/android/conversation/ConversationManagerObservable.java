package com.qredo.device.android.conversation;

import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;
import com.qredo.device.android.conversation.callback.ConversationCallbackSubscriber;
import com.qredo.device.android.conversation.callback.ConversationCreatedListener;
import com.qredo.device.android.rendezvous.RendezvousRef;

import java.util.Set;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class ConversationManagerObservable
{
    @NonNull private final ConversationManager conversationManager;

    public ConversationManagerObservable(
            @NonNull ConversationManager conversationManager)
    {
        this.conversationManager = conversationManager;
    }

    @NonNull public Observable<Set<Conversation>> list(@NonNull final RendezvousRef rendezvousRef)
    {
        return Observable.create(new OnSubscribe<Set<Conversation>>()
        {
            @Override public void call(Subscriber<? super Set<Conversation>> subscriber)
            {
                conversationManager.list(
                        rendezvousRef,
                        new ConversationCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Set<Conversation>> list(@NonNull final String tag)
    {
        return Observable.create(new OnSubscribe<Set<Conversation>>()
        {
            @Override public void call(Subscriber<? super Set<Conversation>> subscriber)
            {
                conversationManager.list(
                        tag,
                        new ConversationCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Set<Conversation>> listAll()
    {
        return Observable.create(new OnSubscribe<Set<Conversation>>()
        {
            @Override public void call(Subscriber<? super Set<Conversation>> subscriber)
            {
                conversationManager.listAll(
                        new ConversationCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Conversation> get(@NonNull final ConversationRef conversationRef)
    {
        return Observable.create(new OnSubscribe<Conversation>()
        {
            @Override public void call(Subscriber<? super Conversation> subscriber)
            {
                conversationManager.get(
                        conversationRef,
                        new ConversationCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Boolean> delete(@NonNull final ConversationRef conversationRef)
    {
        return Observable.create(new OnSubscribe<Boolean>()
        {
            @Override public void call(Subscriber<? super Boolean> subscriber)
            {
                conversationManager.delete(
                        conversationRef,
                        new ConversationCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Boolean> leave(@NonNull final ConversationRef conversationRef)
    {
        return Observable.create(new OnSubscribe<Boolean>()
        {
            @Override public void call(Subscriber<? super Boolean> subscriber)
            {
                conversationManager.leave(
                        conversationRef,
                        new ConversationCallbackSubscriber<>(subscriber));
            }
        });
    }

    /**
     * This Observable never completes. You need to unsubscribe from it.
     * @param ref
     * @return
     */
    @NonNull public Observable<Conversation> listen(@NonNull final RendezvousRef ref)
    {
        return Observable.create(new OnSubscribe<Conversation>()
        {
            @Override public void call(final Subscriber<? super Conversation> subscriber)
            {
                final ConversationCreatedListener listener = new ConversationCreatedListener()
                {
                    @Override public void onReceived(@NonNull Conversation conversation)
                    {
                        subscriber.onNext(conversation);
                    }

                    @Override public void onFailure(@NonNull String reason)
                    {
                        subscriber.onError(new QredoError(reason));
                    }
                };
                conversationManager.addListener(ref, listener);
                subscriber.add(Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        conversationManager.removeListener(listener);
                    }
                }));
            }
        });
    }
}
