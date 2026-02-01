import java.util.Scanner;

public class SimpleKeyboardBuffer {

    // ------------ DATA STRUCTURES ----------------

    private static char[] buffer;      // circular buffer
    private static int cap;            // capacity
    private static int head = 0;       // next to process (FIFO)
    private static int tail = 0;       // next free slot
    private static int size = 0;       // number of items

    private static char[] output = new char[1000]; // output queue
    private static int outSize = 0;

    private static char[] backstack;   // for BACKSPACE undo on buffer
    private static int backTop = -1;

    // ------------ CONFIG ----------------

    private static int processRate = 0;

    // ------------ METRICS ----------------

    private static int totalKeys = 0;
    private static int dropped = 0;
    private static int occupancySum = 0;
    private static int occupancySamples = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            if (!sc.hasNextLine()) break;
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] p = line.split(" ");
            String cmd = p[0].toUpperCase();

            switch (cmd) {
                case "CONFIG":
                    configure(Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                    break;

                case "KEY":
                    keyPress(p[1].charAt(0));
                    break;

                case "BACKSPACE":
                    backspace();
                    break;

                case "ENTER":
                    enter();
                    break;

                case "TICK":
                    tick();
                    break;

                case "OUTPUT":
                    printOutput();
                    break;

                case "METRICS":
                    printMetrics();
                    break;
            }

            recordOccupancy();
        }

        sc.close();
    }

    // ------------- COMMAND FUNCTIONS ----------------

    private static void configure(int capacity, int pr) {
        cap = capacity;
        processRate = pr;

        buffer = new char[cap];
        backstack = new char[cap];

        head = tail = size = 0;
        backTop = -1;

        totalKeys = dropped = 0;
        occupancySum = occupancySamples = 0;

        outSize = 0;

        System.out.println("CONFIGURED: cap=" + cap + " rate=" + processRate);
    }

    // KEY <char>
    private static void keyPress(char c) {
        totalKeys++;

        if (size == cap) {
            dropped++;
            System.out.println("DROPPED: Buffer full.");
            return;
        }

        buffer[tail] = c;
        tail = (tail + 1) % cap;
        size++;

        backstack[++backTop] = c; // push onto BACKSPACE stack

        printBuffer();
    }

    // BACKSPACE
    private static void backspace() {
        if (size == 0) {
            printBuffer();
            return;
        }

        char removed = backstack[backTop--];  // pop last inserted
        tail = (tail - 1 + cap) % cap;        // move tail back
        size--;

        System.out.println("Removed: " + removed);
        printBuffer();
    }

    // TICK — process P keys FIFO
    private static void tick() {
        int processed = 0;
        System.out.print("Processed: ");

        while (processed < processRate && size > 0) {
            char c = buffer[head];
            head = (head + 1) % cap;
            size--;

            output[outSize++] = c;
            System.out.print(c + " ");

            processed++;
        }

        System.out.println();
        printBuffer();
    }

    // ENTER — flush entire buffer
    private static void enter() {
        System.out.print("Flushed: ");
        while (size > 0) {
            char c = buffer[head];
            head = (head + 1) % cap;
            size--;

            output[outSize++] = c;
            System.out.print(c + " ");
        }
        System.out.println();

        printBuffer();
    }

    // OUTPUT
    private static void printOutput() {
        System.out.print("Output Stream: ");
        for (int i = 0; i < outSize; i++)
            System.out.print(output[i] + " ");
        System.out.println();
    }

    // METRICS
    private static void printMetrics() {
        double dropRate = (totalKeys == 0) ? 0
                : (dropped * 100.0 / totalKeys);
        double avgOcc = (occupancySamples == 0) ? 0
                : (double) occupancySum / occupancySamples;

        System.out.printf("Dropped: %d, Total: %d, Drop Rate: %.1f%%\n",
                dropped, totalKeys, dropRate);
        System.out.printf("Avg Occupancy: %.1f/%d\n", avgOcc, cap);
    }

    // HELPER: print buffer
    private static void printBuffer() {
        System.out.print("Buffer: [");
        for (int i = 0; i < size; i++) {
            int idx = (head + i) % cap;
            System.out.print(buffer[idx]);
            if (i < size - 1) System.out.print(", ");
        }
        System.out.println("] (" + size + "/" + cap + ")");
    }

    // METRIC: occupancy tracking
    private static void recordOccupancy() {
        occupancySum += size;
        occupancySamples++;
    }
}
