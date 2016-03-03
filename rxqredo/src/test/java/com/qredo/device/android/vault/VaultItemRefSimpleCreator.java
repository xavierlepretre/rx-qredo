package com.qredo.device.android.vault;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class VaultItemRefSimpleCreator
{
    @NonNull public static VaultItemRef mock(@Nullable byte[] bytes)
    {
        return new VaultItemRef(bytes);
    }
}
