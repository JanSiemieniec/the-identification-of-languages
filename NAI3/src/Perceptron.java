import java.util.ArrayList;
import java.util.List;

public class Perceptron {
    public double t;
    public List<Double> w = new ArrayList<>();
    public double a;
    public double whichLanguage;

    public Perceptron(double whichLanguage) {
        this.t = Math.random();
        for (int i = 0; i < 26; i++) {
            w.add((double) 0);      //ustawienie poczatkowych wag na 0 znacznie poprawilo wydajnosc
        }
        this.a = 0.05;
        this.whichLanguage = whichLanguage;
    }

    public void learning(List<Double> data) {
        int d;
        if (whichLanguage == data.get(data.size() - 1))
            d = 1;      //sprawdza czy powinien sie aktywowac czy nie na podtawie ostatniego elementu
        else d = 0;
        boolean right = isRight(data, w, t);
        int y = getY(right);                                //sprawdza czy sie aktywuje
        while (d - y != 0) {                    //póki jest zla odpowiedz to trenuje
            w = traningW(w, data, a, d, y);
            t = traningT(t, a, y, d);
            right = isRight(data, w, t);
            y = getY(right);
        }
    }

    public static List<Double> traningW(List<Double> w, List<Double> p, double a, int d, int y) {
        for (int i = 0; i < w.size(); i++) {
            w.set(i, w.get(i) + (d - y) * a * p.get(i));
        }
        return w;
    }

    public static double traningT(double t, double a, int y, int d) {
        return t - (d - y) * a;
    }

    public static int getY(boolean right) {
        if (right) return 1;
        return 0;
    }

    private boolean isRight(List<Double> data, List<Double> w, double t) {      //sprawdza czy sie odpala
        double scalar = scalarProduct(data, w);
        return scalar - t >= 0;
    }


    public static double scalarProduct(List<Double> p, List<Double> w) {
        double sum = 0;
        for (int i = 0; i < w.size(); i++) {
            sum += p.get(i) * w.get(i);
        }
        return sum;
    }

    public static List<Double> normalize(List<Double> w) {          //dzielenie kazdej skladowej przed dlugosc wektora
        double len = Math.sqrt(scalarProduct(w, w));
        List<Double> tmp = new ArrayList<>(w);
        for (int i = 0; i < 26; i++) {
            tmp.set(i, tmp.get(i) / len);
        }
        return tmp;
    }
}
