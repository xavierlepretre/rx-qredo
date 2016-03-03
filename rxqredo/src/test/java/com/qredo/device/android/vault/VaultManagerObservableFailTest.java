package com.qredo.device.android.vault;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;
import com.qredo.device.android.vault.callback.VaultCallback;
import com.qredo.device.android.vault.callback.VaultItemCreatedListener;
import com.qredo.device.android.vault.callback.VaultItemHeaderMatcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class VaultManagerObservableFailTest
{
    private VaultManager vaultManager;
    private VaultManagerObservable vaultManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        vaultManager = mock(VaultManager.class);
        vaultManagerObservable = new VaultManagerObservable(vaultManager);
    }

    @NonNull private Answer<Object> passItOnToVaultCallback(
            @IntRange(from = 0, to = 2) final int index)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((VaultCallback) invocation.getArguments()[index])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testPut_passesOn() throws Exception
    {
        VaultItem item = VaultItemSimpleCreator.mock(null);
        doAnswer(passItOnToVaultCallback(1))
                .when(vaultManager)
                .put(eq(item), any(VaultCallback.class));
        vaultManagerObservable
                .put(item)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testGet_passesOn() throws Exception
    {
        VaultItemRef ref = VaultItemRefSimpleCreator.mock(null);
        doAnswer(passItOnToVaultCallback(1))
                .when(vaultManager)
                .get(eq(ref), any(VaultCallback.class));
        vaultManagerObservable
                .get(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testDelete_passesOn() throws Exception
    {
        VaultItemRef ref = VaultItemRefSimpleCreator.mock(null);
        doAnswer(passItOnToVaultCallback(1))
                .when(vaultManager)
                .delete(eq(ref), any(VaultCallback.class));
        vaultManagerObservable
                .delete(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListHeaders_passesOn() throws Exception
    {
        doAnswer(passItOnToVaultCallback(0))
                .when(vaultManager)
                .listHeaders(any(VaultCallback.class));
        vaultManagerObservable
                .listHeaders()
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testFindHeaders_passesOn() throws Exception
    {
        VaultItemHeaderMatcher matcher = mock(VaultItemHeaderMatcher.class);
        doAnswer(passItOnToVaultCallback(1))
                .when(vaultManager)
                .findHeaders(eq(matcher), any(VaultCallback.class));
        vaultManagerObservable
                .findHeaders(matcher)
                .toBlocking()
                .getIterator()
                .next();
    }

    @NonNull private Answer<Object> passItOnToVaultListener()
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((VaultItemCreatedListener) invocation.getArguments()[0])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListen_passesOn() throws Exception
    {
        doAnswer(passItOnToVaultListener())
                .when(vaultManager)
                .addListener(any(VaultItemCreatedListener.class));
        vaultManagerObservable
                .listen()
                .toBlocking()
                .getIterator()
                .next();
    }
}