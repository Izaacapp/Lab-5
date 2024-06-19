import java.util.*;

public class Main {
    final public static int NEG_INF = Integer.MIN_VALUE;
    final public static int POS_INF = Integer.MAX_VALUE;
    public static Random rndObj = new Random();
    private ArrayList<node> levels;
    private int size;

    // Makes an empty list.
    public Main() {
        // Initially, I am just one level with min and max.
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
        // Store answers here.
        ArrayList<node> res = new ArrayList<node>();
        node cur = levels.get(size-1);
        // We search from top, so that we can skip terms.
        for (int i=size-1; i>=0; i--) {
            // Go down this level until we're right before an equal or bigger item.
            while (cur.next.data < value) cur = cur.next;
            // This is the floor of value on this list, so add it.
            res.add(cur);
            // Go down to the next level.
            if (i>0) cur = cur.down;
        }
        // So the list will be in the proper order, since I built it backwards.
        Collections.reverse(res);
        // Stores all of the relevant pointers.
        return res;
    }

    // Inserts value into the set, returns true if the value was inserted. (False means the value was already in the set).
    public boolean insert(int value) {
        // Find all the "previous" nodes.
        ArrayList<node> beforeList = search(value);
        // This value is already in the set.
        if (beforeList.get(0).next.data == value) return false;
        // Temp pointer I will use.
        node curn = null;
        int i = 0;
        // Farthest we'll go up the lists.
        while (i <= size) {
            // Intentionally generate 1 for first level as we need to at least insert it.
            int val = i==0 ? 1 : rndObj.nextInt(2);
            if (val == 0) break;
            // We've decided to create this node.
            node newn = new node(value, i);
            // Not necessary for bottom level.
            if (i > 0) {
                curn.up = newn;
                newn.down = curn;
            }
            // Special case where we are adding a new level to our list.
            // We add the level and then connect it to the rest of the lists.
            if (i == size) {
                node nextL = buildLevel(size);
                levels.add(nextL);
                connectLastLevel();
                beforeList.add(nextL);
            }
            // Lots of patching... (joining newn between prev and next)
            node tmpLow = beforeList.get(i);
            node tmpNext = tmpLow.next;
            newn.prev = tmpLow;
            newn.next = tmpNext;
            tmpLow.next = newn;
            tmpNext.prev = newn;
            // Need to update the object's size and get out.
            if (i == size) {
                size++;
                break;
            }
            // Go up next level.
            i++;
            curn = newn;
        }
        // We inserted it.
        return true;
    }

    // Deletes value from the list. Returns true if value was in the list and was deleted.
    // Returns false if value wasn't in the list and takes no action.
    public boolean delete(int value) {
        // Find all the "previous" nodes.
        ArrayList<node> beforeList = search(value);
        // This value is not in the set.
        if (beforeList.get(0).next.data != value) return false;
        // Deleting the node(s).
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
        // Remove empty levels if necessary
        while (levels.size() > 1 && levels.get(levels.size() - 1).next == levels.get(levels.size() - 1)) {
            levels.remove(levels.size() - 1);
            size--;
        }
        return true;
    }

    // Connects the last level to the rest of the lists.
    public void connectLastLevel() {
        // We can obtain both of these.
        node top = levels.get(levels.size()-1);
        node below = levels.get(levels.size()-2);
        // Link left sides up and down.
        top.down = below;
        below.up = top;
        // End of top list.
        top = top.next;
        // Go to end of second to top list.
        while (below.data != POS_INF) below = below.next;
        // Link right sides up and down.
        top.down = below;
        below.up = top;
    }

    // For debugging.
    public void printAllLevels() {
        System.out.println(levels.size() + " and " + size);
        for (int i=0; i<size; i++) {
            System.out.print("Level " + i + ": ");
            printLevel(i);
        }
        System.out.println("---------------------------");
    }

    // Prints level id. For debugging.
    public void printLevel(int id) {
        node cur = levels.get(id);
        while (cur != null) {
            System.out.print(cur.data + " ");
            cur = cur.next;
        }
        System.out.println();
    }

    // Basic insert test.
    public static void basicInsertTest() {
        // Create the object.
        Main mine = new Main(); // This constructor creates a level as well.
        // Do 10 inserts.
        for (int i=0; i<10; i++) {
            // Generate the item.
            int item = rndObj.nextInt(1000);
            System.out.println("Gen " + item);
            // Insert it.
            boolean flag = mine.insert(item);
            // Print what happened.
            if (flag) System.out.println("Inserted " + item);
            else System.out.println("Rejected " + item);
            // See all the lists.
            mine.printAllLevels();
        }
        // Let us delete.
        Scanner sc = new Scanner(System.in);
        int item = 0;
        while(item != -1) {
            System.out.println("Enter an item to delete: ");
            item = sc.nextInt();
            boolean flag = mine.delete(item);
            if (flag) {
                System.out.println("Deleted " + item);
                mine.printAllLevels();
            } else {
                System.out.println(item + " cannot be deleted.");
            }
        }
        sc.close();
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
        // Perform basic insert test.
        basicInsertTest(); // This is just to test whether your insert and delete function work or not.

        // Declare array of numbers to pass to your test function.
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
