package com.qredo.device.android.vault;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class VaultItemSimpleCreator
{
    @NonNull public static VaultItem mock(@Nullable byte[] bytes)
    {
        return new VaultItem(bytes);
    }
}
