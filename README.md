# Vanilla Enchants

VanillaEnchants is a Minecraft server plugin for Bukkit/Spigot that modifies the level cap of enchantments created by combining items on an anvil. For example if you combine two diamond pickaxes, each with Efficiency V, you get a pickaxe with Efficiency VI.

Vanilla Enchants has been tested on `Minecraft 1.14.4` using Spigot

## Why does this exist?

I created this plugin because I was unsatisfied with the alternative options for custom enchants. In my opinion, the quality of a plugin has a direct relation to how many chat commands it registers. This plugin is designed to be intuitive to the player and merely augment existing game mechanics. There are no chat commands, and as of now no permissions.

## Mechanics

- Combining items in an anvil with the same enchantment level will increase the enchantment by 1 (up to the custom limit)
- The item in the left slot will always be the resulting item. This allows players to put enchants into a book.
- Enchantments that don't normally go on an item can be used (Ex. Efficiency on a helmet)
- Books can enchant ANY item (not necessarily intended, but not up to doing that extra logic at the moment)
- Mutual exclusivity is ignored (ie. you can have Smite and Sharpness on the same sword)
- The cost of combining enchants on a book is equal to the sum total of all levels on the resulting book.
  - Ex: Book 1 has Looting IV, Book 2 has Power II, the cost to combine them would be (4 + 2) = 6

## Installation

Copy the built jar file to

```bash
<your_server_directory>/plugins/
```

After you run the server for the first time after installation a config file will be created at

```bash
<your_server_directory>/plugins/VanillaEnchants/config.yml
```

## Configuration

The config file is where you set the limits for each enchantment. Using the debug flag, you can find the Minecraft name of any new enchantments that may be introduced to the game in future versions.

#### Default Config
```yaml
#
# VanillaEnchants Configuration File
#
# About editing this file:
# - DO NOT USE TABS. You MUST use spaces or Bukkit will complain and log errors.
# - If you want to check the format of this file before using it,
#   use http://yaml-online-parser.appspot.com/ to validate it.
# - Lines starting with # are comments, and will be ignored.
#

# You can disable the plugin with this value
enable_vanilla_enchants: "true"  # "true" or "false"

# Set this to "true" to find any unknown enchantment names
# The enchantment names of each enchantment on the resulting item will be
# output to the console, so an easy way to find it would be to combine two books.
# ONLY OUTPUTS FOR EVENTS FROM OPS
debug: "false"

# By default VanillaEnchants will use the default enchant limits as seen below. (as of 1.14.4)
#
# To set a limit to infinity (or rather, MAX_INT), comment out the entry by
# putting a '#' at the beginning of the line (or deleted the line)
#
# THIS MEANS ANY ENCHANTMENT NOT IN limits WILL HAVE NO LIMIT
#
# If a new enchantment is added to minecraft, you just need to add the enchantment's
# minecraft name/id below to set the limit as desired. (use debug flag to find it!)
#
# Setting the limits lower than the defaults will not affect enchantments from
# the enchantment table.
# The only thing affected is the anvil repair/combine mechanic. Enchantments will
# not be removed or lowered if the limit is lower that the items current level, it
# will simply not be possible to upgrade that enchantment.

# If you wanted to disable upgrading an enchantment's level you can set the limit to 1 (or 0)
# this could be fun for a more "hardcore" vanilla experience.

limits:
  aqua_affinity: 1
  bane_of_arthropods: 5
  blast_protection: 4
  channeling: 1
  binding_curse: 1       # this is Curse of Binding
  vanishing_curse: 1     # this is Curse of Vanishing
  depth_strider: 3
  efficiency: 5
  feather_falling: 4
  fire_aspect: 2
  fire_protection: 4
  flame: 1
  fortune: 3
  frost_walker: 2
  imaling: 5
  infinity: 1
  knockback: 2
  looting: 3
  loyalty: 3
  luck_of_the_sea: 3
  lure: 3
  mending: 1
  multishot: 1
  piercing: 4
  power: 5
  projectile_protection: 4
  protection: 4
  punch: 2
  quick_charge: 3
  respiration: 3
  riptide: 3
  sharpness: 5
  silk_touch: 1
  smite: 5
  soul_speed: 3
  sweeping: 3            # this is Sweeping Edge
  thorns: 3
  unbreaking: 3

```

## Known Issues/Idiosyncrasies
- If you shift click the resulting item, sometimes it will appear to have been duplicated (duplicate item disappears when interacting with it)
- An enchanting book can be applied to ANY item.
- If a player is in survival mode, and a repair costs more than 40 levels, the UI shows "TOO EXPENSIVE" (a chat message is sent to the player stating the actual cost). The repair still goes through
- The resulting cost is just the sum of the resulting levels of all enchantments, rather than the mystical vanilla mechanics

## Contributing
Pull requests are welcome!

## HELP I found a bug!
Good work! Create an issue on GitHub and I will look into it as soon as I can.
