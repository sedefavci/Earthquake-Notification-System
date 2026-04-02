/**
 * Represents a user in the system who is registered for earthquake notifications.
 * These objects are stored in the doubly linked watcher-list.
 */
public class Watcher {
	private String name; //watcher'ın yani izleyicinin adı (Tom,Jane,Taylor,John,Henry)
	private double latitude; //konumun enlem bilgisi
	private double longitude; // konumun boylam bilgisi

	/**
	 * Constructs a new Watcher with the specified details.
	 * @param name The name of the watcher
	 * @param latitude The latitude coordinate
	 * @param longitude The longitude coordinate
	 */
	public Watcher(String name, double latitude, double longitude){
		// Parametreleri sınıfın değişkenlerine atıyoruz
		this.name=name;
		this.latitude=latitude;
		this.longitude=longitude;
	}

	//GETTER METHODS
	/** @return The name of the watcher */
	public String getName(){return name;}

	/** @return The latitude coordinate of the watcher */
	public double getLatitude(){return latitude;}

	/** @return The longitude coordinate of the watcher */
	public double getLongitude(){return longitude;}

	/**
	 * Returns the string representation of the watcher.
	 * Used for printing notifications and list management.
	 */
	@Override
	public String toString(){return name;} //çıktı formatına uyum sağlamak için sadece ismi döndürüyoruz
}
