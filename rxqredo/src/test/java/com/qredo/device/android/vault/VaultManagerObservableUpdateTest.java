package com.qredo.device.android.vault;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.vault.callback.VaultCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Iterator;

import rx.functions.Func1;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VaultManagerObservableUpdateTest
{
    private VaultManager vaultManager;
    private VaultManagerObservable vaultManagerObservable;
    private VaultItemRef originalRef;
    private VaultItem original;
    private VaultItem updated;
    private VaultItemRef updatedRef;
    private Func1 updater;

    @Before
    public void setUp() throws Exception
    {
        vaultManager = mock(VaultManager.class);
        vaultManagerObservable = new VaultManagerObservable(vaultManager);
        originalRef = VaultItemRefSimpleCreator.mock(null);
        original = VaultItemSimpleCreator.mock(null);
        updated = VaultItemSimpleCreator.mock(null);
        updatedRef = VaultItemRefSimpleCreator.mock(null);
        updater = mock(Func1.class);
        when(updater.call(eq(original))).thenReturn(updated);
    }

    @NonNull private Answer<Object> passItOnToVaultCallback(
            @IntRange(from = 0, to = 2) final int index,
            @NonNull final Object expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((VaultCallback) invocation.getArguments()[index])
                        .onSuccess(expected);
                return null;
            }
        };
    }

    @Test(timeout = 1000)
    public void testUpdate_alwaysCallsGet() throws Exception
    {
        vaultManagerObservable.update(originalRef, updater)
                .subscribe();

        verify(vaultManager).get(eq(originalRef), any(VaultCallback.class));
        verify(updater, never()).call(any());
        verify(vaultManager, never()).put(any(VaultItem.class), any(VaultCallback.class));
        verify(vaultManager, never()).delete(any(VaultItemRef.class), any(VaultCallback.class));
    }

    @Test(timeout = 1000)
    public void testUpdate_ifGet_thenCallsUpdaterAndPut() throws Exception
    {
        doAnswer(passItOnToVaultCallback(1, original))
                .when(vaultManager)
                .get(eq(originalRef), any(VaultCallback.class));
        vaultManagerObservable.update(originalRef, updater)
                .subscribe();

        verify(vaultManager).get(eq(originalRef), any(VaultCallback.class));
        verify(updater).call(eq(original));
        verify(vaultManager).put(eq(updated), any(VaultCallback.class));
        verify(vaultManager, never()).delete(any(VaultItemRef.class), any(VaultCallback.class));
    }

    @Test(timeout = 1000)
    public void testUpdate_ifGetAndPut_thenCallsDelete() throws Exception
    {
        doAnswer(passItOnToVaultCallback(1, original))
                .when(vaultManager)
                .get(eq(originalRef), any(VaultCallback.class));
        doAnswer(passItOnToVaultCallback(1, updatedRef))
                .when(vaultManager)
                .put(eq(updated), any(VaultCallback.class));
        vaultManagerObservable.update(originalRef, updater)
                .subscribe();

        verify(vaultManager).get(eq(originalRef), any(VaultCallback.class));
        verify(updater).call(eq(original));
        verify(vaultManager).put(eq(updated), any(VaultCallback.class));
        verify(vaultManager).delete(eq(originalRef), any(VaultCallback.class));
    }

    @Test(timeout = 1000)
    public void testUpdate_receiveUpdatedRef() throws Exception
    {
        doAnswer(passItOnToVaultCallback(1, original))
                .when(vaultManager)
                .get(eq(originalRef), any(VaultCallback.class));
        doAnswer(passItOnToVaultCallback(1, updatedRef))
                .when(vaultManager)
                .put(eq(updated), any(VaultCallback.class));
        // Just to make sure we do not receive updatedRef for delete
        doAnswer(
                new Answer()
                {
                    @Override public Object answer(InvocationOnMock invocation) throws Throwable
                    {
                        if (invocation.getArguments()[0] == originalRef)
                        {
                            ((VaultCallback) invocation.getArguments()[1]).onSuccess(true);
                            return true;
                        }
                        throw new IllegalArgumentException();
                    }
                })
                .when(vaultManager)
                .delete(any(VaultItemRef.class), any(VaultCallback.class));
        Iterator<VaultItemRef> iterator = vaultManagerObservable.update(originalRef, updater)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(updatedRef);
        assertThat(iterator.hasNext()).isFalse();
    }
}