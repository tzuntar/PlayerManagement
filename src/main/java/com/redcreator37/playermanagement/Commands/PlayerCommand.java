package com.redcreator37.playermanagement.Commands;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import com.redcreator37.playermanagement.PlayerManagement;
import com.redcreator37.playermanagement.PlayerRoutines;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents implementation data of a player-executable plugin command
 */
public abstract class PlayerCommand implements CommandExecutor {

    /**
     * The name of the command by which it can be invoked
     */
    private final String name;

    /**
     * A {@link HashMap} of arguments this command accepts.
     * Keys are names of the arguments, values tell whether a specific
     * argument is required or not
     */
    private final HashMap<String, Boolean> arguments;

    /**
     * List of permissions this command can be expected to require
     */
    private final List<String> permissions;

    /**
     * Constructs a new PlayerCommand instance
     *
     * @param name        The name of the command by which it can be invoked
     * @param arguments   A {@link HashMap} of arguments this command
     *                    accepts. Keys are names of the arguments, values
     *                    tell whether a specific argument is required or not
     * @param permissions List of permissions this command can be
     *                    expected to require
     */
    public PlayerCommand(String name, HashMap<String, Boolean> arguments, List<String> permissions) {
        this.name = name;
        this.arguments = arguments;
        this.permissions = permissions;
    }

    /**
     * Checks whether this entered form of the command is invalid
     *
     * @param player      the {@link Player} who ran the command
     * @param enteredArgs the arguments entered by the player
     * @return {@code true} if the form is invalid, {@code false}
     * otherwise
     */
    public boolean isInvalid(Player player, String[] enteredArgs) {
        List<String> args = Arrays.asList(enteredArgs.clone());
        long requiredArgCount = arguments.values().stream()
                .filter(required -> required).count();
        if (!arguments.isEmpty() && args.size() == 0 || args.size() < requiredArgCount) {
            displayHelp(player);
            return true;
        }
        return permissions.size() == 1 && PlayerRoutines
                .lacksPermission(player, permissions.get(0));
    }

    /**
     * Returns the target {@link ServerPlayer} based on the invoking
     * {@link Player Player's} permissions.
     * <p>
     * This method works in a dual-permission system, where both normal
     * users and admins can use the command. Normal users can see and
     * modify only their data while admins can see and modify data of
     * others as well.
     * <p>
     * This command requires at least two permissions to be set up.
     * The first permission is used when checking for the user's
     * permission, the second is used when checking for admin's. An
     * {@link IllegalStateException} is thrown if less than two
     * permissions are set up.
     *
     * @param player          the {@link Player} who ran the command
     * @param args            the arguments entered by the player
     * @param nameArgPos      the position in the arguments array where
     *                        the player names are expected to be when
     *                        an admin runs the command
     * @param adminArgsLength the length of the entered arguments array
     *                        at which the command will be run in admin
     *                        mode
     * @return an {@link Optional} containing the target
     * {@link ServerPlayer} object
     */
    public Optional<ServerPlayer> getUserOrAdmin(Player player, String[] args,
                                                 int nameArgPos, int adminArgsLength) {
        if (permissions.size() < 2)
            throw new IllegalStateException("Ranking by user or admin requires at"
                    + " least two different permissions");
        String requiredPermission = args.length == adminArgsLength
                ? permissions.get(0) : permissions.get(1);
        if (PlayerRoutines.lacksPermission(player, requiredPermission))
            return Optional.empty();

        String targetName = args.length == adminArgsLength
                ? player.getName() : args[nameArgPos];
        ServerPlayer target = PlayerManagement.players.byUuid(args.length == adminArgsLength
                ? player.getUniqueId()
                : PlayerManagement.players.uuidFromUsername(targetName));
        if (PlayerRoutines.checkPlayerNonExistent(player, target, targetName))
            return Optional.empty();
        return Optional.of(target);
    }

    /**
     * Displays help for this command
     *
     * @param player the {@link Player} which will see the help
     */
    public void displayHelp(Player player) {
        player.sendMessage(CommandHelper.parseCommandUsage(name, arguments));
    }

    /**
     * Overrides the main command process in {@link CommandExecutor}
     * class
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = PlayerRoutines.playerFromSender(sender);
        assert player != null;
        if (isInvalid(player, args)) return true;
        execute(player, args, player.getUniqueId());
        return true;
    }

    /**
     * Runs this command and performs the actions
     *
     * @param player   the {@link Player} who ran the command
     * @param args     the arguments entered by the player
     * @param executor the UUID of the executing player
     */
    public abstract void execute(Player player, String[] args, UUID executor);

    public String getName() {
        return name;
    }
}
