package com.qredo.device.android.rendezvous;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.conversation.ConversationRef;
import com.qredo.device.android.conversation.ConversationRefSimpleCreator;
import com.qredo.device.android.rendezvous.Rendezvous.ResponseCountLimit;
import com.qredo.device.android.rendezvous.callback.RendezvousCallback;
import com.qredo.device.android.rendezvous.callback.RendezvousCreatedListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Iterator;
import java.util.List;
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

public class RendezvousManagerObservableReturnTest
{
    private RendezvousManager rendezvousManager;
    private RendezvousManagerObservable rendezvousManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        rendezvousManager = mock(RendezvousManager.class);
        rendezvousManagerObservable = new RendezvousManagerObservable(rendezvousManager);
    }

    @NonNull private Answer<Object> passItOnToRendezvousCallback(
            @IntRange(from = 0, to = 2) final int index,
            @NonNull final Object expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((RendezvousCallback) invocation.getArguments()[index])
                        .onSuccess(expected);
                return null;
            }
        };
    }

    @Test(timeout = 1000)
    public void testCreate_passesOn() throws Exception
    {
        RendezvousCreationParams params = mock(RendezvousCreationParams.class);
        Rendezvous expected = RendezvousSimpleCreator.mock(null, 1000, ResponseCountLimit.SINGLE_RESPONSE);
        doAnswer(passItOnToRendezvousCallback(1, expected))
                .when(rendezvousManager)
                .create(eq(params), any(RendezvousCallback.class));
        Iterator<Rendezvous> iterator = rendezvousManagerObservable
                .create(params)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testRespond_passesOn() throws Exception
    {
        final ConversationRef expected = ConversationRefSimpleCreator.mock(null);
        doAnswer(passItOnToRendezvousCallback(1, expected))
                .when(rendezvousManager)
                .respond(eq("tag"), any(RendezvousCallback.class));
        Iterator<ConversationRef> iterator = rendezvousManagerObservable
                .respond("tag")
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testGet_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        final Rendezvous expected = RendezvousSimpleCreator.mock(null, 1000, ResponseCountLimit.SINGLE_RESPONSE);
        doAnswer(passItOnToRendezvousCallback(1, expected))
                .when(rendezvousManager)
                .get(eq(ref), any(RendezvousCallback.class));
        Iterator<Rendezvous> iterator = rendezvousManagerObservable
                .get(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testActivate_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        final Rendezvous expected = RendezvousSimpleCreator.mock(null, 1000, ResponseCountLimit.SINGLE_RESPONSE);
        doAnswer(passItOnToRendezvousCallback(2, expected))
                .when(rendezvousManager)
                .activate(eq(ref), eq(1000), any(RendezvousCallback.class));
        Iterator<Rendezvous> iterator = rendezvousManagerObservable
                .activate(ref, 1000)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testDeactivate_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        final Boolean expected = true;
        doAnswer(passItOnToRendezvousCallback(1, expected))
                .when(rendezvousManager)
                .deactivate(eq(ref), any(RendezvousCallback.class));
        Iterator<Boolean> iterator = rendezvousManagerObservable
                .deactivate(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testList_passesOn() throws Exception
    {
        final Set<Rendezvous> expected = mock(Set.class);
        doAnswer(passItOnToRendezvousCallback(0, expected))
                .when(rendezvousManager)
                .list(any(RendezvousCallback.class));
        Iterator<Set<Rendezvous>> iterator = rendezvousManagerObservable
                .list()
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @NonNull private Answer<Object> passItOnToRendezvousListener(
            @NonNull final Rendezvous... expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                for (Rendezvous message : expected)
                {
                    ((RendezvousCreatedListener) invocation.getArguments()[0])
                            .onReceived(message);
                }
                return null;
            }
        };
    }

    @Test
    public void testListen_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        Rendezvous expected1 = RendezvousSimpleCreator.mock(null, 200, ResponseCountLimit.UNLIMITED_RESPONSES);
        Rendezvous expected2 = RendezvousSimpleCreator.mock(null, 1000, ResponseCountLimit.SINGLE_RESPONSE);
        doAnswer(passItOnToRendezvousListener(expected1, expected2))
                .when(rendezvousManager)
                .addListener(any(RendezvousCreatedListener.class));
        final CountDownLatch counter = new CountDownLatch(2);
        final Rendezvous[] received = new Rendezvous[2];
        Subscription subscription = rendezvousManagerObservable.listen()
                .subscribe(new Action1<Rendezvous>()
                {
                    @Override public void call(Rendezvous message)
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