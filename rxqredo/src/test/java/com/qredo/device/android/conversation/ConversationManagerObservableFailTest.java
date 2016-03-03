package com.qredo.device.android.conversation;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;
import com.qredo.device.android.conversation.callback.ConversationCallback;
import com.qredo.device.android.conversation.callback.ConversationCreatedListener;
import com.qredo.device.android.rendezvous.RendezvousRef;
import com.qredo.device.android.rendezvous.RendezvousRefSimpleCreator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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

public class ConversationManagerObservableFailTest
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
            @IntRange(from = 0, to = 1) final int index)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((ConversationCallback) invocation.getArguments()[index])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListRef_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationCallback(1))
                .when(conversationManager)
                .list(eq(ref), any(ConversationCallback.class));
        conversationManagerObservable.list(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListTag_passesOn() throws Exception
    {
        doAnswer(passItOnToConversationCallback(1))
                .when(conversationManager)
                .list(eq("fakeTag"), any(ConversationCallback.class));
        conversationManagerObservable.list("fakeTag")
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListAll_passesOn() throws Exception
    {
        doAnswer(passItOnToConversationCallback(0))
                .when(conversationManager)
                .listAll(any(ConversationCallback.class));
        conversationManagerObservable.listAll()
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testGetRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationCallback(1))
                .when(conversationManager)
                .get(eq(ref), any(ConversationCallback.class));
        conversationManagerObservable.get(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testDeleteRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationCallback(1))
                .when(conversationManager)
                .delete(eq(ref), any(ConversationCallback.class));
        conversationManagerObservable.delete(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testLeaveRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationCallback(1))
                .when(conversationManager)
                .leave(eq(ref), any(ConversationCallback.class));
        conversationManagerObservable.leave(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @NonNull private Answer<Object> passItOnToConversationCreatedCallback()
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((ConversationCreatedListener) invocation.getArguments()[1])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListenRef_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationCreatedCallback())
                .when(conversationManager)
                .addListener(eq(ref), any(ConversationCreatedListener.class));
        conversationManagerObservable.listen(ref)
                .toBlocking()
                .getIterator()
                .next();
    }
}