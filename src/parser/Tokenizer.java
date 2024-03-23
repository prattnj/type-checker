package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;

public class Tokenizer {

    public static List<Token> run(List<String> inputs) {
        List<Token> tokens = new ArrayList<>();

        for (String s : inputs) {
            while (!s.equals("")) {
                char c = s.charAt(0);
                if (c == ' ' || c == '\n' || c == '\t') {
                    s = s.substring(1);
                    continue;
                }

                int maxCharsRead = 0;
                Token.Type maxToken = null;
                SortedMap<Token.Type, Function<String, Integer>> automata = Automata.getAutomata();
                for (Map.Entry<Token.Type, Function<String, Integer>> automaton : automata.entrySet()) {
                    int charsRead = automaton.getValue().apply(s);
                    if (charsRead >= maxCharsRead) {
                        maxCharsRead = charsRead;
                        maxToken = automaton.getKey();
                    }
                }

                if (maxCharsRead != 0) {
                    tokens.add(maxToken == Token.Type.IDENTIFIER ?
                            new Token(s.substring(0, maxCharsRead)) : new Token(maxToken));
                } else return null;
                s = s.substring(maxCharsRead);
            }
        }

        return tokens;
    }
}

