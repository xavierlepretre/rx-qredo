package com.qredo.device.android.conversation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConversationSimpleCreator
{
    @NonNull public static Conversation mock(@Nullable byte[] bytes)
    {
        return new Conversation("fakeRef", ConversationRefSimpleCreator.mock(bytes));
    }
}
