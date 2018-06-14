package com.medievallords.carbyne.utils.tabbed.item;

import com.medievallords.carbyne.utils.tabbed.util.Skin;
import com.medievallords.carbyne.utils.tabbed.util.Skins;
import lombok.ToString;

/**
 * A blank TextTabItem
 */
@ToString
public class BlankTabItem extends TextTabItem {
    public BlankTabItem(Skin skin) {
        super("", 1000, skin);
    }

    public BlankTabItem() {
        this(Skins.DEFAULT_SKIN);
    }
}
