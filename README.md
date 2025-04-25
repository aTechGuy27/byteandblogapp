# ByteAndBlogApp 📝

![Banner](https://via.placeholder.com/1200x300.png?text=ByteAndBlogApp+Banner)

Welcome to **ByteAndBlogApp**, a dynamic full-stack web application that combines blogging, portfolio showcasing, and real-time news updates. Built with **Spring Boot** (backend), **React** (frontend), and **PostgreSQL** (database), this app provides a platform to share blogs, display portfolio projects, and stay updated with the latest news via an RSS feed from NPR.

---

## 🚀 Features

- **Blogging Platform**: Create, read, update, and delete blog posts with rich text content.
- **Portfolio Showcase**: Highlight your projects with images, descriptions, and links.
- **Real-Time News**: Fetch and display the latest news articles from NPR's RSS feed.
- **User Authentication**: Secure user registration, login, and password recovery with JWT-based authentication.
- **Role-Based Access**: Admin users can manage blogs and portfolios, while users can comment on posts.
- **Responsive Design**: Fully responsive UI for seamless use on desktop and mobile devices.
- **Email Integration**: Send password reset emails using Gmail SMTP.
- **File Uploads**: Upload images for blog posts and portfolio items, stored on the server.

---

## 🛠️ Tech Stack

- **Frontend**: React, Tailwind CSS, Framer Motion (for animations)
- **Backend**: Spring Boot, Spring Security, Spring Data JPA
- **Database**: PostgreSQL
- **News Feed**: NPR RSS Feed (`https://www.npr.org/rss/rss.php?id=1001`)
- **Deployment**: Oracle Cloud Infrastructure (OCI) / Render
- **Other Tools**: Maven, Axios, JWT, RestTemplate

---

## 📸 Screenshots

| **Home Page** | **Blog Section** | **Portfolio Section** |
|---------------|----------------|-----------------------|
| ![Home](https://via.placeholder.com/300x200.png?text=Home+Page) | ![Blog](https://via.placeholder.com/300x200.png?text=Blog+Section) | ![Portfolio](https://via.placeholder.com/300x200.png?text=Portfolio+Section) |

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** (for Spring Boot)
- **Maven** (for dependency management)
- **Node.js & npm** (for React frontend)
- **PostgreSQL** (for the database)
- **Git** (for version control)
- An email account with SMTP access (e.g., Gmail) for password reset emails.

---

## 🏁 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/byteandblogapp-backend.git
cd byteandblogapp-backend
```

### 2. Set Up the Backend
- Navigate to the backend directory:
  ```bash
  cd byteandblogapp-backend
  ```
- Configure the database in `src/main/resources/application.properties`:
  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/byteandblogdb
  spring.datasource.username=your-username
  spring.datasource.password=your-password
  spring.datasource.driverClassName=org.postgresql.Driver
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  file.upload-dir=./uploads
  spring.servlet.multipart.enabled=true
  spring.web.resources.static-locations=classpath:/static/,file:./uploads/
  server.port=8081
  jwt.secret=your-32-character-ultra-secure-and-ultra-long-secret
  spring.mail.host=smtp.gmail.com
  spring.mail.port=587
  spring.mail.username=your-email@gmail.com
  spring.mail.password=your-gmail-app-password
  spring.mail.properties.mail.smtp.auth=true
  spring.mail.properties.mail.smtp.starttls.enable=true
  ```
- Build and run the backend:
  ```bash
  mvn clean install
  java -jar target/ByteAndBlogApp-Backend-0.0.1-SNAPSHOT.jar
  ```

### 3. Set Up the Frontend
- Navigate to the frontend directory (assumed to be a separate repo or subdirectory):
  ```bash
  cd byteandblogapp-frontend
  ```
- Install dependencies:
  ```bash
  npm install
  ```
- Build the React app:
  ```bash
  npm run build
  ```
- Copy the build files to the backend's `static` directory:
  ```bash
  cp -r build/* ../byteandblogapp-backend/src/main/resources/static/
  ```

### 4. Run the Application
- With the backend running, access the app at `http://localhost:8081`.
- Register a user, log in, and explore the blogging, portfolio, and news features.

---

## 🌐 Deployment

### Deploying on Oracle Cloud Infrastructure (OCI)

1. **Sign In to OCI Console**:
   - Log in at [cloud.oracle.com](https://cloud.oracle.com) and select a region.

2. **Create a Virtual Cloud Network (VCN)**:
   - Go to **Networking > Virtual Cloud Networks** > **Create VCN with Internet Connectivity**.
   - Name: `byteandblog-vcn`, CIDR: `10.0.0.0/16`.

3. **Create an Autonomous Database (PostgreSQL)**:
   - Go to **Oracle Database > Autonomous Database** > **Create**.
   - Name: `byteandblog-db`, Always Free, Username: `admin`, Password: Set a password.
   - Copy the connection string.

4. **Create a Compute Instance**:
   - Go to **Compute > Instances** > **Create Instance**.
   - Name: `byteandblog-web`, Shape: `VM.Standard.E2.1.Micro`, VCN: `byteandblog-vcn`, Public IP: Yes, SSH Key: Upload your public key.
   - Note the public IP.

5. **Configure Security Lists**:
   - In VCN > Security Lists, add ingress rules for ports 8081 and 22 (SSH).

6. **Connect via SSH**:
   ```bash
   ssh -i ~/.ssh/oci_key opc@<PUBLIC_IP>
   ```

7. **Install Dependencies**:
   ```bash
   sudo dnf update -y
   sudo dnf install java-17-openjdk maven git -y
   ```

8. **Clone Repository**:
   ```bash
   git clone https://github.com/your-username/byteandblogapp-backend.git
   cd byteandblogapp-backend
   ```

9. **Configure Database**:
   - Edit `src/main/resources/application.properties` with the OCI database connection string.

10. **Set Environment Variables**:
    ```bash
    export JWT_SECRET="your-32-character-ultra-secure-and-ultra-long-secret"
    export EMAIL_PASSWORD="your-gmail-app-password"
    export PORT=8081
    ```

11. **Build and Run**:
    ```bash
    mvn clean install
    nohup java -jar target/ByteAndBlogApp-Backend-0.0.1-SNAPSHOT.jar &
    ```

12. **Access the App**:
    - Visit `http://<PUBLIC_IP>:8081`.

---

## 🤝 Contributing

Contributions are welcome! Follow these steps:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Make your changes and commit:
   ```bash
   git commit -m "Add your feature"
   ```
4. Push to your branch:
   ```bash
   git push origin feature/your-feature
   ```
5. Open a Pull Request.

---

## 📜 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 📬 Contact

For questions or feedback, reach out to:

- **Email**: your-email@example.com
- **GitHub**: [your-username](https://github.com/your-username)

---

🌟 **Star this repository if you find it helpful!** 🌟
