import com.satori.rtm.*;
import com.satori.rtm.model.*;
import com.sun.org.apache.regexp.internal.RE;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    static long firstTime = -1;
    static BlockingQueue<AnyJson> buffer1 = new LinkedBlockingQueue<AnyJson>();
    static int events1 = 0;
    static long filename = 0;
    static StringBuilder mytmpstr = new StringBuilder();
    static Map<Long,String> DevNameMap=new HashMap<Long, String>();
    static Map<Long,String> RepNameMap=new HashMap<Long, String>();
    public static void main(String[] args) throws InterruptedException {
                final String endpoint = "wss://open-data.api.satori.com";
        final String appkey = "783ecdCcb8c5f9E66A56cBFeeeB672C3";
        final String channel = "github-events";
        JsonToSnapshot a = new JsonToSnapshot("thread 1");
        Map<Long, Integer> DevsMap = new HashMap<Long, Integer>();
        Map<Long, Integer> RepMap = new HashMap<Long, Integer>();
        final ArrayList<String> eventsMenu[] = new ArrayList[5];
        eventsMenu[0] = new ArrayList<String>();
        eventsMenu[0].add("PullRequestEvent");
        eventsMenu[1] = new ArrayList<String>();
        eventsMenu[1].add("PushEvent");
        eventsMenu[2] = new ArrayList<String>();
        eventsMenu[2].add("pullRequestEvent");eventsMenu[2].add("PushEvent");
        eventsMenu[3] = new ArrayList<String>();
        eventsMenu[3].add("WatchEvent");eventsMenu[3].add("IssueCommentEvent");eventsMenu[3].add("CreateEvent");eventsMenu[3].add("DeleteEvent");eventsMenu[3].add("ForkEvent");
        eventsMenu[3] = new ArrayList<String>();
        eventsMenu[4] = new ArrayList<String>();
        eventsMenu[4].add("pullRequestEvent");eventsMenu[4].add("PushEvent");eventsMenu[4].add("WatchEvent");eventsMenu[4].add("IssueCommentEvent");
        eventsMenu[3].add("CreateEvent");eventsMenu[4].add("DeleteEvent");eventsMenu[4].add("ForkEvent");
        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                        firstTime = System.currentTimeMillis();
                        filename = System.currentTimeMillis();
                    }
                })
                .build();
        SubscriptionAdapter listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for (AnyJson json : data.getMessages()) {
                    if (System.currentTimeMillis() - firstTime >= 180000) {
                        Splitter splitter = new Splitter();
                        buffer1.add(splitter);
                        firstTime = System.currentTimeMillis();
                    }
                    buffer1.add(json);
                    events1++;
                }
            }
        };
        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);
        client.start();
        a.start();

        while(true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nPlease enter command in this format:");
            System.out.println("[Events(by code)] [Output mode] [Time unit] [Period start time] [Period final time]");
            System.out.println("Events codes: 0 -> Pull 1 -> Push 2-> Pull & Push 3 -> Others 4-> All");
            System.out.println("Output mode: D -> Devolopers R -> Repositories T -> Both");
            System.out.println("Time units : H -> Hour M -> Minutes S -> Seconds");
            System.out.println("example: 2 R H 0 1  :: push & pull of Repositories in one hour\n");
            int inputType;
            try{
                    inputType=scanner.nextInt();
                    if(inputType>4 || inputType<0)
                    {
                        System.out.println("invalid input Noob!");
                        continue;
                    }
            }catch (InputMismatchException e){
                System.out.println("invalid input Noob!");

                continue;
            }
            String mode ;
            try {
                mode=scanner.next();
                if(!mode.equals("T") && !mode.equals("R") && !mode.equals("D")) {
                    System.out.println("Invalid input");
                    continue;
                }
            }catch (InputMismatchException e) {
                System.out.println("Invalid input");
                continue;
            }
            boolean devBool = false;
            boolean repBool = false;
            if (mode.equals("T")){
                devBool = true;
                repBool = true;
            }
            if (mode.equals("R")){
                repBool = true;
            }
            if (mode.equals("D")){
                devBool = true;
            }
            String timeUnit ;
            try{
                timeUnit=scanner.next();
                if(!timeUnit.equals("H") && !timeUnit.equals("M") && !timeUnit.equals("S"))
                {
                    System.out.println("inavlid input");
                    continue;
                }
            }catch (InputMismatchException e)
            {
                System.out.println("invalid input");
                continue;
            }
            int timeScale = 0;
            if (timeUnit.equals("H")){
                timeScale = 1000 * 60 * 60;
            }
            if(timeUnit.equals("M"))
                timeScale = 1000 * 60;
            if (timeUnit.equals("S"))
                timeScale = 1000;
            long x, y;
            try {
                x = scanner.nextLong();
                y = scanner.nextLong();
            }catch (InputMismatchException e)
            {
                System.out.println("invalid input");
                continue;
            }
            if (x < 0 || y < 0) {
                System.out.println("Invalid Input!");
                continue;
            }
            long t1 = System.currentTimeMillis();
            if (x > y) {
                long tm = x;
                x = y;
                y = tm;
            }
            int NumOfEvents = 0;
            x = x * timeScale;
            y = y * timeScale;
            x = t1 - x;
            y = t1 - y;
            NumOfEvents = ProcessingData.mapUpdateFromString(DevsMap , RepMap , x ,y , NumOfEvents , mytmpstr , eventsMenu[inputType],DevNameMap,RepNameMap);
            NumOfEvents = ProcessingData.mapUpdate(DevsMap , RepMap , x , y , NumOfEvents , eventsMenu[inputType],DevNameMap,RepNameMap);
            int maxActivity[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            long maxId[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            if (NumOfEvents == 0) {
                System.out.println("No Data Found");
                continue;
            }
            System.out.println("\nNumbers Of Events in this period:" + NumOfEvents + '\n');
            if (devBool) {
                System.out.println("in " + DevsMap.size() + " Devlopers :\n");
                ProcessingData.sort(DevsMap, maxId, maxActivity);
                try {
                    File file=new File("Logs");
                    file.mkdir();
                    PrintWriter writer = new PrintWriter(String.format("Logs/Dev(%d-%d)", x, y), "UTF-8");
                    writer.println(DevsMap.size() + " Devlopers :\n");
                    for (int i = 0; i < 10; i++) {
                        writer.println("id: " + maxId[i] +"  name: "+ DevNameMap.get(maxId[i])+"  events: " + maxActivity[i]);
                        System.out.println("id: " + maxId[i] +"  name: "+ DevNameMap.get(maxId[i])+ "  events: " + maxActivity[i]);
                    }
                    writer.close();
                } catch (IOException e) {
                    System.out.println("can not open or write file!");
                }
            }
            if (repBool) {
                int maxActivity2[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                long maxId2[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                System.out.println("\nin " + RepMap.size() + " Repositories :\n");
                ProcessingData.sort(RepMap, maxId2, maxActivity2);
                try {
                    PrintWriter writer = new PrintWriter(String.format("Logs/Rep(%d-%d)", x, y), "UTF-8");
                    writer.println("\n" + RepMap.size() + " Repositories :\n");
                    for (int i = 0; i < 10; i++) {
                        writer.println("id: " + maxId2[i] +"  name: "+ RepNameMap.get(maxId2[i])+ "  events: " + maxActivity2[i] );
                        System.out.println("id: " + maxId2[i] + "  name: "+ RepNameMap.get(maxId2[i])+"  events: " + maxActivity2[i]);
                    }
                    writer.close();
                } catch (IOException e) {
                    System.out.println("can not open or write file!");
                }
            }
            DevsMap.clear();
            RepMap.clear();
            DevNameMap.clear();
            RepNameMap.clear();
        }

    }
    static class JsonToSnapshot implements Runnable {
        private Thread t;
        private String threadName;

        JsonToSnapshot(String name) {
            threadName = name;
            System.out.println("Creating " + threadName);
        }

        public void run() {
            System.out.println("Running " + threadName);
            try {
                while (true) {
                    if (buffer1.isEmpty())
                        continue;
                    AnyJson tmp = buffer1.take();
                    if (tmp instanceof Splitter) {
                        System.out.println("#");
                        FileWriter writer = null;
                        File file=new File("Data");
                        file.mkdir();
                        try {
                            String tm = String.format("Data/%d", filename);
                            writer = new FileWriter(new File(tm));
                            writer.write(mytmpstr.toString());
                            writer.flush();
                            filename=System.currentTimeMillis();
                            mytmpstr = new StringBuilder();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    snapshot snap = tmp.convertToType(snapshot.class);
                    snap.setTime(System.currentTimeMillis());
                    mytmpstr.append(snap.toString() + '\n');
                }
            } catch (InterruptedException e) {
                FileWriter writer = null;
                try {
                    String tm = String.format("Data/%d", filename);
                    writer = new FileWriter(new File(tm));
                    writer.write(mytmpstr.toString());
                    writer.flush();
                    filename=System.currentTimeMillis();
                    mytmpstr = new StringBuilder();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                System.out.println("JsonToSnapshot Interrupted");
            }
            System.out.println(threadName + " exiting.");
        }
        public Thread getT() {
            return t;
        }
        public void start() {
            System.out.println("Starting " + threadName);
            if (t == null) {
                t = new Thread(this, threadName);
                t.start();
            }
        }
    }
}
