package com.qredo.device.android.conversationmessage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConversationMessageSimpleCreator
{
    @NonNull public static ConversationMessage mock(@Nullable byte[] bytes)
    {
        return new ConversationMessage(bytes);
    }
}
