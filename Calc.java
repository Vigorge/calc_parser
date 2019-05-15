import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Calc {
    private static ArrayList<Lexem> lexems;
    private static HashMap<String, Integer> vars;
    private static ArrayList<String> varList;
    private static int c = 0;

    private static void error() {
        System.out.println("error");
        System.exit(0);
    }

    private static void lexer(String expr) {
        lexems = new ArrayList<Lexem>();
        varList = new ArrayList<String>();
        for (int i = 0; i < expr.length();) {
            if (expr.charAt(i) == '/' || expr.charAt(i) == '+'
                    || expr.charAt(i) == '*' || expr.charAt(i) == '-'
                    || expr.charAt(i) == '(' || expr.charAt(i) == ')') {
                lexems.add(new Lexem(0, Character.toString(expr.charAt(i))));
                i++;
            } else if (expr.charAt(i) >= '0' && expr.charAt(i) <= '9') {
                StringBuilder lexem = new StringBuilder();
                while (i < expr.length() && expr.charAt(i) >= '0' && expr.charAt(i) <= '9') {
                    lexem.append(expr.charAt(i));
                    i++;
                }
                lexems.add(new Lexem(1, lexem.toString()));
            } else if ((expr.charAt(i) >= 'a' && expr.charAt(i) <= 'z') || (expr.charAt(i) >= 'A' && expr.charAt(i) <= 'Z')) {
                StringBuilder lexem = new StringBuilder();
                while (i < expr.length() && ((expr.charAt(i) >= 'a' && expr.charAt(i) <= 'z')
                        || (expr.charAt(i) >= 'A' && expr.charAt(i) <= 'Z')
                        || (expr.charAt(i) >= '0' && expr.charAt(i) <= '9'))) {
                    lexem.append(expr.charAt(i));
                    i++;
                }
                if (!varList.contains(lexem.toString())) {
                    varList.add(lexem.toString());
                }
                lexems.add(new Lexem(2, lexem.toString()));
            } else if (expr.charAt(i) == ' ') {
                i++;
            } else {
                error();
            }
        }
    }

    private static int p_factor() {
        if (lexems.get(c).getId() == 1) {
            return Integer.parseInt(lexems.get(c++).getData());
        } else if (lexems.get(c).getId() == 2) {
            return vars.get(lexems.get(c++).getData());
        } else if (lexems.get(c).getData().equals("(")) {
            c++;
            int res  = p_expr();
            if (c != lexems.size() && lexems.get(c).getData().equals(")")) {
                c++;
                return res;
            } else {
                error();
                return 0;
            }
        } else if (lexems.get(c).getData().equals("-")) {
            c++;
            return -p_factor();
        } else {
            error();
            return 0;
        }
    }

    private static int p_termx(int cur) {
        if (c < lexems.size()) {
            if (lexems.get(c).getData().equals("*")) {
                c++;
                if (c < lexems.size()) {
                    cur *= p_factor();
                    cur = p_termx(cur);
                } else error();
            } else if (lexems.get(c).getData().equals("/")) {
                c++;
                if (c < lexems.size()) {
                    cur /= p_factor();
                    cur = p_termx(cur);
                } else error();
            }
        }
        return cur;
    }

    private static int p_term() {
        return p_termx(p_factor());
    }

    private static int p_exprx(int cur) {
        if (c < lexems.size()) {
            if (lexems.get(c).getData().equals("+")) {
                c++;
                if (c < lexems.size()) {
                    cur += p_term();
                    cur = p_exprx(cur);
                } else error();
            } else if (lexems.get(c).getData().equals("-")) {
                c++;
                if (c < lexems.size()) {
                    cur -= p_term();
                    cur = p_exprx(cur);
                } else error();
            }
        }
        return cur;
    }

    private static int p_expr() {
        return p_exprx(p_term());
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String expression = in.nextLine();
        lexer(expression);
        vars = new HashMap<>();
        for (String i : varList) {
            vars.put(i, in.nextInt());
        }
        int result = p_expr();
        if (c == lexems.size()) {
            System.out.println(result);
        } else error();
    }
}

class Lexem {
    private int id;
    private String data;

    Lexem(int id, String data) {
        this.id = id;
        this.data = data;
    }

    int getId() {
        return id;
    }

    String getData() {
        return data;
    }
}