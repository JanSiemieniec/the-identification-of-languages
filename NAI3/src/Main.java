import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {

        Path paths = Paths.get("Languages");

        List<String> lang = Files.walk(paths).filter(Files::isDirectory).
                map(Path::toString).filter(s -> s.contains("\\")).               //żeby nie brało \Languages
                        map(x -> x = (x.substring(x.indexOf('\\') + 1))).toList();      //+1 żeby nie brało \
        int K = lang.size();
        List<Path> files = Files.walk(paths).filter(Files::isRegularFile).toList(); //zapisuje wszystkie ścieżki plików do listy
        List<List<Double>> traningData = new ArrayList<>();
        for (Path ele : files) {
            double[] frequency = countFrequencyLearning(ele);
            frequency[frequency.length - 1] = getLang(ele, lang);           //wyciaga jaki powinien byc jezyk
            traningData.add(Arrays.stream(frequency).boxed().collect(Collectors.toList()));
        }

        List<Perceptron> layer = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            layer.add(new Perceptron(i));
        }
        for (Perceptron perceptron : layer) {
            List<List<Double>> tmp = new ArrayList<>(traningData);
            while (tmp.size() > 0) {                //losowanie plikow w roznych kolejnosciach
                int random = (int) (Math.random() * (tmp.size() - 1));
                perceptron.learning(tmp.get(random));       //uczenie
                tmp.remove(random);
            }
        }


        System.out.print("paste some piece of text and i'll decide which language this text has been written (");
        for (int i = 0; i < lang.size() - 1; i++) {
            System.out.print(lang.get(i) + ", ");
        }
        System.out.println(lang.get(lang.size() - 1) + ')');
        while (true) {
            List<Double> maxList = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);
            String pastedText = scanner.nextLine();
            List<Double> frequency = Arrays.stream(countFrequency(pastedText, new double[26])).boxed().toList();
            frequency = Perceptron.normalize(frequency);
            double max = 0;
            for (Perceptron ele : layer) {
                ele.w = Perceptron.normalize(ele.w);
                double sigm = 1 / (1 + Math.pow(Math.E, -1 * (Perceptron.scalarProduct(frequency, ele.w) - ele.t)));        //funckaj sigmoidalna
                maxList.add(sigm);
                if (sigm > max) max = sigm;
            }
            String resoult;
            resoult = lang.get(maxList.indexOf(max));
            System.out.println("I think it is " + resoult);     //maksimum selektorow
        }


    }

    public static double[] countFrequencyLearning(Path path) throws FileNotFoundException {
        double[] tab = new double[27];      //ostatni index wskazuje jaki powinien byc to kraj
        File file = new File(String.valueOf(path));
        Scanner scanner = new Scanner(file);
        String linia = "";
        while (scanner.hasNext()) {
            linia += scanner.nextLine();
        }
        return countFrequency(linia, tab);
    }


    public static double[] countFrequency(String linia, double[] tab) {
        int all = 0;
        for (int i = 0; i < linia.length(); i++) {
            if (linia.charAt(i) >= 'A' && linia.charAt(i) <= 'Z') {
                tab[linia.charAt(i) - 'A']++;
                all++;
            } else if (linia.charAt(i) >= 'a' && linia.charAt(i) <= 'z') {
                tab[linia.charAt(i) - 'a']++;
                all++;
            }
        }

        for (int i = 0; i < tab.length; i++) {
            tab[i] /= all;
        }
        return tab;
    }

    public static double getLang(Path path, List<String> langues) {
        for (int i = 0; i < langues.size(); i++) {
            if (path.toString().contains(langues.get(i)))
                return i;
        }
        return 0;
    }
}
