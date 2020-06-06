package com.company;


import java.util.ListIterator;


public class LinkedList<Item> implements Iterable<Item>
{
    private LinkedListItem<Item> head, tail;


    private int size;

    public LinkedList() { size = 0; }

    public void addForward(Item value)
    {
        LinkedListItem<Item> item = new LinkedListItem<>(value, null, null);

        if (head == null)
            tail = head = item;
        else
        {
            head.setPrevious(item);
            item.setNext(head);
            head = item;
        }

        ++size;
    }

    public void addBackward(Item value)
    {
        LinkedListItem<Item> item = new LinkedListItem<>(value, null, null);

        if (head == null)
            tail = head = item;
        else
        {
            tail.setNext(item);
            item.setPrevious(tail);
            tail = item;
        }

        ++size;
    }


    public void add(Item value, int position) throws LanguageException
    {
        if(position == 0)
        {
            addForward(value);
        }
        else if(position >= size)
        {
            addBackward(value);
        }
        else
        {
            LinkedListItem<Item> buf = head;

            for(int i = 0 ; i < position - 1 ; i ++)
                buf = buf.getNext();


            LinkedListItem<Item> item = new LinkedListItem<>(value, buf.getNext(), buf);

            buf.getNext().setPrevious(item);
            buf.setNext(item);

            ++size;
        }
    }



    public Item getFirst() { return head.getValue(); }
    public Item getLast() { return tail.getValue(); }

    public Item get(int position) throws LanguageException
    {
        if(!checkPosition(position))
            throw new LanguageException("Index " + position + " is out of List");

        LinkedListItem<Item> buf = head;

        for(int i = 0 ; i < position ; i ++)
            buf = buf.getNext();

        return buf.getValue();
    }

    

    public void set(Item value, int position) throws LanguageException
    {
        if(!checkPosition(position))
            throw new LanguageException("Index " + position + " is out of List");

        LinkedListItem<Item> buf = head;

        for(int i = 0 ; i < position ; i ++)
            buf = buf.getNext();

        buf.setValue(value);
    }



    public void remove(int position) throws LanguageException
    {
        if(!checkPosition(position))
            throw new LanguageException("Index " + position + " is out of List");

        LinkedListItem<Item> buf = head;

        for(int i = 0 ; i < position ; i ++)
            buf = buf.getNext();


        if(buf.getNext() == null)
        {
            buf.getPrevious().setNext(null);
            tail = buf.getPrevious();
        }
        else if(buf.getPrevious() == null)
        {
            buf.getNext().setPrevious(null);
            head = buf.getNext();
        }
        else
        {
            buf.getPrevious().setNext(buf.getNext());
            buf.getNext().setPrevious(buf.getPrevious());
        }



        size--;
    }



    public int getSize() { return size; }

    private boolean checkPosition(int position) { return position < size + 1; }



    LinkedListItem<Item> getHead() { return head; }
    LinkedListItem<Item> getTail() { return tail; }


    public ListIterator<Item> iterator() { return new LinkedListIterator<>(this); }


}


class LinkedListIterator<Item> implements ListIterator<Item>
{
    private LinkedListItem<Item> current;


    LinkedListIterator(LinkedList<Item> list)
    {
        current = list.getHead();
    }

    @Override
    public boolean hasNext() { return current != null; }

    @Override
    public Item next()
    {
        System.out.println(current);
        Item data = current.getValue();
        current = current.getNext();
        return data;
    }

    @Override
    public boolean hasPrevious() { return current != null; }

    @Override
    public Item previous()
    {
        Item data = current.getValue();
        current = current.getPrevious();
        return data;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public int nextIndex() { return 0; }

    @Override
    public int previousIndex() { return 0; }

    @Override
    public void set(Item value) { }

    @Override
    public void add(Item value) { }
}


class LinkedListItem<Item>
{
    private Item value;

    private LinkedListItem<Item> previous;
    private LinkedListItem<Item> next;

    LinkedListItem(Item data, LinkedListItem<Item> next, LinkedListItem<Item> previous)
    {
        this.value = data;
        this.previous = previous;
        this.next = next;
    }



    void setNext(LinkedListItem<Item> next) { this.next = next; }
    void setPrevious(LinkedListItem<Item> previous) { this.previous = previous; }


    void setValue(Item value) { this.value = value; }
    Item getValue() { return value; }

    LinkedListItem<Item> getNext() { return next; }
    LinkedListItem<Item> getPrevious() { return previous; }
}
