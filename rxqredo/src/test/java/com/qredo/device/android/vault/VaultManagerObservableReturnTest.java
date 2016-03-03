package com.qredo.device.android.vault;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.vault.callback.VaultCallback;
import com.qredo.device.android.vault.callback.VaultItemCreatedListener;
import com.qredo.device.android.vault.callback.VaultItemHeaderMatcher;

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

public class VaultManagerObservableReturnTest
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
    public void testPut_passesOn() throws Exception
    {
        VaultItem item = VaultItemSimpleCreator.mock(null);
        VaultItemRef expected = VaultItemRefSimpleCreator.mock(null);
        doAnswer(passItOnToVaultCallback(1, expected))
                .when(vaultManager)
                .put(eq(item), any(VaultCallback.class));
        Iterator<VaultItemRef> iterator = vaultManagerObservable
                .put(item)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testGet_passesOn() throws Exception
    {
        VaultItemRef ref = VaultItemRefSimpleCreator.mock(null);
        final VaultItem expected = VaultItemSimpleCreator.mock(null);
        doAnswer(passItOnToVaultCallback(1, expected))
                .when(vaultManager)
                .get(eq(ref), any(VaultCallback.class));
        Iterator<VaultItem> iterator = vaultManagerObservable
                .get(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testDelete_passesOn() throws Exception
    {
        VaultItemRef ref = VaultItemRefSimpleCreator.mock(null);
        final Boolean expected = true;
        doAnswer(passItOnToVaultCallback(1, expected))
                .when(vaultManager)
                .delete(eq(ref), any(VaultCallback.class));
        Iterator<Boolean> iterator = vaultManagerObservable
                .delete(ref)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testListHeaders_passesOn() throws Exception
    {
        final Set<VaultItemHeader> expected = mock(Set.class);
        doAnswer(passItOnToVaultCallback(0, expected))
                .when(vaultManager)
                .listHeaders(any(VaultCallback.class));
        Iterator<Set<VaultItemHeader>> iterator = vaultManagerObservable
                .listHeaders()
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test(timeout = 1000)
    public void testFindHeaders_passesOn() throws Exception
    {
        VaultItemHeaderMatcher matcher = mock(VaultItemHeaderMatcher.class);
        final Set<VaultItemHeader> expected = mock(Set.class);
        doAnswer(passItOnToVaultCallback(1, expected))
                .when(vaultManager)
                .findHeaders(eq(matcher), any(VaultCallback.class));
        Iterator<Set<VaultItemHeader>> iterator = vaultManagerObservable
                .findHeaders(matcher)
                .toBlocking()
                .getIterator();

        assertThat(iterator.next()).isSameAs(expected);
        assertThat(iterator.hasNext()).isFalse();
    }

    @NonNull private Answer<Object> passItOnToVaultListener(
            @NonNull final VaultItemHeader... expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                for (VaultItemHeader message : expected)
                {
                    ((VaultItemCreatedListener) invocation.getArguments()[0])
                            .onReceived(message);
                }
                return null;
            }
        };
    }

    @Test
    public void testListen_passesOn() throws Exception
    {
        VaultItemHeader expected1 = VaultItemHeaderSimpleCreator.mock(null);
        VaultItemHeader expected2 = VaultItemHeaderSimpleCreator.mock(null);
        doAnswer(passItOnToVaultListener(expected1, expected2))
                .when(vaultManager)
                .addListener(any(VaultItemCreatedListener.class));
        final CountDownLatch counter = new CountDownLatch(2);
        final VaultItemHeader[] received = new VaultItemHeader[2];
        Subscription subscription = vaultManagerObservable.listen()
                .subscribe(new Action1<VaultItemHeader>()
                {
                    @Override public void call(VaultItemHeader message)
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