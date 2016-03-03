package com.qredo.device.android;

import android.content.Context;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class QredoClientObservableFailTest
{
    private QredoClientAdapter clientAdapter;
    private QredoClientObservable qredoClientObservable;

    @Before
    public void setUp() throws Exception
    {
        clientAdapter = mock(QredoClientAdapter.class);
        qredoClientObservable = new QredoClientObservable(clientAdapter);
    }

    @NonNull private Answer<Object> passItOnToQredoConnection()
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((QredoConnection) invocation.getArguments()[4])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testBind_passesOn() throws Exception
    {
        Context context = mock(Context.class);
        doAnswer(passItOnToQredoConnection())
                .when(clientAdapter)
                .bind(
                        eq("secret"),
                        eq("id"),
                        eq("uSecret"),
                        eq(context),
                        any(QredoConnection.class));
        qredoClientObservable
                .bind("secret", "id", "uSecret", context)
                .toBlocking()
                .getIterator()
                .next();
    }
}