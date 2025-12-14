package org.example;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringJoiner;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import lombok.Data;
import lombok.NonNull;

@Data
public class LinkedList<E> implements Iterable<E> {
  @NonNull
  private LinkedListNode<E> firstNode;
  @NonNull
  private LinkedListNode<E> lastNode;

  public <E> LinkedList() {
    this.firstNode = LinkedListNode.empty();
    this.lastNode = LinkedListNode.empty();
  }

  @Override
  public ListIterator<E> iterator() {
    return new LinkedListIterator<>(this);
  }

  public E get(int i) throws IndexOutOfBoundsException {
    try {
      int j = 0;
      LinkedListNode<E> node = this.firstNode;
      while (j++ < i) {
        node = node.getNext();
      }
      return node.getValue();
    } catch (NoSuchElementException e) {
      throw new IndexOutOfBoundsException();
    }
  }

  public E getFirst() throws NoSuchElementException {
    return this.firstNode.getValue();
  }

  public E getLast() throws NoSuchElementException {
    return this.lastNode.getValue();
  }

  public void add(int i, E value) {
    ListIterator<E> iter = this.iterator();
    try {
      range(0, i).forEach((int j) -> iter.next());
      iter.add(value);
    } catch (NoSuchElementException e) {
      throw new IndexOutOfBoundsException();
    }
  }

  public void addFirst(E value) {
    LinkedListNode<E>
      node = new Node<>(value),
      first = this.getFirstNode();
    if (first.isEmpty()) {
      this.firstNode = node;
      this.lastNode = node;
    } else {
      node.setNext(first);
      first.setPrevious(node);
      this.firstNode = node;
    }
  }

  public void addLast(E value) {
    LinkedListNode<E>
      node = new Node<>(value),
      last = this.getLastNode();
    if (last.isEmpty()) {
      this.firstNode = node;
      this.lastNode = node;
    } else {
      node.setPrevious(last);
      last.setNext(node);
      this.lastNode = node;
    }
  }

  public void remove(int i) {
    ListIterator<E> iter = this.iterator();
    try {
      rangeClosed(0, i).forEach((int j) -> iter.next());
      iter.remove();
    } catch (NoSuchElementException e) {
      throw new IndexOutOfBoundsException();
    }
  }

  public static <E> LinkedList<E> of(Iterable<E> values) {
    LinkedList<E> list = new LinkedList<>();
    for (E value: values) {
      list.addLast(value);
    }
    return list;
  }

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner(", ", "[", "]");
    for (E value: this) {
      sj.add(value.toString());
    }
    return sj.toString();
  }
}

class LinkedListIterator<E> implements ListIterator<E> {
  @NonNull
  final private LinkedList<E> list;
  @NonNull
  private LinkedListNode<E> node;
  private int index;

  public LinkedListIterator(LinkedList<E> list) {
    this.list = list;
    this.node = LinkedListNode.empty();
    this.index = -1;
  }

