# Vanilla WoW Armory with Discord interface

## Features
### Invitable Discord bot with commands:

- Anonymus users will have access to:

  | Command | Description | Aliases|
  | ------------------- | --------------------------------------------- | ----- |
  | $help               | Shows information about the use of the bot    | h |
  | $search searchText* | Search for various ingame objects             | s |
  | $register           | Register to the application                   | reg, r |
  
- Registered users will access to:

  | Command | Description | Aliases|
  | ------------------------------- | ------------------------------------------------------------------------------------------------- | ---------- |
  | $character-create               | Create character, this will initiate a questions, which the user have 15 second to answer each.   | create, cc |
  | $character-list [@userMention]  | List self, or mentioned user's characters.                                                        | list, l |
  | $character-talent [charName]    | Set character's talent                                                                            | talent, ct |

More planned feature here: [Vanilla WoW Armory Trello board](https://trello.com/b/sDiDwVVN/vanilla-wow-armory)

## Installation
Currently the bot is not deployed, so you have to run it on your computer, using MAVEN to install, build, and run it.
You have to set first the ownerId, and discord bot token in the application.properties, which I added a sample only.
