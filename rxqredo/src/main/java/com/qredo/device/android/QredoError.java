package com.qredo.device.android;

import android.support.annotation.NonNull;

public class QredoError extends RuntimeException
{
    public QredoError(@NonNull String detailMessage)
    {
        super(detailMessage);
    }
}
