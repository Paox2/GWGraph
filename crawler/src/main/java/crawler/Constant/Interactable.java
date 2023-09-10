package crawler.Constant;

import java.util.Arrays;
import java.util.List;

/**
 * Constant class about all interactable key word
 */
public class Interactable {

    /**
     * Interactable HTML tag name.
     */
    public static final List<String> TAGS = Arrays.asList(
            "a",         // Anchor (Link)
            "area",      // Clickable area
            "button",    // Button
            "input",     // Input
            "select",    // Select (Dropdown)
            "textarea",  // Textarea (Multiline Input)
            "form",      // Form
            "audio",     // Audio
            "video",     // video
            "details",   // detail
            "iframe"     // iframe
    );

    /**
     * Interactable HTML attributes for normal event.
     * Apply for all html tags but more in 'form'.
     */
    public static final List<String> EVENT = Arrays.asList(
            "onfocus",        // On Focus
            "onblur",         // On Blur
            "onchange",       // On Change
            "oninput",        // On Input
            "oninvalid",      // On Invalid
            "onsubmit",       // ON Submit
            "onreset",        // ON Reset
            "onselect",       // ON Select
            "oncontextmenu",  // ON Context Menu
            "ontoggle",       // On toggle
            "onsearch",
            "onkeypress",     // On Key Press
            "onkeydown",      // On Key Down
            "onkeyup",
            "onclick",        // On Click
            "ondblclick",     // On Double Click
            "onmousedown",    // On Mouse Down
            "onmouseup",      // On Mouse Up
            "onmouseover",    // On Mouse Over
            "onmousemove",    // On Mouse Move
            "onmouseout",     // On Mouse Out
            "onmouseenter",   // On Mouse Enter
            "onmouseleave",   // On Mouse Leave
            "onwheel",
            "ondrag",         // ON Drag
            "ondragend",      // ON Drag End
            "ondragenter",    // ON Drag Enter
            "ondragleave",    // ON Drag Leave
            "ondragover",     // ON Drag Over
            "ondragstart",    // ON Drag start
            "ondrop",         // ON Drop
            "onscroll",
            "oncopy",         // ON Copy
            "oncut",          // ON Cut
            "onpaste",
            "onabort",
            "oncanplay",
            "oncanplaythrough",
            "oncuechange",
            "ondurationchange",
            "onemptied",
            "onended",
            "onerror",
            "onloadeddata",
            "onloadedmetadata",
            "onloadstart",
            "onpause",
            "onplay",
            "onprogress",
            "onratechange",
            "onseeked",
            "onseeking",
            "onstalled",
            "onsuspend",
            "ontimeupdate",
            "onvolumechange",
            "onwaiting"
    );

    /**
     * Interactable HTML attributes for normal event.
     * Apply for all html tags but more in 'form'.
     */
    public static final List<String> NORMAL_EVENT = Arrays.asList(
            "onfocus",        // On Focus
            "onblur",         // On Blur
            "onchange",       // On Change
            "oninput",        // On Input
            "oninvalid",      // On Invalid
            "onsubmit",       // ON Submit
            "onreset",        // ON Reset
            "onselect",       // ON Select
            "oncontextmenu",  // ON Context Menu
            "ontoggle",       // On toggle
            "onsearch"        // On Search
    );

    /**
     * Interactable HTML attributes for keyboard event.
     */
    public static final List<String> KEYBOARD_EVENT = Arrays.asList(
            "onkeypress",     // On Key Press
            "onkeydown",      // On Key Down
            "onkeyup"        // On Key Up
    );

    /**
     * Interactable HTML attributes for mouse event.
     */
    public static final List<String> MOUSE_EVENT = Arrays.asList(
            "onclick",        // On Click
            "ondblclick",     // On Double Click
            "onmousedown",    // On Mouse Down
            "onmouseup",      // On Mouse Up
            "onmouseover",    // On Mouse Over
            "onmousemove",    // On Mouse Move
            "onmouseout",     // On Mouse Out
            "onmouseenter",   // On Mouse Enter
            "onmouseleave",   // On Mouse Leave
            "onwheel"         // On Wheel
    );

    /**
     * Interactable HTML attributes for drag event.
     */
    public static final List<String> DRAG_EVENT = Arrays.asList(
            "ondrag",         // ON Drag
            "ondragend",      // ON Drag End
            "ondragenter",    // ON Drag Enter
            "ondragleave",    // ON Drag Leave
            "ondragover",     // ON Drag Over
            "ondragstart",    // ON Drag start
            "ondrop",         // ON Drop
            "onscroll"        // ON Scroll
    );

    /**
     * Interactable HTML attributes for clipboard event.
     */
    public static final List<String> CLIPBOARD_EVENT = Arrays.asList(
            "oncopy",         // ON Copy
            "oncut",          // ON Cut
            "onpaste"         // ON Paste
    );

    /**
     * Interactable HTML attributes for media event.
     * For all html tags but more in 'video', 'audio', etc.
     */
    public static final List<String> MEDIA_EVENT = Arrays.asList(
            "onabort",
            "oncanplay",
            "oncanplaythrough",
            "oncuechange",
            "ondurationchange",
            "onemptied",
            "onended",
            "onerror",
            "onloadeddata",
            "onloadedmetadata",
            "onloadstart",
            "onpause",
            "onplay",
            "onprogress",
            "onratechange",
            "onseeked",
            "onseeking",
            "onstalled",
            "onsuspend",
            "ontimeupdate",
            "onvolumechange",
            "onwaiting"
    );

    /**
     * Interactable HTML attributes.
     */
    public static final List<String> INTERACTIVE_ATTRI = Arrays.asList(
            "draggable",
            "contenteditable",
            "tabindex"
    );
}
