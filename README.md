# CarnacoSolverX
bruh, this name sound funny asf

## This plugin makes your chat and TabList prettier
- Adds prefixes, suffixes, automatic message coloring and HEX styling for them.
- You can use HEX styles in actual message.
- Also you could add some emoji placeholders or replace default minecraft`s &x colors to any HEX color you prefer.
- Ping display in TabList is also one of the functions of this plugin.

## Commands
- **/prefix <set/clear> <player> [prefix]**
  - Sets or clears to default player`s prefix in chat and tablist. Supports HEX.
  - **Example**: `/prefix set N0rM1x &#ffBB00Friend &e` `/prefix clear N0rM1x`
- **/suffix <set/clear> <player> [suffix]**
  - Sets or clears to default player`s suffix in chat and tablist. Supports HEX.
  - **Example**: `/suffix set N0rM1x &#ffBB00 cool` `/suffix clear N0rM1x`
- **/chatcolor <set/clear> <player> [chat color]**
  - Sets or clears to default player`s regular chat color. Supports HEX.
  - **Example**: `/chatcolor set N0rM1x &#ffBB00` `/chatcolor clear N0rM1x`

## Other features
- Emoji placeholders (`emojis.yml`)
  - **Example**: `"heart": "❤️"`
  - Will change `:heart:` in chat to `❤️`
- Modifiyng default minecraft color placeholders [`&x`] (`colors_config.yml`)
  - **Example**: `d: "<#ff0000>"`
  - Will make everything after `&d` placeholder in chat to be painted completely **red** (`#ff00000`)
- Language changing
  - Used only in commands feedback, you could change it in config.yml, for **example**: `"lang: en-US"` will change command feedback language to **English**.
  - For now plugin contains 3 languages: **English(en-US), Russian(ru-RU) and Ukrainian(ua-UA)**.
  - Also you could add your own langauge in `message_config` directory.

## Info

If you have some suggestions or want support for some languages, feel free to tell about that in Discussion tab.
Plugin made for Minecraft 1.21.4, works on Paper and Purpur
