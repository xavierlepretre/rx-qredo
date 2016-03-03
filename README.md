# RxQredo

Helper methods to facilitate the use of Qredo methods in an Rx workflow.

## Usage:
### Get a `QredoClient`
```Java
Observable<QredoClient> clientObservable = new QredoClientObservable(new QredoClientAdapter())
    .bind(
        "YOUR APP SECRET",
        "USER ID",
        "USER SECRET",
        this); // Context
```
Do not forget to `.unsubscribe()` to unbind.

### Get a `VaultItem`
```Java
Observable<VaultItem> itemObservable = new VaultManagerObservable(qredoClient.getVaultManager())
    .get(ref);
```

### Listen to live `VaultItem` updates
```Java
Observable<VaultItemHeader> liveVaultItemUpdates = new VaultManagerObservable(qredoClient.getVaultManager())
    .listen();
```
Do not forget to `.unsubscribe()`.
