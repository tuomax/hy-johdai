package h1;

import java.util.HashMap;

public class Pysakki {
	
	/** Pysakin yksiselitteinen koodi. ID, jota voit kayttaa pysakin 
	 *  tunnistamiseen.
	 */
	public String koodi;
	
	/** Pysakin osoite. Jotkin osoitteet sisaltavat kadun ja numeron, toiset
	 * vain kadun nimen. */
	public String osoite;
	
	/** Pysakin nimi. Tama ei valttamatta ole yksiselitteinen. */
	public String nimi;
	
	/** Pysakin x-koordinaatti. */
	public int x;
	
	/** Pysakin y-koordinaatti. */
	public int y;
	
	/** Pysakin naapuripysakit ja niille matkustamiseen kuluva aika. 
	 *  key = Pysakki.koodi, value = matkustusminuutit. */
	public HashMap<String, Integer> naapurit;

	public Pysakki() {
		this.koodi = "";
		this.osoite = "";
		this.nimi = "";
		this.x = 0;
		this.y = 0;
		this.naapurit = new HashMap<String, Integer>();
	}
}
