package kickupper.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import kickupper.Configuration;

public class Runner {
    
    public static void main(String[] args) {
        interactiveSearch();
    }

    /**
     * Feel free to modify this function!
     *
     * @throws IOException
     */
    private static void interactiveSearch() {
        try {
            Searcher searcher = new Searcher(Configuration.indexDir);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println("Type text to search, blank to quit.");
            System.out.print("> ");
            String input;
            while ((input = br.readLine()) != null && !input.equals("")) {
                input = input.replaceAll("[^A-Za-z0-9 ]", "");
                input = "\"" + input + "\"";
                SearchResult result = searcher.search(input);
                ArrayList<ResultDoc> results = result.getDocs();
                
                int rank = 1;
                if (results.size() == 0) {
                    System.out.println("No results found!");
                }
                for (ResultDoc rdoc : results) {
                    System.out.println(rank + "- [" + rdoc.score() + "]. [" + rdoc.freqPos() + "<>" + rdoc.freqNeg() + "] " + rdoc.content());
                    System.out.println("------------------------------------------------------");
//                    System.out.println(result.getSnippet(rdoc).replaceAll("\n", " "));
                    ++rank;
                }
                System.out.println();
                System.out.print("> ");
            }
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
