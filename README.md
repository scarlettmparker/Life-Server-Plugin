# VGS Secret Life Server Plugin

# Plugin Information
- **Name**: VideoGamesLifeServer
- **Main Class**: `org.scarlettparker.videogameslifeserver.Main`
- **API Version**: `1.20`
- **Author**: Scarlett Parker

# Player Commands

### /givelife
- **Description**: Give a life to a player.
- **Usage**: `/givelife <player>`

### /whattask
- **Description**: Displays the player's current task. Admins will see excluded players.
- **Usage**: `/whattask`

### /whatttask taskID
- **Description**: Admin only command to see task difficulty and description by task ID.

### /tokens
- **Description**: Displays current number of tokens.
- **Usage**: `/tokens`

### /gift
- **Description**: Gift a player a number of tokens.
- **Usage**: `/gift <player> <tokens>`

# Admin Commands
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

## Task Commands

### /addtask
- **Description**: Adds a new task. Use tags {receiver} or {sender} in the description to display a random player's name, or the player with the task's name respectively in the description.
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

### /settask
- **Description**: Sets a player's task.
- **Usage**: `/settask <player> <taskID>`

## Shop & Other

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

# Rules and Events

## Life Rules

- Players cannot hold more than 4 lives at a time.
- Players once revived cannot be gifted more than one life.
- Players may only attempt 2 tasks per session.

## World Events

- Player information such as deaths and task history are stored in the playerbase.json file.
- It's recommended to turn on keepInventory with `/gamerule keepInventory true` as players will drop their items when losing their final life.
- It's recommended to turn off showDeathMessages with `/gamerule showDeathMessages false` as there are custom death messages are in place.

# Example Usage

## Here are some example usages of the commands:
- To give a life to a player named Scarlett: `/givelife Scarlett`
- To set the number of lives for a player named Scarlett to 5: `/setlife Scarlett 5`
- To set the punishment for a player named Scarlett: `/setlife Scarlett knockback`
- To add a new task: `/addtask`, and follow through the steps message by message.
- To clear the task of a player named Scarlett: `/cleartask Scarlett`
 - To clear the punishments of a player named Scarlett: `/clearpunishments Scarlett`

