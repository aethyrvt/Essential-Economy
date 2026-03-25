# EssentialEconomy
A lightweight, modern economy plugin built for Paper servers.

## Features
- **Dual storage** — Choose between YAML (file-based) or MySQL
- **Fully customizable messages** — Edit every message in `config.yml` using legacy color codes (`&a`, `&c`, etc.)
- **Locale-aware formatting** — Configure your currency's number format per locale (e.g. `en-US`, `de-DE`)
- **Custom currency** — Set singular/plural currency names and an optional custom symbol
- **Vault support** — Integrates with other plugins via the Vault API
- **Suffix shorthand** — Accept amounts like `10k`, `5m`, `1b` in commands
- **Balance leaderboard** — Auto-refreshing `/balancetop` with configurable update interval
- **Hot reload** — Reload config without restarting with `/money reload`

## Requirements
- **Paper** 1.21+ (or any Paper fork)
- **Java** 17+
- **Vault** (required — the plugin will not start without it)

## Commands

| Command | Description | Permission |
|---|---|---|
| `/balance` | View your balance | `essentialeconomy.balance` |
| `/balance <player>` | View another player's balance | `essentialeconomy.balance.other` |
| `/pay <player> <amount>` | Pay another player | `essentialeconomy.pay` |
| `/balancetop [n]` | Show the top n wealthiest players | `essentialeconomy.balancetop` |
| `/money give <player\|*> <amount>` | Give money to a player or all | `essentialeconomy.money.give` |
| `/money take <player\|*> <amount>` | Take money from a player or all | `essentialeconomy.money.take` |
| `/money set <player\|*> <amount>` | Set a player's balance | `essentialeconomy.money.set` |
| `/money reload` | Reload the plugin config | `essentialeconomy.money.reload` |
| `/money help` | Show command help | *(none)* |

## Configuration
Key settings in `config.yml`:

```yaml
currencyNameSingular: "Dollar"
currencyNamePlural: "Dollars"
startingBalance: 100.00
locale: "en-US"              # Controls number formatting
customSymbolEnabled: false   # Optional custom currency symbol

mysql:
  use-mysql: false           # Set to true to use MySQL instead of YAML
  host: "localhost"
  port: 3306
  database: "database"
  username: "username"
  password: "password"

BalanceTopTimerInterval: 1200  # Leaderboard refresh rate in ticks (1200 = 60s)
```

See [`config.yml`](src/config.yml) for the full messages and suffix configuration.

## Installation
1. Drop `EssentialEconomy.jar` into your `plugins/` folder.
2. Install [Vault](https://www.spigotmc.org/resources/vault.34315/).
3. Start your server — a default `config.yml` will be generated.
4. Edit `plugins/EssentialEconomy/config.yml` to your liking, then run `/money reload`.

