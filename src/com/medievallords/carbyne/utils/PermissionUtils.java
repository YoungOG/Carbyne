package com.medievallords.carbyne.utils;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.permissions.PermissionAttachment;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Dalton on 6/24/2017.
 */
public class PermissionUtils
{

    public static final void setPermissions(final PermissionAttachment attachment, final Map<String, Boolean> permissions, final boolean recalculate) {
        try {
            final Field field = PermissionAttachment.class.getDeclaredField("permissions");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            final Map<String, Boolean> presentPerms = (Map<String, Boolean>) field.get(attachment);
            presentPerms.clear();
            presentPerms.putAll(permissions);
            if (recalculate) attachment.getPermissible().recalculatePermissions();
        } catch(Throwable t) {
            Carbyne.getInstance().getLogger().log(Level.WARNING, "Fall back to single permission adding - adding transient permissions failed - caused by:\n");
            t.printStackTrace();
            for (final String perm : attachment.getPermissions().keySet()) {
                attachment.unsetPermission(perm);
            }
            for (final Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                attachment.setPermission(entry.getKey(), entry.getValue());
            }
        }
    }

}
