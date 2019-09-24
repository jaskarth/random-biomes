package supercoder79.randombiomes.testing;

import net.minecraft.world.biome.Biome;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StupidShit {
    public static void setFinalStatic(Field field, Object newValue) throws IllegalAccessException, NoSuchFieldException {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
