package tk.rdvdev2.ninjaplugin.ninja.declarations

import java.util.stream.Collectors

interface NinjaDeclaration {
    List<String> parse()


    static class Helpers {
        static String parsePath(String path) {
            List<Character> chars = path.chars.toList()
            boolean ignoreNext = false
            for (int i = 0; i < chars.size(); i++) {
                if (ignoreNext) {
                    ignoreNext = false
                    continue
                }
                if (chars.get(i).equals(" ".chars[0]) || chars.get(i).equals(":".chars[0])) {
                    chars.add(i, "\$".chars[0])
                    ignoreNext = true
                }
            }
            return chars.stream().map(String.&valueOf).collect(Collectors.joining())
        }
    }
}