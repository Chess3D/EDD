import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class SecureTree extends MerkleTree {
    private Data data;


    // Constructs a SecureTree from given Data
    public SecureTree(Data data) throws NoSuchAlgorithmException {
        super(data.hashAll());

        this.data = data;
    }


    // Builds the secure tree from a given folder
    public SecureTree(String path) throws Exception {
        super(path);

        Vector<Datum> data = new Vector<Datum>();
        for (int i = 0; i < getLeaves().size(); ++i) {
            byte[] hash = tree.get(i);
            byte[] hashRoot = ByteBuffer.allocate(hash.length + getRoot().length)
                .put(hash)
                .put(getRoot())
                .array();

            MessageDigest SHA3 = MessageDigest.getInstance("SHA3-256");
            byte[] hashRootHash = SHA3.digest(hashRoot);

            String subpath = "";
            for (byte b : hashRootHash) {
                subpath += String.format("%03o", b);
            }

            File file = new File(path, subpath);

            FileInputStream input = new FileInputStream(file);

            Datum datum = new Datum(input.readAllBytes());
            data.add(datum);

            input.close();
        }

        this.data = new Data(data);
        
        decrypt();
        if (!verify(new MerkleTree(this.data.hashAll()))) {
            throw new Error("ERROR:  Data tampering detected");
        }
    }


    // Encrypts the secure tree, using the root as the key and the leaves as the salts and IVs
    public void encrypt() throws Exception {
        data.encrypt(getRoot(), getLeaves());
    }

    // Decrypts the secure tree
    public void decrypt() throws Exception {
        data.decrypt(getRoot(), getLeaves());
    }


    // Checks if the tree is encrypted
    public boolean getEncrypted() {
        return data.getEncrypted();
    }


    // Returns the data stored in the secured tree
    public Data getData() {
        return data;
    }


    // Exports the secure tree to a folder
    public void export(String path) throws Exception {
        if (!data.getEncrypted()) {
            encrypt();
        }

        File file = new File(path);
        file.mkdir();

        super.export(path);

        for (int i = 0; i < data.size(); ++i) {
            byte[] hash = tree.get(i);
            byte[] hashRoot = ByteBuffer.allocate(hash.length + getRoot().length)
                .put(hash)
                .put(getRoot())
                .array();

            MessageDigest SHA3 = MessageDigest.getInstance("SHA3-256");
            byte[] hashRootHash = SHA3.digest(hashRoot);

            String subpath = "";
            for (byte b : hashRootHash) {
                subpath += String.format("%03o", b);
            }

            file = new File(path, subpath);
            file.createNewFile();

            FileOutputStream output = new FileOutputStream(file);
            output.write(data.get(i).getDatum());

            output.close();
        }
    }


    // Exports the data to a file
    public void toFile(String path) throws Exception {
        data.decrypt(getRoot(), getLeaves());

        File file = new File(path);
        file.createNewFile();

        FileOutputStream output = new FileOutputStream(file);
        output.write(data.toDatum().getDatum());

        output.close();
    }
}
