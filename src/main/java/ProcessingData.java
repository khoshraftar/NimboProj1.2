import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProcessingData {
    static void sort(Map<Long, Integer> map , long[] maxId , int [] maxActivity){
        for (Long id : map.keySet()) {
            maxActivity[10] = map.get(id);
            maxId[10] = id;
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 10 - i; j++) {
                    if (maxActivity[j] < maxActivity[j + 1]) {
                        int tmp = maxActivity[j];
                        maxActivity[j] = maxActivity[j + 1];
                        maxActivity[j + 1] = tmp;
                        long tm2 = maxId[j];
                        maxId[j] = maxId[j + 1];
                        maxId[j + 1] = tm2;
                    }
                }
            }
        }
    }
    static int mapUpdate(Map <Long , Integer> devsMap, Map<Long , Integer> repsMap , long x , long y , int NumOfEvents,ArrayList<String> typeChecker){
        File tm = new File("/Users/amir/Desktop/NimboProj1/Data");
        String s[] = tm.list();
        for (int i = 0; i < s.length; i++) {
            long t = Long.parseLong(s[i]);
            if (t < x && t > y - 600000) {
                Scanner scanner1;
                File file = new File("/Users/amir/Desktop/NimboProj1/Data/" + s[i]);
                try {
                    scanner1 = new Scanner(file);
                    while (scanner1.hasNext()) {
                        long tmpTime = scanner1.nextLong();
                        String type =scanner1.next();
                        Long DevId = scanner1.nextLong();
                        Long RepId = scanner1.nextLong();
                        if(!typeChecker.contains(type))
                            continue;
                        if (tmpTime < x && tmpTime > y) {
                            System.out.println(x + "fu" + y);

                            NumOfEvents++;

                            if (devsMap.containsKey(DevId)) {
                                int h = devsMap.get(DevId) + 1;
                                devsMap.put(DevId, h);
                            } else devsMap.put(DevId, 1);

                            if (repsMap.containsKey(RepId)) {
                                int h = repsMap.get(RepId) + 1;
                                repsMap.put(RepId, h);
                            } else repsMap.put(RepId, 1);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("found:  " + t);
            }
        }
        return NumOfEvents;
    }
    static int mapUpdateFromString(Map <Long , Integer> devsMap, Map<Long , Integer> repsMap , long x , long y ,
                                    int NumOfEvents , final StringBuilder mytmpstr , ArrayList<String> typeChecker) {
        Scanner scanner2 = new Scanner(mytmpstr.toString());
        while (scanner2.hasNext()) {
            long tmpT = scanner2.nextLong();
            String tmpS = scanner2.next();
            long tmpA = scanner2.nextLong();
            long tmpR = scanner2.nextLong();
            if (!typeChecker.contains(tmpS))
                continue;
            if (tmpT < x && tmpT > y) {
                System.out.println(x + "  " + y);
                NumOfEvents++;
                if (devsMap.containsKey(tmpA)) {
                    int tmp = devsMap.get(tmpA) + 1;
                    devsMap.put(tmpA, tmp);
                } else devsMap.put(tmpA, 1);
                if (repsMap.containsKey(tmpR)) {
                    int tmp = repsMap.get(tmpR) + 1;
                    repsMap.put(tmpR, tmp);
                } else repsMap.put(tmpR, 1);
            }
        }
        return NumOfEvents;
    }
}
