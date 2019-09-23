package supercoder79.randombiomes;

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
    public static List<Biome> returnNewBiomeList() {
        return new List<Biome>() {
            public int i = 0;
            @Override
            public int size() {
                return i;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Biome> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean add(Biome biome) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Biome> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, Collection<? extends Biome> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public Biome get(int index) {
                return null;
            }

            @Override
            public Biome set(int index, Biome element) {
                return null;
            }

            @Override
            public void add(int index, Biome element) {

            }

            @Override
            public Biome remove(int index) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<Biome> listIterator() {
                return null;
            }

            @Override
            public ListIterator<Biome> listIterator(int index) {
                return null;
            }

            @Override
            public List<Biome> subList(int fromIndex, int toIndex) {
                return null;
            }
        };
    }
}
