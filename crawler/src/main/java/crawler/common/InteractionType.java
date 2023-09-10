package crawler.common;

import lombok.AllArgsConstructor;

/**
 * User interaction type enum.
 */
@AllArgsConstructor
public enum InteractionType {
    NORMAL("normal"),
    KEYBOARD("keyboard"),
    MOUSE("mouse"),
    DRAG("drag"),
    CLIPBOARD("clipboard"),
    MEDIA("media"),
    INTERACTIVEATTRI("interactive attribute");

    private final String value;
}
