package com.qredo.device.android.conversationmessage;

import com.qredo.device.android.conversation.ConversationRef;
import com.qredo.device.android.conversation.ConversationRefSimpleCreator;
import com.qredo.device.android.conversationmessage.callback.ConversationMessageCallback;
import com.qredo.device.android.conversationmessage.callback.ConversationMessageListener;
import com.qredo.device.android.vault.callback.VaultItemCreatedListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import rx.Subscription;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ConversationMessageManagerObservableSubscriptionTest
{
    private ConversationMessageManager conversationMessageManager;
    private ConversationMessageManagerObservable conversationMessageManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        conversationMessageManager = mock(ConversationMessageManager.class);
        conversationMessageManagerObservable = new ConversationMessageManagerObservable(conversationMessageManager);
    }

    @Test
    public void testSendRef_callsSend() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        ConversationMessage message = ConversationMessageSimpleCreator.mock(null);
        conversationMessageManagerObservable.send(ref, message)
            .subscribe();

        verify(conversationMessageManager).send(
                eq(ref),
                eq(message),
                any(ConversationMessageCallback.class));
    }

    @Test
    public void testGetRef_callsGet() throws Exception
    {
        ConversationMessageRef ref = ConversationMessageRefSimpleCreator.mock(null);
        conversationMessageManagerObservable.get(ref)
                .subscribe();

        verify(conversationMessageManager).get(
                eq(ref),
                any(ConversationMessageCallback.class));
    }

    @Test
    public void testListHeaders_callsListHeader() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        conversationMessageManagerObservable.listHeaders(ref)
                .subscribe();

        verify(conversationMessageManager).listHeaders(
                eq(ref),
                any(ConversationMessageCallback.class));
    }

    @Test
    public void testListMessagesRef_callsListMessages() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        conversationMessageManagerObservable.listMessages(ref)
                .subscribe();

        verify(conversationMessageManager).listMessages(
                eq(ref),
                any(ConversationMessageCallback.class));
    }

    @Test
    public void testDeleteRef_callsDelete() throws Exception
    {
        ConversationMessageRef ref = ConversationMessageRefSimpleCreator.mock(null);
        conversationMessageManagerObservable.delete(ref)
                .subscribe();

        verify(conversationMessageManager).delete(
                eq(ref),
                any(ConversationMessageCallback.class));
    }

    @Test
    public void testListenRef_callsAddAndNotRemove() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        Subscription subscription = conversationMessageManagerObservable.listen(ref)
                .subscribe();

        verify(conversationMessageManager).addListener(eq(ref), any(ConversationMessageListener.class));
        verify(conversationMessageManager, never()).removeListener(any(ConversationMessageListener.class));
        subscription.unsubscribe();
    }

    @Test
    public void testListenRefAndUnsubscribe_callsAddAndRemove() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        conversationMessageManagerObservable.listen(ref)
                .subscribe()
                .unsubscribe();
        ArgumentCaptor<ConversationMessageListener> listenerCaptor = ArgumentCaptor.forClass(ConversationMessageListener.class);

        verify(conversationMessageManager).addListener(eq(ref), listenerCaptor.capture());
        verify(conversationMessageManager).removeListener(listenerCaptor.capture());
        assertThat(listenerCaptor.getAllValues().get(0)).isSameAs(listenerCaptor.getAllValues().get(1));
    }
}