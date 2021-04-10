package com.chattriggers.ctjs.commands

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.CTJSMod
import com.chattriggers.ctjs.triggers.OnTrigger

//#if MC==10809
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
//#else
//$$ import com.mojang.brigadier.CommandDispatcher
//$$ import com.mojang.brigadier.arguments.StringArgumentType
//$$ import com.mojang.brigadier.context.CommandContext
//$$ import net.minecraft.command.CommandSource
//$$ import net.minecraft.command.Commands
//$$ import com.mojang.brigadier.tree.CommandNode
//#endif

//#if MC==10809
import net.minecraftforge.client.ClientCommandHandler
//#else
//#endif

class Command(
    trigger: OnTrigger,
    private val name: String,
    private val usage: String,
    private val tabCompletionOptions: MutableList<String>
//#if MC==10809
) : CommandBase() {
//#else
//$$ ) {
//#endif
    var triggers = mutableListOf(trigger)
        private set

    //#if MC==10809
    override fun getCommandName() = this.name

    override fun getRequiredPermissionLevel() = 0

    override fun getCommandUsage(sender: ICommandSender) = this.usage

    override fun addTabCompletionOptions(
        sender: ICommandSender?,
        args: Array<out String>?,
        pos: BlockPos?
    ): MutableList<String> {
        return this.tabCompletionOptions
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender, args: Array<String>) = trigger(args)
    //#endif

    fun register() {
        activeCommands[name] = this

        //#if MC==10809
        ClientCommandHandler.instance.registerCommand(this)
        //#else
        //$$ fun execute(context: CommandContext<CommandSource>): Int {
        //$$     val args = context.nodes.map {
        //$$         context.input.substring(it.range.start, it.range.end)
        //$$     }.toTypedArray()
        //$$     triggers.forEach { it.trigger(args) }
        //$$     return 1
        //$$ }
        //$$
        //$$ var command = Commands.literal(name)
        //$$
        //$$ for (option in tabCompletionOptions) {
        //$$     command = command.then(Commands.argument(
        //$$         option,
        //$$         StringArgumentType.greedyString()
        //$$     )).executes(::execute)
        //$$ }
        //$$
        //$$ CTJS.commandDispatcher.register(command.executes(::execute))
        //#endif
    }

    fun unregister() {
        activeCommands.remove(name)

        //#if MC==10809
        ClientCommandHandler.instance.commandSet.remove(this)
        ClientCommandHandler.instance.commandMap.remove(name)
        //#else
        //$$ CTJS.commandDispatcher.root.children.removeIf { it.name == name }
        //#endif
    }

    private fun trigger(args: Array<String>) {
        triggers.forEach { it.trigger(args) }
    }

    companion object {
        internal val activeCommands = mutableMapOf<String, Command>()
    }
}
