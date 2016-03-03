package com.qredo.device.android.vault;

import android.support.annotation.NonNull;

import com.qredo.device.android.QredoError;
import com.qredo.device.android.vault.callback.VaultCallbackSubscriber;
import com.qredo.device.android.vault.callback.VaultItemCreatedListener;
import com.qredo.device.android.vault.callback.VaultItemHeaderMatcher;

import java.util.Set;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class VaultManagerObservable
{
    @NonNull private final VaultManager vaultManager;

    public VaultManagerObservable(@NonNull VaultManager vaultManager)
    {
        this.vaultManager = vaultManager;
    }

    @NonNull public Observable<VaultItemRef> put(@NonNull final VaultItem item)
    {
        return Observable.create(new OnSubscribe<VaultItemRef>()
        {
            @Override public void call(final Subscriber<? super VaultItemRef> subscriber)
            {
                vaultManager.put(
                        item,
                        new VaultCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<VaultItem> get(@NonNull final VaultItemRef ref)
    {
        return Observable.create(new OnSubscribe<VaultItem>()
        {
            @Override public void call(final Subscriber<? super VaultItem> subscriber)
            {
                vaultManager.get(
                        ref,
                        new VaultCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull public Observable<Boolean> delete(@NonNull final VaultItemRef ref)
    {
        return Observable.create(new OnSubscribe<Boolean>()
        {
            @Override public void call(final Subscriber<? super Boolean> subscriber)
            {
                vaultManager.delete(
                        ref,
                        new VaultCallbackSubscriber<>(subscriber));
            }
        });
    }

    /**
     * Convenience method.
     * @param ref
     * @param updater
     * @return
     */
    @NonNull public Observable<VaultItemRef> update(
            @NonNull final VaultItemRef ref,
            @NonNull Func1<VaultItem, VaultItem> updater)
    {
        return get(ref)
                .map(updater)
                .flatMap(new Func1<VaultItem, Observable<VaultItemRef>>()
                {
                    @Override public Observable<VaultItemRef> call(VaultItem item)
                    {
                        return put(item);
                    }
                })
                .flatMap(new Func1<VaultItemRef, Observable<VaultItemRef>>()
                {
                    @Override public Observable<VaultItemRef> call(final VaultItemRef newRef)
                    {
                        return delete(ref)
                                .map(new Func1<Boolean, VaultItemRef>()
                                {
                                    @Override public VaultItemRef call(Boolean aBoolean)
                                    {
                                        return newRef;
                                    }
                                });
                    }
                });
    }

    /**
     * Convenience method.
     * @param ref
     * @param updater
     * @return
     */
    @NonNull public Observable<VaultItemRef> updateAsync(
            @NonNull final VaultItemRef ref,
            @NonNull Func1<VaultItem, Observable<VaultItem>> updater)
    {
        return get(ref)
                .flatMap(updater)
                .flatMap(new Func1<VaultItem, Observable<VaultItemRef>>()
                {
                    @Override public Observable<VaultItemRef> call(VaultItem item)
                    {
                        return put(item);
                    }
                })
                .flatMap(new Func1<VaultItemRef, Observable<VaultItemRef>>()
                {
                    @Override public Observable<VaultItemRef> call(final VaultItemRef newRef)
                    {
                        return delete(ref)
                                .map(new Func1<Boolean, VaultItemRef>()
                                {
                                    @Override public VaultItemRef call(Boolean aBoolean)
                                    {
                                        return newRef;
                                    }
                                });
                    }
                });
    }

    @NonNull public Observable<Set<VaultItemHeader>> listHeaders()
    {
        return Observable.create(new OnSubscribe<Set<VaultItemHeader>>()
        {
            @Override public void call(final Subscriber<? super Set<VaultItemHeader>> subscriber)
            {
                vaultManager.listHeaders(new VaultCallbackSubscriber<>(subscriber));
            }
        });
    }

    @NonNull
    public Observable<Set<VaultItemHeader>> findHeaders(
            @NonNull final VaultItemHeaderMatcher matcher)
    {
        return Observable.create(new OnSubscribe<Set<VaultItemHeader>>()
        {
            @Override public void call(final Subscriber<? super Set<VaultItemHeader>> subscriber)
            {
                vaultManager.findHeaders(
                        matcher,
                        new VaultCallbackSubscriber<>(subscriber));
            }
        });
    }

    /**
     * This Observable never completes. You need to unsubscribe from it.
     * @return
     */
    @NonNull public Observable<VaultItemHeader> listen()
    {
        return Observable.create(new OnSubscribe<VaultItemHeader>()
        {
            @Override public void call(final Subscriber<? super VaultItemHeader> subscriber)
            {
                final VaultItemCreatedListener listener = new VaultItemCreatedListener()
                {
                    @Override public void onReceived(@NonNull VaultItemHeader vaultItemHeader)
                    {
                        subscriber.onNext(vaultItemHeader);
                    }

                    @Override public void onFailure(@NonNull String reason)
                    {
                        subscriber.onError(new QredoError(reason));
                    }
                };
                vaultManager.addListener(listener);
                subscriber.add(Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        vaultManager.removeListener(listener);
                    }
                }));
            }
        });
    }
}
