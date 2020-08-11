package com.redcreator37.playermanagement.Commands;

import org.bukkit.ChatColor;

import java.util.Arrays;

/**
 * Contains helper methods for command handler classes
 */
final class CommandHelper {

    /**
     * Non-instantiable
     */
    private CommandHelper() {
    }

    /**
     * Returns the formatted command usage help for this command
     *
     * @param cmd          the command (without the <code>/</code>)
     * @param argumentList the list of arguments, separate sub-arguments
     *                     with vertical bars (<code>|</code>). Prefix
     *                     non-optional arguments with an asterisk.
     *                     Example: for <code>/ping name [hello|hello1|hello2]
     *                     </code>: <code>{"*name", "hello|hello1|hello2"}</code>
     * @return the formatted string
     */
    static String parseCommandUsage(String cmd, String[] argumentList) {
        StringBuilder usage = new StringBuilder();
        usage.append("§6Usage: §a/").append(cmd).append(" ");
        for (String s : argumentList) {
            String[] args = s.split("\\|");
            if (s.charAt(0) == '*')
                usage.append(formatRequiredArgs(args)).append(" ");
            else usage.append(formatOptionalArgs(args)).append(" ");
        }
        return usage.toString();
    }

    /**
     * Returns the formatted list of optional command arguments
     *
     * @param arguments the command arguments to format
     * @return the formatted string
     */
    private static String formatOptionalArgs(String[] arguments) {
        StringBuilder result = new StringBuilder("§8[");
        for (String a : arguments)
            result.append(ChatColor.AQUA).append(a).append("§8|");
        result.deleteCharAt(result.length() - 1);
        result.append("§8]");
        return result.toString();
    }

    /**
     * Returns the formatted list of required command arguments
     *
     * @param arguments the command arguments to format
     * @return the formatted string
     */
    private static String formatRequiredArgs(String[] arguments) {
        StringBuilder result = new StringBuilder("§8[");
        for (String a : arguments)
            result.append(ChatColor.RED).append(a.substring(1)).append("§8|");
        result.deleteCharAt(result.length() - 1);
        result.append("§8]");
        return result.toString();
    }

    /**
     * Returns all arguments after this index in one string
     *
     * @param args the full array with command arguments
     * @param from the index after which to start parsing
     * @return all arguments after the index separated with spaces
     */
    static String getFullEntry(String[] args, int from) {
        assert from <= 0 : "Array index must be non-negative!";
        StringBuilder b = new StringBuilder();
        for (String arg : Arrays.copyOfRange(args, from, args.length))
            b.append(arg).append(" ");
        return b.toString();
    }

}
