package com.qredo.device.android.rendezvous;

import com.qredo.device.android.conversation.ConversationRef;
import com.qredo.device.android.conversation.ConversationRefSimpleCreator;
import com.qredo.device.android.rendezvous.callback.RendezvousCallback;
import com.qredo.device.android.rendezvous.callback.RendezvousCreatedListener;
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

public class RendezvousManagerObservableSubscriptionTest
{
    private RendezvousManager rendezvousManager;
    private RendezvousManagerObservable rendezvousManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        rendezvousManager = mock(RendezvousManager.class);
        rendezvousManagerObservable = new RendezvousManagerObservable(rendezvousManager);
    }

    @Test
    public void testCreate_callsCreate() throws Exception
    {
        RendezvousCreationParams params = mock(RendezvousCreationParams.class);
        rendezvousManagerObservable.create(params)
            .subscribe();

        verify(rendezvousManager).create(
                eq(params),
                any(RendezvousCallback.class));
    }

    @Test
    public void testRespond_callsRespond() throws Exception
    {
        rendezvousManagerObservable.respond("tag")
                .subscribe();

        verify(rendezvousManager).respond(
                eq("tag"),
                any(RendezvousCallback.class));
    }

    @Test
    public void testGet_callsGet() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        rendezvousManagerObservable.get(ref)
                .subscribe();

        verify(rendezvousManager).get(
                eq(ref),
                any(RendezvousCallback.class));
    }

    @Test
    public void testActivate_callsActivate() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        rendezvousManagerObservable.activate(ref, 1000)
                .subscribe();

        verify(rendezvousManager).activate(
                eq(ref),
                eq(1000),
                any(RendezvousCallback.class));
    }

    @Test
    public void testDeactivate_callsDeactivate() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        rendezvousManagerObservable.deactivate(ref)
                .subscribe();

        verify(rendezvousManager).deactivate(
                eq(ref),
                any(RendezvousCallback.class));
    }

    @Test
    public void testList_callsList() throws Exception
    {
        rendezvousManagerObservable.list()
                .subscribe();

        verify(rendezvousManager).list(
                any(RendezvousCallback.class));
    }

    @Test
    public void testListenRef_callsAddAndNotRemove() throws Exception
    {
        Subscription subscription = rendezvousManagerObservable.listen()
                .subscribe();

        verify(rendezvousManager).addListener(any(RendezvousCreatedListener.class));
        verify(rendezvousManager, never()).removeListener(any(RendezvousCreatedListener.class));
        subscription.unsubscribe();
    }

    @Test
    public void testListenRefAndUnsubscribe_callsAddAndRemove() throws Exception
    {
        rendezvousManagerObservable.listen()
                .subscribe()
                .unsubscribe();
        ArgumentCaptor<RendezvousCreatedListener> listenerCaptor = ArgumentCaptor.forClass(RendezvousCreatedListener.class);

        verify(rendezvousManager).addListener(listenerCaptor.capture());
        verify(rendezvousManager).removeListener(listenerCaptor.capture());
        assertThat(listenerCaptor.getAllValues().get(0)).isSameAs(listenerCaptor.getAllValues().get(1));
    }
}