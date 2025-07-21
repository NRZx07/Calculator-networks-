
# 🧮 UDP Calculator Server & Client (Java)

This project demonstrates a **UDP-based Calculator** using Java's `DatagramSocket`. It allows clients to send simple math expressions to a server, and the server responds with the result.

---

## 🚀 Features

- Uses **UDP protocol** (connectionless and lightweight)
- Supports **basic arithmetic operations**: `+`, `-`, `*`, `/`
- Clean separation between **client** and **server**
- Localhost binding for secure testing
- Handles invalid input gracefully

---

## 🛠️ Requirements

- Java JDK 8 or higher
- Terminal or command-line interface

---

## 📁 Files

- `Calculator.java` → The **server** that listens for and processes expressions.
- `Client.java` → The **client** that sends expressions to the server.

---

## 🧑‍💻 How It Works

### ✅ Example Input (from client):

### ✅ Example Output (from server):

---

## 🧾 Supported Operations

| Expression Format | Operation      |
|-------------------|----------------|
| `a + b`           | Addition       |
| `a - b`           | Subtraction    |
| `a * b`           | Multiplication |
| `a / b`           | Division       |

> Expressions must be space-separated (e.g., `10 * 2`, not `10*2`)

---

## ⚙️ How to Run

### 🟢 Compile both files:
```bash
javac Calculator.java
javac Client.java

