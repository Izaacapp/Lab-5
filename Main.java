import java.util.*;

public class Main {

    final public static int NEG_INF = Integer.MIN_VALUE;
    final public static int POS_INF = Integer.MAX_VALUE;
    
    public static Random rndObj = new Random();
    
    private ArrayList<node> levels;
    private int size;
    
    // Makes an empty list.
    public Main() {
        levels = new ArrayList<node>();
        levels.add(buildLevel(0));
        size = 1;
    }

    // This builds level id to be an empty level.
    public node buildLevel(int id) {
        node first = new node(NEG_INF, id);
        node last = new node(POS_INF, id);
        first.next = last;
        last.prev = first;        
        return first;
    }
    
    // Returns a list of nodes at each level that are right before value.
    public ArrayList<node> search(int value) {
        ArrayList<node> res = new ArrayList<node>();
        node cur = levels.get(size-1);
        
        for (int i=size-1; i>=0; i--) {
            while (cur.next.data < value) cur = cur.next;
            res.add(cur);
            if (i>0) cur = cur.down;
        }
        
        Collections.reverse(res);
        return res;
    }
    
    // Inserts value into the set, returns true iff the value was inserted.
    public boolean insert(int value) {
        ArrayList<node> beforeList = search(value);
        
        if (beforeList.get(0).next.data == value) return false;
        
        node curn = null;
        int i = 0;
        
        while (i <= size) {
            int val = i==0 ? 1 : rndObj.nextInt(2);
            if (val == 0) break;
            
            node newn = new node(value, i);
            
            if (i > 0) {
                curn.up = newn;
                newn.down = curn;
            }
            
            if (i == size) {
                node nextL = buildLevel(size);
                levels.add(nextL);
                connectLastLevel();
                beforeList.add(nextL);
            }
            
            node tmpLow = beforeList.get(i);
            node tmpNext = tmpLow.next;
            newn.prev = tmpLow;
            newn.next = tmpNext;
            tmpLow.next = newn;
            tmpNext.prev = newn;
            
            if (i == size) {
                size++;
                break;
            }
            
            i++;
            curn = newn;
        }
        
        return true;
    }
    
    // Deletes value from the list. Returns true if value was in the list and was deleted.
    public boolean delete(int value) {
        ArrayList<node> beforeList = search(value);
        
        if (beforeList.get(0).next.data != value) return false;
        
        for (int i = 0; i < beforeList.size(); i++) {
            node toDelete = beforeList.get(i).next;
            
            if (toDelete.data == value) {
                toDelete.prev.next = toDelete.next;
                toDelete.next.prev = toDelete.prev;
                
                if (toDelete.up != null) {
                    toDelete = toDelete.up;
                } else {
                    break;
                }
            }
        }
        
        return true;
    }
    
    // Returns the number of items on the top level.
    private int topLevelSize() {
        node cur = levels.get(size-1);
        int sz = 0;
        while (cur != null) {
            cur = cur.next;
            sz++;
        }
        return sz;
    }
    
    // Connects the last level to the rest of the lists.
    public void connectLastLevel() {
        node top = levels.get(levels.size()-1);
        node below = levels.get(levels.size()-2);
        
        top.down = below;
        below.up = top;
        
        top = top.next;
        
        while (below.data != POS_INF) below = below.next;
        
        top.down = below;
        below.up = top;
    }
    
    // For debugging.
    public void printAllLevels() {
        System.out.println(levels.size()+" and "+size);
        for (int i=0; i<size; i++) {
            System.out.print("Level "+i+": ");
            printLevel(i);
        }
        System.out.println("---------------------------");
    }
    
    // Prints level id. For debugging.
    public void printLevel(int id) {
        node cur = levels.get(id);
        while (cur != null) {
            System.out.print(cur.data+" ");
            cur = cur.next;
        }
        System.out.println();
    }
    
    // Basic insert test.
    public static void basicInsertTest() {
        Main mine = new Main(); //this constructor creates a level as well
        
        // Do 100 inserts.
        for (int i=0; i<10; i++) {
            int item = rndObj.nextInt(1000);
            System.out.println("Gen "+item);
            
            boolean flag = mine.insert(item);
            
            if (flag)
                System.out.println("Inserted "+item);
            else
                System.out.println("Rejected "+item);
            
            mine.printAllLevels();
        }
        
        Scanner sc = new Scanner(System.in);
        int item = 0;
        while(item !=-1) {
            System.out.println("Enter an item to delete: ");
            item = sc.nextInt();
            boolean flag = mine.delete(item);
            if (flag) {
                System.out.println("Deleted "+item);
                mine.printAllLevels();
            } else {
                System.out.println(item+" cannot be deleted.");
            }
        }
    }
    
    // Returns all the items in the skip list in order.
    public ArrayList<Integer> getList() {
        node bottom = levels.get(0);
        ArrayList<Integer> res = new ArrayList<Integer>();
        bottom = bottom.next;
        while (bottom.data != POS_INF) {
            res.add(bottom.data);
            bottom = bottom.next;
        }
        return res;
    }
    
    // A large test of random inserts followed by random deletes.
    public static void largeTestRandom(int testSize) {
        Random rand = new Random();
        
        int[] insertData = rand.ints(testSize, 0, 2000000).toArray();
        int[] deleteData = rand.ints(testSize, 0, 2000000).toArray();
        
        Main skipList = new Main();
        long startInsertTime = System.currentTimeMillis();
        for (int num : insertData) {
            skipList.insert(num);
        }
        long skipListInsertTime = System.currentTimeMillis() - startInsertTime;
        
        long startDeleteTime = System.currentTimeMillis();
        for (int num : deleteData) {
            skipList.delete(num);
        }
        long skipListDeleteTime = System.currentTimeMillis() - startDeleteTime;
        
        long skipListTotalTime = skipListInsertTime + skipListDeleteTime;

        TreeSet<Integer> treeSet = new TreeSet<>();
        startInsertTime = System.currentTimeMillis();
        for (int num : insertData) {
            treeSet.add(num);
        }
        long treeSetInsertTime = System.currentTimeMillis() - startInsertTime;
        
        startDeleteTime = System.currentTimeMillis();
        for (int num : deleteData) {
            treeSet.remove(num);
        }
        long treeSetDeleteTime = System.currentTimeMillis() - startDeleteTime;
        
        long treeSetTotalTime = treeSetInsertTime + treeSetDeleteTime;

        System.out.println("Test size: " + testSize);
        System.out.println("======");
        System.out.println("Skip list insertion took " + skipListInsertTime + " ms.");
        System.out.println("Skip list deletion took " + skipListDeleteTime + " ms.");
        System.out.println("Skip list actions took " + skipListTotalTime + " ms.");
        System.out.println("Tree set insertion took " + treeSetInsertTime + " ms.");
        System.out.println("Tree set deletion took " + treeSetDeleteTime + " ms.");
        System.out.println("Tree set actions took " + treeSetTotalTime + " ms.");
        System.out.println();
    }
    
    public static void main(String[] args) {
        int[] testSizes = {50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000, 450000, 500000};

        for (int testSize : testSizes) {
            largeTestRandom(testSize);
        }
    }
}

class node {
    public int data;
    public node next;
    public node prev;
    public node up;
    public node down;
    public int level;

    public node(int myval, int mylev) {
        data = myval;
        level = mylev;
        next = null;
        prev = null;
        up = null;
        down = null;
    }
}
