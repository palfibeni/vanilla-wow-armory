# Vanilla WoW Armory with Discord interface

## Features
- Invitable Discord bot with commands:

-- Anonymus users will have access to:
  | Command             | Description |
  | ------------------- | ----------- |
  | $search searchText* | Search for various ingame objects |
  | $register           | Register to the application |
-- Registered users will access to:
  | Command                         | Description |
  | ------------------------------- | ----------- |
  | $character-create               | Create character, this will initiate a questions, which the user have 15 second to answer each. |
  | $character-list [@userMention]  | List self, or mentioned user's characters. |

More planned feature here: [Vanilla WoW Armory Trello board](https://trello.com/b/sDiDwVVN/vanilla-wow-armory)

## Installation
Currently the bot is not deployed, so you have to run it on your computer, using MAVEN to install, build, and run it.
You have to set first the ownerId, and discord bot token in the application.properties, which I added a sample only.
