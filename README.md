# RxQredo

Helper methods to facilitate the use of Qredo methods in an Rx workflow.

## Vault Usage:
### Get a `QredoClient`
```Java
Observable<QredoClient> clientObservable = new QredoClientObservable(new QredoClientAdapter())
    .bind(
        "YOUR APP SECRET",
        "USER ID",
        "USER SECRET",
        this); // Context
```
It is your responsibility to `.unsubscribe()`, to unbind, in order to avoid memory leakage.

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
It is your responsibility to `.unsubscribe()` in order to avoid memory leakage.
