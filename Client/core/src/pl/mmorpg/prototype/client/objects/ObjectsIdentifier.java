package pl.mmorpg.prototype.client.objects;


import java.util.HashMap;
import java.util.Map;

import pl.mmorpg.prototype.clientservercommon.ObjectsIdentifiers;

public class ObjectsIdentifier
{
    private static Map<Class<?>, String> identifiers = new HashMap<>();
    
    static
    {
		identifiers.put(Player.class, ObjectsIdentifiers.PLAYER);
    }
    
    public static String getObjectIdentifier(Class<?> type)
    {
        return identifiers.get(type);
    }
}