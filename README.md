# Presenti Makerspace

Un BOT Telegram per segnare le presenze al makerspace.

Il database e' in `.yaml`.

## Comands

Aggiungi una nuova persona.

```
/persona Isaac Asimov
```

Aggiungi un nuovo giorno di presenza.

```
/presenza Isaac Asimov 2021-09-21
```

Esporta i dati.

```
/export
```

## Dependencies

- Java
- Clojure
- Leiningen

## Installation

1. Assicurati di aver installato le dipendenze.
1. Aggiungi il [bot token](https://core.telegram.org/bots) in `token.env`.
1. Esegui lo script `init.sh` fa tutto in automatico.

``` shell
sh init.sh
```

## Development

``` shell
lein run
```

## Deploy

Di seguito trovi due modi per fare avviare l'app.

### Shell

Per avviare l'app da shell.

``` shell
export TELEGRAM_TOKEN="<TOKEN>"
java -jar ./target/uberjar/presenti-makerspace-0.1.0-SNAPSHOT-standalone.jar
```

### PM2

Se vuoi usare [PM2](https://pm2.keymetrics.io/docs/usage/quick-start/) per facilitarti la gestione dei processi.

```shell
export TELEGRAM_TOKEN="<TOKEN>"
pm2 start java -jar ./target/uberjar/presenti-makerspace-0.1.0-SNAPSHOT-standalone.jar
```

Comandi PM2 utili.

```shell
pm2 ls # lista processi.
pm2 0 # log del processo.
```

## Troubleshooting

### Il bot risponde solo a `/start`

Verifica che l'`ID` della chat con il bot sia presente in [core.clj](./src/presenti_makerspace/core.clj). Aggiungi un nuovo `<CHAT ID>`.

```clojure
(def whitelist #{<CHAT ID> <CHAT ID> ...})
```

Per conoscere la `CHAT ID` avvia il bot e copia il numero in `:id`.

```
Bot joined new chat:  {:id 703914890, ...}
```

[isseu #6](https://gitlab.com/pdpfsug/proj/presenti-makerspace/-/issues/6)

## [Missing Features](https://gitlab.com/pdpfsug/proj/presenti-makerspace/-/issues?label_name%5B%5D=feature-request)

- Rimozione di una persona.
- Rimozione di una presenza.
- Descrizione della presenza.
- Esportazione CSV compatibile con i programmi foglio di calcolo.

## [License](./LICENSE)

The Unlicensed
