package com.qredo.device.android.conversationmessage;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.conversation.Conversation;
import com.qredo.device.android.conversation.ConversationRef;
import com.qredo.device.android.conversation.ConversationRefSimpleCreator;
import com.qredo.device.android.conversation.ConversationSimpleCreator;
import com.qredo.device.android.conversation.callback.ConversationCreatedListener;
import com.qredo.device.android.conversationmessage.callback.ConversationMessageCallback;
import com.qredo.device.android.conversationmessage.callback.ConversationMessageListener;
import com.qredo.device.android.rendezvous.RendezvousRef;
import com.qredo.device.android.rendezvous.RendezvousRefSimpleCreator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ConversationMessageManagerObservableReturnTest
{
    private ConversationMessageManager conversationMessageManager;
    private ConversationMessageManagerObservable conversationMessageManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        conversationMessageManager = mock(ConversationMessageManager.class);
        conversationMessageManagerObservable = new ConversationMessageManagerObservable(conversationMessageManager);
    }

    @NonNull private Answer<Object> passItOnToConversationMessageCallback(
            @IntRange(from = 0, to = 2) final int index,
            @NonNull final Object expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((ConversationMessageCallback) invocation.getArguments()[index])
                        .onSuccess(expected);
                return null;
            }
        };
    }

    @Test(timeout = 1000)
    public void testSendRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        ConversationMessage message = ConversationMessageSimpleCreator.mock(null);
        final ConversationMessageRef expected = ConversationMessageRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageCallback(2, expected))
                .when(conversationMessageManager)
                .send(eq(ref), eq(message), any(ConversationMessageCallback.class));
        Iterator<ConversationMessageRef> iterator = conversationMessageManagerObservable
                .send(ref, message)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testGetRef_passesOn() throws Exception
    {
        ConversationMessageRef ref = ConversationMessageRefSimpleCreator.mock(null);
        final ConversationMessage expected = ConversationMessageSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageCallback(1, expected))
                .when(conversationMessageManager)
                .get(eq(ref), any(ConversationMessageCallback.class));
        Iterator<ConversationMessage> iterator = conversationMessageManagerObservable
                .get(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testListHeaders_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        final List<ConversationMessageHeader> expected = mock(List.class);
        doAnswer(passItOnToConversationMessageCallback(1, expected))
                .when(conversationMessageManager)
                .listHeaders(eq(ref), any(ConversationMessageCallback.class));
        Iterator<List<ConversationMessageHeader>> iterator = conversationMessageManagerObservable
                .listHeaders(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testListMessages_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        final List<ConversationMessage> expected = mock(List.class);
        doAnswer(passItOnToConversationMessageCallback(1, expected))
                .when(conversationMessageManager)
                .listMessages(eq(ref), any(ConversationMessageCallback.class));
        Iterator<List<ConversationMessage>> iterator = conversationMessageManagerObservable
                .listMessages(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testDeleteRef_passesOn() throws Exception
    {
        ConversationMessageRef ref = ConversationMessageRefSimpleCreator.mock(null);
        final Boolean expected = true;
        doAnswer(passItOnToConversationMessageCallback(1, expected))
                .when(conversationMessageManager)
                .delete(eq(ref), any(ConversationMessageCallback.class));
        Iterator<Boolean> iterator = conversationMessageManagerObservable.delete(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @NonNull private Answer<Object> passItOnToConversationMessageListener(
            @NonNull final ConversationMessage... expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                for (ConversationMessage message : expected)
                {
                    ((ConversationMessageListener) invocation.getArguments()[1])
                            .onReceived(message);
                }
                return null;
            }
        };
    }

    @Test
    public void testListenRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        ConversationMessage expected1 = ConversationMessageSimpleCreator.mock(null);
        ConversationMessage expected2 = ConversationMessageSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageListener(expected1, expected2))
                .when(conversationMessageManager)
                .addListener(eq(ref), any(ConversationMessageListener.class));
        final CountDownLatch counter = new CountDownLatch(2);
        final ConversationMessage[] received = new ConversationMessage[2];
        Subscription subscription = conversationMessageManagerObservable.listen(ref)
                .subscribe(new Action1<ConversationMessage>()
                {
                    @Override public void call(ConversationMessage message)
                    {
                        received[2 - (int) counter.getCount()] = message;
                        counter.countDown();
                    }
                });
        counter.await(1000, TimeUnit.MILLISECONDS);

        assertThat(received[0]).isSameAs(expected1);
        assertThat(received[1]).isSameAs(expected2);
        assertThat(subscription.isUnsubscribed()).isFalse();
        subscription.unsubscribe();
    }
}