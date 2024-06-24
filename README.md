# VGS Secret Life Server Plugin

## Table of Contents
* [Plugin Information](#plugin-information)
* [Setup and Installation](#setup-and-installation)
    * [Dependencies](#dependencies)
    * [Compilation](#compilation)
    * [Usage on Minecraft](#usage-on-minecraft)
* [Player Commands](#player-commands)
* [Admin Commands](#admin-commands)
* [Setup Commands](#setup-commands)
* [Task Commands](#task-commands)
* [Shop & Other](#shop-&-other)
* [Extra Information](#extra-information)
    * [Life Rules](#life-rules)
    * [World Events](#world-events)
    * [Task Tags](#task-tags)
    * [List of Punishments](#list-of-punishments)
* [Example Usages](#example-usages)
* [Contributions](#contributions)
* [License](#license)

## Plugin Information
- **Name**: VideoGamesLifeServer
- **Main Class**: `org.scarlettparker.videogameslifeserver.Main`
- **API Version**: `1.20`
- **Author**: Scarlett Parker

This plugin is based on the Secret Life YouTube series, however, it is not played on Ultra Hardcore. The server is set to normal difficulty and completing tasks reward players with tokens that can be spent in the shop for powerful items. Failing hard and red tasks result in a custom punishment, and failing/completing tasks announces to the chat of the failure/completion.

## Setup and Installation

### Dependencies
Though not required, this server uses the [NametagEdit](https://www.spigotmc.org/resources/nametagedit.3836/) plugin to modify name colours above player heads. Without NametagEdit, that feature of the plugin won't work.

### Compilation

First, ensure you have [Maven](https://maven.apache.org/download.cgi) installed. If not, download and install it from Maven's official website and follow the [installation instructions](https://maven.apache.org/install.html) to ensure you can use it in your terminal.\
In the root directory of the project, run `mvn clean install` to compile the project. The compiled .jar file	can then be found in the `target` directory as `VideoGamesLifeServer.jar`.

### Usage on Minecraft
This plugin can be used by placing the compiled .jar file into the `plugins` folder of a Minecraft server that supports Spigot plugins. Simply put the file in and reload/start the server.\
There are a base 101 tasks that come with the plugin set-up. You may use the commands listed below to add, exclude or remove tasks to your discretion.

## Player Commands

### /givelife
- **Description**: Give a life to a player. Can be used from console for an unlimited source of lives.
- **Usage**: `/givelife <player>`

### /whattask
- **Description**: Displays the player's current task. Admins will see excluded players.
- **Usage**: `/whattask`

### /whatttask <task>
- **Description**: Admin only command to see task difficulty and description by task ID.

### /tokens
- **Description**: Displays current number of tokens.
- **Usage**: `/tokens`

### /gift
- **Description**: Gift a player a number of tokens. Can be used from console for an unlimited source of tokens.
- **Usage**: `/gift <player> <tokens>`

## Setup Commands

### /startlife
- **Description**: Starts the life server.
- **Usage**: `/startlife <lives>`

### /startlife confirm
- **Description**: Performs the same thing as /startlife, only if a player file already exists

### /starttasks
- **Description**: Hands out tasks for the players at the start of the session.
- **Usage**: `/starttasks`

### /starttasks reset
- **Description**: Resets the task JSON file to its default from the source code.

### /cleartask all
- **Description**: Clears everyone's tasks and sets their session tasks to 0. Similar to starttasks, but without the task distribution.

### /filltasks
- **Description**: Give tasks to all players that currently don't have any tasks.

## Task Commands

Task commands require permission `vgs.tasks` to execute. For example, to complete tasks, players need the permission `vgs.tasks.completetask` to be able to complete tasks. These permissions exist only for `newtask`, `completetask` and `failtask`.

### /addtask
- **Description**: Adds a new task. Use tags {receiver}, {sender} or {player} in the description to display a random player's name, or the player with the task's name respectively in the description.
- **Usage**: `/addtask`

### /cleartask
- **Description**: Clears a player's task.
- **Usage**: `/cleartask <player>`

### /completetask
- **Description**: Completes a player's task.
- **Usage**: `/completetask <player>`

### /deletetask
- **Description**: Deletes a task.
- **Usage**: `/deletetask <taskID>`

### /edittask
- **Description**: Edits a task by its name.
- **Usage**: `/edittask`

### /excludeplayer
- **Description**: Excludes a player from a task.
- **Usage**: `/excludeplayer <taskID> <player>`

### /excludetask
- **Description**: Excludes a task.
- **Usage**: `/excludetask <taskID>`

### /failtask
- **Description**: Fails a user's task.
- **Usage**: `/failtask <player>`

### /forcetask
- **Description**: Force a player's next task.
- **Usage**: `/forcetask <player> <taskID>`

### /listtasks
- **Description**: Lists all (non-excluded) tasks. Hovering over tasks in chat will give their descriptions.
- **Usage**: `/listtasks [allowed|excluded]`

### /newtask
- **Description**: Gives a player a new task.
- **Usage**: `/newtask <normal|hard> <player>`

### /setpriority
- **Description**: Sets the priorty of a task.
- **Usage**: `/setpriority <taskID> priority`

### /settask
- **Description**: Sets a player's task.
- **Usage**: `/settask <player> <taskID>`

## Shop & Other

### /endtag
- **Description**: Ends the tag game, and will notify all players that it will soon be ending.
- **Usage**: `/endtag`

### /setlife
- **Description**: Sets a player's lives. Lives cannot be set below 1.
- **Usage**: `/setlife <player> <lives>`

### /settokens
- **Description**: Sets a player's tokens.
- **Usage**: `/settokens <player> <tokens>`
	
### /shop
- **Description**: Displays the token shop.
- **Usage**: `/shop`

### /setvillagershop
- **Description**: Sets up the villager shop.
- **Usage**: `/setvillagershop`

### /clearpunishments
- **Description**: Clears a player's punishments.
- **Usage**: `/clearpunishments <player>`

### /setpunishment
- **Description**: Adds a punishment to a player.
- **Punishment IDs**: 
- **Usage**: `/setpunishment <player> <punishmentID>`

## Extra Information

### Life Rules

- Players cannot hold more than 4 lives at a time.
- Players once revived cannot be gifted more than one life.
- Players may only attempt 2 tasks per session.

### World Events

- Player information such as deaths and task history are stored in the playerbase.json file.
- It's recommended you do not modify the game rules, as they will be set in place by the plugin.

### Task Tags

There are three task tags you can add to the task description, and are as follows:
- `{receiver}` will add a random online player to the task description, for tasks that are targeted at another player.
- `{sender}` will add the current task holder's name to the task description.
- `{player}` will add another player to the task, meaning multiple players will receive this task. For example, a task with the description "Beat {player} in a fist fight." will give 2 players the task, and they will see the opposing player's name in the description.

### List of Punishments

There are 4 different punishments that will be randomly given upon failing a hard task, and 1 that will be given upon failing a red task. They may also be set with `/setpunishment player punishment`, and are as follows:
- `weak1` will give the player Weakness I until they complete a task or die.
- `weak2` will give the player Weakness II until they complete a task or die.
- `fragile1` will give the player Fragility (35% increased damage) until they complete a task or die.
- `knockback` will give the player Featherweight (7x increased knockback) until they complete a task or die.
- `hearts6` will set the player to 6 hearts until they complete a task or die.

## Example Usages

### Here are some example usages of the commands
- To give a life to a player named Scarlett: `/givelife Scarlett`
- To set the number of lives for a player named Scarlett to 5: `/setlife Scarlett 5`
- To set the punishment for a player named Scarlett: `/setlife Scarlett knockback`
- To add a new task: `/addtask`, and follow through the steps message by message.
- To clear the task of a player named Scarlett: `/cleartask Scarlett`
 - To clear the punishments of a player named Scarlett: `/clearpunishments Scarlett`

## Contributions

Contributions to this project are welcome. Whether it's a bug fix, a small feature or simply commenting, I'm happy to discuss changes to the project. Make a pull request and we can discuss potential changes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.
