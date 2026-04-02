/**
 * Represents an earthquake event with data parsed from the earthquake input file.
 * Records are maintained in the earthquake-list for a 6-hour window.
 */
public class Earthquake {
	private String id; //depremin id'si
	private int time; //depremin gerçekleştiği saat
	private String place; //depremin gerçekleştiği yerin ismi
	private double magnitude; // depremin büyüklüğü
	private double latitude; //depremin merkezinin enlemi
	private double longitude; //depremin merkezinin boylamı

	/**
	 * Constructs an Earthquake object with the given parameters.
	 * @param id The earthquake ID
	 * @param time The hour the earthquake occurred
	 * @param place Description of the location
	 * @param magnitude The magnitude value
	 * @param latitude The latitude coordinate
	 * @param longitude The longitude coordinate
	 */
	public Earthquake(String id, int time, String place, double magnitude, double latitude, double longitude) {
		//verileri nesne içine kaydediyoruz
		this.id = id;
		this.time = time;
		this.place = place;
		this.magnitude = magnitude;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	//GETTER METHODS
	/** @return The timestamp of the earthquake */
	//simülasyon saati ilerledikçe, listenin başındaki depremlerin kaç saat önce olduğunu kontrol edip 6 saati geçenleri silmek için kullanıyoruz
	public int getTime() { return time; }

	/** @return The magnitude of the earthquake */
	//en büyüğü bulmak ve mesafe hesaplamak için gerekli
	public double getMagnitude() { return magnitude; }

	/** @return The location description */
	//çıktı mesajlarında yer bilgisini yazmak için
	public String getPlace() { return place; }

	/** @return The latitude of the epicenter */
	//izleyiciye olan uzaklığı ölçmek için
	public double getLatitude() { return latitude; }

	/** @return The longitude of the epicenter */
	// İzleyiciye olan uzaklığı ölçmek için
	public double getLongitude() { return longitude; }

	/**
	 * Returns a formatted string for the query-largest output.
	 */
	//query-largest sorgusu için istenen format
	//istenen "Magnitude <mag> at <place>" formatını döndürüyor
	@Override
	public String toString() {
		return "Magnitude " + magnitude + " at " + place;
	}
}

