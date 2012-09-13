package h1;

import java.util.HashMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;


/** Lataa ja avaa gson-2.2.2.zip osoitteesta 
 *  http://google-gson.googlecode.com/files/google-gson-2.2.2-release.zip
 *  
 *  Komentorivilta kaantaessasi kutsu 
 *  javac -cp /PATH/TO/google-gson-2.2.2.jar BFS.java
 *  
 *  Jos kaytat IDEa, niin hae googlella miten saat sen omaan IDEesi kiinni
 *  "linking external jar files to <IDE> project", tjsp rimpsulla.
 */
import com.google.gson.*;

public class BFS {
	
	/** Kaikki pysakit taulukossa. 
	 * 
	 * 	Pari huomioita pysakkien muodostamasta verkosta: 
	 *  1.) verkko on osittain suunnattu, eli jossain kohdissa pysakkien valin
	 *  paasee kulkemaan vain toiseen suuntaan.
	 *  
	 *  2.) Kahden pysakin valin kaaren paino voi vaihtua riippuen siita kumpaan
	 *  suuntaan se kuljetaan (A->B 2 min, mutta B->A 3 min). 
	 *  (Huom. Kaarten painoja ei tarvita ensimmaisen viikon laskuharjoituksissa.)
	 * */
	Pysakki[] pysakit;
	
	/** Pysakit key:koodi value:olio hakupareina. Oliot ovat samat kuin 
	 *  pysakit-taulukossa. Pysakki p = this.psMap.get(Pysakki.koodi)
	 * */
	HashMap<String, Pysakki> psMap;
	
	/** Konstruktori joka alustaa 'pysakit' ja 'psMap' tietorakenteet. 
	 *  Lukee 'verkko.json' tiedoston ja erittelee seka muuntaa sielta kaikki
	 *  Pysakki-oliot.
	 * 
	 * @param filePath /PATH/TO/verkko.json 
 	 */
	public BFS(String filePath) {
		JsonArray psArr = readJSON(filePath);
		Gson gson = new Gson();
		this.pysakit = new Pysakki[psArr.size()];
		for (int i = 0; i < psArr.size(); i++) {
			this.pysakit[i] = gson.fromJson(psArr.get(i), Pysakki.class);
//			System.out.println(this.pysakit[i].koodi + " " + this.pysakit[i].nimi);
		}
		System.out.println("Pysäkkejä luettu " + psArr.size() + " kpl");
		this.psMap = new HashMap<String, Pysakki>();
		for (Pysakki p: pysakit) this.psMap.put(p.koodi, p);
		
		int yhteyksia = 0;
		for (Pysakki p: this.pysakit) {
			for (String s: p.naapurit.keySet()) {
				yhteyksia++;
				// System.out.println(p.koodi +" "+ p.nimi +" -> " + s + " " + this.psMap.get(s).nimi + " " + p.naapurit.get(s) +  " min"); 	
			}
		}
		System.out.println("Pysäkkien välisiä yhteyksiä alustettu " + yhteyksia + " kpl");
	}
	
	/** Apumetodi, kayta konstruktoria. Lukee annetun tiedoston palautettavaan
	 *  Stringiin.
	 * 
	 * @param filePath path to file.
	 * @return file as string
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(String filePath) throws java.io.IOException { 
		byte[] buffer = new byte[(int) new File(filePath).length()]; 
		BufferedInputStream f = null; 
		
		try { 
			f = new BufferedInputStream(new FileInputStream(filePath)); 
			f.read(buffer); 
		} 
		finally { 
			if (f != null) 
				try { 
					f.close();
				} 
				catch (IOException ignored) { } 
		} 
		return new String(buffer); 
	}
	
	/** Apumetodi, kayta konstruktoria. Parsii annetusta tiedostosta JSON
	 *  taulukon.
	 *
	 * @param filePath tiedostopolku luettavaan tiedostoon
	 * @return JsonArray, joka edustaa tiedostosta luettuja JSON-olioita.
	 */
	private static JsonArray readJSON(String filePath) {
		
		JsonParser parser = new JsonParser();
		String json = "";
		try {
			json = readFileAsString(filePath);
		}
		catch (Exception e) { }
		
		JsonArray arr = parser.parse(json).getAsJsonArray();
		return arr;	
	}
	
