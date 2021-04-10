package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.Localization;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Contains helper methods for command handler classes
 */
public final class CommandHelper {

    /**
     * Non-instantiable
     */
    private CommandHelper() {
    }

    /**
     * Returns the formatted command usage help for this command
     *
     * @param name      name of the command (without the {@code /})
     * @param arguments a {@link HashMap} of arguments, where key is
     *                  the argument name and value is a boolean
     *                  which tells whether the specific argument is
     *                  required or not
     * @return the formatted string
     */
    public static String parseCommandUsage(String name, HashMap<String, Boolean> arguments) {
        StringBuilder usage = new StringBuilder(Localization.lc("usage"));
        usage.append(name).append(" ");
        arguments.forEach((argName, req) -> {
            String[] args = argName.split("\\|");
            usage.append(req ? formatRequiredArgs(args)
                    : formatOptionalArgs(args)).append(" ");
        });
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
        result.deleteCharAt(result.length() - 1).append("§8]");
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
            result.append(ChatColor.RED).append(a).append("§8|");
        result.deleteCharAt(result.length() - 1).append("§8]");
        return result.toString();
    }

    /**
     * Returns all arguments after this index in one string
     *
     * @param args the full array with command arguments
     * @param from the index after which to start parsing
     * @return all arguments after the index separated with spaces
     */
    public static String getFullEntry(String[] args, int from) {
        assert from <= 0 : "Array index must be non-negative!";
        StringBuilder b = new StringBuilder();
        for (String arg : Arrays.copyOfRange(args, from, args.length))
            b.append(arg).append(" ");
        return b.toString();
    }

}
