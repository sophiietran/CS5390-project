//public enum Operator { ADD, SUB, MUL, DIV, MOD }

//public class Operator {
//    public enum Type { ADD, SUB, MUL, DIV, MOD }
//}

public enum Operator {
    ADD('+') {
        @Override
        public double apply(double a, double b) {
            return a + b;
        }
    },
    SUB('-') {
        @Override
        public double apply(double a, double b) {
            return a - b;
        }
    },
    MUL('*') {
        @Override
        public double apply(double a, double b) {
            return a * b;
        }
    },
    DIV('/') {
        @Override
        public double apply(double a, double b) {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return a / b;
        }
    },
    MOD('%') {
        @Override
        public double apply(double a, double b) {
            if (b == 0) throw new ArithmeticException("Modulo by zero");
            return a % b;
        }
    };

    private final Character symbol;

    // Constructor to associate a symbol (e.g., "+") with each enum
    Operator(Character symbol) {
        this.symbol = symbol;
    }

    // Abstract method that each enum constant implements
    public abstract double apply(double a, double b);

    // Get the symbol associated with the operator (e.g., "+" for ADD)
    public Character getSymbol() {
        return symbol;
    }

    // Convert a symbol (like "+") to the corresponding OperatorType enum
    public static Operator fromSymbol(Character symbol) {
        for (Operator op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Invalid operator symbol: " + symbol);
    }
}