package com.qredo.device.android.rendezvous;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RendezvousRefSimpleCreator
{
    @NonNull public static RendezvousRef mock(@Nullable byte[] bytes)
    {
        return new RendezvousRef(bytes);
    }
}
