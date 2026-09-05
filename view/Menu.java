package view;

/**
 * Lifecycle callback implemented by every menu panel.
 *
 * onShow() is invoked by showPanel(String) each
 * time a panel is brought to the foreground. Implementations use it to request
 * keyboard focus and to refresh whatever they display.
 */
public interface Menu {

    /** Called just after this panel becomes visible. */
    void onShow();
}

