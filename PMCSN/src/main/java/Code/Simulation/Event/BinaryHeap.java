package Code.Simulation.Event;

public class BinaryHeap {
    Event[] allEvent; // pointer to array of elements in heap
    int capacity; // maximum possible size of min heap
    int heap_size; // Current number of elements in min heap

    public BinaryHeap(int capacity){
        this.heap_size = 0;
        this.capacity = capacity;
        this.allEvent = new Event[capacity];
    }

    public int getHeapSize(){
        return this.heap_size;
    }
    public int parent(int i) { return (i-1)/2; }

    // to get index of left child of node at index i
    public int left(int i) { return (2*i + 1); }

    // to get index of right child of node at index i
    public int right(int i) { return (2*i + 2); }

    // Returns the minimum key (key at root) from min heap
    public Event getMin() { return allEvent[0]; }

    // Inserts a new key 'k'
    public void insertEvent(Event event) {
        if (heap_size == capacity)
            return;

        // First insert the new key at the end
        heap_size++;
        int i = heap_size - 1;
        allEvent[i] = event;

        // Fix the min heap property if it is violated
        while (i != 0 && allEvent[parent(i)].getTimeNext() > allEvent[i].getTimeNext()) {
            Event temp = allEvent[i];
            allEvent[i]= allEvent[parent(i)];
            allEvent[parent(i)] = temp;
            i = parent(i);
        }
    }

    // Method to remove minimum element (or root) from min heap
    public Event extractMin() {
        if (heap_size <= 0)
            return null;
        if (heap_size == 1) {
            heap_size--;
            return allEvent[0];
        }

        // Store the minimum value, and remove it from heap
        Event root = allEvent[0];
        allEvent[0] = allEvent[heap_size-1];
        heap_size--;
        MinHeapify(0);

        return root;
        }


    // A recursive method to heapify a subtree with the root at given index
    // This method assumes that the subtrees are already heapified
    void MinHeapify(int i) {
        int l = left(i);
        int r = right(i);
        int smallest = i;
        if (l < heap_size && allEvent[l].getTimeNext() < allEvent[i].getTimeNext())
            smallest = l;
        if (r < heap_size && allEvent[r].getTimeNext() < allEvent[smallest].getTimeNext())
            smallest = r;
        if (smallest != i) {
            Event temp = allEvent[i];
            allEvent[i]= allEvent[smallest];
            allEvent[smallest] = temp;

            MinHeapify(smallest);
        }
    }


}
