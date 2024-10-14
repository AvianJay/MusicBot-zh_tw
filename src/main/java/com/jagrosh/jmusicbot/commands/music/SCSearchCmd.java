/*
 * 版權所有 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * 根據 Apache License 2.0 版（以下簡稱「許可證」）授權使用本文件；
 * 除非遵守許可證，否則您不得使用本文件。
 * 您可以在以下網址獲取許可證副本：
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非適用法律要求或書面同意，根據許可證分發的軟體按「現狀」提供，
 * 不附帶任何明示或默示的保證或條件。
 * 請參閱許可證以瞭解具體的許可權和限制。
 */
package com.jagrosh.jmusicbot.commands.music;

import com.jagrosh.jmusicbot.Bot;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SCSearchCmd extends SearchCmd 
{
    public SCSearchCmd(Bot bot)
    {
        super(bot);
        this.searchPrefix = "scsearch:"; // 使用 Soundcloud 搜索的前綴
        this.name = "scsearch"; // 指令名稱為 scsearch
        this.help = "搜尋 Soundcloud 上提供的查詢"; // 指令說明
        this.aliases = bot.getConfig().getAliases(this.name); // 指令的別名
    }
}
