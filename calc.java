import java.util.*;


import static java.lang.System.*;

/**
 * The program makes the calculation of complex
 * expressions through the Polish notation
 */
public class calc {

    private static ArrayList<String> spareExpression = new ArrayList<>();
    private static boolean cloning = true;


    public static void main(String[] args) {
        if (args.length >= 1) {
            userInterface(args);
        } else {
            out.println("No arguments were entered");
        }
    }
    /**
     * Starts counting with arguments, and then prompts the user for new
     *
     * @param args command line
     */
    private static void userInterface(String[] args) {
        ArrayList<String> polishEntry;
        Scanner in = new Scanner(System.in);
        boolean userInput = true;
        polishEntry = conversionToPolishEntry(args[0]);
        out.println(polishEntry);
        if (polishEntry == null) {
            out.println("Formula is incorrectly written");
            return;
        }
        while (true) {
            if (!userInput) {
                out.println("Do you want to continue Y/N");
                String answer = in.nextLine();
                if (answer.equals("y") || answer.equals("Y")) {
                    out.println("Enter unknown");
                    for (int i = 1; i < args.length; i++) {
                        args[i] = in.nextLine();
                    }
                } else {
                    return;
                }
            }
            out.println(calculator(polishEntry, args));
            userInput = false;
        }
    }

    /**
     * Parse a string of arguments and if there are unknowns writes them to Hashmap
     *
     * @param args command line
     * @return hashmap
     */
    private static HashMap determiningTheValues(String[] args) {
        HashMap<String, String> valuesForSubstitution = new HashMap<>();
        for (String arg : args) {
            for (int i = 0; i < arg.length(); i++) {
                if (arg.charAt(i) == '=') {
                    valuesForSubstitution.put(arg.substring(0, i), arg.substring(i + 1, arg.length()));
                }
            }
        }
        return valuesForSubstitution;
    }

    /**
     * Pass through the array of values ​​and the hash map if
     * there are identical replaces the unknown with its value
     *
     * @param valuesForSubstitution Hashmap with unknowns
     * @param expression   ArrayList with operands in reverse Polish notation
     * @return ArrayList with values
     */
    private static ArrayList<String> substitution(HashMap<String,String> valuesForSubstitution, ArrayList<String> expression) {
        if (cloning) {
            spareExpression = (ArrayList<String>) expression.clone();
            cloning = false;
        }
        for (int i = 0; i < expression.size(); i++) {
            for (int j = 0; j < valuesForSubstitution.size(); j++) {
                if (valuesForSubstitution.containsKey(spareExpression.get(i))) {
                    expression.set(i, valuesForSubstitution.get(spareExpression.get(i)));
                }
            }
        }

        return expression;
    }

    /**
     * breaks the formula into parts
     * @param formula incoming formula
     * @return Array with operators and values
     */
    private static ArrayList<String> parser(String formula) {
        ArrayList<String> meanings = new ArrayList<>();
        if (formula.charAt(0) == '-') {
            formula = "0" + formula;
        }
        StringTokenizer st = new StringTokenizer(formula, "^+-*/()", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if ("+-/*()".contains(token)) {
                meanings.add(token);
            } else {
                meanings.add(token);
            }
        }
        for(int i = 0; i < meanings.size();i++){
            if(meanings.get(i).equals("(") && meanings.get(i + 1).equals("-")){
                meanings.set(i + 1,"-"+meanings.get(i + 2));
                meanings.remove(i + 2);
            }
        }

        return meanings;
    }

    /**
     * Checks expressions for correctness
     * @param meanings an arraay with steamed values
     * @return true if the expression is correct
     */
    private static boolean validation(ArrayList<String> meanings) {
        int count = 0;
        for (String str : meanings) {
            if (str.equals("(")) {
                count++;
            } else if (str.equals(")")) {
                count--;
            }
        }
        for (int i = 0; i < meanings.size(); i++) {
            if (thisInAnOperator(meanings.get(i)) && thisInAnOperator(meanings.get(i + 1))) {
                return false;
            }
        }
        if (count == 0) {
            return true;
        }


        return false;
    }