	/** Tulostaa annetun reitin R plot komennot. Kutsu tata metodia, ja 
	 * 	copypastea tulostuvat kolmerivia R:n komentoriville sen jalkeen kun 
	 *  olet kirjoittanut source("/PATH/TO/rplot.txt") - komennon ja R on 
	 *  piirtany raitiovaunuverkon uuteen ikkunaan. Valitsemasi reitin tulisi 
	 *  nakya verkon paalla oranssina. 
	 *  
	 *  Vapaavalintainen visualisointityokalu jos haluaa kayttaa.
	 * 
	 * @param x reitin (pysakkien) x-koordinaatit
	 * @param y reitin (pysakkien) y-koordinaatit
	 */
	public void rLine(int[] x, int[] y) {
		String rx = "x <- c(";
		String ry = "y <- c(";
		
		for (int i = 0; i< x.length; i++) {
			rx = rx + x[i] + ", ";
			ry = ry + y[i] + ", ";
		}
		rx = rx.substring(0, rx.length() -2) +")";
		ry = ry.substring(0, ry.length() -2) +")";
		System.out.println(rx);
		System.out.println(ry);
		System.out.println("lines(x,y, lwd = 2, col = \"orange\")");
	}

	
	/** Toteuta leveyssuuntainen haku.
	 *  Pida muistissa omassa hakutilaoliossasi mika oli edeltava tila, josta
	 *  ko. tilaan paastiin. 
	 *  
	 *  Maaliin paastyasi tulosta lapikaytyjen Pysakkien koodit (ja nimet)
	 *  Voit halutessasi myos visualisoida kuljettua reittia rLine-metodin avulla.
	 *  
	 * @param lahto Lahtopysakin koodi
	 * @param maali Maalipysakin koodi
	 */
	public void haku(String lahto, String maali, boolean visualize) {
		System.out.format("Haetaan nopein (vähiten pysäkkivälejä) reitti pysäkiltä %s pysäkille %s\n", lahto, maali);
		
		LinkedList<Hakualkio> tutkittavat = new LinkedList<Hakualkio>();
		List<String> tutkitut = new ArrayList<String>();
		Pysakki alku = psMap.get(lahto);
		tutkittavat.push(new Hakualkio(alku, null));
		tutkitut.add(lahto);
		
		Hakualkio loytyi = etsiReitti(tutkittavat, tutkitut, maali);
		
		if (loytyi == null) {
			System.out.println("\nReittiä ei ole olemassa!");
		}
		else {
			System.out.println("\nReitti löytyi!");
			
			System.out.println("Reitti käänteisessä järjestyksessä:");
			
			do {
				System.out.format("\tKoodi %s / Nimi %s\n", loytyi.pysakki.koodi, loytyi.pysakki.nimi);
				loytyi = loytyi.edellinen;
			} while (loytyi != null);
		}
		
	}
	
	private Hakualkio etsiReitti(LinkedList<Hakualkio> tutkittavat, List<String> tutkitut, String maaliId) {
		while (!tutkittavat.isEmpty()) {
			Hakualkio alkio = tutkittavat.pop();
			if (alkio.pysakki.koodi.equals(maaliId)) {
				return alkio;
			}
			else {
				Hakualkio edellinen = alkio;
				for (String s: alkio.pysakki.naapurit.keySet()) {
					Pysakki p = psMap.get(s);
					if (!tutkitut.contains(p.koodi)) {
						Hakualkio h = new Hakualkio(p, edellinen);
						tutkittavat.add(h);
						tutkitut.add(p.koodi);
					}
				}
			}
		}
		return null;
	}
	
	private class Hakualkio {
		protected Pysakki pysakki; // pysäkki
		protected Hakualkio edellinen; // edellinen pysäkki
		public Hakualkio(Pysakki pysakki, Hakualkio edellinen) {
			this.pysakki = pysakki;
			this.edellinen = edellinen;
		}
	}
	
	public static void main(String[] args) {
		BFS bfs = new BFS("./verkko.json");
		System.out.println("-- Alustus ohi --");
		String lahto = "1250429"; // oletus
		String maali = "1121480"; // oletus
		if (args.length < 2) {
			System.out.println( "Käynnistys:\n" +
								"BFS [lähtö] [maali] [R]\n" + 
								"    [lähtö] = lähtöpysäkin koodi\n" + 
								"    [maali] = kohdepysäkin koodi\n" +
								"    [R] = Visualisoidaanko reitti (tyhjä: ei / muu: kyllä)");
			System.out.format("Ilman parametreja käytetään oletuspysäkkejä %s ja %s\n\n", lahto, maali);
		}
		if (args.length >= 2) {
			lahto = args[0];
			maali = args[1];
		}
		boolean visualize = false;
		if (args.length >= 3) {
			visualize = true;
		}
		bfs.haku(lahto, maali, visualize);
	}

}
