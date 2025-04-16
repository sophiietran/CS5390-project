//public enum Operator { ADD, SUB, MUL, DIV, MOD }

//public class Operator {
//    public enum Type { ADD, SUB, MUL, DIV, MOD }
//}

public enum Operator {
    ADD('+') {
        @Override
        public double operate(double val1, double val2) {
            return val1 + val2;
        }
    },
    SUB('-') {
        @Override
        public double operate(double val1, double val2) {
            return val1 - val2;
        }
    },
    MUL('*') {
        @Override
        public double operate(double val1, double val2) {
            return val1 * val2;
        }
    },
    DIV('/') {
        @Override
        public double operate(double val1, double val2) {
            if (val2 == 0) {
                throw new ArithmeticException("Division by zero");
            } else {
                return val1 / val2;
            }
        }
    },
    MOD('%') { // TODO: seems in Java, mod behaves differently than most languages
        @Override
        public double operate(double val1, double val2) {
            if (val2 == 0) {
                throw new ArithmeticException("Modulo by zero");
            } else {
                return val1 % val2;
            }
        }
    };

    private final Character symbol;

    // Associate each num with a math character
    Operator(Character symbol) {
        this.symbol = symbol;
    }

    // Abstract method to implement each operator
    public abstract double operate(double val1, double val2);

    // Get arithmetic operation associated with character symbol
    public static Operator fromSymbol(Character symbol) {
        for (Operator op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        
        throw new IllegalArgumentException("Invalid operator symbol: " + symbol);
    }
}