public class Main {

    public static void main(String[] args) {
        int c = 5;
        int num1 = 20;
        int num2 = 10;

        System.out.println("num1 > " + (c++ + ++c));

        Calculator calc = new Calculator(num1, num2);
        System.out.println("result > " + calc.plus(num1, num2));
    }

}

class Calculator {
    public int num1;
    public int num2;

    Calculator(int num1, int num2) {
        this.num1 = num1;
        this.num2 = num2;
    }

    public int plus(int num1, int num2) {
        return num1 + num2;
    }
}
// 2-3-2 c++ + ++cの計算結果が間違っています。12です。