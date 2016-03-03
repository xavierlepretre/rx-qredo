package com.qredo.device.android.conversation;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.conversation.callback.ConversationCallback;
import com.qredo.device.android.conversation.callback.ConversationCreatedListener;
import com.qredo.device.android.rendezvous.RendezvousRef;
import com.qredo.device.android.rendezvous.RendezvousRefSimpleCreator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ConversationManagerObservableReturnTest
{
    private ConversationManager conversationManager;
    private ConversationManagerObservable conversationManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        conversationManager = mock(ConversationManager.class);
        conversationManagerObservable = new ConversationManagerObservable(conversationManager);
    }

    @NonNull private Answer<Object> passItOnToConversationCallback(
            @IntRange(from = 0, to = 1) final int index,
            @NonNull final Object expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((ConversationCallback) invocation.getArguments()[index])
                        .onSuccess(expected);
                return null;
            }
        };
    }

    @Test(timeout = 1000)
    public void testListRef_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        final Set<Conversation> expected = mock(Set.class);
        doAnswer(passItOnToConversationCallback(1, expected))
                .when(conversationManager)
                .list(eq(ref), any(ConversationCallback.class));
        Iterator<Set<Conversation>> iterator = conversationManagerObservable.list(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testListTag_passesOn() throws Exception
    {
        final Set<Conversation> expected = mock(Set.class);
        doAnswer(passItOnToConversationCallback(1, expected))
                .when(conversationManager)
                .list(eq("fakeTag"), any(ConversationCallback.class));
        Iterator<Set<Conversation>> iterator = conversationManagerObservable.list("fakeTag")
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testListAll_passesOn() throws Exception
    {
        final Set<Conversation> expected = mock(Set.class);
        doAnswer(passItOnToConversationCallback(0, expected))
                .when(conversationManager)
                .listAll(any(ConversationCallback.class));
        Iterator<Set<Conversation>> iterator = conversationManagerObservable.listAll()
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testGetRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        final Conversation expected = ConversationSimpleCreator.mock(null);
        doAnswer(passItOnToConversationCallback(1, expected))
                .when(conversationManager)
                .get(eq(ref), any(ConversationCallback.class));
        Iterator<Conversation> iterator = conversationManagerObservable.get(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testDeleteRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        final Boolean expected = true;
        doAnswer(passItOnToConversationCallback(1, expected))
                .when(conversationManager)
                .delete(eq(ref), any(ConversationCallback.class));
        Iterator<Boolean> iterator = conversationManagerObservable.delete(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testLeaveRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        final Boolean expected = true;
        doAnswer(passItOnToConversationCallback(1, expected))
                .when(conversationManager)
                .leave(eq(ref), any(ConversationCallback.class));
        Iterator<Boolean> iterator = conversationManagerObservable.leave(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @NonNull private Answer<Object> passItOnToConversationCreatedCallback(
            @NonNull final Conversation... expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                for (Conversation conversation : expected)
                {
                    ((ConversationCreatedListener) invocation.getArguments()[1])
                            .onReceived(conversation);
                }
                return null;
            }
        };
    }

    @Test
    public void testListenRef_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        Conversation expected1 = ConversationSimpleCreator.mock(null);
        Conversation expected2 = ConversationSimpleCreator.mock(null);
        doAnswer(passItOnToConversationCreatedCallback(expected1, expected2))
                .when(conversationManager)
                .addListener(eq(ref), any(ConversationCreatedListener.class));
        final CountDownLatch counter = new CountDownLatch(2);
        final Conversation[] received = new Conversation[2];
        Subscription subscription = conversationManagerObservable.listen(ref)
                .subscribe(new Action1<Conversation>()
                {
                    @Override public void call(Conversation conversation)
                    {
                        received[2 - (int) counter.getCount()] = conversation;
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