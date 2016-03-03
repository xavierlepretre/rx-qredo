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

    private void exampleGetVaultItem(@NonNull QredoClient qredoClient, @NonNull VaultItemRef ref)
    {
        Observable<VaultItem> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .get(ref);
    }

    @Override protected void onDestroy()
    {
        clientSubscription.unsubscribe();
        super.onDestroy();
    }
}
