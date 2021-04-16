package assignment09;

import static org.junit.Assert.*;

import org.junit.Test;

public class HeapTest {
  @SafeVarargs
  private final <E> Heap<E> uutFromArgs(E... args) {
    Heap<E> h = new Heap<>();
    for (E e : args)
      h.add(e);
    return h;
  }

  @Test
  public void heapConstructorNoSiftDown() {
    Integer[] nums = { 10, 30, 20, 50, 40 };
    Heap<Integer> h = new Heap<>(nums);
    assertEquals("[10,30,20,50,40]", h.toString());
    assertEquals(5, h.size());
  }

  @Test
  public void heapConstructorSiftDownRight() {
    Integer[] nums = { 60, 30, 20, 50, 40 };
    Heap<Integer> h = new Heap<>(nums);
    assertEquals("[20,30,60,50,40]", h.toString());
    assertEquals(5, h.size());
  }

  @Test
  public void heapConstructorSiftDownLeft() {
    Integer[] nums = { 60, 20, 30, 50, 10 };
    Heap<Integer> h = new Heap<>(nums);
    assertEquals("[10,20,30,50,60]", h.toString());
    assertEquals(5, h.size());
  }

  @Test
  public void heapAddMany1() {
    Heap<Integer> h = uutFromArgs(10, 30, 20, 50, 40);
    assertEquals("[10,30,20,50,40]", h.toString());
    assertEquals(5, h.size());
  }

  @Test
  public void heapAddMany2() {
    Heap<Integer> h = uutFromArgs(60, 30, 20, 50, 40);
    assertEquals("[20,40,30,60,50]", h.toString());
    assertEquals(5, h.size());
  }

  @Test
  public void heapAddMany3() {
    Heap<Integer> h = uutFromArgs(60, 20, 30, 50, 10);
    assertEquals("[10,20,30,60,50]", h.toString());
    assertEquals(5, h.size());
  }

  @Test
  public void heapifyOneElt() {
    Integer[] nums = { 10 };
    Heap<Integer> h = new Heap<>(nums);
    assertEquals("[10]", h.toString());
    assertEquals(1, h.size());
  }

  @Test
  public void heapifyTwoElts1() {
    Integer[] nums = { 10, 20 };
    Heap<Integer> h = new Heap<>(nums);
    assertEquals("[10,20]", h.toString());
    assertEquals(2, h.size());
  }

  @Test
  public void heapifyTwoElts2() {
    Integer[] nums = { 20, 10 };
    Heap<Integer> h = new Heap<>(nums);
    assertEquals("[10,20]", h.toString());
    assertEquals(2, h.size());
  }

  @Test
  public void heapifyThreeElts1() {
    Integer[] nums = { 10, 20, 30 };
    Heap<Integer> h = new Heap<>(nums);
    assertEquals("[10,20,30]", h.toString());
    assertEquals(3, h.size());
  }

  @Test
  public void heapifyThreeElts2() {
    Integer[] nums = { 10, 30, 20 };
    Heap<Integer> h = new Heap<>(nums);
    assertEquals("[10,30,20]", h.toString());
    assertEquals(3, h.size());
  }

  @Test
  public void h30add20() {
    Heap<Integer> h = new Heap<>();
    h.add(30);
    h.add(20);
    assertEquals("[20,30]", h.toString());
    assertEquals(2, h.size());
  }

  @Test
  public void removeFirstSingle() {
    Heap<Integer> h = new Heap<>();
    h.add(30);
    int removed = h.removeFirst();
    assertEquals("[]", h.toString());
    assertEquals(0, h.size());
    assertEquals(30, removed);
  }

  @Test
  public void removeFirst1() {
    Heap<Integer> h = uutFromArgs(10, 30, 20, 50, 40);
    assertEquals(10, (int) h.removeFirst());
    assertEquals("[20,30,40,50]", h.toString());
    assertEquals(4, h.size());
  }

  @Test
  public void removeFirst2() {
    Heap<Integer> h = uutFromArgs(10, 30, 20, 50, 40);
    h.removeFirst();
    assertEquals(20, (int) h.removeFirst());
    assertEquals("[30,50,40]", h.toString());
    assertEquals(3, h.size());
  }

  @Test
  public void removeFirstAll() {
    Heap<Integer> h = uutFromArgs(10, 30, 20, 50, 40);
    assertEquals(10, (int) h.removeFirst());
    assertEquals(20, (int) h.removeFirst());
    assertEquals(30, (int) h.removeFirst());
    assertEquals(40, (int) h.removeFirst());
    assertEquals(50, (int) h.removeFirst());
    assertEquals("[]", h.toString());
    assertEquals(0, h.size());
  }

  @Test
  public void changeOrder() {
    Integer[] nums = { 60, 30, 20, 50, 40 };
    Heap<Integer> h = new Heap<>(nums);
    h.changeOrder((o1, o2) -> -Integer.compare(o1, o2));
    assertEquals("[60,50,20,30,40]", h.toString());
    assertEquals(5, h.size());
  }

  @Test
  public void heapStrings1() {
    Heap<String> h = uutFromArgs("red");
    assertEquals("[red]", h.toString());
    assertEquals(1, h.size());
  }

  @Test
  public void heapStrings2() {
    Heap<String> h = uutFromArgs("red", "blue", "black", "yellow", "green");
    assertEquals("[black,green,blue,yellow,red]", h.toString());
    assertEquals(5, h.size());
  }

  @Test
  public void heapStringsRemoveFirst() {
    Heap<String> h = uutFromArgs("red");
    assertEquals("red", h.removeFirst());
    assertEquals("[]", h.toString());
    assertEquals(0, h.size());
  }

}
