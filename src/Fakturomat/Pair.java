package Fakturomat;

import java.io.*;

public class Pair<K, V> implements Serializable {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return this.key; }
    public V getValue() { return this.value; }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(key);
        oos.writeObject(value);
    }

    @Serial
    @SuppressWarnings (value="unchecked")
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        key = (K) ois.readObject();
        value = (V) ois.readObject();
    }
}
