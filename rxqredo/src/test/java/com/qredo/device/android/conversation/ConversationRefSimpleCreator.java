package com.qredo.device.android.conversation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConversationRefSimpleCreator
{
    @NonNull public static ConversationRef mock(@Nullable byte[] bytes)
    {
        return new ConversationRef(bytes);
    }
}
