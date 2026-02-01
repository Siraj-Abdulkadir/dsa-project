class ContactBrowser:
    def __init__(self):
        self.contacts = []
        self.current = 0
    
    def add(self, name, phone):
        self.contacts.append((name, phone))
        self.contacts.sort()
    
    def down(self):
        if self.current < len(self.contacts) - 1:
            self.current += 1
    
    def up(self):
        if self.current > 0:
            self.current -= 1
    
    def delete(self):
        if self.contacts:
            self.contacts.pop(self.current)
            if self.current >= len(self.contacts) and self.contacts:
                self.current = len(self.contacts) - 1
    
    def show(self):
        if self.contacts:
            name, phone = self.contacts[self.current]
            print(name, phone)
        else:
            print("Empty")

# Test
c = ContactBrowser()
c.add("Segni", "111")
c.add("Siraj", "333")
c.add("Firaol Sultan", "222")
c.show()     # Firaol Sultan
c.down()
c.show()     # Segni
c.down()
c.show()     # Siraj
c.delete()
c.show()     # Segni
   