package com.qredo.device.android.conversationmessage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConversationMessageRefSimpleCreator
{
    @NonNull public static ConversationMessageRef mock(@Nullable byte[] bytes)
    {
        return new ConversationMessageRef(bytes);
    }
}
