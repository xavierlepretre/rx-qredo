package com.qredo.device.android;

import android.content.Context;

import com.qredo.device.android.conversation.callback.ConversationCreatedListener;
import com.qredo.device.android.vault.VaultItemRef;
import com.qredo.device.android.vault.VaultItemRefSimpleCreator;
import com.qredo.device.android.vault.callback.VaultCallback;
import com.qredo.device.android.vault.callback.VaultItemCreatedListener;
import com.qredo.device.android.vault.callback.VaultItemHeaderMatcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import rx.Subscription;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class QredoClientObservableSubscriptionTest
{
    private QredoClientAdapter clientAdapter;
    private QredoClientObservable qredoClientObservable;

    @Before
    public void setUp() throws Exception
    {
        clientAdapter = mock(QredoClientAdapter.class);
        qredoClientObservable = new QredoClientObservable(clientAdapter);
    }

    @Test
    public void testBind_callsBindOnly() throws Exception
    {
        Context context = mock(Context.class);
        qredoClientObservable.bind(
                "secret", "id", "uSecret", context)
                .subscribe();

        verify(clientAdapter).bind(
                eq("secret"), eq("id"), eq("uSecret"), eq(context),
                any(QredoConnection.class));
        verify(clientAdapter, never()).unbind(any(QredoConnection.class));
    }

    @Test
    public void testBindUnsubscribe_callsUnBind() throws Exception
    {
        Context context = mock(Context.class);
        qredoClientObservable.bind(
                "secret", "id", "uSecret", context)
                .subscribe()
                .unsubscribe();
        ArgumentCaptor<QredoConnection> connectionCaptor = ArgumentCaptor.forClass(QredoConnection.class);

        verify(clientAdapter).bind(
                eq("secret"), eq("id"), eq("uSecret"), eq(context),
                connectionCaptor.capture());
        verify(clientAdapter).unbind(connectionCaptor.capture());
        assertThat(connectionCaptor.getAllValues().get(0)).isSameAs(connectionCaptor.getAllValues().get(1));
    }
}