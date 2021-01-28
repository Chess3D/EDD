import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Vector;

public class MerkleTree {
    protected Vector<byte[]> tree = new Vector<byte[]>();


    // Constructor used by the MerkleTree
    public MerkleTree(Vector<byte[]> leaves) throws NoSuchAlgorithmException {
        int nodes = (int) Math.pow(2, (Math.log(leaves.size()) / Math.log(2)) + 1) - 1;
        tree.ensureCapacity(nodes);

        tree.addAll(leaves);

        for (int i = 0; tree.size() < nodes; i += 2) {
            byte[] hash = ByteBuffer.allocate(512).put(tree.elementAt(i)).put(tree.elementAt(i + 1)).array();

            tree.addElement(hash(hash));
        }
    }


    // Constructs the merkle tree from a given file
    public MerkleTree(String path) throws Exception {
        File file = new File(path, "leaves");

        FileInputStream input = new FileInputStream(file);
        Vector<byte[]> leaves = new Vector<byte[]>();

        while (true) {
            byte[] temp = new byte[32];

            if (input.read(temp, 0, 32) == -1) {
                break;
            }

            leaves.add(temp);
        }

        input.close();

        int nodes = (int) Math.pow(2, (Math.log(leaves.size()) / Math.log(2)) + 1) - 1;
        tree.ensureCapacity(nodes);

        tree.addAll(leaves);

        for (int i = 0; tree.size() < nodes; i += 2) {
            byte[] hash = ByteBuffer.allocate(512).put(tree.elementAt(i)).put(tree.elementAt(i + 1)).array();

            tree.addElement(hash(hash));
        }
    }


    // Hash functions so logic does not need to be copied
    protected byte[] hash(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest SHA3 = MessageDigest.getInstance("SHA3-256");
        return SHA3.digest(input);
    }


    // Returns the root of the tree
    public byte[] getRoot() {
        return tree.lastElement();
    }


    // Returns all the leaves of the tree
    public Vector<byte[]> getLeaves() {
        int count = (tree.size() + 1) / 2;
        return new Vector<byte[]>(tree.subList(0, count));
    }


    // Returns the hash tree
    public Vector<byte[]> getTree() {
        return tree;
    }


    // Verifies the data use only the root node
    public boolean verify(MerkleTree compare) {
        return Arrays.equals(getRoot(), compare.getRoot());
    }


    // Exports the merkle tree to a file
    public void export(String path) throws Exception {
        File file = new File(path, "leaves");
        file.createNewFile();

        FileOutputStream output = new FileOutputStream(file);
        for (byte[] leaf : getLeaves()) {
            output.write(leaf);
        }

        output.close();
    }
}