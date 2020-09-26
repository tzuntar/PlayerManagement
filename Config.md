# Configuration File Values

The following is the structure of the config file as of PlayerManagement 1.6:

```yaml
General:
  OutputPrefix: §8[§9Server§8]  # chat output prefix
  DatabasePath: PlayerData.db   # the database file name / path
  DateFormat: yyyy-MM-dd        # date format to use when storing player data, must be a valid Java date format!
  CardPrice: 2500.0             # the price of an ID card (ignored on /registerid)
  CardItemLore: Unique Player ID Card # ID card item lore (changing this will invalidate all existing ID cards on the server!)
  ScoreboardSignText: §8[§9TopPlayers§8]  # the formatted text that [TopPlayers] on signs will be replaced with
Company:
  EstablishPrice: 3000.0        # the price of establishing a new company
Punishments:
  Amount: 3000.0                # the default fine
  MaxBeforeBan: 5               # the number of times a player can be punished before they're auto-banned
AutomaticEconomy:
  Enabled: true                 # toggles the automatic economy
  TimeInSeconds: 1200           # the interval in seconds
  Threshold: 1000.0             # the balance after which an unemployed player will stop receiving money
  MoneyAmount: 250.0            # base amount of money, given to unemployed players
Rewards:
  Enabled: true                 # toggles advancement rewards
  Reward:
    Rank1: 10.0                 # rank 1 reward
    Rank2: 25.0                 # rank 2 ...
    Rank3: 50.0                 # rank 3 ...
  Advancements:
    Rank1:
    - story/mine_stone          # list of rank 1 advancements
    # ...
    Rank2:
    - story/form_obsidian       # list of rank 2 ...
    # ...
    Rank3:
    - story/shiny_gear          # list of rank 3 ...
    # ...
PlayerList:
  Enabled: true                 # toggles the advanced TAB playerlist
  UpdateIntervalSeconds: 300    # the interval at which the *non-player parts of the list* will be updated
  Header:                       # the output before the list of players
  - §b§oA Minecraft Server                                   §r§a{playercount}§8/§a{maxplayers}
  - §8--------------------------------------------
  Footer:                       # the output after the list of players
  - §8--------------------------------------------
  - '§6Money: §f{playerbalance}    §r§8: :    §6Messages: §f{playermail}'
  - '§6Gamemode: §f{playergamemode}    §r§8: :    §6World: §f{playerworld}'
  - '§6Rank: §f{playerrank}    §r§8: :    §6Company: §f{playercompany}'
  - §8--------------------------------------------
  - §7§oStatistics are updated every 5 min
  - §8--------------------------------------------
  - '§4YouTube:   §r§f§nlink§r      §r§8: :      §9Discord:   §r§f§nlink§r             '
  - |2-

    §8§oPowered by PlayerManagement v1.6 by RedCreator37
  # you can remove these 3 lines ^^^
  Display:                      # formatting used for players of each rank in the playerlist
    Generic: §8[ §f{playername} §8]
    Members: §8[ §a{playername} §8]
    VIP: §8[ §b{playername} §8]
    Admins: §8[ §c{playername} §8]
  Label:                        # formatted rank labels
    Generic: §fNone
    Members: §aMembers
    VIP: §bVIP
    Admins: §cAdmins
```

## Placeholders

These are the `{placeholders}` supported as of PlayerManagement 1.6:

```yaml
{playercount}     # the number of online players
{maxplayers}      # the number of max players allowed
{playername}      # username of the current player
{playerbalance}   # balance of the current player
{playermail}      # mail count of the current player
{playerworld}     # world in which the player is located
{playerrank}      # rank of the current player
{playercompany}   # company of the current player
{playergamemode}  # gamemode of the current player
```

## Permissions

The following permissions are used by this plugin:

```yaml
management.user:
  description: The recommended user-level permission
  default: op
management.admin:
  description: Gives access to all commands
  default: op
management.company:
  description: Gives access to most company-related commands
  default: op
management.company.establish:
  description: Gives a player the permission to establish a new company
  default: op
management.company.employ:
  description: Gives a player the permission to employ at other companies
  default: op
playerlist.member:
  description: Member rank
  default: op
playerlist.vip:
  description: VIP rank
  default: op
playerlist.admin:
  description: Admin rank
  default: op
```
