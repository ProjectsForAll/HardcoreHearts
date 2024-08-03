package host.plas.hardcorehearts.events;

import host.plas.hardcorehearts.HardcoreHearts;
import tv.quaint.events.BaseEventHandler;
import tv.quaint.events.BaseEventListener;

public class BouListener implements BaseEventListener {
    public BouListener() {
        BaseEventHandler.bake(this, HardcoreHearts.getInstance());
        HardcoreHearts.getInstance().logInfo("Registered BouListener!");
    }
}
