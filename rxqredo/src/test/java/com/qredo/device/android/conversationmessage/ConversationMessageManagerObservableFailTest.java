package com.qredo.device.android.conversationmessage;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;
import com.qredo.device.android.conversation.ConversationRef;
import com.qredo.device.android.conversation.ConversationRefSimpleCreator;
import com.qredo.device.android.conversationmessage.callback.ConversationMessageCallback;
import com.qredo.device.android.conversationmessage.callback.ConversationMessageListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ConversationMessageManagerObservableFailTest
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
            @IntRange(from = 0, to = 2) final int index)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((ConversationMessageCallback) invocation.getArguments()[index])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testSendRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        ConversationMessage message = ConversationMessageSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageCallback(2))
                .when(conversationMessageManager)
                .send(eq(ref), eq(message), any(ConversationMessageCallback.class));
        conversationMessageManagerObservable
                .send(ref, message)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testGetRef_passesOn() throws Exception
    {
        ConversationMessageRef ref = ConversationMessageRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageCallback(1))
                .when(conversationMessageManager)
                .get(eq(ref), any(ConversationMessageCallback.class));
        conversationMessageManagerObservable
                .get(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListHeaders_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageCallback(1))
                .when(conversationMessageManager)
                .listHeaders(eq(ref), any(ConversationMessageCallback.class));
        conversationMessageManagerObservable
                .listHeaders(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListMessages_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageCallback(1))
                .when(conversationMessageManager)
                .listMessages(eq(ref), any(ConversationMessageCallback.class));
        conversationMessageManagerObservable
                .listMessages(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testDeleteRef_passesOn() throws Exception
    {
        ConversationMessageRef ref = ConversationMessageRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageCallback(1))
                .when(conversationMessageManager)
                .delete(eq(ref), any(ConversationMessageCallback.class));
        conversationMessageManagerObservable.delete(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @NonNull private Answer<Object> passItOnToConversationMessageListener()
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((ConversationMessageListener) invocation.getArguments()[1])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListenRef_passesOn() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        doAnswer(passItOnToConversationMessageListener())
                .when(conversationMessageManager)
                .addListener(eq(ref), any(ConversationMessageListener.class));
        conversationMessageManagerObservable.listen(ref)
                .toBlocking()
                .getIterator()
                .next();
    }
}