package org.example;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.Data;
import lombok.NonNull;

@Data
public class LinkedList<E> implements Iterable<E> {
  @NonNull
  private LinkedListNode<E> firstNode;
  @NonNull
  private LinkedListNode<E> lastNode;

  public <E> LinkedList() {
    this.firstNode = Empty.getEmpty();
    this.lastNode = Empty.getEmpty();
  }

  @Override
  public Iterator<E> iterator() {
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

  public void prepend(E value) {
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

  public void append(E value) {
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

  public LinkedList<E> of(Iterable<E> values) {
    LinkedList<E> list = new LinkedList<>();
    for (E value: values) {
      list.append(value);
    }
    return list;
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
    this.node = list.getFirstNode();
  }

  @Override
  public boolean hasNext() {
    return node instanceof Node<E>;
  }

  @Override
  public E next() throws NoSuchElementException {
    E value = this.node.getValue();
    this.node = this.node.getNext();
    return value;
  }

  @Override
  public boolean hasPrevious() {
    try {
      return node.getPrevious() instanceof Node<E>;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  @Override
  public E previous() throws NoSuchElementException {
    this.node = this.node.getPrevious();
    return this.node.getValue();
  }

  @Override
  public int nextIndex() {
    return this.index;
  }

  @Override
  public int previousIndex() {
    return this.index - 1;
  }

  @Override
  public void remove() {
    try {
      this.node.getPrevious().setNext(this.node.getNext());
    } catch (NoSuchElementException e) {}
    try {
      this.node.getNext().setPrevious(this.node.getPrevious());
    } catch (NoSuchElementException e) {}
  }

  @Override
  public void set(E value) {
    node.setValue(value);
  }

  @Override
  public void add(E value) {
    LinkedListNode<E> n = new Node<>(value);
    n.setNext(this.node);
    try {
      this.node.setPrevious(n);
    } catch (NoSuchElementException e) {
      this.list.setFirstNode(n);
      this.list.setLastNode(n);
    }
  }
}

sealed interface LinkedListNode<E> permits Empty, Node {
  public boolean isEmpty();
  public E getValue() throws NoSuchElementException;
  public void setValue(E value) throws NoSuchElementException;
  public LinkedListNode<E> getPrevious() throws NoSuchElementException;
  public Optional<LinkedListNode<E>> getPreviousOptional();
  public void setPrevious(LinkedListNode<E> value) throws NoSuchElementException;
  public LinkedListNode<E> getNext() throws NoSuchElementException;
  public Optional<LinkedListNode<E>> getNextOptional() ;
  public void setNext(LinkedListNode<E> value) throws NoSuchElementException;
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
    this.previous = (LinkedListNode<E>)Empty.getEmpty();
    this.next = (LinkedListNode<E>)Empty.getEmpty();
  }

  @Override
  public boolean isEmpty() {
    return false;
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
}