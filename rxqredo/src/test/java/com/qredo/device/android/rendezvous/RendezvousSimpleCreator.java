package com.qredo.device.android.rendezvous;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.qredo.device.android.rendezvous.Rendezvous.ResponseCountLimit;

public class RendezvousSimpleCreator
{
    @NonNull public static Rendezvous mock(
            @Nullable byte[] refBytes,
            int duration,
            ResponseCountLimit responseCountLimit)
    {
        return new Rendezvous(
                RendezvousRefSimpleCreator.mock(refBytes),
                "tag",
                new RendezvousInfo(duration, responseCountLimit));
    }
}
