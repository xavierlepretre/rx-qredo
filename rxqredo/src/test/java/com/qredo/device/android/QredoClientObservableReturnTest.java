package com.qredo.device.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.qredo.device.android.vault.VaultItemHeader;
import com.qredo.device.android.vault.VaultItemHeaderSimpleCreator;
import com.qredo.device.android.vault.callback.VaultItemCreatedListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class QredoClientObservableReturnTest
{
    private QredoClientAdapter clientAdapter;
    private QredoClientObservable qredoClientObservable;

    @Before
    public void setUp() throws Exception
    {
        clientAdapter = mock(QredoClientAdapter.class);
        qredoClientObservable = new QredoClientObservable(clientAdapter);
    }

    @NonNull private Answer<Object> passItOnToQredoConnection(
            @NonNull final QredoClient... expected)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                for (QredoClient client : expected)
                {
                    ((QredoConnection) invocation.getArguments()[4])
                            .onSuccess(client);
                }
                return null;
            }
        };
    }

    @Test
    public void testBind_passesOn() throws Exception
    {
        QredoClient expected = mock(QredoClient.class);
        Context context = mock(Context.class);
        doAnswer(passItOnToQredoConnection(expected))
                .when(clientAdapter)
                .bind(
                        eq("secret"),
                        eq("id"),
                        eq("uSecret"),
                        eq(context),
                        any(QredoConnection.class));
        final CountDownLatch counter = new CountDownLatch(1);
        final QredoClient[] received = new QredoClient[1];
        Subscription subscription = qredoClientObservable.bind(
                "secret", "id", "uSecret", context)
                .subscribe(new Action1<QredoClient>()
                {
                    @Override public void call(QredoClient client)
                    {
                        received[0] = client;
                        counter.countDown();
                    }
                });
        counter.await(1000, TimeUnit.MILLISECONDS);

        assertThat(received[0]).isSameAs(expected);
        assertThat(subscription.isUnsubscribed()).isFalse();
        subscription.unsubscribe();
    }
}