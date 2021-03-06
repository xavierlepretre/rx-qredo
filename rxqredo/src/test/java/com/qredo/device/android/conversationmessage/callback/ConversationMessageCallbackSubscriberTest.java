package com.qredo.device.android.conversationmessage.callback;

import com.qredo.device.android.QredoError;

import org.junit.Test;

import rx.Subscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ConversationMessageCallbackSubscriberTest
{
    @Test
    public void testFailureSendsException() throws Exception
    {
        Subscriber subscriber = mock(Subscriber.class);
        ConversationMessageCallback callback = new ConversationMessageCallbackSubscriber(subscriber);
        callback.onFailure("Failed");

        verify(subscriber, never()).onNext(any());
        verify(subscriber, never()).onCompleted();
        verify(subscriber).onError(isA(QredoError.class));
    }

    @Test
    public void testValueSendsAndCompletes() throws Exception
    {
        Subscriber subscriber = mock(Subscriber.class);
        ConversationMessageCallback callback = new ConversationMessageCallbackSubscriber(subscriber);
        Object value = mock(Object.class);
        callback.onSuccess(value);

        verify(subscriber).onNext(eq(value));
        verify(subscriber).onCompleted();
        verify(subscriber, never()).onError(any(QredoError.class));
    }
}