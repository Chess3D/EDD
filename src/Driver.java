import java.nio.charset.StandardCharsets;

public class Driver {
    public static void main(String[] args) throws Exception {

        // Instructions for an at home demonstration
        // Note:  If working with a file uncomment 1f and if working with a String uncomment 1s
        // 1.  Comment out "Block 2"
        // 2.  Change the value of "input" found at the start of "Block 1" to your hearts desire
        // 3.  Run the code
        // 4.  Take a look around the output files found in the new test directory
        // 5.  Comment out "Block 1"
        // 6.  Uncomment "Block 2"
        // 7.  Run the code


        // Block 1f
        String input = "./frankenstein.txt";
        Datum datum = new Datum(input, true);
        Data data = new Data(datum, false, 1024);
        SecureTree secure = new SecureTree(data);

        secure.export("./test");


        // // Block 1s
        // String input = "The Cat in the Hat by Dr. Seuss";
        // Datum datum = new Datum(input, false);
        // Data data = new Data(datum, false, 1024);
        // SecureTree secure = new SecureTree(data);

        // String decrypted = new String(secure.getData().toDatum().getDatum(), StandardCharsets.UTF_8);

        // secure.encrypt();
        // String encrypted = new String(secure.getData().toDatum().getDatum(), StandardCharsets.UTF_8);

        // System.out.println(decrypted);
        // System.out.println(encrypted);

        // secure.export("./test");


        // Block 2
        SecureTree fromFiles = new SecureTree("./test");
        fromFiles.toFile("./output");
    }
}