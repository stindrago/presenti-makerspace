# Presenti Makerspace

Un BOT Telegram per segnare le presenze al makerspace.

Il database e' in `.yaml`.

## Comands

Aggiungi una nuova persona

```
/persona Isaac Asimov
```

Aggiungi un nuovo giorno di presenza

```
/presenza Isaac Asimov 2021-09-21
```

Esporta i dati

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
1. Esegui lo script `init.sh` fa tutto in automatico

``` shell
sh init.sh
```

## Development

``` shell
lein run
```

## Deploy

``` shell
java -jar ./target/uberjar/presenti-makerspace-0.1.0-SNAPSHOT-standalone.jar
```

## [License](./LICENSE)

The Unlicensed
