package com.qredo.device.android;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Just so we can mock it.
 */
public class QredoClientAdapter
{
    public void bind(
            @NonNull String appSecret,
            @NonNull String userId,
            @NonNull String userSecret,
            @NonNull Context applicationContext,
            @NonNull QredoConnection qredoConnection)
    {
        QredoClient.bind(appSecret, userId, userSecret, applicationContext, qredoConnection);
    }

    public void unbind(@NonNull QredoConnection qredoConnection)
    {
        QredoClient.unbind(qredoConnection);
    }
}
