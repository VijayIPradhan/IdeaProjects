import org.graalvm.polyglot.*;


public class Main {
    public static void main(String[] args) {
        try (Context context = Context.create()) {
            // Evaluate JavaScript code
            Value result = context.eval("js",
                    "var greeting = 'hello world';" +
                            "console.log(greeting);" +
                            "greeting;");

            // Get the result as a Java object
            String greeting = result.asString();
            System.out.println("JavaScript result: " + greeting);
        }
    }
}
