package hk.ust.comp4321;

public class SearchEngineInterface {
//    // input a url
//    String url;
//    // Create a crawler object with the url and the number of page to be crawled
//    Crawler crawler;
//    // ExtractLinks from the url
//
//    SearchEngineInterface(String url, int numPage) {
//        this.crawler = new Crawler(url, numPage);
//    }
//
//    public void searchEngineInterface() {
//        System.out.println("----------------------------------------------------------------------");
//        System.out.println("Running...");
//
//        while (true) {
//            System.out.println("----------------------------------------------------------------------");
//            System.out.println("Please enter the URL you want to search: ");
//            Scanner scanner = new Scanner(System.in);
//            String url = scanner.nextLine();
//            System.out.println("Please enter the number of pages you want to search: ");
//            int numPage = scanner.nextInt();
//            SearchEngineInterface searchEngineInterface = new SearchEngineInterface(url, numPage);
//            // which test to run
//            System.out.println("Please enter the test you want to run: ");
//            System.out.println("1. Test 1");
//            System.out.println("2. Test 2");
//            System.out.println("3. Test 3");
//            int test = scanner.nextInt();
//            switch (test) {
//                case 1:
//                    test1();
//                    break;
//                case 2:
//                    test2();
//                    break;
//                case 3:
//                    test3();
//                    break;
//                default:
//                    System.out.println("Invalid test number");
//                    break;
//            }
//        }
//
//    }
//    // extractlinks
//    public void test1(){
//        System.out.println("Please enter the URL you want to search: ");
//        Scanner scanner = new Scanner(System.in);
//        String url = scanner.nextLine();
//        System.out.println("Please enter the number of pages you want to search: ");
//        int numPage = scanner.nextInt();
//        SearchEngineInterface searchEngineInterface = new SearchEngineInterface(url, numPage);
//        try {
//            Vector<String> links = searchEngineInterface.crawler.extractLinks();
//            System.out.println("Links in " + crawler.getUrl() + ":");
//            for (int i = 0; i < links.size(); i++)
//                System.out.println(links.get(i));
//            System.out.println("");
//
//        } catch (ParserException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static void test2() {
//
//    }
//
//    public static void test3() {
//
//    }
}