  @Override
  public boolean hasNext() {
    try {
      return index < 0
        ? this.list.getFirstNode().isPresent()
        : this.node.getNext().isPresent();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  @Override
  public E next() throws NoSuchElementException {
    this.node = index < 0
      ? this.list.getFirstNode()
      : this.node.getNext();
    E value = this.node.getValue();
    this.index++;
    return value;
  }

  @Override
  public boolean hasPrevious() {
    try {
      return node.getPrevious().isPresent();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  @Override
  public E previous() throws NoSuchElementException {
    this.node = this.node.getPrevious();
    this.index--;
    return this.node.getValue();
  }

  @Override
  public int nextIndex() {
    return this.index + 1;
  }

  public int getIndex() {
    return this.index;
  }

  @Override
  public int previousIndex() {
    return this.index - 1;
  }

  @Override
  public void remove() {
    switch (this.node) {
      case Empty _e -> {
        throw new IllegalStateException(this.index < 0
          ? "Iteration has not yet begun."
          : "Iteration is over.");
      }
      case Node current -> {
        switch (current.getPrevious()) {
          case Empty _e -> {
            this.list.setFirstNode(current.getNext());
          }
          case Node previous -> {
            previous.setNext(current.getNext());
          }
        }
        switch (current.getNext()) {
          case Empty _e -> {
            this.list.setLastNode(current.getPrevious());
          }
          case Node next -> {
            next.setPrevious(current.getPrevious());
          }
        }
      }
    }
  }

  @Override
  public void set(E value) {
    if (node.isPresent()) {
      node.setValue(value);
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void add(E value) {
    LinkedListNode<E> n = new Node<>(value);
    switch (this.node) {
      case Empty _e -> {
        if (index < 0) {
          LinkedListNode<E> first = this.list.getFirstNode();
          n.setNext(first);
          first.setPrevious(n);
          this.list.setFirstNode(n);
        } else {
          LinkedListNode<E> last = this.list.getLastNode();
          n.setPrevious(last);
          last.setNext(n);
          this.list.setLastNode(n);
          this.index++;
        }
      }
      case Node current -> {
        n.setPrevious(current);
        n.setNext(current.getNext());
        switch (current.getNext()) {
          case Empty _e -> {
            this.list.setLastNode(n);
          }
          case Node next -> {
            next.setPrevious(n);
          }
        }
        current.setNext(n);
      }
    }
  }
}

sealed interface LinkedListNode<E> permits Empty, Node {
  public boolean isEmpty();
  public boolean isPresent();
  public E getValue() throws NoSuchElementException;
  public void setValue(E value) throws NoSuchElementException;
  public LinkedListNode<E> getPrevious() throws NoSuchElementException;
  public Optional<LinkedListNode<E>> getPreviousOptional();
  public void setPrevious(LinkedListNode<E> value) throws NoSuchElementException;
  public void setPreviousSafe(LinkedListNode<E> value);
  public LinkedListNode<E> getNext() throws NoSuchElementException;
  public Optional<LinkedListNode<E>> getNextOptional() ;
  public void setNext(LinkedListNode<E> value) throws NoSuchElementException;
  public void setNextSafe(LinkedListNode<E> value);
  public static <E> LinkedListNode<E> empty() {
    return Empty.getEmpty();
  }
}

final class Empty<E> implements LinkedListNode<E> {
  private static final Empty<Void> emptySingleton = new Empty();

  private Empty() {}

  static Empty getEmpty() {
    return emptySingleton;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean isPresent() {
    return false;
  }

  @Override
  public E getValue() throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  @Override
  public void setValue(E value) throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  @Override
  public LinkedListNode<E> getPrevious() throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  @Override
  public Optional<LinkedListNode<E>> getPreviousOptional() {
    return Optional.empty();
  }

  @Override
  public void setPrevious(LinkedListNode<E> value) throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  @Override
  public void setPreviousSafe(LinkedListNode<E> value) {}

  @Override
  public LinkedListNode<E> getNext() throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  @Override
  public Optional<LinkedListNode<E>> getNextOptional() {
    return Optional.empty();
  }

  @Override
  public void setNext(LinkedListNode<E> value) throws NoSuchElementException {
    throw new NoSuchElementException();
  }

  @Override
  public void setNextSafe(LinkedListNode<E> value) {}
}

@Data
final class Node<E> implements LinkedListNode<E> {
  @NonNull
  private LinkedListNode<E> previous;
  private E value;
  @NonNull
  private LinkedListNode<E> next;

  Node(E value) {
    this.value = value;
    this.previous = (LinkedListNode<E>)LinkedListNode.empty();
    this.next = (LinkedListNode<E>)LinkedListNode.empty();
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean isPresent() {
    return true;
  }

  @Override
  public E getValue() {
    return this.value;
  }

  @Override
  public void setValue(E value) {
    this.value = value;
  }

  @Override
  public LinkedListNode<E> getPrevious() {
    return this.previous;
  }

  @Override
  public Optional<LinkedListNode<E>> getPreviousOptional() {
    return Optional.of(this.previous);
  }

  @Override
  public void setPrevious(LinkedListNode<E> previous) {
    this.previous = previous;
  }

  @Override
  public void setPreviousSafe(LinkedListNode<E> previous) {
    this.setPrevious(previous);
  }

  @Override
  public LinkedListNode<E> getNext() {
    return this.next;
  }

  @Override
  public Optional<LinkedListNode<E>> getNextOptional() {
    return Optional.of(this.next);
  }

  @Override
  public void setNext(LinkedListNode<E> next) {
    this.next = next;
  }

  @Override
  public void setNextSafe(LinkedListNode<E> next) {
    this.setNext(next);
  }
}