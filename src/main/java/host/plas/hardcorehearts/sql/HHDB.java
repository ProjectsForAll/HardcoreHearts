package host.plas.hardcorehearts.sql;

import host.plas.bou.sql.DBOperator;
import host.plas.bou.sql.DatabaseType;
import host.plas.hardcorehearts.HardcoreHearts;
import host.plas.hardcorehearts.data.players.HHPlayer;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class HHDB extends DBOperator {
    @Getter @Setter
    private static HHDB instance;

    public HHDB() {
        super(HardcoreHearts.getDatabaseConfig().getConnectorSet(), HardcoreHearts.getInstance());

        setInstance(this);
    }

    @Override
    public void ensureTables() {
        String s1 = Statements.getStatement(HHDB.getInstance(), Statements.Centralized.CREATE_TABLE);

        execute(s1, stmt -> {});
    }

    @Override
    public void ensureDatabase() {
        String s1 = Statements.getStatement(HHDB.getInstance(), Statements.Centralized.CREATE_DATABASE);

        execute(s1, stmt -> {});
    }

    public CompletableFuture<Optional<HHPlayer>> loadPlayerAsync(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(HHDB.getInstance(), Statements.Centralized.SELECT);

            AtomicReference<Optional<HHPlayer>> player = new AtomicReference<>(Optional.empty());

            executeQuery(s1, stmt -> {
                try {
                    stmt.setString(1, uuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, rs -> {
                try {
                    if (rs.next()) {
                        int lives = rs.getInt("Lives");
                        int deaths = rs.getInt("Deaths");
                        int kills = rs.getInt("Kills");
                        int revives = rs.getInt("Revives");
                        boolean isDeathBanned = rs.getInt("IsDeathBanned") == 1;

                        HHPlayer hhPlayer = new HHPlayer(uuid);
                        hhPlayer.setLives(lives);
                        hhPlayer.setDeaths(deaths);
                        hhPlayer.setKills(kills);
                        hhPlayer.setRevives(revives);
                        hhPlayer.setDeathBanned(isDeathBanned);

                        player.set(Optional.of(hhPlayer));
                    }
                } catch (SQLException e) {
                    HardcoreHearts.getInstance().logSevereWithInfo("Failed to load player: " + uuid, e);
                }
            });

            return player.get();
        });
    }

    public CompletableFuture<Boolean> savePlayerAsync(HHPlayer player) {
        if (player.isSaving()) return CompletableFuture.completedFuture(false);

        player.setSaving(true);

        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(HHDB.getInstance(), Statements.Centralized.UPDATE);

            execute(s1, stmt -> {
                try {
                    stmt.setString(1, player.getIdentifier());
                    stmt.setInt(2, player.getLives());
                    stmt.setInt(3, player.getDeaths());
                    stmt.setInt(4, player.getKills());
                    stmt.setInt(5, player.getRevives());
                    stmt.setInt(6, player.isDeathBanned() ? 1 : 0);

                    if (getType() == DatabaseType.MYSQL) {
                        stmt.setInt(7, player.getLives());
                        stmt.setInt(8, player.getDeaths());
                        stmt.setInt(9, player.getKills());
                        stmt.setInt(10, player.getRevives());
                        stmt.setInt(11, player.isDeathBanned() ? 1 : 0);
                    }
                } catch (SQLException e) {
                    HardcoreHearts.getInstance().logSevereWithInfo("Failed to save player: " + player.getIdentifier(), e);
                }
            });

            player.setSaving(false);
            return true;
        });
    }

    public void savePlayer(HHPlayer player, boolean async) {
        if (async) {
            savePlayerAsync(player);
        } else {
            savePlayerAsync(player).join();
        }
    }

    public void savePlayer(HHPlayer player) {
        savePlayer(player, true);
    }

    public CompletableFuture<Boolean> deletePlayerAsync(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(HHDB.getInstance(), Statements.Centralized.DELETE);

            execute(s1, stmt -> {
                try {
                    stmt.setString(1, uuid);
                } catch (SQLException e) {
                    HardcoreHearts.getInstance().logSevereWithInfo("Failed to delete player: " + uuid, e);
                }
            });

            return true;
        });
    }

    public void deletePlayer(String uuid, boolean async) {
        if (async) {
            deletePlayerAsync(uuid);
        } else {
            deletePlayerAsync(uuid).join();
        }
    }

    public void deletePlayer(String uuid) {
        deletePlayer(uuid, true);
    }
}
