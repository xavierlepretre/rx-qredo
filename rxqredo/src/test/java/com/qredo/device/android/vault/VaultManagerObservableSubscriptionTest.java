package com.qredo.device.android.vault;

import com.qredo.device.android.QredoConnection;
import com.qredo.device.android.vault.VaultManager;
import com.qredo.device.android.vault.VaultManagerObservable;
import com.qredo.device.android.vault.callback.VaultCallback;
import com.qredo.device.android.vault.callback.VaultItemCreatedListener;
import com.qredo.device.android.vault.callback.VaultItemHeaderMatcher;

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

public class VaultManagerObservableSubscriptionTest
{
    private VaultManager vaultManager;
    private VaultManagerObservable vaultManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        vaultManager = mock(VaultManager.class);
        vaultManagerObservable = new VaultManagerObservable(vaultManager);
    }

    @Test
    public void testPut_callsPut() throws Exception
    {
        VaultItem item = VaultItemSimpleCreator.mock(null);
        vaultManagerObservable.put(item)
                .subscribe();

        verify(vaultManager).put(
                eq(item),
                any(VaultCallback.class));
    }

    @Test
    public void testGet_callsGet() throws Exception
    {
        VaultItemRef ref = VaultItemRefSimpleCreator.mock(null);
        vaultManagerObservable.get(ref)
                .subscribe();

        verify(vaultManager).get(
                eq(ref),
                any(VaultCallback.class));
    }

    @Test
    public void testDelete_callsDelete() throws Exception
    {
        VaultItemRef ref = VaultItemRefSimpleCreator.mock(null);
        vaultManagerObservable.delete(ref)
                .subscribe();

        verify(vaultManager).delete(
                eq(ref),
                any(VaultCallback.class));
    }

    // TODO test update

    @Test
    public void testListHeaders_callsListHeaders() throws Exception
    {
        vaultManagerObservable
                .listHeaders()
                .subscribe();

        verify(vaultManager)
                .listHeaders(any(VaultCallback.class));
    }

    @Test
    public void testFindHeaders_callsFindHeaders() throws Exception
    {
        VaultItemHeaderMatcher matcher = mock(VaultItemHeaderMatcher.class);
        vaultManagerObservable
                .findHeaders(matcher)
                .subscribe();

        verify(vaultManager)
                .findHeaders(
                        eq(matcher),
                        any(VaultCallback.class));
    }

    @Test
    public void testListenRef_callsAddAndNotRemove() throws Exception
    {
        Subscription subscription = vaultManagerObservable.listen()
                .subscribe();

        verify(vaultManager).addListener(any(VaultItemCreatedListener.class));
        verify(vaultManager, never()).removeListener(any(VaultItemCreatedListener.class));
        subscription.unsubscribe();
    }

    @Test
    public void testListenRefAndUnsubscribe_callsAddAndRemove() throws Exception
    {
        vaultManagerObservable.listen()
                .subscribe()
                .unsubscribe();
        ArgumentCaptor<VaultItemCreatedListener> listenerCaptor = ArgumentCaptor.forClass(VaultItemCreatedListener.class);

        verify(vaultManager).addListener(listenerCaptor.capture());
        verify(vaultManager).removeListener(listenerCaptor.capture());
        assertThat(listenerCaptor.getAllValues().get(0)).isSameAs(listenerCaptor.getAllValues().get(1));
    }
}