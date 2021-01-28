import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

public class Data {

    // Breaks the datum into data of size size
    private Vector<Datum> data = new Vector<Datum>();


    // Is the data encrypted?
    private boolean encrypted;


    // Constructor
    public Data(Datum datum, boolean encrypted, int maxSize) {
        byte[] byteArray = datum.getDatum();

        // Calculate the number of chunks required
        int chunks = (int) Math.ceil((double) byteArray.length / maxSize);

        // Take the ceiling of log base 2 of chunks
        chunks = (int) Math.ceil(Math.log(chunks) / Math.log(2));

        chunks = (int) Math.pow(2, chunks);

        Vector<ByteArrayOutputStream> byteStreamVector = new Vector<ByteArrayOutputStream>(chunks);
        for (int i = 0; i < chunks; ++i) {
            byteStreamVector.addElement(new ByteArrayOutputStream());
        }

        for (int i = 0; i < byteArray.length; ++i) {
            byteStreamVector.elementAt(i % chunks).write(byteArray[i]);
        }

        for (ByteArrayOutputStream stream: byteStreamVector) {
            data.addElement(new Datum(stream.toByteArray()));
        }

        this.encrypted = encrypted;
    }


    // Constructor for importing data from outside source
    public Data(Vector<Datum> data) {
        this.data = data;
        this.encrypted = true;
    }


    // Hash the data
    public Vector<byte[]> hashAll() throws NoSuchAlgorithmException {
        if (encrypted) {
            throw new Error("ERROR:  Cannot hash encrypted data");
        }

        Vector<byte[]> hashed = new Vector<byte[]>();

        for (Datum datum : data) {
            hashed.add( datum.hash() );
        }

        return hashed;
    }


    // Encrypt the data
    public void encrypt(byte[] key, Vector<byte[]> sIV) throws Exception {
        if (encrypted) {
            return;
        }

        for (int i = 0; i < data.size(); ++i) {
            byte[] salt = Arrays.copyOfRange(sIV.get(i), 0, 16);
            byte[] IV = Arrays.copyOfRange(sIV.get(i), 16, 32);

            data.get(i).encrypt(key, salt, IV);
        }

        encrypted = true;
    }


    // Decrypt the data
    public void decrypt(byte[] key, Vector<byte[]> sIV) throws Exception {
        if (!encrypted) {
            return;
        }

        for (int i = 0; i < data.size(); ++i) {
            byte[] salt = Arrays.copyOfRange(sIV.get(i), 0, 16);
            byte[] IV = Arrays.copyOfRange(sIV.get(i), 16, 32);

            data.get(i).decrypt(key, salt, IV);
        }

        encrypted = false;
    }


    // Returns the stored data
    public Vector<Datum> getData() {
        return data;
    }


    // Gets an individual datum from the data
    public Datum get(int index) {
        return data.get(index);
    }


    // Returns the number of datum contained in data
    public int size() {
        return data.size();
    }


    // Return the encryption state
    public boolean getEncrypted() {
        return encrypted;
    }


    // Returns the data to it's original form
    public Datum toDatum() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        for (int i = 0; true; ++i) {

            byte[] temp = data.elementAt(i % data.size()).getDatum();

            if (i / data.size() < temp.length) {
                byteStream.write(temp[i / data.size()]);
            } else {
                break;
            }
        }

        Datum output = new Datum(byteStream.toByteArray());
        return output;
    }


    // Used in unit testing to find bugs
    public String toString() {
        String output = "Encrypted:  " + encrypted + "\n";

        for (Datum datum: data) {
            output += datum + "\n";
        }

        return output;
    }
}
