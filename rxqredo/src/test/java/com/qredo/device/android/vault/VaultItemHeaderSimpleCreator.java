package com.qredo.device.android.vault;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.qredo.device.android.ItemMetadataSimpleCreator;

public class VaultItemHeaderSimpleCreator
{
    @NonNull public static VaultItemHeader mock(@Nullable byte[] bytes)
    {
        return new VaultItemHeader(
                VaultItemRefSimpleCreator.mock(bytes),
                ItemMetadataSimpleCreator.mock());
    }
}
