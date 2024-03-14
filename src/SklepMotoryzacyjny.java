import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface Observer {
    void update(String message);
}

class Produkt {
    private String nazwa;
    private double cena;

    public Produkt(String nazwa, double cena) {
        this.nazwa = nazwa;
        this.cena = cena;
    }

    public String getNazwa() { return nazwa; }
    public double getCena() { return cena; }

    @Override
    public String toString() {
        return nazwa + " - " + cena + " PLN";
    }
}

class Klient implements Observer {
    private double stanKonta;

    public Klient(double stanKonta) {
        this.stanKonta = stanKonta;
    }

    public double getStanKonta() { return stanKonta; }

    public void kupProdukt(double cena) {
        stanKonta -= cena;
    }

    @Override
    public void update(String message) {
        System.out.println("Powiadomienie dla klienta: " + message);
    }
}

interface ProduktFactory {
    Produkt createProdukt(String nazwa, double cena);
}

class ProduktFactoryImpl implements ProduktFactory {
    @Override
    public Produkt createProdukt(String nazwa, double cena) {
        return new Produkt(nazwa, cena);
    }
}

class SklepMotoryzacyjny {
    private static SklepMotoryzacyjny instance;
    private List<Observer> observers = new ArrayList<>();
    private List<Produkt> listaProduktow = new ArrayList<>();
    private double stanKonta;
    private ProduktFactory produktFactory;

    private SklepMotoryzacyjny() {
        this.stanKonta = 10000; // Domyślny stan konta sklepu
        this.produktFactory = new ProduktFactoryImpl();
    }

    public static synchronized SklepMotoryzacyjny getInstance() {
        if (instance == null) {
            instance = new SklepMotoryzacyjny();
        }
        return instance;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    public void dodajProdukt(Produkt produkt) {
        listaProduktow.add(produkt);
        notifyObservers("Nowy produkt dostępny: " + produkt.getNazwa());
    }

    public void dodajProdukt(String nazwa, double cena) {
        Produkt produkt = produktFactory.createProdukt(nazwa, cena);
        dodajProdukt(produkt);
    }

    public void wyswietlDostepneProdukty() {
        System.out.println("Dostepne produkty:");
        for (int i = 0; i < listaProduktow.size(); i++) {
            System.out.println((i + 1) + ". " + listaProduktow.get(i));
        }
    }

    public Produkt getProdukt(int indeks) {
        if (indeks >= 0 && indeks < listaProduktow.size()) {
            return listaProduktow.get(indeks);
        } else {
            return null;
        }
    }

    public void kupProdukt(int indeks, int ilosc, Klient klient) {
        Produkt produkt = getProdukt(indeks);
        if (produkt != null && ilosc > 0) {
            double cenaCalkowita = produkt.getCena() * ilosc;
            if (klient.getStanKonta() >= cenaCalkowita) {
                klient.kupProdukt(cenaCalkowita);
                stanKonta += cenaCalkowita;
                System.out.println("Kupiono " + ilosc + " sztuk " + produkt.getNazwa() + " za " + cenaCalkowita + " PLN");
            } else {
                System.out.println("Brak środków na koncie klienta.");
            }
        } else {
            System.out.println("Niepoprawny indeks produktu lub ilość.");
        }
    }

    public void wyswietlStanKonta() {
        System.out.println("Stan konta w sklepie: " + stanKonta + " PLN");
    }

    public static void main(String[] args) {
        SklepMotoryzacyjny sklep = SklepMotoryzacyjny.getInstance();
        sklep.dodajProdukt("Olej silnikowy", 50);
        sklep.dodajProdukt("Filtr oleju", 20);

        Klient klient = new Klient(5000); // Początkowy stan konta klienta
        sklep.addObserver(klient);

        Scanner scanner = new Scanner(System.in);
        int wybor;
        int ilosc;

        System.out.println("Stan konta klienta przed zakupami: " + klient.getStanKonta() + " PLN");

        while (true) {
            System.out.println("Wybierz produkt do zakupu:");
            sklep.wyswietlDostepneProdukty();
            System.out.println("0. Zakoncz zakupy");
            System.out.println("3. Dodaj produkt");
            wybor = scanner.nextInt();

            if (wybor == 0) {
                break;
            }

            if (wybor == 3) {
                System.out.println("Podaj nazwe produktu:");
                scanner.nextLine();
                String nazwa = scanner.nextLine();
                System.out.println("Podaj cene produktu:");
                double cena = scanner.nextDouble();
                sklep.dodajProdukt(nazwa, cena);
            }

            System.out.println("Podaj ilosc:");
            ilosc = scanner.nextInt();

            sklep.kupProdukt(wybor - 1, ilosc, klient);
            System.out.println("Stan konta klienta po zakupach: " + klient.getStanKonta() + " PLN");
            sklep.wyswietlStanKonta();
        }

        scanner.close();
    }
}
