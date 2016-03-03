package com.qredo.device.android.rendezvous;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;
import com.qredo.device.android.rendezvous.callback.RendezvousCallback;
import com.qredo.device.android.rendezvous.callback.RendezvousCreatedListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class RendezvousManagerObservableFailTest
{
    private RendezvousManager rendezvousManager;
    private RendezvousManagerObservable rendezvousManagerObservable;

    @Before
    public void setUp() throws Exception
    {
        rendezvousManager = mock(RendezvousManager.class);
        rendezvousManagerObservable = new RendezvousManagerObservable(rendezvousManager);
    }

    @NonNull private Answer<Object> passItOnToRendezvousCallback(
            @IntRange(from = 0, to = 2) final int index)
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((RendezvousCallback) invocation.getArguments()[index])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testCreate_passesOn() throws Exception
    {
        RendezvousCreationParams params = mock(RendezvousCreationParams.class);
        doAnswer(passItOnToRendezvousCallback(1))
                .when(rendezvousManager)
                .create(eq(params), any(RendezvousCallback.class));
        rendezvousManagerObservable
                .create(params)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testRespond_passesOn() throws Exception
    {
        doAnswer(passItOnToRendezvousCallback(1))
                .when(rendezvousManager)
                .respond(eq("tag"), any(RendezvousCallback.class));
        rendezvousManagerObservable
                .respond("tag")
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testGet_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        doAnswer(passItOnToRendezvousCallback(1))
                .when(rendezvousManager)
                .get(eq(ref), any(RendezvousCallback.class));
        rendezvousManagerObservable
                .get(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testActivate_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        doAnswer(passItOnToRendezvousCallback(2))
                .when(rendezvousManager)
                .activate(eq(ref), eq(1000), any(RendezvousCallback.class));
        rendezvousManagerObservable
                .activate(ref, 1000)
                .toBlocking()
                .getIterator()
                .next();
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testDeactivate_passesOn() throws Exception
    {
        RendezvousRef ref = RendezvousRefSimpleCreator.mock(null);
        doAnswer(passItOnToRendezvousCallback(1))
                .when(rendezvousManager)
                .deactivate(eq(ref), any(RendezvousCallback.class));
        rendezvousManagerObservable
                .deactivate(ref)
                .toBlocking()
                .getIterator()
                .next();
    }

    @NonNull private Answer<Object> passItOnToRendezvousListener()
    {
        return new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((RendezvousCreatedListener) invocation.getArguments()[0])
                        .onFailure("fakeFail");
                return null;
            }
        };
    }

    @Test(timeout = 1000, expected = QredoError.class)
    public void testListen_passesOn() throws Exception
    {
        doAnswer(passItOnToRendezvousListener())
                .when(rendezvousManager)
                .addListener(any(RendezvousCreatedListener.class));
        rendezvousManagerObservable.listen()
                .toBlocking()
                .getIterator()
                .next();
    }
}