    /**
     * The function of converting an expression into a reverse Polish entry
     *
     * @param formula mathematical expression
     * @return An array with an expression in the reverse Polish entry
     */
    private static ArrayList<String> conversionToPolishEntry(String formula) {
        ArrayList<String> meanings = parser(formula);
        ArrayList<String> expression = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        if (validation(meanings)) {
            for (String str : meanings) {
                if (thisInAnFunction(str)) {
                    operators.push(str);
                }
                if (str.equals("(")) {
                    operators.push(str);
                }
                if (str.equals(")")) {
                    if (!operators.empty() && !operators.peek().equals("(")) {
                        while (!operators.peek().equals("(")) {
                            expression.add(operators.pop());
                        }
                    }
                    operators.pop();
                }
                if (thisInAnOperator(str)) {
                    if (operators.empty()) {
                        operators.push(str);
                    } else {
                        if (priority(operators.peek()) < priority(str)) {
                            operators.push(str);
                        } else if (priority(operators.peek()) >= priority(str)) {
                            expression.add(operators.pop());
                            if (!operators.empty()) {
                                if (priority(operators.peek()) >= priority(str)) {
                                    expression.add(operators.pop());
                                }
                            }
                            operators.push(str);
                        }
                    }
                } else if (!str.equals("(") && !str.equals(")") && !thisInAnFunction(str)) {
                    expression.add(str);
                }
            }
            while (!operators.empty()) {
                expression.add(operators.pop());
            }
            return expression;
        }
        return null;


    }

    /**
     * Finds expressions using a Polish entry
     *
     * @param meanings An array with an expression in the reverse Polish entry
     * @param args     argument string
     * @return The meaning of a mathematical expression
     */
    private static double calculator(ArrayList<String> meanings, String[] args) {
        Stack<Double> result = new Stack<>();
        ArrayList<String> number = substitution(determiningTheValues(args), meanings);
        double res = 0;
        double secondOperand;
        double firstOperand;

        for (String aNumber : number) {
            if (!thisInAnOperator(aNumber) && !thisInAnFunction(aNumber)) {
                try {
                    result.push(Double.parseDouble(aNumber));
                } catch (NumberFormatException e) {
                    out.println("not enough parameters");
                    return 0;
                }
            } else {
                secondOperand = result.pop();
                if (result.size() == 0) {
                    firstOperand = 0;
                } else {
                    firstOperand = result.pop();
                }
                if (aNumber.equals("sin")) {
                    result.push(firstOperand);
                    result.push(Math.sin(secondOperand));
                }
                if (aNumber.equals("cos")) {
                    result.push(firstOperand);
                    result.push(Math.cos(secondOperand));
                }
                if (aNumber.equals("tan")) {
                    result.push(firstOperand);
                    result.push(Math.tan(secondOperand));
                }
                if (aNumber.equals("atan")) {
                    result.push(firstOperand);
                    result.push(Math.atan(secondOperand));
                }
                if (aNumber.equals("log10")) {
                    result.push(firstOperand);
                    result.push(Math.log10(secondOperand));
                }
                if (aNumber.equals("log2")) {
                    result.push(firstOperand);
                    result.push(Math.log(secondOperand));
                }
                if (aNumber.equals("sqrt")) {
                    result.push(firstOperand);
                    result.push(Math.sqrt(secondOperand));
                }
                if (aNumber.equals("^")) {
                    result.push(Math.pow(firstOperand, secondOperand));
                }
                if (aNumber.equals("*")) {
                    result.push(firstOperand * secondOperand);
                }
                if (aNumber.equals("/")) {
                    result.push(firstOperand / secondOperand);
                }
                if (aNumber.equals("+")) {
                    result.push(firstOperand + secondOperand);
                }
                if (aNumber.equals("-")) {
                    result.push(firstOperand - secondOperand);
                }
            }
        }
        if (!result.empty()) {
            res = result.peek();
        }
        return res;
    }

    /**
     * Determines the priorities of operators
     *
     * @param o the operator whose priority is to be found
     * @return operator priority
     */
    private static int priority(String o) {
        String[] operators = {"^", "*", "/", "+", "-", "("};
        if (o.equals(operators[0])) {
            return 3;
        } else if (o.equals(operators[1]) || o.equals(operators[2])) {
            return 2;
        } else if (o.equals(operators[3]) || o.equals(operators[4])) {
            return 1;
        } else if (o.equals(operators[5])) {
            return 0;
        }

        return 4;
    }


    /**
     * Determines whether the string is an operator
     * @param o string to be indentified
     * @return true if it is an operator
     */
    private static boolean thisInAnOperator(String o) {
        if (o.equals("^") || o.equals("*") || o.equals("/") || o.equals("+") || o.equals("-")) {
            return true;
        }
        return false;

    }

    /**
     * Determines whether the string is an function
     * @param o string to be indentified
     * @return true if it is an function
     */
    private static boolean thisInAnFunction(String o) {
        if (o.equals("sin") || o.equals("cos") || o.equals("tan") || o.equals("atan") || o.equals("log10")
                || o.equals("log2") || o.equals("sqrt")) {
            return true;
        }
        return false;

    }
}
