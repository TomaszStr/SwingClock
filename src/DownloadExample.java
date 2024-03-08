import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Semaphore;
public class DownloadExample {

    // lista plików do pobrania
    static String [] toDownload = {
            "https://home.agh.edu.pl/~pszwed/wyklad-c/01-jezyk-c-intro.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/02-jezyk-c-podstawy-skladni.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/03-jezyk-c-instrukcje.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/04-jezyk-c-funkcje.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/05-jezyk-c-deklaracje-typy.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/06-jezyk-c-wskazniki.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/07-jezyk-c-operatory.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/08-jezyk-c-lancuchy-znakow.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/09-jezyk-c-struktura-programow.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/10-jezyk-c-dynamiczna-alokacja-pamieci.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/11-jezyk-c-biblioteka-we-wy.pdf",
            "https://home.agh.edu.pl/~pszwed/wyklad-c/preprocesor-make-funkcje-biblioteczne.pdf",
    };

    static AtomicInteger count = new AtomicInteger(0);

    static Semaphore sem = new Semaphore(0);

    static class Downloader implements Runnable{
        private final String url;

        Downloader(String url){
            this.url = url;
        }

        public void run(){
            int i = url.lastIndexOf('/')+1;
            String fileName = url.substring(i);//nazwa pliku - wyodrębnij z z url

            try(InputStream in = new URL(url).openStream(); FileOutputStream out = new FileOutputStream(fileName) ){
                for(;;){
                    // czytaj znak z in
                    // jeśli <0 break
                    //zapisz znak do out
                    int read = in.read();
                    if(read<0) break;
                    out.write(read);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Done:"+fileName);
            count.incrementAndGet();
            sem.release();
        }
    }

    static void sequentialDownload(){
        double t1 = System.nanoTime()/1e6;
        for(String url:toDownload){
            new Downloader(url).run();  // uwaga tu jest run()
        }
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);
    }

    static void concurrentDownload(){
        double t1 = System.nanoTime()/1e6;
        for(String url:toDownload){
            // uruchom Downloader jako wątek... czyli wywołaj start()
            new Thread(new Downloader(url)).start();
        }
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);
    }

    static void concurrentDownload2(){
        double t1 = System.nanoTime()/1e6;
        for(String url:toDownload){
            // uruchom Downloader jako wątek... czyli wywołaj start()
            new Thread(new Downloader(url)).start();
        }
        while(count.intValue()!=toDownload.length){
            // wait...
            Thread.yield();
        }
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);
    }

    static void concurrentDownload3(){
        double t1 = System.nanoTime()/1e6;
        for(String url:toDownload){
            // uruchom Downloader jako wątek... czyli wywołaj start()
            new Thread(new Downloader(url)).start();
        }
        try {
            sem.acquire(toDownload.length);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"t2-t1=%f\n",t2-t1);
    }
}