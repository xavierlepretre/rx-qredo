package com.github.xavierlepretre.rxqredo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qredo.device.android.QredoClient;
import com.qredo.device.android.QredoClientAdapter;
import com.qredo.device.android.QredoClientObservable;
import com.qredo.device.android.vault.VaultItem;
import com.qredo.device.android.vault.VaultItemHeader;
import com.qredo.device.android.vault.VaultItemRef;
import com.qredo.device.android.vault.VaultManagerObservable;
import com.qredo.device.android.vault.callback.VaultItemHeaderMatcher;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity
{
    private Subscription clientSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientSubscription = new QredoClientObservable(new QredoClientAdapter())
                .bind(
                        "YOUR APP SECRET",
                        "USER ID",
                        "USER SECRET",
                        this)
                .flatMap(new Func1<QredoClient, Observable<VaultItemHeader>>()
                {
                    @Override public Observable<VaultItemHeader> call(QredoClient qredoClient)
                    {
                        return new VaultManagerObservable(qredoClient.getVaultManager())
                                .listen();
                    }
                })
                .subscribe(
                        new Action1<VaultItemHeader>()
                        {
                            @Override public void call(VaultItemHeader itemHeader)
                            {

                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {

                            }
                        });
    }

    private void examplePutVaultItem(@NonNull QredoClient qredoClient, @NonNull VaultItem item)
    {
        Observable<VaultItemRef> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .put(item);
    }

    private void exampleGetVaultItem(@NonNull QredoClient qredoClient, @NonNull VaultItemRef ref)
    {
        Observable<VaultItem> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .get(ref);
    }

    private void exampleDeleteVaultItem(@NonNull QredoClient qredoClient, @NonNull VaultItemRef ref)
    {
        Observable<Boolean> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .delete(ref);
    }

    private void exampleUpdateVaultItem(
            @NonNull QredoClient qredoClient,
            @NonNull VaultItemRef ref,
            @NonNull Func1<VaultItem, VaultItem> updater)
    {
        Observable<VaultItemRef> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .update(ref, updater);
    }

    private void exampleUpdateAsyncVaultItem(
            @NonNull QredoClient qredoClient,
            @NonNull VaultItemRef ref,
            @NonNull Func1<VaultItem, Observable<VaultItem>> updater)
    {
        Observable<VaultItemRef> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .updateAsync(ref, updater);
    }

    private void exampleListHeaders(@NonNull QredoClient qredoClient)
    {
        Observable<Set<VaultItemHeader>> headersObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .listHeaders();
    }

    private void exampleFindHeaders(@NonNull QredoClient qredoClient, @NonNull VaultItemHeaderMatcher matcher)
    {
        Observable<Set<VaultItemHeader>> headersObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .findHeaders(matcher);
    }

    @Override protected void onDestroy()
    {
        clientSubscription.unsubscribe();
        super.onDestroy();
    }
}
