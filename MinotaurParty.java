import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

class ConcurrentLinkedList
{
    private ConcurrentHashMap<Integer, String> presents = new ConcurrentHashMap<>();

    public void addPresent(int tag, String guestName)
    {
        presents.put(tag, guestName);
    }

    public void removePresent(int tag)
    {
        presents.remove(tag);
    }

    public boolean containsPresent(int tag)
    {
        return presents.containsKey(tag);
    }
}


class Present
{
    int tag;
    String guestName;

    public Present(int tag, String guestName)
    {
        this.tag = tag;
        this.guestName = guestName;
    }
}


class Servant implements Runnable
{
    private ConcurrentLinkedQueue<Present> unorderedBag;
    private ConcurrentLinkedList orderedChain;
    private ConcurrentHashMap<Integer, String> cards;
    private String name;

    public Servant(ConcurrentLinkedQueue<Present> unorderedBag, ConcurrentLinkedList orderedChain, ConcurrentHashMap<Integer, String> cards, String name)
    {
        this.unorderedBag = unorderedBag;
        this.orderedChain = orderedChain;
        this.cards = cards;
        this.name = name;
    }

    @Override
    public void run()
    {
        while (true)
        {
            Present present = unorderedBag.poll();
            if (present == null)
            {
                break;
            }

            if (present.tag % 2 == 0)
            {
                orderedChain.addPresent(present.tag, present.guestName);
                System.out.println(name + " added present with tag " + present.tag + " to the ordered chain");
            } else
            {
                orderedChain.removePresent(present.tag);
                cards.put(present.tag, present.guestName);
                System.out.println(name + " wrote a \"Thank you\" note for present with tag " + present.tag);
            }
        }
    }
}

public class MinotaurParty
{
    public static void main(String[] args)
    {
        ConcurrentLinkedQueue<Present> unorderedBag = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedList orderedChain = new ConcurrentLinkedList();
        ConcurrentHashMap<Integer, String> cards = new ConcurrentHashMap<>();

        // Fill the unordered bag with presents
        for (int i = 1; i <= 500000; i++)
        {
            unorderedBag.offer(new Present(i, "Guest " + i));
        }

        // Create and start servant threads
        Thread servant1 = new Thread(new Servant(unorderedBag, orderedChain, cards, "Servant 1"));
        Thread servant2 = new Thread(new Servant(unorderedBag, orderedChain, cards, "Servant 2"));
        Thread servant3 = new Thread(new Servant(unorderedBag, orderedChain, cards, "Servant 3"));
        Thread servant4 = new Thread(new Servant(unorderedBag, orderedChain, cards, "Servant 4"));

        servant1.start();
        servant2.start();
        servant3.start();
        servant4.start();

        // Join servant threads
        try
        {
            servant1.join();
            servant2.join();
            servant3.join();
            servant4.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Check if there are more presents in the unordered bag than "Thank you" notes
        if (!unorderedBag.isEmpty())
        {
            System.out.println("There are more presents left in the unordered bag than \"Thank you\" notes.");
        } else
        {
            System.out.println("All presents have been processed.");
        }
    }
}