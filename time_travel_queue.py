from collections import deque

class TimeTravelQueue:
    def __init__(self):
        self.queue = deque()
        self.history = []

    def add(self, value):
        self.queue.append(value)
        self.history.append(("ADD", value))

    def remove(self):
        if not self.queue:
            print("Queue is empty.")
            return
        value = self.queue.popleft()
        self.history.append(("REMOVE", value))
        print(f"Returned {value}.")

    def undo(self):
        if not self.history:
            print("Nothing to undo.")
            return

        op, value = self.history.pop()

        if op == "ADD":
            self.queue.pop()
            print(f"Reverting ADD({value})... Removed {value} from back.")
        else:
            self.queue.appendleft(value)
            print(f"Reverting REMOVE({value})... Pushed {value} to front.")

    def print_queue(self):
        print("Q:", list(self.queue))


def main():
    q = TimeTravelQueue()
    print("Enter commands (ADD x, REMOVE, UNDO, PRINT). Type EXIT to stop.")

    while True:
        command = input("> ").strip()

        if command == "EXIT":
            break

        parts = command.split()

        if not parts:
            continue

        if parts[0] == "ADD" and len(parts) == 2:
            q.add(parts[1])
        elif parts[0] == "REMOVE":
            q.remove()
        elif parts[0] == "UNDO":
            q.undo()
        elif parts[0] == "PRINT":
            q.print_queue()
        else:
            print("Invalid command.")


if __name__ == "__main__":
    main()
