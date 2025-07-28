# chat2unittest

`chat2unittest` is a standalone Java tool that **automatically generates JUnit 4 test classes** for Java production methods using a **Large Language Model (LLM)** via a configurable API.

🧠 This tool is completely **LLM-agnostic**: you can use any model (LLaMA, GPT, Claude, etc.) **as long as it exposes a chat-completions API** (like OpenAI’s).

---

## ✅ Key Features

- Accepts a JSON file listing methods to be tested
- Sends the full class source to your LLM with an appropriate prompt
- Receives generated JUnit test code from the LLM
- Automatically writes test classes to the correct location under `src/test/java`

---

<pre> ## 📁 Project Structure (Expected) project-root/ ├── src/ │ ├── main/ │ │ └── java/ # Production code │ └── test/ │ └── java/ # Generated tests go here └── input.json # File listing methods to test </pre>

The tool supports also projects with multiple submodules, each having that structure (module1/src/..., module2/src/..., ect...).


---

## 📦 Prerequisites

- A running LLM service exposing a `POST /v1/chat/completions` endpoint
- The `chat2unittest.jar` file

---

## 🧾 Input Format (`input.json`)

```json
[
  {
    "absolute/path/to/Class1.java": "[method1, method2, ...]",
    "absolute/path/to/Class2.java": "[method1, method2, ...]"
  }
]
```

Example

```json
[
  {
    "/home/user/project/submodule1/src/main/java/com/example/MyClass.java": "[myMethod1, myMethod2]",
    "/home/user/project/submodule2/src/main/java/com/example/AnotherClass.java": "[anotherMethod]"
  }
]
```

## 🚀 How To Run

java -jar ju-to-jmh/chat2unittest.jar input.json -host https://xxxxxxxx/v1/chat/completions -mdl model_name -tmp temperature

Where:
- host: URL of your LLM chat endpoint
- mdl: Model name (e.g., "codellama-13b-instruct")
- tmp: Temperature for the LLM 

## 📝 Output

The generated test classes will be written to the corresponding `src/test/java` directory, maintaining the same package structure as the production code.
The generated class will be named `ClassNameTest.java` for each `ClassName.java` in the input and contain the test methods for the specified methods in the json file.

## 🛠️ Notes

- The tool has been compiled using Java 23.0.1 and Maven 3.8.9
- The chat2unittest.jar is target/chat2unittest-1.0-SNAPSHOT-jar-with-dependencies.jar renamed.