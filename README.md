# RxQredo

Helper methods to facilitate the use of Qredo methods in an Rx workflow.

## Getting a Connection:
### Get a `QredoClient`
```Java
Observable<QredoClient> clientObservable = new QredoClientObservable(new QredoClientAdapter())
    .bind(
        "YOUR APP SECRET",
        "USER ID",
        "USER SECRET",
        this); // Context
```
Returns a single `QredoClient`, but never calls `.onCompleted()`.
It is your responsibility to call `subscription.unsubscribe()`, as this triggers the `unbind` method, in order to avoid memory leakage.

## Vault Usage:
### Put a `VaultItem`
```Java
Observable<VaultItemRef> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
    .put(item);
```
Returns a single `VaultItemRef`.

### Get a `VaultItemRef`
```Java
Observable<VaultItem> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
    .get(ref);
```
Returns a single `VaultItem`.

### Delete a `VaultItemRef`
```Java
Observable<Boolean> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
    .delete(ref);
```
Returns a single `Boolean`.

### Update a `VaultItemRef`
```Java
Func1<VaultItem, VaultItem> updater;
Observable<VaultItemRef> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
    .update(ref, updater);
```
Returns a single `VaultItemRef`, that of the updated `VaultItem`. The previous version has been deleted.
The `updater` takes in the old version and you need to make it return the new version.

### Update a `VaultItemRef` asynchronously
```Java
Func1<VaultItem, Observable<VaultItem>> updater;
Observable<VaultItemRef> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
    .updateAsync(ref, updater);
```
Returns a single `VaultItemRef`, that of the updated `VaultItem`. The previous version has been deleted.
The `updater` takes in the old version and you need to make it return the new version in an `Observable`.

### List Item Headers
```Java
Observable<Set<VaultItemHeader>> headersObservable = new VaultManagerObservable(qredoClient.getVaultManager())
                .listHeaders();
```
Returns a single `Set`.

### Find Item Headers
```Java
VaultItemHeaderMatcher matcher;
Observable<Set<VaultItemHeader>> headersObservable = new VaultManagerObservable(qredoClient.getVaultManager())
    .findHeaders(matcher);
```
Returns a single `Set`.

### Listen to live `VaultItem` updates
```Java
Observable<VaultItemHeader> liveVaultItemUpdates = new VaultManagerObservable(qredoClient.getVaultManager())
    .listen();
```
Returns 0 or more `VaultItem`, but never calls `.onCompleted()`.
It is your responsibility to call `subscription.unsubscribe()` , as this triggers the `removeListener` method, in order to avoid memory leakage.

## Rendezvous Usage:
### Create a `Rendezvous`
```Java
Observable<Rendezvous> rendezvousObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
    .create(params);
```
Returns a single `Rendezvous`.

### Respond to a `Rendezvous`
 ```Java
Observable<ConversationRef> conversationRefObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
     .respond(tag);
 ```
 Returns a single `ConversationRef`.

 ### Get a `Rendezvous`
 ```Java
Observable<Rendezvous> rendezvousObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
     .get(ref);
 ```
 Returns a single `Rendezvous`.

 ### Activate a `Rendezvous`
 ```Java
Observable<Rendezvous> rendezvousObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
     .activate(ref, duration);
```
Returns a single `Rendezvous`.

### Deactivate a `Rendezvous`
```Java
Observable<Boolean> resultObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
    .deactivate(ref);
```
Returns a single `Boolean`.

### List `Rendezvous`
```Java
Observable<Set<Rendezvous>> rendezvousesObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
    .list();
```
Returns a single `Set`.

### Listen to live new `Rendezvous`
```Java
Observable<Rendezvous> rendezvousObservable = new RendezvousManagerObservable(qredoClient.getRendezvousManager())
    .listen();
```
Returns 0 or more `Rendezvous`, but never calls `.onCompleted()`.
It is your responsibility to call `subscription.unsubscribe()` , as this triggers the `removeListener` method, in order to avoid memory leakage.

## Conversation Usage:
### List `Conversation` by `RendezvousRef`
```Java
Observable<Set<Conversation>> conversationsObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
    .list(ref);
```
Returns a single `Set`.

### List `Conversation` by Tag
```Java
Observable<Set<Conversation>> conversationsObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
    .list(tag);
```
Returns a single `Set`.

### List All `Conversation`
```Java
Observable<Set<Conversation>> conversationsObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
    .listAll();
```
Returns a single `Set`.

### Get a `Conversation`
```Java
Observable<Conversation> conversationObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
    .get(ref);
```
Returns a single `Conversation`.

### Delete a `Conversation`
```Java
Observable<Boolean> resultObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
    .delete(ref);
```
Returns a single `Boolean`.

### Leave a `Conversation`
```Java
Observable<Boolean> resultObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
    .leave(ref);
```
Returns a single `Boolean`.

### Listen to live created `Conversation`
```Java
Observable<Conversation> conversationsObservable = new ConversationManagerObservable(qredoClient.getConversationManager())
    .listen(ref);
```
Returns 0 or more `Conversation`, but never calls `.onCompleted()`.
It is your responsibility to call `subscription.unsubscribe()` , as this triggers the `removeListener` method, in order to avoid memory leakage.

## ConversationMessage Usage:
## Get a `ConversationMessage`
```Java
Observable<ConversationMessage> conversationMessageObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
    .get(ref);
```
Returns a single `ConversationMessage`.

## List Message Headers of a `Conversation`
```Java
Observable<List<ConversationMessageHeader>> conversationMessageHeadersObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
    .listHeaders(ref);
```
Returns a single `List`.

## List `ConversationMessage`s
```Java
Observable<List<ConversationMessage>> conversationMessagesObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
    .listMessages(ref);
```
Returns a single `List`.

## Delete a `ConversationMessage`
```Java
Observable<Boolean> resultObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
    .delete(ref);
```
Returns a single `Boolean`.

## Listen to live create `ConversationMessage`
```Java
Observable<ConversationMessage> conversationMessagesObservable = new ConversationMessageManagerObservable(qredoClient.getConversationMessageManager())
    .listen(ref);
```
Returns 0 or more `ConversationMessage`, but never calls `.onCompleted()`.
It is your responsibility to call `subscription.unsubscribe()` , as this triggers the `removeListener` method, in order to avoid memory leakage.
