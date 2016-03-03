package com.qredo.device.android.conversationmessage;

import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;
import com.qredo.device.android.conversation.ConversationRef;
import com.qredo.device.android.conversationmessage.callback.ConversationMessageCallbackSubscriber;
import com.qredo.device.android.conversationmessage.callback.ConversationMessageListener;

import java.util.List;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class ConversationMessageManagerObservable
{
    public static final String TAG = ConversationMessageManagerObservable.class.getSimpleName();

    @NonNull private final ConversationMessageManager conversationMessageManager;

    public ConversationMessageManagerObservable(
            @NonNull ConversationMessageManager conversationMessageManager)
    {
        this.conversationMessageManager = conversationMessageManager;
    }

    @NonNull public Observable<ConversationMessageRef> send(
            @NonNull final ConversationRef ref,
            @NonNull final ConversationMessage message)
    {
        return Observable.create(new OnSubscribe<ConversationMessageRef>()
        {
            @Override public void call(Subscriber<? super ConversationMessageRef> subscriber)
            {
                conversationMessageManager.send(
                        ref,
                        message,
                        new ConversationMessageCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<ConversationMessage> get(@NonNull final ConversationMessageRef ref)
    {
        return Observable.create(new OnSubscribe<ConversationMessage>()
        {
            @Override public void call(Subscriber<? super ConversationMessage> subscriber)
            {
                conversationMessageManager.get(
                        ref,
                        new ConversationMessageCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull
    public Observable<List<ConversationMessageHeader>> listHeaders(@NonNull final ConversationRef ref)
    {
        return Observable.create(new OnSubscribe<List<ConversationMessageHeader>>()
        {
            @Override
            public void call(Subscriber<? super List<ConversationMessageHeader>> subscriber)
            {
                conversationMessageManager.listHeaders(
                        ref,
                        new ConversationMessageCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull
    public Observable<List<ConversationMessage>> listMessages(@NonNull final ConversationRef ref)
    {
        return Observable.create(new OnSubscribe<List<ConversationMessage>>()
        {
            @Override
            public void call(Subscriber<? super List<ConversationMessage>> subscriber)
            {
                conversationMessageManager.listMessages(
                        ref,
                        new ConversationMessageCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Boolean> delete(@NonNull final ConversationMessageRef ref)
    {
        return Observable.create(new OnSubscribe<Boolean>()
        {
            @Override public void call(Subscriber<? super Boolean> subscriber)
            {
                conversationMessageManager.delete(
                        ref,
                        new ConversationMessageCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<ConversationMessage> listen(@NonNull final ConversationRef ref)
    {
        return Observable.create(new OnSubscribe<ConversationMessage>()
        {
            @Override public void call(final Subscriber<? super ConversationMessage> subscriber)
            {
                final ConversationMessageListener listener = new ConversationMessageListener()
                {
                    @Override
                    public void onCounterpartLeft(@NonNull ConversationMessage conversationMessage)
                    {
                        // TODO not sure
                    }

                    @Override
                    public void onReceived(@NonNull ConversationMessage conversationMessage)
                    {
                        subscriber.onNext(conversationMessage);
                    }

                    @Override public void onFailure(@NonNull String reason)
                    {
                        subscriber.onError(new QredoError(reason));
                    }
                };
                conversationMessageManager.addListener(ref, listener);
                subscriber.add(Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        conversationMessageManager.removeListener(listener);
                    }
                }));
            }
        });
    }
}
