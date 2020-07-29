# PlayerManagement 1.5

PlayerManagement is a server-side Bukkit plugin for handling player data and events. It's originally been written for use on RP (role-play) servers, in an attempt to provide (somewhat) realistic economy.

## Features

- support for common role-play features (companies, jobs, etc.)
- all player data is stored in an SQLite database file
- enchanted books used as in-game "ID cards" to retrieve the data about a specific player
- support for permission-based ranks
- an enhanced and customizable <kbd>Tab</kbd> player list with support for ranks
- rewards on completed advancements
- signs used as scoreboards

RP features | Enhanced player list | Advancement rewards | Scoreboard signs
----------- | -------------------- | ------------------- | ----------------
![RP features](https://user-images.githubusercontent.com/35228139/84886093-3ccdf380-b094-11ea-887c-a6572495be22.png) | ![Enhanced player list](https://user-images.githubusercontent.com/35228139/84885811-d6e16c00-b093-11ea-8e2a-f69cecfd1be7.png) | ![Advancement rewards](https://user-images.githubusercontent.com/35228139/84885878-ed87c300-b093-11ea-9316-31a8caceeeb5.png) | ![Scoreboard signs](https://user-images.githubusercontent.com/35228139/84886242-6edf5580-b094-11ea-8d51-327f535fdcae.png)

Features not directly related to role-play can be turned off to avoid conflicts with other plugins.

## Dependencies

This plugin requires Bukkit >= 1.14.4. You also need to have installed the following plugins:

- [EssentialsX](https://github.com/EssentialsX/Essentials) >= 2.16.1
- Vault >= 1.7

Older versions of these plugins *might* work, but are not supported. This plugin has also been tested to work on Bukkit 1.15.2.

## Usage

Download [the latest release](https://github.com/RedCreator37/PlayerManagement/releases) and put it into `plugins` folder of your server. Run your server for the first time to generate the config files and a blank database.

> ℹ It's highly recommended that you stop the server at this point to make sure everything is [configured properly](./Config.md).

Once in the game, run `/registerid Some Name` to register yourself into the database. *Some Name* can be anything and can contain spaces.

> ⚠ NOTE: all commands are restricted to OP players by default. Make sure to give other players [the correct permissions](./Config.md), otherwise they won't be able to register themselves.

To use top player signs:

- Create a sign with just `[topplayers]` in the first line (this is not case-sensitive, `[TopPlayers]` will also work)
- Press `Done` or press <kbd>Esc</kbd> on the keyboard
