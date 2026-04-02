import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
/**
 * Main class that manages the discrete event simulation.
 * It coordinates file reading, time progression, and notifications.
 */
public class NotificationSystem {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter watcher file name: ");
		String watcherFileName = scanner.nextLine();
		System.out.println("Enter earthquake file name: ");
		String earthquakeFileName = scanner.nextLine();
		System.out.println();
		try {
			File watcherFile = new File(watcherFileName);
			File earthquakeFile = new File(earthquakeFileName);

			Scanner watcherSc = new Scanner(watcherFile);
			Scanner earthquakeSc = new Scanner(earthquakeFile);

			DoublyLinkedList<Watcher> watcherList = new DoublyLinkedList<>();
			SinglyLinkedList<Earthquake> earthquakeList = new SinglyLinkedList<>();

			//depremlerin dosyadan okunduğunda kaybolmaması için hepsini en başta bu listeye alıyoruz
			//çünkü Scanner bir kez okuduğunda o veri dosyadan gider, biz burada veriyi hafızaya hapsediyoruz
			SinglyLinkedList<Earthquake> earthquakeStorage = new SinglyLinkedList<>();
			while (earthquakeSc.hasNext("<earthquake>")) {
				earthquakeStorage.addLast(parseEarthquake(earthquakeSc));
			}
			earthquakeSc.close();
			int currentHour = 0; //simülasyonu 0. saatten başlatıyoruz

			//watcher dosyasından okunan ama saati henüz gelmemiş olan komutu burada bekletiyoruz
			//bu değişkenler sayesinde "gelecekten gelen" veriyi cebimizde tutup saati gelince kullanacağız
			int nextEventTime = -1;
			String nextCommand = "";
			if (watcherSc.hasNextInt()) {
				nextEventTime = watcherSc.nextInt();
				nextCommand = watcherSc.next();
			}
			//hem dosyalar bitene kadar hem de listede deprem varken döngü sürer
			//bu döngü her dönüşte simülasyon zamanını bir saat ileriye taşır
			//tüm olayların kronolojik sırada gerçekleşmesini bu yapı sağlar
			while (nextEventTime != -1 || !earthquakeStorage.isEmpty() || !earthquakeList.isEmpty()) {
				//listenin başındaki depremlerin üzerinden 6 saat geçip geçmediğine bakılır
				//şu anki saatten deprem saati çıkarıldığında sonuç 6'dan büyükse temizlenir
				//bu işlem her saat başında yapılarak listenin güncel kalması sağlanır
				while (!earthquakeList.isEmpty() && (currentHour - earthquakeList.first().getTime()) > 6) {
					earthquakeList.removeFirst(); //süresi dolan depremi listeden siliyoruz
				}
				//watcher dosyasında o anki saate ait bir komut var mı kontrol edilir
				//eğer bir sonraki komutun saati şu anki saate eşitse işlem yapılır
				//bu kontrol sayesinde komutlar tam zamanında devreye girmiş olur
				while (nextEventTime != -1 && nextEventTime == currentHour) {
					//yeni bir izleyici ekleme komutu geldiyse bu blok çalışır
					//izleyicinin koordinatlarını ve ismini sırayla okuyup kaydeder
					if (nextCommand.equals("add")) {
						//burada nextDouble yerine parseDouble kullanarak nokta hatasını çözüyoruz
						//bu metot sistem dilinden bağımsız olarak noktayı her zaman tanır
						double lat = Double.parseDouble(watcherSc.next()); //enlemi metin olarak okuyup sayıya çeviriyoruz
						//boylam bilgisini de aynı şekilde güvenli yolla okuyoruz
						//böylece InputMismatchException hatasından tamamen kurtulmuş oluyoruz
						double lon = Double.parseDouble(watcherSc.next()); //boylamı metin olarak okuyup sayıya çeviriyoruz
						String name = watcherSc.next(); //izleyici ismini alıyoruz

						watcherList.addLast(new Watcher(name, lat, lon)); //listeye ekliyoruz
						System.out.println(name + " is added to the watcher-list\n"); //\n koyma sebebim output-all-file dosyasındaki gibi gözükmesi için
					}
					//isime göre izleyici silme komutu geldiyse bu blok çalışır
					//listeyi baştan sona gezip ismi eşleşen düğümü bulur ve siler
					else if (nextCommand.equals("delete")) {
						String nameToDelete = watcherSc.next(); //silinecek ismi okuyoruz

						//orijinal listedeki elemanları geçici olarak tutacak bir liste oluşturuyoruz
						//ismin listede olup olmadığını anlamak için bir kontrol değişkeni kullanıyoruz
						DoublyLinkedList<Watcher> tempWatcherList = new DoublyLinkedList<>();
						boolean isFound = false; //silinecek ismin bulunup bulunmadığını takip eder
						//bu kısım orijinal liste boşalana kadar tüm elemanları tek tek çıkarır
						//her eleman çıkarıldığında ismi kontrol edilerek yeni listeye aktarılır
						while (!watcherList.isEmpty()) {
							Watcher w = watcherList.removeFirst(); //listenin başındaki izleyiciyi alıyoruz

							//bu kısım alınan izleyicinin ismi silinecek isimle aynı mı diye bakar
							if (w.getName().equals(nameToDelete)) {
								isFound = true; //isim eşleştiği için bu elemanı yeni listeye eklemiyoruz
							} else {
								tempWatcherList.addLast(w); //isim farklıysa izleyiciyi geçici listeye ekliyoruz
							}
						}
						//bu kısım ayıklanmış izleyicilerden oluşan listeyi ana listeye geri atar
						//böylece orijinal liste içinden sadece istenen isim temizlenmiş olur
						watcherList = tempWatcherList;

						//eğer silme işlemi yapıldıysa ekrana onay mesajını yazdırır
						//isim listede hiç yoksa herhangi bir mesaj basmadan işlemi bitirir
						if (isFound) {
							System.out.println(nameToDelete + " is removed from the watcher-list\n"); //burada da \n koyma sebebim output dosyasındaki gibi gözükmesi için, labda output okunaklı olsun dendiği için
						}
					}
					//en büyük depremi sorgulama komutu geldiyse bu blok çalışır
					//mevcut 6 saatlik liste içindeki en şiddetli depremi bulur
					else if (nextCommand.equals("query-largest")) {
						//eğer deprem listesi boşsa outputta böyle gözükür
						if (earthquakeList.isEmpty()) {
							System.out.println("No record on list\n");
						}
						//eğer deprem listesi boş değilse arama işlemini başlatır
						//liste boşken sorgu yapılırsa herhangi bir işlem yapmadan geçer
						//!earthquakeList.isEmpty()
						 else {
							Earthquake largest = earthquakeList.first(); //ilk depremi referans alıyoruz
							SinglyLinkedList<Earthquake> tempEarthquakeList = new SinglyLinkedList<>(); //geçici liste

							//bu kısım tüm depremleri tek tek çıkarıp en büyüğünü tespit eder
							//her deprem çıkarıldığında şiddeti (magnitude) kontrol ediliyor
							while (!earthquakeList.isEmpty()) {
								Earthquake currentEarthquake = earthquakeList.removeFirst(); //depremi listeden alıyoruz
								if (currentEarthquake.getMagnitude() > largest.getMagnitude()) {
									largest = currentEarthquake; //daha büyük şiddet bulunca güncelliyoruz
								}
								tempEarthquakeList.addLast(currentEarthquake); //tekrar listeye eklemek için geçiciye alıyoruz
							}
							earthquakeList = tempEarthquakeList; //ayıklanmış listeyi geri yüklüyoruz
							System.out.println("Largest earthquake in the past 6 hours: ");
							System.out.println("Magnitude "+largest.getMagnitude()+" at "+largest.getPlace()+"\n");
						}
					}
					//bir sonraki komutu dosyadan okuyup beklemeye alıyoruz
					if (watcherSc.hasNextInt()) {
						nextEventTime = watcherSc.nextInt();
						nextCommand = watcherSc.next();
					} else {
						nextEventTime = -1; //dosya bittiyse durduruyoruz
					}
				}
				//hafızaya aldığımız depremlerden saati şu anki simülasyon saatine uyan var mı bakılır
				//while kullanıyoruz çünkü aynı saatte birden fazla deprem gerçekleşmiş olabilir
				while (!earthquakeStorage.isEmpty() && earthquakeStorage.first().getTime() == currentHour) {
					//saati gelen depremi depodan (storage) çıkartıyoruz
					Earthquake newEq = earthquakeStorage.removeFirst();
					//bu depremi 6 saatlik aktif deprem listesine ekliyoruz
					earthquakeList.addLast(newEq);
					//deprem eklendi mesajını basıyoruz
					System.out.println("Earthquake " + newEq.getPlace() + " is inserted into the earthquake-list");
					//watcherları kontrol etmek için geçici liste
					DoublyLinkedList<Watcher> tempForFelt = new DoublyLinkedList<>();
					//listedeki tüm izleyicileri tek tek çıkarıp depremi hissedip hissetmedikleri ölçülüyor
					while (!watcherList.isEmpty()) {
						Watcher w = watcherList.removeFirst(); //sıradaki izleyiciyi alıyoruz
						double distance = calculateDistance(w.getLatitude(), w.getLongitude(), newEq.getLatitude(), newEq.getLongitude());//mesafe hesaplama metodu
						//mesafe şartı kontrolü: distance < (2 * magnitude^3)
						if (distance < (2 * Math.pow(newEq.getMagnitude(), 3))) {
							System.out.println("Earthquake " + newEq.getPlace() + " is close to " + w.getName());
						}
						//izleyiciyi orijinal sırasını bozmadan geri eklemek için geçici listeye atıyoruz
						tempForFelt.addLast(w);
					}
					System.out.println();
					//taranan tüm izleyicileri ana listeye geri aktarıyoruz
					watcherList = tempForFelt;
				}
				currentHour++;//saati bir artırarak simülasyonu bir sonraki adıma taşıyoruz
			}
			watcherSc.close();
			earthquakeSc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: File not found.");
		}
	}
	/**
	 * Reads an earthquake record from the scanner and returns an Earthquake object.
	 * This method parses the XML-like tags and handles multi-word location names.
	 * @param scanner The scanner for the earthquake file
	 * @return A new Earthquake object containing all parsed data
	 */
	public static Earthquake parseEarthquake(Scanner scanner) {
		//burada <earthquake> etiketini okuyor ama kaydetmiyoruz
		//bu etiket sadece bir bloğun başladığını bize haber veriyor
		//herhangi bir veriye sahip olmadığı için sistemden temizliyoruz
		scanner.next();

		// <id> 001 </id>
		//depremin benzersiz kimlik numarasını ayıklıyor
		scanner.next(); //<id> etiketini okuyor ama kaydetmiyoruz
		String id = scanner.next(); //gerçek id verisini alıp değişkene atıyoruz
		scanner.next(); //</id> etiketini okuyup sistemden atlıyoruz

		// <time> 6 </time>
		//depremin gerçekleştiği simülasyon saatini belirliyor
		scanner.next(); //<time> etiketini okuyor ama kaydetmiyoruz
		//Integer.parseInt kullanarak sistem dilinden bağımsız güvenli okuma yapıyoruz
		//bu sayede InputMismatchException hatasının önüne geçmiş oluyoruz
		int time = Integer.parseInt(scanner.next()); //saati sayıya çeviriyoruz
		scanner.next(); //</time> etiketini okuyup sistemden geçiyoruz

		// <place> 4km East of San Francisco, CA </place>
		//depremin yer ismini kelime kelime birleştirir
		scanner.next(); //<place> etiketini okuyor ama kaydetmiyoruz
		String place = ""; //yer ismini toplamak için boş kutu oluşturuyoruz

		//yer ismi birden fazla kelime olabildiği için döngüyle topluyoruz
		// kapanış etiketi gelene kadar tüm kelimeleri tek tek toplar
		// san francisco gibi ayrı yazılan yerleri tek parça yapar
		while (!scanner.hasNext("</place>")) {
			place += scanner.next() + " "; // her kelimeyi araya boşluk koyarak ekler
		}
		place = place.trim(); // metnin sonundaki fazla boşluğu temizliyoruz
		scanner.next(); // </place> etiketini okuyup sistemden atlıyoruz

		// <coordinates> -115.5808, 33.0187, 9.5 </coordinates>
		//depremin konumunu ve derinliğini okur
		scanner.next(); //<coordinates> etiketini okuyor ama kaydetmiyoruz
		//Double.parseDouble her zaman nokta (.) karakterini ondalık olarak bekler
		//bu metot sayesinde bilgisayarın dili ne olursa olsun hata almayız
		//enlem bilgisini okur ve sonundaki virgül karakterini siler
		//temizlenen metni ondalıklı sayıya (double) çevirip saklar
		double latitude = Double.parseDouble(scanner.next().replace(",", ""));
		//boylam bilgisini okur ve sonundaki virgül karakterini siler
		//bu koordinatları daha sonra mesafe hesabında kullanacağız
		double longitude = Double.parseDouble(scanner.next().replace(",", ""));
		//derinlik bilgisini okur ama hesaplamada lazım olmadığı için atar
		scanner.next(); //derinlik (depth) bilgisini okuyor ama kaydetmiyoruz
		scanner.next(); //</coordinates> etiketini okuyup sistemden atlıyoruz

		// <magnitude> 3.971... </magnitude>
		//depremin büyüklüğünü okuyarak bildirim şartını belirler
		scanner.next(); //<magnitude> etiketini okuyor ama kaydetmiyoruz
		double magnitude = Double.parseDouble(scanner.next()); //şiddeti alıyoruz
		scanner.next(); //</magnitude> etiketini okuyup sistemden geçiyoruz
		//deprem bloğunun tamamen bittiğini sisteme haber verir
		//tarayıcıyı (scanner) bir sonraki deprem verisine hazır hale getirir
		scanner.next(); //</earthquake> etiketini okuyup sistemden atlıyoruz

		//elde edilen tüm bu verilerle yeni bir deprem nesnesi üretir
		//bu nesne daha sonra earthquake-list içine dahil edilecektir
		//privacy leak olmaması için verileri doğrudan constructor ile gönderiyoruz
		return new Earthquake(id, time, place, magnitude, latitude, longitude); //nesneyi döndürüyoruz
	}
	/**
	 * Calculates the straight-line (Euclidean) distance between two sets of coordinates.
	 * @param lat1 Latitude of the first point (Watcher)
	 * @param lon1 Longitude of the first point (Watcher)
	 * @param lat2 Latitude of the second point (Earthquake epicenter)
	 * @param lon2 Longitude of the second point (Earthquake epicenter)
	 * @return The calculated distance as a double
	 */
	public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		//öklid uzaklık formülü: sqrt((x1-x2)^2 + (y1-y2)^2)
		return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2));
	}
	/**
	 * Reads the text content located between two specified XML-like tags.
	 * This method is robust against both spaced and non-spaced tag formats.
	 * @param sc       The Scanner object reading the earthquake file
	 * @param startTag The opening tag to look for (e.g., "<id>")
	 * @param endTag   The closing tag that marks the end of content (e.g., "</id>")
	 * @return The cleaned string content between the tags
	 */
	public static String readTagContent(Scanner sc, String startTag, String endTag) {
		String content = ""; //bu kısımda içine veriyi toplayacağımız boş bir metin oluşturuyoruz
		while (sc.hasNext()) {//dosyada kelime olduğu sürece okumaya devam ediyoruz
			String token = sc.next(); //burada sıradaki parçayı okuyoruz
			content += token + " "; //okuduğu her parçayı araya boşluk koyarak birleştiriyor
			if (token.contains(endTag)) { //bu kısımda ise kapanış etiketini gördüğü an duruyor
				break;
			}
		}
		//birleşen metnin içinden etiketleri silip sadece temiz veriyi bırakıyoruz
		return content.replace(startTag, "").replace(endTag, "").trim();
	}
}
