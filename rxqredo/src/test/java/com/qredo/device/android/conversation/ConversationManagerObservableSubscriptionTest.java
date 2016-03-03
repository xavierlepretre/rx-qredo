package com.qredo.device.android.conversation;

import com.qredo.device.android.conversation.callback.ConversationCallback;
import com.qredo.device.android.conversation.callback.ConversationCreatedListener;
import com.qredo.device.android.rendezvous.RendezvousRef;
import com.qredo.device.android.rendezvous.RendezvousRefSimpleCreator;
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

public class ConversationManagerObservableSubscriptionTest
{
    private ConversationManager conversationManager;
    private ConversationManagerObservable conversationManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        conversationManager = mock(ConversationManager.class);
        conversationManagerObservable = new ConversationManagerObservable(conversationManager);
    }

    @Test
    public void testListRef_callsList() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        conversationManagerObservable.list(ref)
            .subscribe();

        verify(conversationManager).list(
                eq(ref),
                any(ConversationCallback.class));
    }

    @Test
    public void testListTag_callsList() throws Exception
    {
        conversationManagerObservable.list("fakeTag")
                .subscribe();

        verify(conversationManager).list(
                eq("fakeTag"),
                any(ConversationCallback.class));
    }

    @Test
    public void testListAll_callsListAll() throws Exception
    {
        conversationManagerObservable.listAll()
                .subscribe();

        verify(conversationManager).listAll(any(ConversationCallback.class));
    }

    @Test
    public void testGetRef_callsGet() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        conversationManagerObservable.get(ref)
                .subscribe();

        verify(conversationManager).get(
                eq(ref),
                any(ConversationCallback.class));
    }

    @Test
    public void testDeleteRef_callsDelete() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        conversationManagerObservable.delete(ref)
                .subscribe();

        verify(conversationManager).delete(
                eq(ref),
                any(ConversationCallback.class));
    }

    @Test
    public void testLeaveRef_callsLeave() throws Exception
    {
        ConversationRef ref = ConversationRefSimpleCreator.mock(null);
        conversationManagerObservable.leave(ref)
                .subscribe();

        verify(conversationManager).leave(
                eq(ref),
                any(ConversationCallback.class));
    }

    @Test
    public void testListenRef_callsAddAndNotRemove() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        Subscription subscription = conversationManagerObservable.listen(ref)
                .subscribe();

        verify(conversationManager).addListener(eq(ref), any(ConversationCreatedListener.class));
        verify(conversationManager, never()).removeListener(any(ConversationCreatedListener.class));
        subscription.unsubscribe();
    }

    @Test
    public void testListenRefAndUnsubscribe_callsAddAndRemove() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        conversationManagerObservable.listen(ref)
                .subscribe()
                .unsubscribe();
        ArgumentCaptor<ConversationCreatedListener> listenerCaptor = ArgumentCaptor.forClass(ConversationCreatedListener.class);

        verify(conversationManager).addListener(eq(ref), listenerCaptor.capture());
        verify(conversationManager).removeListener(listenerCaptor.capture());
        assertThat(listenerCaptor.getAllValues().get(0)).isSameAs(listenerCaptor.getAllValues().get(1));
    }
}