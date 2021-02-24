# TestTaskAPI

configuration file - src/main/java/global/Config.java

application entry point - src/main/java/TestTaskApp.java

# Using

localhost:8000/api/install - create tables

localhost:8000/api/login?login={string}&pswd={string} - returns token

localhost:8000/api/payment?token={string}

localhost:8000/api/logout?token={string}
