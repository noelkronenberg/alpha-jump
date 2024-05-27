package communication;

import search.BasisKI;

public class Middleware {
    public static void main(String[] args) {
        BasisKI ki = new BasisKI();
        System.out.println(ki.orchestrator(args[0]));
    }
}