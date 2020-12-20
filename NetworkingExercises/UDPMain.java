package zadachiNetworking;

// UDP e poednostaven protokol
// Imame ednostaven message passing;
// Na serverot mu stiga poraka i povratna adresa
// Ja obrabotuva i go vrakja odgovorot
// Nema konekcija, shto znachi shtom pratime neshto, nema opcija da dodademe ushte neshto na toa
// Edinstveno shto mozhe da napravime e da pratime celosno nova poraka;
// I ovde mozhe da kreirame Worker-i na serverska strana
// koi paralelno kje ja prochitaat porakata, i kje pratat nova do korisnikot
public class UDPMain {

    public static void main(String[] args) throws InterruptedException {
        new UDPServer().start();
        Thread.sleep(1000);
        for (int i = 0; i < 5; ++i)
            new UDPClient().start();
    }
}
