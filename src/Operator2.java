public enum Operator2 {
    ADD("+") {
        @Override
        public double apply(double a, double b) {
            return a + b;
        }
    },
    SUB("-") {
        @Override
        public double apply(double a, double b) {
            return a - b;
        }
    },
    MUL("*") {
        @Override
        public double apply(double a, double b) {
            return a * b;
        }
    },
    DIV("/") {
        @Override
        public double apply(double a, double b) {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return a / b;
        }
    },
    MOD("%") {
        @Override
        public double apply(double a, double b) {
            if (b == 0) throw new ArithmeticException("Modulo by zero");
            return a % b;
        }
    };

    private final String symbol;

    // Constructor to associate a symbol (e.g., "+") with each enum
    Operator2(String symbol) {
        this.symbol = symbol;
    }

    // Abstract method that each enum constant implements
    public abstract double apply(double a, double b);

    // Get the symbol associated with the operator (e.g., "+" for ADD)
    public String getSymbol() {
        return symbol;
    }

    // Convert a symbol (like "+") to the corresponding OperatorType enum
    public static Operator2 fromSymbol(String symbol) {
        for (Operator2 op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Invalid operator symbol: " + symbol);
    }
}