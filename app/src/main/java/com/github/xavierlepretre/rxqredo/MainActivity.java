package com.github.xavierlepretre.rxqredo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.qredo.device.android.QredoClient;
import com.qredo.device.android.QredoClientAdapter;
import com.qredo.device.android.QredoClientObservable;
import com.qredo.device.android.conversation.Conversation;
import com.qredo.device.android.conversation.ConversationManagerObservable;
import com.qredo.device.android.conversation.ConversationRef;
import com.qredo.device.android.conversationmessage.ConversationMessage;
import com.qredo.device.android.conversationmessage.ConversationMessageHeader;
import com.qredo.device.android.conversationmessage.ConversationMessageManagerObservable;
import com.qredo.device.android.conversationmessage.ConversationMessageRef;
import com.qredo.device.android.rendezvous.Rendezvous;
import com.qredo.device.android.rendezvous.RendezvousCreationParams;
import com.qredo.device.android.rendezvous.RendezvousManagerObservable;
import com.qredo.device.android.rendezvous.RendezvousRef;
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

    //<editor-fold desc="Vault Examples">
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
    //</editor-fold>

    //<editor-fold desc="Rendezvous Examples">
    private void exampleCreateRendezvous(@NonNull QredoClient qredoClient, @NonNull RendezvousCreationParams params)
    {
        Observable<Rendezvous> rendezvousObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
                .create(params);
    }

    private void exampleRespondRendezvous(@NonNull QredoClient qredoClient, @NonNull String tag)
    {
        Observable<ConversationRef> conversationRefObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
                .respond(tag);
    }

    private void exampleGetRendezvousByRef(@NonNull QredoClient qredoClient, @NonNull RendezvousRef ref)
    {
        Observable<Rendezvous> rendezvousObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
                .get(ref);
    }

    private void exampleActivateRendezvousByTag(@NonNull QredoClient qredoClient, @NonNull RendezvousRef ref, int duration)
    {
        Observable<Rendezvous> rendezvousObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
                .activate(ref, duration);
    }

    private void exampleDeactivateRendezvousByTag(@NonNull QredoClient qredoClient, @NonNull RendezvousRef ref)
    {
        Observable<Boolean> resultObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
                .deactivate(ref);
    }
    private void exampleListRendezvous(@NonNull QredoClient qredoClient)
    {
        Observable<Set<Rendezvous>> rendezvousesObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
                .list();
    }

    private void exampleListenToLiveRendezvous(@NonNull QredoClient qredoClient)
    {
        Observable<Rendezvous> rendezvousObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
                .listen();
    }
    //</editor-fold>

    //<editor-fold desc="Conversation Examples">
    private void exampleListConversationsByRef(@NonNull QredoClient qredoClient, @NonNull RendezvousRef ref)
    {
        Observable<Set<Conversation>> conversationsObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
                .list(ref);
    }

    private void exampleListConversationsByTag(@NonNull QredoClient qredoClient, @NonNull String tag)
    {
        Observable<Set<Conversation>> conversationsObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
                .list(tag);
    }

    private void exampleListAllConversations(@NonNull QredoClient qredoClient)
    {
        Observable<Set<Conversation>> conversationsObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
                .listAll();
    }

    private void exampleGetConversations(@NonNull QredoClient qredoClient, @NonNull ConversationRef ref)
    {
        Observable<Conversation> conversationObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
                .get(ref);
    }

    private void exampleDeleteConversations(@NonNull QredoClient qredoClient, @NonNull ConversationRef ref)
    {
        Observable<Boolean> resultObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
                .delete(ref);
    }

    private void exampleLeaveConversations(@NonNull QredoClient qredoClient, @NonNull ConversationRef ref)
    {
        Observable<Boolean> resultObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
                .leave(ref);
    }

    private void exampleListenToLiveCreatedConversations(@NonNull QredoClient qredoClient, @NonNull RendezvousRef ref)
    {
        Observable<Conversation> conversationsObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
                .listen(ref);
    }
    //</editor-fold>

    //<editor-fold desc="ConversationMessage Examples">
    private void exampleGetConversationMessage(@NonNull QredoClient qredoClient, @NonNull ConversationMessageRef ref)
    {
        Observable<ConversationMessage> conversationMessageObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
                .get(ref);
    }

    private void exampleListConversationMessageHeaders(@NonNull QredoClient qredoClient, @NonNull ConversationRef ref)
    {
        Observable<List<ConversationMessageHeader>> conversationMessageHeadersObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
                .listHeaders(ref);
    }

    private void exampleListConversationMessages(@NonNull QredoClient qredoClient, @NonNull ConversationRef ref)
    {
        Observable<List<ConversationMessage>> conversationMessagesObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
                .listMessages(ref);
    }

    private void exampleDeleteConversationMessages(@NonNull QredoClient qredoClient, @NonNull ConversationMessageRef ref)
    {
        Observable<Boolean> resultObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
                .delete(ref);
    }

    private void exampleListenToLiveConversationMessages(@NonNull QredoClient qredoClient, @NonNull ConversationRef ref)
    {
        Observable<ConversationMessage> conversationMessagesObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
                .listen(ref);
    }
    //</editor-fold>

    @Override protected void onDestroy()
    {
        clientSubscription.unsubscribe();
        super.onDestroy();
    }
}
