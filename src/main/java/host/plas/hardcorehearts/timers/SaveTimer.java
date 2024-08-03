package host.plas.hardcorehearts.timers;

import host.plas.bou.scheduling.BaseRunnable;
import host.plas.hardcorehearts.data.players.HHPlayer;
import host.plas.hardcorehearts.data.players.PlayerManager;

public class SaveTimer extends BaseRunnable {
    public SaveTimer() {
        super(30 * 20L, 10 * 20L); // 30 seconds, 10 seconds
    }

    @Override
    public void run() {
        PlayerManager.getLoadedPlayers().forEach(HHPlayer::save);
    }
}
